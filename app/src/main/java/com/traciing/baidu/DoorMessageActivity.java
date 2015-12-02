package com.traciing.baidu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.traciing.SearchMapApplication;
import com.traciing.baidu.R;
import com.traciing.common.HttpService;
import com.traciing.dialog.MyDialog;
import com.traciing.domain.DoorMessage;
import com.traciing.utils.SystemUtil;
import com.traciing.xml.XmlAllMessage;
import com.traciing.xml.XmlDoorMessage;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoorMessageActivity extends Activity {

    private Spinner warehouse_choose;
    private Spinner store_house;
    private EditText start_time;
    private EditText end_time;
    private Button door_seach;
    private ListView door_message_list;
    private TextView itemname_click;

    private List<String> storehouse_list=null;
    private List<String> door_list=null;
    private static ArrayAdapter<String> adapter_spin_warehouse;
    private static ArrayAdapter<String> adapter_spin_door;
    private Map<String ,List> spin_map=new HashMap<String, List>();

    private String initEndDateTime = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    MyDialog dialog;

    private static CellAdapterForListviewThreeD adapter_door_listview=null;
    private static View view=null;
    private static List<DoorMessage> list_door=null;

    private String xml_data;
    XmlAllMessage xam=null;
    XmlDoorMessage xd=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_message);

        initUploadingDialog();

        list_door=new ArrayList<DoorMessage>();
        itemname_click= (TextView) findViewById(R.id.item_name_storechoose);
        itemname_click.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        warehouse_choose= (Spinner) findViewById(R.id.door_store_name_choose);
        store_house= (Spinner) findViewById(R.id.door_md_name_choose);
        start_time= (EditText) findViewById(R.id.start_time_door);
        end_time= (EditText) findViewById(R.id.end_time_door);
        door_message_list= (ListView) findViewById(R.id.door_message_listview);
        door_seach= (Button) findViewById(R.id.door_search);
        door_seach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_time.getText().toString().equals("")||start_time.getText()==null){
                    SystemUtil.showConfirm(DoorMessageActivity.this, R.string.start_time_null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                    return;
                }
                if(end_time.getText().toString().equals("")||end_time.getText()==null){
                    SystemUtil.showConfirm(DoorMessageActivity.this, R.string.end_time_null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                    return;
                }
                if(store_house.getSelectedItem()==null){
                    SystemUtil.showConfirm(DoorMessageActivity.this, R.string.door_null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                    return;
                }
                String st=start_time.getText().toString();
                String et=end_time.getText().toString();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    if(df.parse(et).getTime()<df.parse(st).getTime()){
                        SystemUtil.showConfirm(DoorMessageActivity.this, R.string.start_big_end, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                return;
                            }
                        });
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Search_door_Message();
            }
        });

        //checkbox 多选过滤
        itemname_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View.OnClickListener ok = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshItemMessageItemChoose();
                    }
                };

                View.OnClickListener cancel = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //do nothing
                    }
                };
                SystemUtil.showPartConfirm(DoorMessageActivity.this, R.string.door_item_choose, R.string.alert_quit, ok, cancel);
            }
        });
        xml_data=getIntent().getStringExtra("xmlstr");

        //编辑框日期定义
        initEditText(start_time);
        initEditText(end_time);

        initSpinner(adapter_spin_warehouse,storehouse_list,warehouse_choose);
    }

    public void initEditText(final EditText edit){
        edit.setInputType(InputType.TYPE_NULL);
        edit.clearFocus();
        edit.setCursorVisible(false);
        Date date_no=new Date(System.currentTimeMillis());
        initEndDateTime=sdf.format(date_no);
        edit.setText(initEndDateTime);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        DoorMessageActivity.this, initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(edit);
            }
        });
    }

    public void initSpinner(ArrayAdapter adapter_spin_common,List list_common,Spinner spin_common){
        Serializer serializer = new Persister();
        try {
            xam=serializer.read(XmlAllMessage.class,xml_data,false);
        } catch (Exception e) {
            System.out.println("xml解析异常:"+e);
        }
        storehouse_list=new ArrayList<String>();
        if(xam.getWarehousedata()!=null) {
            for (int i = 0; i < xam.getWarehousedata().size(); i++) {
                door_list = new ArrayList<String>();
                storehouse_list.add(xam.getWarehousedata().get(i).getWarehousename());
                if( xam.getWarehousedata().get(i).getStoredata()!=null) {
                    for (int k = 0; k < xam.getWarehousedata().get(i).getStoredata().size(); k++) {
                        if(xam.getWarehousedata().get(i).getStoredata().get(k).getStoreinfo()!=null) {
                            for (int m = 0; m < xam.getWarehousedata().get(i).getStoredata().get(k).getStoreinfo().size(); m++) {
                                door_list.add(xam.getWarehousedata().get(i).getStoredata().get(k).getStoreinfo().get(m).getStore_name());
                                spin_map.put(storehouse_list.get(i), door_list);
                            }
                        }else{
                            spin_map.put(storehouse_list.get(i), door_list);
                        }
                    }
                }
            }
        }
        adapter_spin_common=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,storehouse_list);
        adapter_spin_common.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_common.setAdapter(adapter_spin_common);
        spin_common.setOnItemSelectedListener(new SpinnerSelectedListener());
        spin_common.setVisibility(View.VISIBLE);

        door_list=spin_map.get(spin_common.getSelectedItem());
        initSpinner1(adapter_spin_door, door_list, store_house);
    }

    public void initSpinner1(ArrayAdapter adapter_spin_common,List list_common,Spinner spin_common){
        adapter_spin_common=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list_common);
        adapter_spin_common.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_common.setAdapter(adapter_spin_common);
