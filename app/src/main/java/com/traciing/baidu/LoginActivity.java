package com.traciing.baidu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.traciing.baidu.R;
import com.traciing.common.HttpService;
import com.traciing.dialog.MyDialog;
import com.traciing.server.UpdateService;
import com.traciing.utils.SystemUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends Activity {

    private TextView user_name;
    private TextView password;
    private Button login_in;
    MyDialog dialog;

    public static  Activity instance=null;

    private int DOWN_ERROR=0;//用此来传递下载APK消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instance=this;
        initUploadingDialog();
        user_name= (TextView) findViewById(R.id.user_edit);
        password= (TextView) findViewById(R.id.password_edit);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i==EditorInfo.IME_ACTION_GO){
                    if(user_name.getText().toString().equals("")||user_name.getText().toString()==null){
                        SystemUtil.showConfirm(LoginActivity.this, R.string.user_name_null, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                return;
                            }
                        });
                    }else if(password.getText().toString().equals("")||password.getText().toString()==null){
                        SystemUtil.showConfirm(LoginActivity.this, R.string.pass_word_null, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                return;
                            }
                        });
                    }else {
                        checkuser(user_name.getText().toString(), password.getText().toString());
                    }
                }
                return false;
            }
        });

        login_in= (Button) findViewById(R.id.enter);
        login_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_name.getText().toString().equals("")||user_name.getText().toString()==null){
                    SystemUtil.showConfirm(LoginActivity.this, R.string.user_name_null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                    return;
                }
                if(password.getText().toString().equals("")||password.getText().toString()==null){
                    SystemUtil.showConfirm(LoginActivity.this, R.string.pass_word_null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                    return;
                }
                checkuser(user_name.getText().toString(),password.getText().toString());
            }
        });
        checkVersion();
    }

    //检测user，并下载当前用户的信息情况
    private void checkuser(final String un,String pw){
        dialog.show();
        dialog.changeInfo("登录中...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String str = null;
                try {
                    // 网络不好，重试3次
                    System.out.println("传输的值为:"+user_name.getText().toString()+":::::"+password.getText().toString());
                    for (int i = 0; i < 3; i++) {
                        str = HttpService.checkuser(user_name.getText().toString(),password.getText().toString());
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
                System.out.println("到主线程中的" + str);
                if(str.equals("-1")){
                    SystemUtil.showConfirm(LoginActivity.this, R.string.check_user_error, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
                    return;
                }
                if(!str.equals("0")){
                    Intent intent = new Intent(LoginActivity.this,
                            MapActivity.class);
                    intent.putExtra("xmlstr",str);
                    startActivity(intent);
                }
            }
        }.execute();
    }

    //检测是否需要更新
    private void checkVersion() {
        dialog.show();
        dialog.changeInfo("正在检查配置...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String url = null;
                try {
                    // 网络不好，重试3次
                    for (int i = 0; i < 3; i++) {
                        System.out.println("====="+SystemUtil.getVersionName());
                        url = HttpService.checkVersion(SystemUtil.getVersionName());
                        if(url!=null) {
                                break;
                        }
                    }
                }catch(Exception E){
                    System.out.print(E);
                }
                return url;
            }

            protected void onPostExecute(String url) {
                dialog.cancel();
                System.out.println("到主线程中的" + url);
                final String url1 = url;
                if (url != null) {
                    if (url.equals("0")) {
                    } else if (isEmpty(url) && !url.startsWith("http://")) {
                        SystemUtil.showConfirm(LoginActivity.this, R.string.alert_server_error, clickToQuitListener);
                    } else {
                        SystemUtil.showConfirm(LoginActivity.this, R.string.alert_upgrade, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                downLoadApk(url1);
//                                Intent serviceIntent = new Intent(
//                                        LoginActivity.this,
//                                        UpdateService.class);
//                                serviceIntent.putExtra("url",
//                                        url1);
//                                startService(serviceIntent);
                            }
                        }, 3);
                    }
                }else{
                    SystemUtil.showConfirm(LoginActivity.this, R.string.alert_intent_error, clickToQuitListener);
                }
            }
        }.execute();
    }

    private View.OnClickListener clickToQuitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginActivity.this.finish();
        }
    };

    public static boolean isEmpty(String url) {
        return url == null || url.length() == 0;
    }

    private void initUploadingDialog() {
        dialog = new MyDialog(LoginActivity.this, R.style.MyDialog);
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

    /*
    * 下载APK
     */
    protected void downLoadApk(final String url){
        System.out.println("下载的URL为"+url);
        final ProgressDialog pd;
        pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread(){
            @Override
            public void run(){
                try {
                    File file=getFileFromServer(url,pd);
                    sleep(3000);
                    intallAPK(file);
                    pd.dismiss();
                } catch (Exception e) {
                    Message msg=new Message();
                    msg.what=DOWN_ERROR;
                    handler.handleMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /*
    安装APK
     */
    protected void intallAPK(File file){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);//执行动作
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");//执行的数据类型
        startActivity(intent);
    }

    public static File getFileFromServer(String path,ProgressDialog pd) throws Exception {
        //判断当前手机的sdcard是否挂载在手机上
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url=new URL(path);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取文件大小
            pd.setMax(conn.getContentLength());
            InputStream is=conn.getInputStream();
            File file=new File(Environment.getExternalStorageDirectory(),path.substring(path.lastIndexOf("/") + 1));
//            if (file.exists())
//                file.delete();
//                file.createNewFile();

            FileOutputStream fos=new FileOutputStream(file);
            BufferedInputStream bis=new BufferedInputStream(is);
            byte[] buffer=new byte[1024];
            int len;
            int total=0;
            while((len=bis.read(buffer))!=-1){
                fos.write(buffer,0,len);
                total+=len;
                //显示当前的下载量
                pd.setProgress(total);
            }
            System.out.println("下载完成");
            fos.close();
            bis.close();
            is.close();
            return file;
        }else{
            return null;
        }
    }

    /*
    线程总定义
     */
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //下载失败
                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
