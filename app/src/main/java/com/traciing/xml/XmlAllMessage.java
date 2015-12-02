package com.traciing.xml;

import com.traciing.domain.AllcarMessage;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Administrator on 2015/5/7.
 */

@Root(name = "datas")
public class XmlAllMessage {

    @ElementList(entry = "cardata", inline = true, required = false)
    private List<XmlCarData> cardata;

    public List<XmlWareHouse> getWarehousedata() {
        return warehousedata;
    }

    public void setWarehousedata(List<XmlWareHouse> warehousedata) {
        this.warehousedata = warehousedata;
    }

    @ElementList(entry = "warehousedata", inline = true, required = false)
    private List<XmlWareHouse> warehousedata;

    public List<XmlCarData> getCardata() {
        return cardata;
    }

    public void setCardata(List<XmlCarData> cardata) {
        this.cardata = cardata;
    }

    //车辆信息
    public static class XmlCarData {

        @Element(name="reportTime",required = false)
        private String reportTime;
        @Element(name="carnumber",required = false)
        private String carnumber;
        @Element(name="lngPosition",required = false)
        private String lngpostion;
        @Element(name="latPosition",required = false)
        private String latpostion;
        @Element(name="customername",required = false)
        private String customername;

        public String getStatu() {
            return statu;
        }

        public void setStatu(String statu) {
            this.statu = statu;
        }

        public String getReportTime() {
            return reportTime;
        }

        public void setReportTime(String reportTime) {
            this.reportTime = reportTime;
        }

        public String getCustomername() {
            return customername;
        }

        public void setCustomername(String customername) {
            this.customername = customername;
        }

        @Element(name="statu",required = false)
        private String statu;

        public String getCarnumber() {
            return carnumber;
        }
        public void setCarnumber(String carnumber) {
            this.carnumber = carnumber;
        }

        public String getLngpostion() {
            return lngpostion;
        }

        public void setLngpostion(String lngpostion) {
            this.lngpostion = lngpostion;
        }

        public String getLatpostion() {
            return latpostion;
        }

        public void setLatpostion(String latpostion) {
            this.latpostion = latpostion;
        }

        public List<XmlTems> getTem() {
            return tem;
        }

        public void setTem(List<XmlTems> tem) {
            this.tem = tem;
        }

        @ElementList(entry = "tem", inline = true, required = false)
        private List<XmlTems> tem;

    }

    public static class XmlTems{

        @ElementList(entry = "relationtem", inline = true, required = false)
        private List<XmlRelationTem> relationtem;

        public List<XmlRelationTem> getRelationtem() {
            return relationtem;
        }

        public void setRelationtem(List<XmlRelationTem> relationtem) {
            this.relationtem = relationtem;
        }
    }

    public static class XmlRelationTem{
        @Element(name="passId",required = false)
        private String passid;

        public String getPassid() {
            return passid;
        }

        public void setPassid(String passid) {
            this.passid = passid;
        }

        public String getPassvalue() {
            return passvalue;
        }

        public void setPassvalue(String passvalue) {
            this.passvalue = passvalue;
        }

        @Element(name="passValue",required = false)
        private String passvalue;
    }

    public static class XmlWareHouse{
        @Element(name="lngPosition",required = false)
        private String lngPostion;

        @Element(name="latPosition",required = false)
        private String latPostion;

        @Element(name="warehousename",required = false)
        private String warehousename;

        @ElementList(entry = "storedata", inline = true, required = false)
        private List<XmlStoredata> storedata;

        public String getLngPostion() {
            return lngPostion;
        }

        public void setLngPostion(String lngPostion) {
            this.lngPostion = lngPostion;
        }

        public String getLatPostion() {
            return latPostion;
        }

        public void setLatPostion(String latPostion) {
            this.latPostion = latPostion;
        }

        public String getWarehousename() {
            return warehousename;
        }

        public void setWarehousename(String warehousename) {
            this.warehousename = warehousename;
        }

        public List<XmlStoredata> getStoredata() {
            return storedata;
        }

        public void setStoredata(List<XmlStoredata> storedata) {
            this.storedata = storedata;
        }
    }

    public static class XmlStoredata{
        @ElementList(entry = "storeinfo", inline = true, required = false)
        private List<XmlStoreinfo> storeinfo;

        public List<XmlStoreinfo> getStoreinfo() {
            return storeinfo;
        }

        public void setStoreinfo(List<XmlStoreinfo> storeinfo) {
            this.storeinfo = storeinfo;
        }
    }

    public static class XmlStoreinfo{
        @Element(name="store_name",required = false)
        private String store_name;

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }
    }
}
