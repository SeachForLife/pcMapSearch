package com.traciing.baidu;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.traciing.common.HttpService;
import com.traciing.dialog.MyDialog;
import com.traciing.xml.XmlCarMessage;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class chartActivity extends Activity implements View.OnClickListener {

    private AbstractDemoChart mChart;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView mChartView;
    private LinearLayout layout;

    private EditText date_choose;
    private ImageView refu;
    private TextView car_message;
    private String initEndDateTime = null;
    private XYMultipleSeriesRenderer renderer;

    List<Date[]> x = new ArrayList<Date[]>();
    List<double[]> values = new ArrayList<double[]>();
    String[] titles =null;
    private String car_message_current;
    private String car_num;
    XmlCarMessage xmlcar=null;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    MyDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        refu= (ImageView) findViewById(R.id.refu);
        refu.setOnClickListener(this);

        car_message= (TextView) findViewById(R.id.car_message);
        car_message.setMovementMethod(ScrollingMovementMethod.getInstance());

        car_message_current=getIntent().getStringExtra("carmessage");
        car_num=getIntent().getStringExtra("car_num");
        System.out.println("传进来的xml为:"+car_message_current);
        Serializer serializer = new Persister();
        try {
            xmlcar=serializer.read(XmlCarMessage.class,car_message_current,false);
        } catch (Exception e) {
            System.out.println("xml解析异常:"+e);
        }
        System.out.println("xml jilru ");
        titles=new String[]{"厢前","厢后"};
        date_choose= (EditText) findViewById(R.id.date);
        date_choose.setInputType(InputType.TYPE_NULL);
        date_choose.clearFocus();
        date_choose.setCursorVisible(false);
        Date date_no=new Date(System.currentTimeMillis());
        initEndDateTime=sdf.format(date_no);
        System.out.println("8888888888888888888"+initEndDateTime);
        initUploadingDialog();
        date_choose.setText(initEndDateTime);
        date_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        chartActivity.this, initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(date_choose,refu);
            }
        });
        refreshDateTable(date_choose.getText().toString());
    }

    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        renderer.setAxisTitleTextSize(16);//XY name 大小
        renderer.setChartTitleTextSize(16);
        renderer.setLabelsTextSize(10);
        renderer.setLegendTextSize(15);//sers 大小
        renderer.setPointSize(4f);//点 大小
        renderer.setYAxisMax(30);
        renderer.setYAxisMin(-10);
        renderer.setShowGrid(true);
        renderer.setInScroll(true);
        renderer.setXTitle("时间（T）");
        renderer.setYTitle("温度(℃)");
        renderer.setChartTitle("车辆信息温度表");
        renderer.setMargins(new int[]{40, 40, 10, 10});
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.CENTER);
        renderer.setXLabels(10);
        renderer.setYLabels(5);
        renderer.setAxesColor(Color.BLACK);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
                                                       List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        System.out.println("--==========-=-=-=-=-="+xValues.size());
        int length = titles.length;
        try {
            for (int i = 0; i < length; i++) {
                TimeSeries series = new TimeSeries(titles[i]);
                if (xValues.size() != 0 && yValues.size() != 0) {
                    Date[] xV = xValues.get(i);
                    double[] yV = yValues.get(i);
                    int seriesLength = xV.length;
                    for (int k = 0; k < seriesLength; k++) {
                        series.add(xV[k], yV[k]);
                    }
                }
                dataset.addSeries(series);
            }
        }catch (Exception e){
            System.out.println("出现异常"+e);
        }
        return dataset;
    }

    @Override
    public void onClick(View v) {
        if(v==refu){
            dialog.show();
            dialog.changeInfo("查询中...");
            //拿取到当天的温度数据
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... voids) {
                    String er = null;
                    try {
                        // 网络不好，重试3次
                        for (int i = 0; i < 3; i++) {
                            er = HttpService.getCarMessage(car_num, date_choose.getText().toString());
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
                    Serializer serializer = new Persister();
                    try {
                        xmlcar=serializer.read(XmlCarMessage.class,er,false);
                    } catch (Exception e) {
                        System.out.println("xml解析异常:"+e);
                    }
                    System.out.println("到主线程中的"+er);
                    refreshDateTable(date_choose.getText().toString());
                }
            }.execute();
        }
    }

    public void refreshDateTable(String date){
        x.clear();
        values.clear();
//        Date date_no=new Date(System.currentTimeMillis());
//        String dd=sdf.format(date_no);
        System.out.println("***************"+xmlcar.getTems());
        if(xmlcar.getTems()!=null) {
            int temsize = xmlcar.getTems().size();
            System.out.println("==========+" + temsize);
            List<Date> listxqdate = new ArrayList<Date>();
            List<Date> listxhdate = new ArrayList<Date>();
            List<Double> listxqtem = new ArrayList<Double>();
            List<Double> listxhtem = new ArrayList<Double>();
            SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                for (int i = 0; i < temsize; i++) {
                    if (xmlcar.getTems().get(i).getLocation().equals("厢前")) {
                        listxqdate.add(sdfdate.parse(xmlcar.getTems().get(i).getTime().toString()));
                        listxqtem.add(Double.parseDouble(xmlcar.getTems().get(i).getValue().toString()));
                    } else {
                        listxhdate.add(sdfdate.parse(xmlcar.getTems().get(i).getTime().toString()));
                        listxhtem.add(Double.parseDouble(xmlcar.getTems().get(i).getValue().toString()));
                    }
                }
            } catch (Exception e) {
                System.out.println("添加数组时出现异常:" + e);
            }
            System.out.println("----------" + listxqdate.size());
            Date[] xqd = new Date[listxqdate.size()];
            for (int i = 0; i < listxqdate.size(); i++)
                xqd[i] = listxqdate.get(i);
            x.add(xqd);
            Date[] xhd = new Date[listxhdate.size()];
            for (int i = 0; i < listxhdate.size(); i++)
                xhd[i] = listxhdate.get(i);
            x.add(xhd);

            double[] xqv = new double[listxqtem.size()];
            for (int i = 0; i < listxqtem.size(); i++)
                xqv[i] = listxqtem.get(i);
            values.add(xqv);
            double[] xhv = new double[listxhtem.size()];
            for (int i = 0; i < listxhtem.size(); i++)
                xhv[i] = listxhtem.get(i);
            values.add(xhv);
        }else{
//            x.add(new Date[]{new Date(2015,4,20)});
//            values.add(new double[] { 10});
//            x.add(new Date[]{new Date(2015,4,20)});
//            values.add(new double[] { 20});
        }
        System.out.println("条形图完毕");
        String mess_xians="车牌号:"+xmlcar.getCarNumber()+"\n";
