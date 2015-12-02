package com.traciing.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpService {

    public static final int TIMEOUT_TO_OPEN_CONNECT = 5 * 1000;
    public static final int TIMEOUT_SOCKET = 10 * 1000;

    private static String url = "http://211.155.86.70:8080/Milk/AppServlet";

    private static HttpClient buildHttpClient() {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_TO_OPEN_CONNECT);
        client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SOCKET);
        return client;
    }

    public static InputStream getCarDate(String drivernum,String date){
        try {
            HttpClient client = buildHttpClient();
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            parameters.add(new BasicNameValuePair("method", "getCarMessage"));
            parameters.add(new BasicNameValuePair("drivernum", drivernum));
            parameters.add(new BasicNameValuePair("date", date));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
                    "utf-8");
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                return is;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    //获取当天当前车辆的信息
    public static String getCarMessage(String car_name,String date){
        InputStream in = null;
        try {
            in=getCarDate(car_name,date);
            if(in==null){
                return "0";
            }
            String content= null;
            content = new String(StreamTools.getBytes(in));
            System.out.println("获取当天的此车的数据值为:"+content);
            return content;
        } catch (Exception e) {
            return "0";
        }
    }

    public static String checkVersion(String version) {
        InputStream in = null;
        try {
            in = getNewApk(version);
            System.out.println("0-----------"+in);
            if (in == null) {
                System.out.println("read false");
                return null;
            }
            String content = new String(StreamTools.getBytes(in));
            System.out.println("check version "+content);
            return content;
        } catch (Exception e) {
            return null;
        }
    }

    private static InputStream getNewApk(String version)
    {
        // 1.创建一个浏览器
        try {
            HttpClient client = buildHttpClient();

            // 2.构建一个post的请求
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            parameters.add(new BasicNameValuePair("method", "checkVersionSearch"));
            parameters.add(new BasicNameValuePair("version", version));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
                    "utf-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                // byte[] result = StreamTools.getBytes(is);
                // return new String(result);
                return is;
            }
        }catch(Exception e){
            System.out.println("error"+e);
        }
        return null;
    }

    public static String checkuser(String username,String password){
        InputStream in = null;
        try {
            in=getuser(username,password);
            System.out.println("rrrrrr:"+in);
            if(in==null){
                return "0";
            }
            String content= null;
            content = new String(StreamTools.getBytes(in));
            System.out.println("获取的数据值为:"+content);
            return content;
        } catch (Exception e) {
            return "0";
        }
    }

    private static InputStream getuser(String username,String password){
        try {
            System.out.println("发送的data为:"+username+":::"+password);
            HttpClient client = buildHttpClient();
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            parameters.add(new BasicNameValuePair("method", "getUserMessage"));
            parameters.add(new BasicNameValuePair("username", username));
            parameters.add(new BasicNameValuePair("password", password));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
                    "utf-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                return is;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    //仓库信息查询
    public static String getStoreMessage(String storehouse_name,String date){
        InputStream in = null;
        try {
            System.out.println("=++++++"+storehouse_name);
            in=getStoreData(storehouse_name,date);
            if(in==null){
                return "0";
            }
            String content= null;
            content = new String(StreamTools.getBytes(in));
            System.out.println("获取的数据值为:"+content);
            return content;
        } catch (Exception e) {
            return "0";
        }
    }

    private static InputStream getStoreData(String storehousename,String date){
        try {
            HttpClient client = buildHttpClient();
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            parameters.add(new BasicNameValuePair("method", "getStoreMessage"));
            parameters.add(new BasicNameValuePair("warehousename", storehousename));
            parameters.add(new BasicNameValuePair("date", date));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
                    "utf-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                return is;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    //轨迹查询
    public static String getCarRecent(String car_name, String date){
        InputStream in = null;
        try {
            in=getCarRecentData(car_name,date);
            if(in==null){
                return "0";
            }
            String content= null;
            content = new String(StreamTools.getBytes(in));
            System.out.println("获取的数据值为:"+content);
            return content;
        } catch (Exception e) {
            return "0";
        }
    }

    private static InputStream getCarRecentData(String carname,String date){
        try {
            HttpClient client = buildHttpClient();
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            parameters.add(new BasicNameValuePair("method", "getCarRecentMessage"));
            parameters.add(new BasicNameValuePair("carnumber", carname));
            parameters.add(new BasicNameValuePair("date", date));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
                    "utf-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                return is;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    //门店信息查询
    public static String getDoorMessage(String warehousename, String doorname,String begintime,String endtime){
        InputStream in = null;
        try {
            in=getDoorData(warehousename,doorname,begintime,endtime);
            if(in==null){
                return "0";
            }
            String content= null;
            content = new String(StreamTools.getBytes(in));
            System.out.println("获取的数据值为:"+content);
            return content;
        } catch (Exception e) {
            return "0";
        }
    }

    private static InputStream getDoorData(String warehousename,String doorname,String begintime,String endtime){
        try {
            HttpClient client = buildHttpClient();
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            parameters.add(new BasicNameValuePair("method", "getHistoryOrderRecord"));
            parameters.add(new BasicNameValuePair("warehousename", warehousename));
            parameters.add(new BasicNameValuePair("doorname", doorname));
            parameters.add(new BasicNameValuePair("begintime", begintime));
            parameters.add(new BasicNameValuePair("endtime", endtime));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
                    "utf-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                return is;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
}
