package com.traciing.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.traciing.baidu.R;

public class MyDialog extends Dialog {

    Context context;
    TextView text;
    String info = "连接设备....";

    public MyDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        this.setOnKeyListener(new DialogInterface.OnKeyListener() {
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

    public void setInfo(String info_tem) {

        this.info = info_tem;
    }

    public void changeInfo(String info_tem) {
        if (text == null) {
            setInfo(info_tem);
            return;
        }
        text.setText(info_tem);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mydialog);
        text = (TextView) findViewById(R.id.info);
        text.setText(info);
    }

}
