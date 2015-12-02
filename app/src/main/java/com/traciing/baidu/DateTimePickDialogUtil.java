package com.traciing.baidu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimePickDialogUtil implements OnDateChangedListener
        {
    private DatePicker datePicker;
    private AlertDialog ad;
    public String dateTime;
    private String initDateTime;
    private Activity activity;

    public DateTimePickDialogUtil(Activity activity, String initDateTime) {
        this.activity = activity;
        this.initDateTime = initDateTime;

    }

    public void init(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
//        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
//        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

            public AlertDialog dateTimePicKDialog(final EditText inputDate) {
                LinearLayout dateTimeLayout = (LinearLayout) activity
                        .getLayoutInflater().inflate(R.layout.common_datetime, null);
                datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
//        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
                init(datePicker);
//        timePicker.setIs24HourView(true);
//        timePicker.setOnTimeChangedListener(this);

                ad = new AlertDialog.Builder(activity)
                        .setTitle(initDateTime)
                        .setView(dateTimeLayout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                System.out.println("uuuuuuuuuuuuu" + dateTime);
                                inputDate.setText(dateTime);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
//                                inputDate.setText("");
                            }
                        }).show();

                onDateChanged(null, 0, 0, 0);
                return ad;
            }

    public AlertDialog dateTimePicKDialog(final EditText inputDate,final ImageView refu) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
//        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        init(datePicker);
//        timePicker.setIs24HourView(true);
//        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.out.println("uuuuuuuuuuuuu" + dateTime);
                        inputDate.setText(dateTime);
                        refu.callOnClick();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return ad;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(dateTime);
    }

    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();


        String yearStr=initDateTime.split("-")[0].trim();
        String monthStr=initDateTime.split("-")[1].trim();
        String dayStr=initDateTime.split("-")[2].split(" ")[0].trim();

//        String hourStr=initDateTime.split(":")[0].split(" ")[1].trim();
//        String minuteStr=initDateTime.split(":")[1].trim();
        System.out.println(yearStr+":"+monthStr+":"+dayStr);

        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
//        int currentHour = Integer.valueOf(hourStr.trim()).intValue();
//        int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay);
        return calendar;
    }

    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern);
        } else {
            loc = srcStr.lastIndexOf(pattern);
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc);
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length());
        }
        return result;
    }

}