//        spin_common.setOnItemSelectedListener(new SpinnerSelectedListener());
        spin_common.setVisibility(View.VISIBLE);
    }

    private class SpinnerSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            door_list=spin_map.get(warehouse_choose.getSelectedItem());
            initSpinner1(adapter_spin_door,door_list,store_house);
            if(store_house.getSelectedItem()==null){
                store_house.setClickable(false);
            }else{
                store_house.setClickable(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void initUploadingDialog() {
        dialog = new MyDialog(DoorMessageActivity.this, R.style.MyDialog);
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

    private void Search_door_Message(){
        dialog.show();
        dialog.changeInfo("查询中...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String str = null;
                try {
                    // 网络不好，重试3次
                    for (int i = 0; i < 3; i++) {
                        str = HttpService.getDoorMessage(warehouse_choose.getSelectedItem().toString(),
                                store_house.getSelectedItem().toString(),start_time.getText().toString(),
                                end_time.getText().toString());
                        System.out.println("Door 中查询到的"+str);
                        if(!str.equals("0")){
                            break;
                        }
                    }
                }catch(Exception E){
                    System.out.print(E);
                }
                return str;
            }

            protected void onPostExecute(String str) {
                dialog.cancel();
                Serializer serializer = new Persister();
                try {
                    xd=serializer.read(XmlDoorMessage.class,str,false);
                } catch (Exception e) {
                    System.out.println("xml解析异常:"+e);
                }
                refreshItemMessage();
            }
        }.execute();
    }

    //lsitview
    public static class CellAdapterForListviewThreeD extends BaseAdapter {

        LayoutInflater inflater;

        public CellAdapterForListviewThreeD(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list_door.size();
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
                holder1.item_lotno_listview= (TextView) view.findViewById(R.id.all_orderno_storehouse_in);
                holder1.order_num_listview= (TextView) view.findViewById(R.id.all_num_storehouse_in);
                holder1.up_order_listview= (TextView) view.findViewById(R.id.traciing_storehouse_in);
                holder1.time_listview= (TextView) view.findViewById(R.id.out_store_storehouse_in);

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
            System.out.println("=9098989898989"+list_door.size()+":::::"+list_door.get(position).getItem_name_door());
            if(list_door.get(position).getItem_name_door()!=null) {
                holder.item_name_listview.setText(list_door.get(position).getItem_name_door());
                holder.item_lotno_listview.setText(list_door.get(position).getLot_no_door());
                holder.order_num_listview.setText(list_door.get(position).getOrder_no_door());
                holder.up_order_listview.setText(list_door.get(position).getUp_order_door());
                holder.time_listview.setText(list_door.get(position).getDate_door());
            }
            return view;
        }
        public class ViewHolder {
            TextView item_name_listview;
            TextView item_lotno_listview;
            TextView order_num_listview;
            TextView up_order_listview;
            TextView time_listview;
        }
    }

    private void refreshItemMessageItemChoose(){
        list_door.clear();
        int door_siez=xd.getOrderrecord().size();
        double all_store_num=0;
        double all_traciing_num=0;
        for(int m=0;m<SystemUtil.itemList.size();m++) {
            if(SystemUtil.itemList.get(m)!=null){
                System.out.println("显示的名字"+SystemUtil.itemList.get(m));
            for (int i = 0; i < door_siez; i++) {
                if (xd.getOrderrecord().get(i).getOrderinfo() != null) {
                    int itemqty_size = xd.getOrderrecord().get(i).getOrderinfo().size();
                    for (int k = 0; k < itemqty_size; k++) {
                        if (xd.getOrderrecord().get(i).getOrderinfo().get(k).getItemname() != null) {
                            if (SystemUtil.itemList.get(m).toString().equals(xd.getOrderrecord().get(i).getOrderinfo().get(k).getItemname().toString())) {
                                DoorMessage dm = new DoorMessage();
                                if (xd.getOrderrecord().get(i).getOrderinfo().get(k).getOrderqty() != null)
                                    all_store_num += Double.parseDouble(xd.getOrderrecord().get(i).getOrderinfo().get(k).getOrderqty().toString());
                                if (xd.getOrderrecord().get(i).getOrderinfo().get(k).getActualqty() != null)
                                    all_traciing_num += Double.parseDouble(xd.getOrderrecord().get(i).getOrderinfo().get(k).getActualqty().toString());
                                dm.setItem_name_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getItemname());
                                dm.setLot_no_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getLotno());
                                dm.setOrder_no_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getOrderqty());
                                dm.setUp_order_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getActualqty());
                                dm.setDate_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getTime());
                                list_door.add(dm);
                            }
                        }
                    }
                }}
            }
        }

        DoorMessage dm1 = new DoorMessage();
        dm1.setItem_name_door("合计");
        dm1.setLot_no_door("");
        dm1.setOrder_no_door(String.valueOf(all_store_num));
        dm1.setUp_order_door(String.valueOf(all_traciing_num));
        list_door.add(dm1);

        adapter_door_listview = new CellAdapterForListviewThreeD(DoorMessageActivity.this);
        door_message_list.setAdapter(adapter_door_listview);
    }

    private void refreshItemMessage(){
        SearchMapApplication.item_name_list_door=new ArrayList<String>();
        list_door.clear();
        int door_siez=xd.getOrderrecord().size();
        double all_store_num=0;
        double all_traciing_num=0;
        for(int i=0;i<door_siez;i++){
            if(xd.getOrderrecord().get(i).getOrderinfo()!=null) {
                int itemqty_size = xd.getOrderrecord().get(i).getOrderinfo().size();
                for (int k = 0; k < itemqty_size; k++) {
                    DoorMessage dm = new DoorMessage();
                    if(xd.getOrderrecord().get(i).getOrderinfo().get(k).getOrderqty()!=null)
                        all_store_num+=Double.parseDouble(xd.getOrderrecord().get(i).getOrderinfo().get(k).getOrderqty().toString());
                    if(xd.getOrderrecord().get(i).getOrderinfo().get(k).getActualqty()!=null)
                        all_traciing_num+=Double.parseDouble(xd.getOrderrecord().get(i).getOrderinfo().get(k).getActualqty().toString());
                    dm.setItem_name_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getItemname());
                    dm.setLot_no_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getLotno());
                    dm.setOrder_no_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getOrderqty());
                    dm.setUp_order_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getActualqty());
                    dm.setDate_door(xd.getOrderrecord().get(i).getOrderinfo().get(k).getTime());
                    if(xd.getOrderrecord().get(i).getOrderinfo().get(k).getItemname()!=null)
                        if(!SearchMapApplication.item_name_list_door.contains(xd.getOrderrecord().get(i).getOrderinfo().get(k).getItemname().toString()))
                            SearchMapApplication.item_name_list_door.add(xd.getOrderrecord().get(i).getOrderinfo().get(k).getItemname().toString());
                    list_door.add(dm);
                }
            }
        }
        DoorMessage dm1 = new DoorMessage();
        dm1.setItem_name_door("合计");
        dm1.setLot_no_door("");
        dm1.setOrder_no_door(String.valueOf(all_store_num));
        dm1.setUp_order_door(String.valueOf(all_traciing_num));
        list_door.add(dm1);

        adapter_door_listview = new CellAdapterForListviewThreeD(DoorMessageActivity.this);
        door_message_list.setAdapter(adapter_door_listview);
    }
}
