package com.traciing;

import android.app.Application;
import com.traciing.utils.SystemUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/5/7.
 */
public class SearchMapApplication extends Application {

    public static List<String> item_name_list_door=null;

    private static SearchMapApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        SystemUtil.init(this);
        mInstance = this;
        initHook();
    }

    private void initHook() {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                handleException();
            }

            public void handleException(){
                System.out.println("程序奔溃了");
            }
        });
    }
}
