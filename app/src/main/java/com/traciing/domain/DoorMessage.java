package com.traciing.domain;

/**
 * Created by Administrator on 2015/5/12.
 */
public class DoorMessage {

    private String item_name_door;//商品名称
    private String lot_no_door;//批号
    private String order_no_door;//订单数量
    private String up_order_door;//已提交
    private String date_door;// 时间

    public String getDate_door() {
        return date_door;
    }

    public void setDate_door(String date_door) {
        this.date_door = date_door;
    }

    public String getItem_name_door() {
        return item_name_door;
    }

    public void setItem_name_door(String item_name_door) {
        this.item_name_door = item_name_door;
    }

    public String getLot_no_door() {
        return lot_no_door;
    }

    public void setLot_no_door(String lot_no_door) {
        this.lot_no_door = lot_no_door;
    }

    public String getOrder_no_door() {
        return order_no_door;
    }

    public void setOrder_no_door(String order_no_door) {
        this.order_no_door = order_no_door;
    }

    public String getUp_order_door() {
        return up_order_door;
    }

    public void setUp_order_door(String up_order_door) {
        this.up_order_door = up_order_door;
    }
}
