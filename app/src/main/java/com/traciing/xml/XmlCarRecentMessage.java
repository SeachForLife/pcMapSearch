package com.traciing.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Administrator on 2015/5/9.
 */

@Root(name = "datas")
public class XmlCarRecentMessage {

    @ElementList(entry = "gpsinfos", inline = true, required = false)
    private List<XmlGpsinfo> gpsinfos;

    @ElementList(entry = "storeinfos", inline = true, required = false)
    private List<XmlStoreinfo> storeinfos;

    public List<XmlGpsinfo> getGpsinfos() {
        return gpsinfos;
    }

    public void setGpsinfos(List<XmlGpsinfo> gpsinfos) {
        this.gpsinfos = gpsinfos;
    }

    public List<XmlStoreinfo> getStoreinfos() {
        return storeinfos;
    }

    public void setStoreinfos(List<XmlStoreinfo> storeinfos) {
        this.storeinfos = storeinfos;
    }

    public static class XmlGpsinfo{
        @ElementList(entry = "gpsdata", inline = true, required = false)
        private List<XmlGps> gpsdata;

        public List<XmlGps> getGpsdata() {
            return gpsdata;
        }

        public void setGpsdata(List<XmlGps> gpsdata) {
            this.gpsdata = gpsdata;
        }
    }

    public static class XmlStoreinfo{
        @ElementList(entry = "storedata", inline = true, required = false)
        private List<XmlStroe> storedata;

        public List<XmlStroe> getStoredata() {
            return storedata;
        }

        public void setStoredata(List<XmlStroe> storedata) {
            this.storedata = storedata;
        }
    }

    public static class XmlGps{
        @Element(name="lngPosition",required = false)
        private String lngPosition;

        @Element(name="latPosition",required = false)
        private String latPosition;

        @Element(name="distance",required = false)
        private String distance;

        public String getLatPosition() {
            return latPosition;
        }

        public void setLatPosition(String latPosition) {
            this.latPosition = latPosition;
        }

        public String getLngPosition() {
            return lngPosition;
        }

        public void setLngPosition(String lngPosition) {
            this.lngPosition = lngPosition;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }

    public static class XmlStroe{
        @Element(name="lngPosition",required = false)
        private String lngPosition;
        @Element(name="latPosition",required = false)
        private String latPosition;
        @Element(name="storeName",required = false)
        private String storeName;

        @ElementList(entry = "orderInfo", inline = true, required = false)
        private List<XmlOrderinfo> orderInfo;

        public String getLngPosition() {
            return lngPosition;
        }

        public void setLngPosition(String lngPosition) {
            this.lngPosition = lngPosition;
        }

        public String getLatPosition() {
            return latPosition;
        }

        public void setLatPosition(String latPosition) {
            this.latPosition = latPosition;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public List<XmlOrderinfo> getOrderInfo() {
            return orderInfo;
        }

        public void setOrderInfo(List<XmlOrderinfo> orderInfo) {
            this.orderInfo = orderInfo;
        }
    }

    public static class XmlOrderinfo{
        @ElementList(entry = "orderdata", inline = true, required = false)
        private List<XmlOrderdata> orderdata;

        public List<XmlOrderdata> getOrderdata() {
            return orderdata;
        }

        public void setOrderdata(List<XmlOrderdata> orderdata) {
            this.orderdata = orderdata;
        }
    }

    public static class XmlOrderdata{
        @Element(name="itemName",required = false)
        private String itemName;
        @Element(name="lotNo",required = false)
        private String lotNo;
        @Element(name="qty",required = false)
        private String qty;
        @Element(name="sureTime",required = false)
        private String sureTime;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getLotNo() {
            return lotNo;
        }

        public void setLotNo(String lotNo) {
            this.lotNo = lotNo;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getSureTime() {
            return sureTime;
        }

        public void setSureTime(String sureTime) {
            this.sureTime = sureTime;
        }
    }
}
