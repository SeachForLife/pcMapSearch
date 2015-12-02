package com.traciing.baidu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.traciing.baidu.R;
import com.traciing.common.HttpService;
import com.traciing.dialog.MyDialog;
import com.traciing.domain.AllcarMessage;
import com.traciing.domain.storehouseMessage;
import com.traciing.xml.XmlAllMessage;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllcarMessageActivity extends Activity {

    private ListView allcar_message_list;

    private static CellAdapterForListviewThreeD adapter_listview=null;
    private static View view=null;
    private static List<AllcarMessage> list_car=null;
    static MyDialog dialog=null;
    private String xml_data;

    XmlAllMessage xam=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allcar_message);

        initUploadingDialog();

        xml_data=getIntent().getStringExtra("xmldata");
        Serializer serializer = new Persister();
        try {
            xam=serializer.read(XmlAllMessage.class,xml_data,false);
        } catch (Exception e) {
            System.out.println("xml解析异常:"+e);
        }
        allcar_message_list= (ListView) findViewById(R.id.all_car_message);
        list_car=new ArrayList<AllcarMessage>();
        if(xam.getCardata()!=null) {
            System.out.println("====" + xam.getCardata().size());
            for (int i = 0; i < xam.getCardata().size(); i++) {
                AllcarMessage am = new AllcarMessage();
                am.setCar_name(xam.getCardata().get(i).getCarnumber());
                am.setCutomername(xam.getCardata().get(i).getCustomername());
                System.out.println("获取到的车辆所属为"+xam.getCardata().get(i).getCustomername());
                am.setCar_state(xam.getCardata().get(i).getStatu());
                for (int k = 0; k < xam.getCardata().get(i).getTem().size(); k++) {
                    if(xam.getCardata().get(i).getTem().get(k).getRelationtem()!=null) {
                        for (int m = 0; m < xam.getCardata().get(i).getTem().get(k).getRelationtem().size(); m++) {
                            System.out.println("0------0" + m);
                            if (xam.getCardata().get(i).getTem().get(k).getRelationtem().get(m).getPassid().toString().equals("厢前")) {
                                am.setStart_tem(xam.getCardata().get(i).getTem().get(k).getRelationtem().get(m).getPassvalue().toString());
                            } else {
                                am.setFinish_tem(xam.getCardata().get(i).getTem().get(k).getRelationtem().get(m).getPassvalue().toString());
                            }
                        }
                    }
                }
                list_car.add(am);
            }
        }

        adapter_listview = new CellAdapterForListviewThreeD(this);
        allcar_message_list.setAdapter(adapter_listview);
    }


    //lsitview
    public class CellAdapterForListviewThreeD extends BaseAdapter {

        LayoutInflater inflater;

        public CellAdapterForListviewThreeD(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list_car.size();
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder1=null;
            if(convertView==null) {
                holder1 = new ViewHolder();
                view = inflater.inflate(R.layout.car_message_listview, null);
                holder1.car_num_listview= (TextView) view.findViewById(R.id.item_name_storechoose_in);
                holder1.car_num_listview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
                holder1.car_customername_listview= (TextView) view.findViewById(R.id.car_customername);
                holder1.car_state_listview= (TextView) view.findViewById(R.id.item_num_storehouse_in);
                holder1.car_front_listview= (TextView) view.findViewById(R.id.all_num_storehouse_in);
                holder1.car_behind_listview= (TextView) view.findViewById(R.id.traciing_storehouse_in);
                holder1.recent_listview= (TextView) view.findViewById(R.id.out_store_storehouse_in);
                holder1.recent_listview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线

                view.setTag(holder1);
            }else{
                view=convertView;
                holder1= (ViewHolder) view.getTag();
            }
            final ViewHolder holder=holder1;
            holder.car_num_listview.setText(list_car.get(position).getCar_name().toString());
            holder.car_customername_listview.setText(list_car.get(position).getCutomername().toString());
            if(list_car.get(position).getCar_state()!=null)
                holder.car_state_listview.setText(list_car.get(position).getCar_state().toString());
            if(list_car.get(position).getStart_tem()!=null) {
                holder.car_front_listview.setText(list_car.get(position).getStart_tem().toString());
            }
            if(list_car.get(position).getFinish_tem()!=null)
                holder.car_behind_listview.setText(list_car.get(position).getFinish_tem().toString());
            holder.recent_listview.setText("轨迹");

            holder.car_num_listview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("查看详细信息");
                    dialog.show();
                    dialog.changeInfo("查询中...");
                    //拿取到当天的温度数据
                    new AsyncTask<Void, Void, String>() {

                        @Override
                        protected String doInBackground(Void... voids) {
                            String er = null;
                            try {
                                String str = holder.car_num_listview.getText().toString();
                                str=str.substring(1,str.length());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date_no = new Date(System.currentTimeMillis());
                                // 网络不好，重试3次
                                for (int i = 0; i < 3; i++) {
                                    er = HttpService.getCarMessage(str, sdf.format(date_no));
                                    if (!er.equals("0")) {
                                        break;
                                    }
                                }
                                System.out.println("获取到当日xml为:" + er);
                            } catch (Exception E) {
                                System.out.print(E);
                            }
                            return er;
                        }

                        protected void onPostExecute(String er) {
                            dialog.cancel();
                            System.out.println("到主线程中的" + er);
                            if (!er.equals("0")) {
                                Intent intent = new Intent(AllcarMessageActivity.this,
                                        chartActivity.class);
                                intent.putExtra("car_num", (holder.car_num_listview.getText().toString()).substring(1, holder.car_num_listview.getText().length()));
                                intent.putExtra("carmessage", er);
                                startActivity(intent);
                            }
                        }
                    }.execute();
                }
            });

            holder.recent_listview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("查看轨迹信息");
                    dialog.show();
                    dialog.changeInfo("查询中...");
                    //拿取到当天的温度数据
                    new AsyncTask<Void, Void, String>() {

                        @Override
                        protected String doInBackground(Void... voids) {
                            String er = null;
                            try {
                                String str = holder.car_num_listview.getText().toString();
                                str=str.substring(1,str.length());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date_no = new Date(System.currentTimeMillis());
                                // 网络不好，重试3次
                                for (int i = 0; i < 3; i++) {
                                    System.out.println("轨迹传输的车辆信息为"+str);
//                                    er = HttpService.getCarRecent(str, "2015-11-19");
                                    er = HttpService.getCarRecent(str, sdf.format(date_no));
                                    if (!er.equals("0")) {
                                        break;
                                    }
                                }
                                System.out.println("车辆轨迹GPS信息：" + er);
                            } catch (Exception E) {
                                System.out.print(E);
                            }
                            return er;
                        }

                        protected void onPostExecute(String er) {
                            dialog.cancel();
                            System.out.println("到主线程中的" + er);
                            if (!er.equals("0")) {
                                Intent intent = new Intent(AllcarMessageActivity.this,
                                        CarRecentActivity.class);
                                intent.putExtra("xml_data",er);
                                startActivity(intent);
                            }
                        }
                    }.execute();

                }
            });
            return view;
        }
        public class ViewHolder {
            TextView car_num_listview;
            TextView car_customername_listview;
            TextView car_state_listview;
            TextView car_front_listview;
            TextView car_behind_listview;
            TextView recent_listview;
        }
    }

    private void initUploadingDialog() {
        dialog = new MyDialog(AllcarMessageActivity.this, R.style.MyDialog);
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
