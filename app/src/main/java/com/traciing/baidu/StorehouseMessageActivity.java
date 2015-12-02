package com.traciing.baidu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.traciing.SearchMapApplication;
import com.traciing.baidu.R;
import com.traciing.common.HttpService;
import com.traciing.dialog.MyDialog;
import com.traciing.domain.storehouseMessage;
import com.traciing.utils.SystemUtil;
import com.traciing.xml.XmlAllMessage;
import com.traciing.xml.XmlCarMessage;
import com.traciing.xml.XmlStoreHouseMessage;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StorehouseMessageActivity extends Activity {

    private Spinner store_name_spin;
    private ListView store_name_message;
    private TextView store_tem_message;
    private TextView item_warehouse_click;

    private static ArrayAdapter<String> adapter_spin;
    private List<String> storehouse_list=null;

    private static CellAdapterForListviewThreeD adapter_listview=null;
    private static View view=null;
    private static List<storehouseMessage> list_store=null;

    private String xml_data;
    XmlAllMessage xam=null;
    XmlStoreHouseMessage xshm=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storehouse_message);

        item_warehouse_click= (TextView) findViewById(R.id.item_name_storechoose);
        item_warehouse_click.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        item_warehouse_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View.OnClickListener ok = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(int i=0;i< SystemUtil.itemList.size();i++)
                            System.out.println("选择的ITeam"+SystemUtil.itemList.get(i));
                        refreshMessageClick();
                    }
                };

                View.OnClickListener cancel = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //do nothing
                    }
                };
                SystemUtil.showPartConfirm(StorehouseMessageActivity.this, R.string.door_item_choose, R.string.alert_quit, ok, cancel);
            }
        });
        store_tem_message= (TextView) findViewById(R.id.store_tem_storehouse);
        store_name_message= (ListView) findViewById(R.id.message_storehouse);
        store_name_spin= (Spinner) findViewById(R.id.store_name_choose);

        xml_data=getIntent().getStringExtra("xmlstr");

        //仓库选择
        initStorehouseChoose();
        initUploadingDialog();
        //刷新当前页面
        list_store=new ArrayList<storehouseMessage>();
        refreshMessage();
    }

    private void initStorehouseChoose(){
        Serializer serializer = new Persister();
        try {
            xam=serializer.read(XmlAllMessage.class,xml_data,false);
        } catch (Exception e) {
            System.out.println("xml解析异常:"+e);
        }
        //仓库选择
        storehouse_list=new ArrayList<String>();
        int store_num=xam.getWarehousedata().size();
        for(int i=0;i<store_num;i++){
            String store_name=xam.getWarehousedata().get(i).getWarehousename();
            storehouse_list.add(store_name);
        }
//        storehouse_list.add("广州1号");
//        storehouse_list.add("广州2号");
        adapter_spin=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,storehouse_list);
        adapter_spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        store_name_spin.setAdapter(adapter_spin);
        store_name_spin.setOnItemSelectedListener(new SpinnerSelectedListener());
        store_name_spin.setVisibility(View.VISIBLE);
    }

    //spinner
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            System.out.println("spinner ++++++++++++++++=" + store_name_spin.getSelectedItem().toString());
            refreshMessage();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }


    //lsitview
    public static class CellAdapterForListviewThreeD extends BaseAdapter {

        LayoutInflater inflater;

        public CellAdapterForListviewThreeD(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list_store.size();
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder1=null;
            if(convertView==null) {
                holder1 = new ViewHolder();
                view = inflater.inflate(R.layout.store_message_listview, null);
                holder1.item_name_listview= (TextView) view.findViewById(R.id.item_name_storechoose_in);
                holder1.item_orderno_listview= (TextView) view.findViewById(R.id.all_orderno_storehouse_in);
                holder1.store_num_listview= (TextView) view.findViewById(R.id.all_num_storehouse_in);
                holder1.traciing_listview= (TextView) view.findViewById(R.id.traciing_storehouse_in);
                holder1.out_store_listview= (TextView) view.findViewById(R.id.out_store_storehouse_in);

                view.setTag(holder1);
            }else{
                view=convertView;
                holder1= (ViewHolder) view.getTag();
            }
            if(position%2!=0)
                view.setBackgroundColor(0xffe6efff);
            else
                view.setBackgroundColor(0xffffffff);
            final ViewHolder holder=holder1;
            if(list_store.get(position).getItem_name()!=null) {
                holder.item_name_listview.setText(list_store.get(position).getItem_name());
                holder.item_orderno_listview.setText(list_store.get(position).getItem_orderno());
                holder.store_num_listview.setText(list_store.get(position).getStore_num());
                holder.traciing_listview.setText(list_store.get(position).getTraciing_num());
                holder.out_store_listview.setText(list_store.get(position).getOut_store());
            }
            return view;
        }
        public class ViewHolder {
            TextView item_name_listview;
            TextView item_orderno_listview;
            TextView item_num_listview;
            TextView store_num_listview;
            TextView traciing_listview;
            TextView out_store_listview;
        }
    }

    MyDialog dialog;
    private void initUploadingDialog() {
        dialog = new MyDialog(StorehouseMessageActivity.this, R.style.MyDialog);
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

    private void refreshMessage(){
        dialog.show();
        dialog.changeInfo("查询中...");
        //拿取到当天的温度数据
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String er = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date_no=new Date(System.currentTimeMillis());
                    // 网络不好，重试3次
                    for (int i = 0; i < 3; i++) {
                        er = HttpService.getStoreMessage(store_name_spin.getSelectedItem().toString(),sdf.format(date_no));
                        System.out.println("仓库信息获取到的xml为:"+er);
                        if(!er.equals("0")){
                            break;
                        }
                    }
                }catch(Exception E){
                    System.out.print(E);
                }
                return er;
            }

            protected void onPostExecute(String er) {
                dialog.cancel();
                SearchMapApplication.item_name_list_door=new ArrayList<String>();
                Serializer serializer = new Persister();
                try {
                    xshm=serializer.read(XmlStoreHouseMessage.class,er,false);
                } catch (Exception e) {
                    System.out.println("xml解析异常:"+e);
                }
                list_store.clear();
                int store_siez=xshm.getWarehouseqty().size();
                double all_store_num=0;
                double all_traciing_num=0;
                double all_out_store=0;
                for(int i=0;i<store_siez;i++){
                    if(xshm.getWarehouseqty().get(i).getItemqty()!=null) {
                        int itemqty_size = xshm.getWarehouseqty().get(i).getItemqty().size();
                        for (int k = 0; k < itemqty_size; k++) {
                            if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getStoreqty()!=null)
                                all_store_num += Double.parseDouble(xshm.getWarehouseqty().get(i).getItemqty().get(k).getStoreqty().toString());
                            if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getOrderqty()!=null)
                                all_traciing_num += Double.parseDouble(xshm.getWarehouseqty().get(i).getItemqty().get(k).getOrderqty().toString());
                            if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getTransferqty()!=null)
                                all_out_store += Double.parseDouble(xshm.getWarehouseqty().get(i).getItemqty().get(k).getTransferqty().toString());
                            System.out.println("计算" + all_store_num + "----" + all_traciing_num + "---" + all_out_store);
                            storehouseMessage sto = new storehouseMessage();
                            sto.setItem_name(xshm.getWarehouseqty().get(i).getItemqty().get(k).getItemname());
                            sto.setItem_orderno(xshm.getWarehouseqty().get(i).getItemqty().get(k).getLotno());
                            sto.setStore_num(xshm.getWarehouseqty().get(i).getItemqty().get(k).getStoreqty());
                            sto.setTraciing_num(xshm.getWarehouseqty().get(i).getItemqty().get(k).getOrderqty());
                            sto.setOut_store(xshm.getWarehouseqty().get(i).getItemqty().get(k).getTransferqty());
                            if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getItemname()!=null)
                                if (!SearchMapApplication.item_name_list_door.contains(xshm.getWarehouseqty().get(i).getItemqty().get(k).getItemname().toString()))
                                    SearchMapApplication.item_name_list_door.add(xshm.getWarehouseqty().get(i).getItemqty().get(k).getItemname().toString());
                            list_store.add(sto);
                        }
                    }
                }
                storehouseMessage sto1=new storehouseMessage();
                sto1.setItem_name("合计");
                sto1.setItem_orderno("");
                sto1.setStore_num(String.valueOf(all_store_num));
                sto1.setTraciing_num(String.valueOf(all_traciing_num));
                sto1.setOut_store(String.valueOf(all_out_store));
                list_store.add(sto1);
                adapter_listview = new CellAdapterForListviewThreeD(StorehouseMessageActivity.this);
                store_name_message.setAdapter(adapter_listview);
                String tem_message="";
