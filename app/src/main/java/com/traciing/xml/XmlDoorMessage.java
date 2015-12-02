package com.traciing.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Administrator on 2015/5/12.
 */

@Root(name = "datas")
public class XmlDoorMessage {
    @ElementList(entry = "orderrecord", inline = true, required = false)
    private List<XmlOrderRecord> orderrecord;

    public List<XmlOrderRecord> getOrderrecord() {
        return orderrecord;
    }

    public void setOrderrecord(List<XmlOrderRecord> orderrecord) {
        this.orderrecord = orderrecord;
    }

    public static class XmlOrderRecord{
        @ElementList(entry = "orderinfo", inline = true, required = false)
        private List<XmlOrderinfo> orderinfo;

        public List<XmlOrderinfo> getOrderinfo() {
            return orderinfo;
        }

        public void setOrderinfo(List<XmlOrderinfo> orderinfo) {
            this.orderinfo = orderinfo;
        }
    }

    public static class XmlOrderinfo{
        @Element(name="itemname",required = false)
        private String itemname;
        @Element(name="lotno",required = false)
        private String lotno;
        @Element(name="orderqty",required = false)
        private String orderqty;
        @Element(name="actualqty",required = false)
        private String actualqty;
        @Element(name="time",required = false)
        private String time;

        public String getItemname() {
            return itemname;
        }

        public void setItemname(String itemname) {
            this.itemname = itemname;
        }

        public String getLotno() {
            return lotno;
        }

        public void setLotno(String lotno) {
            this.lotno = lotno;
        }

        public String getOrderqty() {
            return orderqty;
        }

        public void setOrderqty(String orderqty) {
            this.orderqty = orderqty;
        }

        public String getActualqty() {
            return actualqty;
        }

        public void setActualqty(String actualqty) {
            this.actualqty = actualqty;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
