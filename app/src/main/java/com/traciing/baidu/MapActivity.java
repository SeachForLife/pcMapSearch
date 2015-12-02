package com.traciing.baidu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
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
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.traciing.common.HttpService;
import com.traciing.dialog.MyDialog;
import com.traciing.utils.SystemUtil;
import com.traciing.xml.XmlAllMessage;
import com.traciing.xml.XmlCarMessage;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MapActivity extends Activity implements View.OnClickListener {

    MapView mMapView = null;
    BaiduMap mBaiduMap=null;
    private Marker mMarker;

    private Button self_menu;
    private Button car_show;
    private Button store_show;
    private Button door_show;
    private Spinner city_change;
    private static ArrayAdapter<String> adapter_spin;
    private List<String> citys_list=null;
    private String xml_data;

    XmlAllMessage xam=null;
    MyDialog dialog;

    //用来记录小窗口信息
    private List<Marker> list_marker=new ArrayList<Marker>();
    private List<String> list_carnum=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);

        initUploadingDialog();

        xml_data=getIntent().getStringExtra("xmlstr");
        Serializer serializer = new Persister();
        try {
            xam=serializer.read(XmlAllMessage.class,xml_data,false);
        } catch (Exception e) {
            System.out.println("xml解析异常:"+e);
        }
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        LatLng point_GZ = new LatLng(23.131748, 113.275742);
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point_GZ)
                .zoom(11)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //切换城市信息
        initchooseCity();
        //车辆及仓库现在位置显示
        initMapMark();

        door_show= (Button) findViewById(R.id.md_message_show);
        door_show.setOnClickListener(this);
        car_show= (Button) findViewById(R.id.car_message_show);
        car_show.setOnClickListener(this);
        store_show= (Button) findViewById(R.id.store_message_show);
        store_show.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v==door_show){
            System.out.println("选择门店信息查询");
            Intent intent = new Intent(MapActivity.this,
                    DoorMessageActivity.class);
            intent.putExtra("xmlstr",xml_data);
            startActivity(intent);
        }else if(v==car_show){
            if(xam.getCardata()!=null){
                System.out.println("选择显示车辆信息");
                Intent intent = new Intent(MapActivity.this,
                        AllcarMessageActivity.class);
                intent.putExtra("xmldata",xml_data);
                startActivity(intent);
            }else{
                SystemUtil.showConfirm(MapActivity.this, R.string.check_user_car_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        return;
                    }
                });
            }
        }else if(v==store_show){
            System.out.println("选择了查询仓库信息");
            if(xam.getWarehousedata()!=null) {
                Intent intent1 = new Intent(MapActivity.this,
                        StorehouseMessageActivity.class);
                intent1.putExtra("xmlstr", xml_data);
                startActivity(intent1);
            }else{
                SystemUtil.showConfirm(MapActivity.this, R.string.check_user_storehouse_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        return;
                    }
                });
            }
        }
    }

    //切换地图
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            LatLng point_select=null;
            if(city_change.getSelectedItem().toString().equals("广州")) {
                point_select = new LatLng(23.131748, 113.275742);
            }else if(city_change.getSelectedItem().toString().equals("佛山")){
                point_select = new LatLng(23.0255516, 113.129714);
            }else if(city_change.getSelectedItem().toString().equals("珠海")){
                point_select = new LatLng(22.27404, 113.588031);
            }else if(city_change.getSelectedItem().toString().equals("东莞")){
                point_select = new LatLng(23.025996, 113.757972);
            }else if(city_change.getSelectedItem().toString().equals("惠州")){
                point_select = new LatLng(23.114604, 114.426468);
            }else if(city_change.getSelectedItem().toString().equals("中山")){
                point_select = new LatLng(22.51954, 113.399351);
            }else if(city_change.getSelectedItem().toString().equals("江门")){
                point_select = new LatLng(22.585796, 113.088186);
            }else if(city_change.getSelectedItem().toString().equals("肇庆")){
                point_select = new LatLng(23.052595, 112.470167);
            }else if(city_change.getSelectedItem().toString().equals("深圳")){
                point_select = new LatLng(22.550326, 114.06823);
            }else if(city_change.getSelectedItem().toString().equals("汕头")){
                point_select = new LatLng(23.354184, 116.690418);
            }else if(city_change.getSelectedItem().toString().equals("韶关")){
                point_select = new LatLng(24.819753, 113.603372);
            }else if(city_change.getSelectedItem().toString().equals("河源")){
                point_select = new LatLng(23.74402, 114.708834);
            }else if(city_change.getSelectedItem().toString().equals("汕尾")){
                point_select = new LatLng(22.79046, 115.38471);
            }else if(city_change.getSelectedItem().toString().equals("阳江")){
                point_select = new LatLng(21.863467, 111.993395);
            }else if(city_change.getSelectedItem().toString().equals("湛江")){
                point_select = new LatLng(21.215248, 110.365929);
            }else if(city_change.getSelectedItem().toString().equals("茂名")){
                point_select = new LatLng(21.667689, 110.934982);
            }else if(city_change.getSelectedItem().toString().equals("清远")){
                point_select = new LatLng(23.68735, 113.065207);
            }else if(city_change.getSelectedItem().toString().equals("潮州")){
                point_select = new LatLng(23.662871, 116.629214);
            }else if(city_change.getSelectedItem().toString().equals("云浮")){
                point_select = new LatLng(22.922, 112.051233);
            }

            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(point_select)
                    .zoom(11)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
    //切换地图结束

    //重写menu，载入新的格式
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 默认点击menu菜单，菜单项不现实图标，反射强制其显示
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {

        if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null)
        {
            if (menu.getClass().getSimpleName().equals("MenuBuilder"))
            {
                try
                {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e)
                {
                }
            }

        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.id_menu_car_message:
                System.out.println("选择显示车辆信息");
                if(xam.getCardata()!=null) {
                    Intent intent = new Intent(MapActivity.this,
                            AllcarMessageActivity.class);
                    intent.putExtra("xmldata", xml_data);
                    startActivity(intent);
                }else{
                    SystemUtil.showConfirm(MapActivity.this, R.string.check_user_car_no, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                }
                break;
            case R.id.id_menu_storehouse_message:
                if(xam.getWarehousedata()!=null){
                    System.out.println("选择了查询仓库信息");
                    Intent intent1 = new Intent(MapActivity.this,
                            StorehouseMessageActivity.class);
                    intent1.putExtra("xmlstr",xml_data);
                    startActivity(intent1);
                }else{
                    SystemUtil.showConfirm(MapActivity.this, R.string.check_user_storehouse_no, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //构建marker  包含车辆，仓库
    private void initMapMark(){

        list_marker.clear();
        list_carnum.clear();

        BitmapDescriptor bitmap_car = BitmapDescriptorFactory
                .fromResource(R.drawable.car_icon);
        if(xam.getCardata()!=null){
            int carsize=xam.getCardata().size();
            System.out.println("当前客户下车辆的数量为"+carsize);
            double lat;
            double lng;
            for(int i=0;i<xam.getCardata().size();i++){
                lat=Double.parseDouble(xam.getCardata().get(i).getLatpostion().toString());
                lng=Double.parseDouble(xam.getCardata().get(i).getLngpostion().toString());
                LatLng point=new LatLng(lat,lng);
                OverlayOptions options = new MarkerOptions()
                        .position(point)
                        .icon(bitmap_car);
                mMarker = (Marker) (mBaiduMap.addOverlay(options));
                list_marker.add(i,mMarker);
                list_carnum.add(i,xam.getCardata().get(i).getCarnumber().toString());
            }
        }
        BitmapDescriptor bitmap_warehouse = BitmapDescriptorFactory
                .fromResource(R.drawable.store_icon);
        if(xam.getWarehousedata()!=null){
            int warehousesize=xam.getWarehousedata().size();
            System.out.println("当前客户下仓库的数量为"+warehousesize);
            double lat;
            double lng;
            for(int i=0;i<warehousesize;i++){
                lat=Double.parseDouble(xam.getWarehousedata().get(i).getLatPostion().toString());
                lng=Double.parseDouble(xam.getWarehousedata().get(i).getLngPostion().toString());
                LatLng point=new LatLng(lat,lng);
                OverlayOptions options = new MarkerOptions()
                        .position(point)
                        .icon(bitmap_warehouse);
                mMarker = (Marker) (mBaiduMap.addOverlay(options));
                if(xam.getCardata()!=null) {
                    list_marker.add(xam.getCardata().size() + i, mMarker);
                    list_carnum.add(xam.getCardata().size() + i, xam.getWarehousedata().get(i).getWarehousename().toString());
                }else{
                    list_marker.add(i, mMarker);
                    list_carnum.add(i, xam.getWarehousedata().get(i).getWarehousename().toString());
                }
            }
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                InfoWindow.OnInfoWindowClickListener listener = null;
                for(int i=0;i<list_marker.size();i++) {
                    if (marker == list_marker.get(i)) {
                        button.setText(list_carnum.get(i).toString());
                        button.setTextColor(0xff000000);
                        final String value=list_carnum.get(i).toString();
                        final int value_postion=i;
                        listener = new InfoWindow.OnInfoWindowClickListener() {
                            public void onInfoWindowClick() {
//                                mBaiduMap.hideInfoWindow();
                                dialog.show();
                                dialog.changeInfo("查询中...");
                                //拿取到当天的温度数据
                                int carsize=xam.getCardata().size();
                                if(value_postion<carsize) {
                                    new AsyncTask<Void, Void, String>() {

                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            String er = null;
                                            try {
                                                String str = value;
                                                str = str.substring(1, str.length());
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                Date date_no = new Date(System.currentTimeMillis());
                                                // 网络不好，重试3次
                                                for (int i = 0; i < 3; i++) {
                                                    er = HttpService.getCarMessage(str, sdf.format(date_no));
                                                    if (!er.equals("0")) {
                                                        break;
                                                    }
                                                }
                                            } catch (Exception E) {
                                                System.out.print(E);
                                            }
                                            return er;
                                        }

                                        protected void onPostExecute(String er) {
                                            dialog.cancel();
                                            if (!er.equals("0")) {
                                                String str = value;
                                                str = str.substring(1, str.length());
                                                Intent intent = new Intent(MapActivity.this,
                                                        chartActivity.class);
                                                intent.putExtra("car_num", str);
                                                intent.putExtra("carmessage", er);
                                                startActivity(intent);
                                            }
                                        }
                                    }.execute();
                                }else{
                                    if(xam.getWarehousedata()!=null) {
                                        Intent intent1 = new Intent(MapActivity.this,
                                                StorehouseMessageActivity.class);
                                        intent1.putExtra("xmlstr", xml_data);
                                        startActivity(intent1);
                                    }else{
                                        SystemUtil.showConfirm(MapActivity.this, R.string.check_user_storehouse_no, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                return;
                                            }
                                        });
                                    }
                                }
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

    private void initchooseCity(){
        //城市切换
        city_change= (Spinner) findViewById(R.id.city_choose_spin);
        citys_list=new ArrayList<String>();
        citys_list.add("广州");
        citys_list.add("深圳");
        citys_list.add("佛山");
        citys_list.add("珠海");
        citys_list.add("东莞");
        citys_list.add("惠州");
        citys_list.add("中山");
        citys_list.add("江门");
        citys_list.add("肇庆");
        citys_list.add("汕头");
        citys_list.add("韶关");
        citys_list.add("河源");
        citys_list.add("汕尾");
        citys_list.add("阳江");
        citys_list.add("湛江");
        citys_list.add("茂名");
        citys_list.add("清远");
        citys_list.add("潮州");
        citys_list.add("云浮");

        adapter_spin=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,citys_list);
        adapter_spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city_change.setAdapter(adapter_spin);
        city_change.setOnItemSelectedListener(new SpinnerSelectedListener());
        city_change.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            MapActivity.this.finish();
            LoginActivity.instance.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        return true;
    }

    private void initUploadingDialog() {
        dialog = new MyDialog(MapActivity.this, R.style.MyDialog);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        || keyCode == KeyEvent.KEYCODE_HOME) {
                    return true;
                } else {
                    return false; // 默认返回 false
                }
            }
        });
    }

}
