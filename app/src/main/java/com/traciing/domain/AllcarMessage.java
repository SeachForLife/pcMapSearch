package com.traciing.domain;

/**
 * Created by Administrator on 2015/5/5.
 */
public class AllcarMessage {

    private String car_name;
    private String cutomername;
    private String car_state;
    private String start_tem;
    private String finish_tem;

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getStart_tem() {
        return start_tem;
    }

    public void setStart_tem(String start_tem) {
        this.start_tem = start_tem;
    }

    public String getCar_state() {
        return car_state;
    }

    public void setCar_state(String car_state) {
        this.car_state = car_state;
    }

    public String getFinish_tem() {
        return finish_tem;
    }

    public void setFinish_tem(String finish_tem) {
        this.finish_tem = finish_tem;
    }

    public String getCutomername() {
        return cutomername;
    }

    public void setCutomername(String cutomername) {
        this.cutomername = cutomername;
    }
}
