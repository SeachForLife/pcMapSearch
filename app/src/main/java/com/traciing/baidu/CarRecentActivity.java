package com.traciing.baidu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.traciing.baidu.R;
import com.traciing.common.HttpService;
import com.traciing.utils.SystemUtil;
import com.traciing.xml.XmlCarMessage;
import com.traciing.xml.XmlCarRecentMessage;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CarRecentActivity extends Activity {

    MapView mMapView = null;
    BaiduMap mBaiduMap=null;
    private Marker mMarker;

    private String xmldata;
    private XmlCarRecentMessage xmlxcr;

    private TextView storeMessageShow;

    //用来记录小窗口信息
    private List<String> list_storename=new ArrayList<String>();
    private List<Marker> list_marker=new ArrayList<Marker>();
    private List<String> list_storemessage=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_car_recent);

        storeMessageShow= (TextView) findViewById(R.id.storeMessage);

        mMapView = (MapView) findViewById(R.id.bmapView1);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        xmldata=getIntent().getStringExtra("xml_data");

        Serializer serializer = new Persister();
        try {
            xmlxcr=serializer.read(XmlCarRecentMessage.class,xmldata,false);
        } catch (Exception e) {
            System.out.println("xml解析异常:"+e);
        }

        //GSP轨迹显示
        List<LatLng> pts = new ArrayList<LatLng>();
        //起始点标志
        BitmapDescriptor bitmap_start = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_st);
        //结束点标志
        BitmapDescriptor bitmap_end = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_en);
        if(xmlxcr.getGpsinfos()!=null){
            for(int k=0;k<xmlxcr.getGpsinfos().size();k++){
                if(xmlxcr.getGpsinfos().get(k).getGpsdata()!=null) {
                    for (int i = 0; i < xmlxcr.getGpsinfos().get(k).getGpsdata().size(); i++) {
                        double lat = Double.parseDouble(xmlxcr.getGpsinfos().get(k).getGpsdata().get(i).getLatPosition().toString());
                        double lng = Double.parseDouble(xmlxcr.getGpsinfos().get(k).getGpsdata().get(i).getLngPosition().toString());
                        LatLng RecentPoint = new LatLng(lat, lng);
                        pts.add(RecentPoint);
                        if (i == 0) {
                            System.out.println("标记起始点");
                            OverlayOptions options = new MarkerOptions()
                                    .position(RecentPoint)
                                    .icon(bitmap_start);
                            mBaiduMap.addOverlay(options);
                            MapStatus mMapStatus = new MapStatus.Builder()
                                    .target(RecentPoint)
                                    .zoom(11)
                                    .build();
                            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                            mBaiduMap.setMapStatus(mMapStatusUpdate);
                        }
                        if (i == (xmlxcr.getGpsinfos().get(k).getGpsdata().size() - 1)) {
                            OverlayOptions options = new MarkerOptions()
                                    .position(RecentPoint)
                                    .icon(bitmap_end);
                            mBaiduMap.addOverlay(options);
                        }
                    }
                    OverlayOptions polygonOption = new PolylineOptions().width(10)
                            .color(0xAAFF0000).points(pts);
                    mBaiduMap.addOverlay(polygonOption);
                }
            }
        }else{
            LatLng point_select = new LatLng(23.131748, 113.275742);
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(point_select)
                    .zoom(11)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }

        //仓库图标
        BitmapDescriptor bitmap_store = BitmapDescriptorFactory
                .fromResource(R.drawable.store_ic);
        //显示所有门店位置,并展示门店信息
        if(xmlxcr.getStoreinfos()!=null){//如果有门店存在进入
            for(int i=0;i<xmlxcr.getStoreinfos().size();i++){
                if(xmlxcr.getStoreinfos().get(i).getStoredata()!=null){
                    for(int k=0;k<xmlxcr.getStoreinfos().get(i).getStoredata().size();k++){
                        double lat = Double.parseDouble(xmlxcr.getStoreinfos().get(i).getStoredata().get(k).getLatPosition().toString());
                        double lng = Double.parseDouble(xmlxcr.getStoreinfos().get(i).getStoredata().get(k).getLngPosition().toString());
                        String StoreName=xmlxcr.getStoreinfos().get(i).getStoredata().get(k).getStoreName().toString();
                        LatLng storePoint = new LatLng(lat, lng);
                        //在地图上把门店标示出来
                        OverlayOptions options = new MarkerOptions()
                                .position(storePoint)
                                .icon(bitmap_store);
                        mMarker = (Marker) (mBaiduMap.addOverlay(options));
                        list_storename.add(k,StoreName);
                        list_marker.add(k,mMarker);
                        //用来记录当前门店的所有信息字段
                        String StoreMessageStr="";
                        if(xmlxcr.getStoreinfos().get(i).getStoredata().get(k).getOrderInfo()!=null){
                            for(int m=0;m<xmlxcr.getStoreinfos().get(i).getStoredata().get(k).getOrderInfo().size();m++){
                                if(xmlxcr.getStoreinfos().get(i).getStoredata().get(k).getOrderInfo().get(m).getOrderdata()!=null){
                                    List<XmlCarRecentMessage.XmlOrderdata> orderDataList=xmlxcr.getStoreinfos().get(i).getStoredata().get(k).getOrderInfo().get(m).getOrderdata();
                                    for(int n=0;n<orderDataList.size();n++){
                                        StoreMessageStr+="订单编号："+orderDataList.get(n).getLotNo()+"\n";
                                        StoreMessageStr+="商品名称："+orderDataList.get(n).getItemName()+"\n";
                                        StoreMessageStr+="数量    ："+orderDataList.get(n).getQty()+"\n";
                                    }
                                }
                            }
                        }
                        System.out.println(StoreMessageStr);
                        list_storemessage.add(k, StoreMessageStr);
                    }
                }
            }
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                //悬浮框提示
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                InfoWindow.OnInfoWindowClickListener listener = null;
                for(int i=0;i<list_marker.size();i++) {
                    if (marker == list_marker.get(i)) {
                        //详细信息展示
                        storeMessageShow.setVisibility(View.VISIBLE);
                        storeMessageShow.setText(list_storemessage.get(i).toString());
//                        storeMessageShow.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                storeMessageShow.setVisibility(View.GONE);
//                            }
//                        });
                        button.setText(list_storename.get(i).toString());
                        button.setTextColor(0xff000000);
                        listener = new InfoWindow.OnInfoWindowClickListener() {
                            public void onInfoWindowClick() {
                                mBaiduMap.hideInfoWindow();
                                storeMessageShow.setVisibility(View.GONE);
                            }
                        };
                        LatLng ll = marker.getPosition();
                        InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
                        mBaiduMap.showInfoWindow(mInfoWindow);
                    }
                }
                return true;
            }
        });

    }


}
