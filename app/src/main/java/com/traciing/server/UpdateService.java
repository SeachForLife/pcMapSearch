package com.traciing.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.traciing.baidu.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateService extends Service {

    private NotificationManager nm;
    private Notification notification;
    private File tempFile = null;
    private boolean cancelUpdate = false;
//    private MyHandler myHandler;
    private int download_precent = 0;
    private RemoteViews views;
    private int notificationId = 1234;

    private int DOWN_ERROR=0;//用此来传递下载APK消息

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        System.out.println("进入下载更新页面");
        downLoadApk(intent.getStringExtra("url"));
//        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notification = new Notification();
//        notification.icon = android.R.drawable.stat_sys_download;
//        // notification.icon=android.R.drawable.stat_sys_download_done;
//        notification.tickerText = "更新";
//        notification.when = System.currentTimeMillis();
//        notification.defaults = Notification.DEFAULT_LIGHTS;
//
//        // 设置任务栏中下载进程显示的views
//        views = new RemoteViews(getPackageName(), R.layout.update);
//        notification.contentView = views;
//
//        // 将下载任务添加到任务栏中
//        nm.notify(notificationId, notification);
//
//        myHandler = new MyHandler(Looper.myLooper(), this);
//
//        // 初始化下载任务内容views
//        Message message = myHandler.obtainMessage(3, 0);
//        myHandler.sendMessage(message);
//
//        // 启动线程执行下载任务
//        downFile(intent.getStringExtra("url"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    // 下载更新文件
//    private void downFile(final String url) {
//        new Thread() {
//            public void run() {
//                try {
//                    HttpClient client = new DefaultHttpClient();
//                    // params[0]代表连接的url
//                    HttpGet get = new HttpGet(url);
//                    HttpResponse response = client.execute(get);
//                    HttpEntity entity = response.getEntity();
//                    long length = entity.getContentLength();
//                    InputStream is = entity.getContent();
//                    if (is != null) {
//                        File rootFile = new File(
//                                "/data",
//                                "/etc");
//                        if (!rootFile.exists() && !rootFile.isDirectory())
//                            rootFile.mkdir();
//
//                        tempFile = new File(
//                                "/data",
//                                "/etc/"
//                                        + url.substring(url.lastIndexOf("/") + 1)
//                        );
//                        System.out.println("-----------------------------+++++++++++++++++++++++++++++++++++++++++++++++"+tempFile);
//                        if (tempFile.exists())
//                            tempFile.delete();
//                        tempFile.createNewFile();
//
//                        BufferedInputStream bis = new BufferedInputStream(is);
//
//                        FileOutputStream fos = new FileOutputStream(tempFile);
//                        BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//                        int read;
//                        long count = 0;
//                        int precent = 0;
//                        byte[] buffer = new byte[1024];
//                        while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
//                            bos.write(buffer, 0, read);
//                            count += read;
//                            precent = (int) (((double) count / length) * 100);
//
//                            if (precent - download_precent >= 5) {
//                                download_precent = precent;
//                                Message message = myHandler.obtainMessage(3,
//                                        precent);
//                                myHandler.sendMessage(message);
//                            }
//                        }
//                        bos.flush();
//                        bos.close();
//                        fos.flush();
//                        fos.close();
//                        is.close();
//                        bis.close();
//                    }
//
//                    if (!cancelUpdate) {
//                        Message message = myHandler.obtainMessage(2, tempFile);
//                        myHandler.sendMessage(message);
//                    } else {
//                        tempFile.delete();
//                    }
//                } catch (Exception e) {
//                    Message message = myHandler.obtainMessage(4, "下载更新文件失败");
//                    myHandler.sendMessage(message);
//                    System.out.println("下载更新失败."+e);
//                }
//            }
//        }.start();
//    }

//    // 安装下载后的apk文件
//    private void Instanll(File file,Context context) {
//        String aa=tempFile.toString();
//        System.out.println("0222"+aa);
//        try {
//            Runtime.getRuntime().exec("chmod 644 " + aa);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file),
//                "application/vnd.android.package-archive");
//        context.startActivity(intent);
//    }

//    class MyHandler extends Handler {
//        private Context context;
//
//        public MyHandler(Looper looper, Context c) {
//            super(looper);
//            this.context = c;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg != null) {
//                switch (msg.what) {
//                    case 0:
//                        Toast.makeText(context, msg.obj.toString(),
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case 1:
//                        break;
//                    case 2:
//                        download_precent = 0;
//                        nm.cancel(notificationId);
//                        Instanll((File) msg.obj, context);
//
//                        stopSelf();
//                        break;
//                    case 3:
//                        views.setTextViewText(R.id.tvProcess, "已下载"
//                                + download_precent + "%");
//                        views.setProgressBar(R.id.pbDownload, 100,
//                                download_precent, false);
//                        notification.contentView = views;
//                        nm.notify(notificationId, notification);
//                        break;
//                    case 4:
//                        nm.cancel(notificationId);
//                        break;
//                }
//            }
//        }
//    }

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
                    Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
