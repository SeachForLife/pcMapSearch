package com.traciing.domain;

/**
 * Created by Administrator on 2015/5/5.
 */
public class storehouseMessage {

    private String item_name;
    private String item_orderno;
    private String item_num;
    private String store_num;
    private String traciing_num;
    private String out_store;

    public String getItem_orderno() {
        return item_orderno;
    }

    public void setItem_orderno(String item_orderno) {
        this.item_orderno = item_orderno;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_num() {
        return item_num;
    }

    public void setItem_num(String item_num) {
        this.item_num = item_num;
    }

    public String getStore_num() {
        return store_num;
    }

    public void setStore_num(String store_num) {
        this.store_num = store_num;
    }

    public String getTraciing_num() {
        return traciing_num;
    }

    public void setTraciing_num(String traciing_num) {
        this.traciing_num = traciing_num;
    }

    public String getOut_store() {
        return out_store;
    }

    public void setOut_store(String out_store) {
        this.out_store = out_store;
    }
}