//                String tem_message="仓库实时温度数据:"+store_name_spin.getSelectedItem().toString()+"\n";
                int tem_size=xshm.getWarehousetem().size();
                for(int i=0;i<tem_size;i++){
                    if(xshm.getWarehousetem().get(i).getPointtem()!=null) {
                        int temsize = xshm.getWarehousetem().get(i).getPointtem().size();
                        for (int k = 0; k < temsize; k++) {
                            tem_message += "监控点" + xshm.getWarehousetem().get(i).getPointtem().get(k).getPointstr().toString()
                                    + ":  " + xshm.getWarehousetem().get(i).getPointtem().get(k).getValue().toString() + "℃" +"        ";
                            if(k/2!=0){
                                tem_message+="\n";
                            }
                        }
                    }
                }
                store_tem_message.setText(tem_message);
            }
        }.execute();
    }

    private void refreshMessageClick(){
        list_store.clear();
        int store_siez=xshm.getWarehouseqty().size();
        double all_store_num=0;
        double all_traciing_num=0;
        double all_out_store=0;
        for(int m=0;m<SystemUtil.itemList.size();m++) {
            if (SystemUtil.itemList.get(m) != null) {
                System.out.println("显示的名字" + SystemUtil.itemList.get(m));
                for (int i = 0; i < store_siez; i++) {
                    int itemqty_size = xshm.getWarehouseqty().get(i).getItemqty().size();
                    for (int k = 0; k < itemqty_size; k++) {
                        if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getItemname()!=null) {
                            if (SystemUtil.itemList.get(m).toString().equals(xshm.getWarehouseqty().get(i).getItemqty().get(k).getItemname().toString())) {
                                if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getStoreqty()!=null)
                                    all_store_num += Double.parseDouble(xshm.getWarehouseqty().get(i).getItemqty().get(k).getStoreqty().toString());
                                if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getOrderqty()!=null)
                                    all_traciing_num += Double.parseDouble(xshm.getWarehouseqty().get(i).getItemqty().get(k).getOrderqty().toString());
                                if(xshm.getWarehouseqty().get(i).getItemqty().get(k).getTransferqty()!=null)
                                    all_out_store += Double.parseDouble(xshm.getWarehouseqty().get(i).getItemqty().get(k).getTransferqty().toString());
                                System.out.println("计算" + all_store_num + "----" + all_traciing_num + "---" + all_out_store);
                                storehouseMessage sto = new storehouseMessage();
                                sto.setItem_name(xshm.getWarehouseqty().get(i).getItemqty().get(k).getItemname());
                                sto.setItem_orderno(xshm.getWarehouseqty().get(i).getItemqty().get(k).getLotno());
                                sto.setStore_num(xshm.getWarehouseqty().get(i).getItemqty().get(k).getStoreqty());
                                sto.setTraciing_num(xshm.getWarehouseqty().get(i).getItemqty().get(k).getOrderqty());
                                sto.setOut_store(xshm.getWarehouseqty().get(i).getItemqty().get(k).getTransferqty());
                                list_store.add(sto);
                            }
                        }
                    }
                }
            }
        }
        storehouseMessage sto1=new storehouseMessage();
        sto1.setItem_name("合计");
        sto1.setItem_orderno("");
        sto1.setStore_num(String.valueOf(all_store_num));
        sto1.setTraciing_num(String.valueOf(all_traciing_num));
        sto1.setOut_store(String.valueOf(all_out_store));
        list_store.add(sto1);
        adapter_listview = new CellAdapterForListviewThreeD(StorehouseMessageActivity.this);
        store_name_message.setAdapter(adapter_listview);
    }
}
