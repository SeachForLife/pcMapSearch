package com.traciing.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.traciing.SearchMapApplication;
import com.traciing.baidu.R;
import com.traciing.domain.DoorMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/5/7.
 */
public class SystemUtil {

    private static Application CONTENT = null;

    /**
     * @param context
     */
    public static void init(Application context) {
        if (context != null && CONTENT == null)
            CONTENT = context;
    }

    public static String getVersionName() {

        PackageManager packageManager = CONTENT.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(CONTENT.getPackageName(), 0);
            System.out.println("----------------------------------------------------------------"+packInfo.versionName);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException swallow) {
            return "";
        }
    }

    public static void showConfirm(Activity activity, int msgResouce, View.OnClickListener listener, int secondsToAutoConfirm) {
        showDialog(activity, R.string.custom_dialog_title, msgResouce, R.string.custom_dialog_btn_ok, listener, R.string.custom_dialog_btn_cancel, null, false, secondsToAutoConfirm);
    }

    public static void showConfirm(Activity activity, int msgResouce, View.OnClickListener listener) {
        showDialog(activity, R.string.custom_dialog_title, msgResouce, R.string.custom_dialog_btn_ok, listener, R.string.custom_dialog_btn_cancel, null, false, -1);
    }

    public static void showPartConfirm(Activity activity, int titleResource, int msgResouce, View.OnClickListener listener, View.OnClickListener cancelListener) {
        showDialog_liebiao(activity, titleResource, msgResouce, R.string.custom_dialog_btn_ok, listener, R.string.custom_dialog_btn_cancel, cancelListener, false, -1);
    }

    public static void showDialog(Activity activity, int titleResource, int msgResouce, int okBtnResource, final View.OnClickListener okListener, int cancelBtnResource, final View.OnClickListener cancelListener, boolean cancelable, final int secondsToAutoConfirm) {

        final Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(cancelable);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return false;
            }
        });
        TextView msgTv = (TextView) dialog.findViewById(R.id.custom_dialog_msg);
        msgTv.setText(msgResouce);
        TextView titleTv = (TextView) dialog.findViewById(R.id.custom_dialog_title);
        titleTv.setText(titleResource);
        final Button okButton = (Button) dialog.findViewById(R.id.custom_dialog_btn_ok);
        okButton.setText(okBtnResource);
        final Runnable runnable = new Runnable() {
            int count = secondsToAutoConfirm;

            @Override
            public void run() {
                if (count < 1) {
                    okButton.performClick();
                    return;
                }
                if (okButton.isShown()) {
                    String ok = "确定";
                    okButton.setText(ok + " (" + count-- + ")");
                    okButton.postDelayed(this, 1000);
                }
            }
        };
        if (secondsToAutoConfirm > 0) {
            okButton.post(runnable);
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (okListener != null) {
                    okListener.onClick(view);
                }
                okButton.removeCallbacks(runnable);
            }
        });
        if (cancelListener != null) {
            Button cancelButton = (Button) dialog.findViewById(R.id.custom_dialog_btn_cancel);
            cancelButton.setText(cancelBtnResource);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    cancelListener.onClick(view);
                }
            });
            cancelButton.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }

    public static EditText et;
    private static ListView listview;
    private static CellAdapterForListviewThreeD adapter=null;
    private static View view=null;

    private static List<Map<String, String>> listData ;
    private static Map<String, String> mapData = null;
    public static List<String> itemList=new ArrayList<String>();
    public static List<CellAdapterForListviewThreeD.ViewHolder> holder_list=new ArrayList<CellAdapterForListviewThreeD.ViewHolder>();

    public static void showDialog_liebiao(Activity activity, int titleResource, int msgResouce, int okBtnResource, final View.OnClickListener okListener, int cancelBtnResource, final View.OnClickListener cancelListener, boolean cancelable, final int secondsToAutoConfirm) {

        final Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_itemname);
        dialog.setCancelable(cancelable);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return false;
            }
        });

        int item_siez = SearchMapApplication.item_name_list_door.size();
        for (int i = 0; i < item_siez; i++) {
            System.out.println("商品信息登记:" + SearchMapApplication.item_name_list_door.get(i).toString());
        }
        itemList.clear();
        for (int i = 0; i < SearchMapApplication.item_name_list_door.size(); i++) {
            itemList.add(i, null);//先赋予都为null，防止下标越界
        }

        listview = (ListView) dialog.findViewById(R.id.Message_list);
        setAdapterData();
        adapter = new CellAdapterForListviewThreeD(dialog.getContext());
        listview.setAdapter(adapter);

        if (item_siez == 2){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 200);
            View view = dialog.findViewById(R.id.layout_item_name);
            view.setLayoutParams(params);
        }else if(item_siez>2){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 300);
            View view = dialog.findViewById(R.id.layout_item_name);
            view.setLayoutParams(params);
        }

        TextView titleTv = (TextView) dialog.findViewById(R.id.custom_dialog_title);
        titleTv.setText(titleResource);
        final Button okButton = (Button) dialog.findViewById(R.id.custom_dialog_btn_ok);
        okButton.setText(okBtnResource);
        final Runnable runnable = new Runnable() {
            int count = secondsToAutoConfirm;

            @Override
            public void run() {
                if (count < 1) {
                    okButton.performClick();
                    return;
                }
                if (okButton.isShown()) {
                    String ok = "确定";
                    okButton.setText(ok + " (" + count-- + ")");
                    okButton.postDelayed(this, 1000);
                }
            }
        };
        if (secondsToAutoConfirm > 0) {
            okButton.post(runnable);
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (okListener != null) {
                    okListener.onClick(view);
                }
                okButton.removeCallbacks(runnable);
            }
        });
        if (cancelListener != null) {
            Button cancelButton = (Button) dialog.findViewById(R.id.custom_dialog_btn_cancel);
            cancelButton.setText(cancelBtnResource);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    cancelListener.onClick(view);
                }
            });
            cancelButton.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }
    private static void setAdapterData() {

        listData = new ArrayList<Map<String, String>>();

        for (int i = 0; i < SearchMapApplication.item_name_list_door.size(); i++) {
            mapData = new HashMap<String, String>();
            mapData.put("item_name", SearchMapApplication.item_name_list_door.get(i));
            listData.add(mapData);
        }
    }

    public static class CellAdapterForListviewThreeD extends BaseAdapter {

        LayoutInflater inflater;

        public CellAdapterForListviewThreeD(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return SearchMapApplication.item_name_list_door.size();
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
                view = inflater.inflate(R.layout.listview_checkbox, null);
                holder1.item_name = (CheckBox) view.findViewById(R.id.check_door);

                holder1.item_name.setTag(position);
                view.setTag(holder1);
            }else{
                view=convertView;
                holder1= (ViewHolder) view.getTag();
            }
            final ViewHolder holder=holder1;
            String item_name_value=listData.get(position).get("item_name").toString();
            holder.item_name.setText(item_name_value);
            System.out.println("------"+listData.get(position).get("item_name"));
            holder.item_name.setClickable(true);
            holder.item_name
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {

                            int pos = Integer.valueOf(buttonView.getTag()
                                    .toString());
                            System.out.println("pos:" + pos);
                            String item_name_current=listData.get(pos).get("item_name");
                            if (isChecked) {
                                holder_list.add(holder);
                                itemList.remove(pos);
                                itemList.add(pos,item_name_current);

                            } else {
                                holder_list.remove(holder);
                                if (itemList.size() > 0) {
                                    itemList.remove(pos);
                                    itemList.add(pos,null);
                                }
                            }
                        }
                    });
            return view;
        }
        public class ViewHolder {
            public CheckBox item_name;
        }
    }
}
