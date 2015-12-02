package com.traciing.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(name = "datas")
public class XmlCarMessage {

    @ElementList(entry = "order", inline = true, required = false)
    private List<XmlOrder> orders;
    @ElementList(entry = "tem", inline = true, required = false)
    private List<XmlTem> tems;
    @Element(name = "carnumber", data = true)
    private String carNumber;
    @Element(name = "date", data = true)
    private String date;

    public List<XmlOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<XmlOrder> orders) {
        this.orders = orders;
    }

    public List<XmlTem> getTems() {
        return tems;
    }

    public void setTems(List<XmlTem> tems) {
        this.tems = tems;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static class XmlTem{
        @Element(name="location",required = false)
        private String location;
        @Element(name="value",required = false)
        private String value;
        @Element(name="statu",required = false)
        private String statu;

        public String getStatu() {
            return statu;
        }

        public void setStatu(String statu) {
            this.statu = statu;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Element(name="time",required = false)
        private String time;
    }

    public static class XmlOrder {

        //送货地址
        @Element(name="customeraddress",required = false)
        private String customeraddress;
        @Element(name = "orderno", required = false)
        private String orderno;
        @Element(name="cangkuid",required = false)
        private String cangkuid;
        @Element(name = "customername", required = false)
        private String customerName;
        @Element(name = "item_name", required = false)
        private String item_name;
        @Element(name = "lot_no", required = false)
        private String lot_no;

        public String getCustomeraddress() {
            return customeraddress;
        }

        public void setCustomeraddress(String customeraddress) {
            this.customeraddress = customeraddress;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getLot_no() {
            return lot_no;
        }

        public void setLot_no(String lot_no) {
            this.lot_no = lot_no;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCangkuid() {
            return cangkuid;
        }

        public void setCangkuid(String cangkuid) {
            this.cangkuid = cangkuid;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        @Element(name = "qty", required = false)
        private String qty;

    }

}