//        System.out.println("mmmmmmmmmmm"+xmlcar.getOrders().size());
        if(xmlcar.getOrders()!=null) {
            int ordersize = xmlcar.getOrders().size();
            for (int i = 0; i < ordersize; i++) {
                mess_xians += "目的地:" + xmlcar.getOrders().get(i).getCustomeraddress() + "\n";
                mess_xians += "客户:" + xmlcar.getOrders().get(i).getCustomerName() + "\n";
                mess_xians += "随车货物:" + xmlcar.getOrders().get(i).getItem_name();
                mess_xians += "   数量:" + xmlcar.getOrders().get(i).getQty() + "\n";
            }
        }
        car_message.setText(mess_xians);
        System.out.println("信息加载完毕");
        try {
            int[] colors = new int[]{Color.BLACK, Color.BLUE};
            PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE, PointStyle.DIAMOND};
            renderer = buildRenderer(colors, styles);
            int length = renderer.getSeriesRendererCount();
            for (int i = 0; i < length; i++) {
                ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
            }
            renderer.setXLabels(15);
            renderer.setYLabels(8);
            renderer.setShowGrid(true);
            renderer.setXLabelsAlign(Paint.Align.RIGHT);
            renderer.setYLabelsAlign(Paint.Align.RIGHT);
            renderer.setZoomEnabled(true);
            renderer.setPanEnabled(true);
            renderer.setZoomButtonsVisible(false);
//            renderer.setPanLimits(new double[]{-10, 20, -10, 40});
//            renderer.setZoomLimits(new double[]{-10, 20, -10, 40});
        }catch (Exception e){
            System.out.println("create table exception:"+e);
        }
        layout = (LinearLayout) findViewById(R.id.chart);
        layout.removeAllViews();
        try{
            mChartView = ChartFactory.getTimeChartView(this, buildDateDataset(titles, x, values), renderer, "HH:mm");
            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        }catch (Exception e){
            System.out.println("create table exception222:"+e);
        }

    }

    private void initUploadingDialog() {
        dialog = new MyDialog(chartActivity.this, R.style.MyDialog);
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
