package com.traciing.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Administrator on 2015/5/9.
 */
@Root(name = "datas")
public class XmlStoreHouseMessage {

    @ElementList(entry = "warehouseqty", inline = true, required = false)
    private List<XmlQty> warehouseqty;

    @ElementList(entry = "warehousetem", inline = true, required = false)
    private List<XmlTem> warehousetem;

    public List<XmlQty> getWarehouseqty() {
        return warehouseqty;
    }

    public void setWarehouseqty(List<XmlQty> warehouseqty) {
        this.warehouseqty = warehouseqty;
    }

    public List<XmlTem> getWarehousetem() {
        return warehousetem;
    }

    public void setWarehousetem(List<XmlTem> warehousetem) {
        this.warehousetem = warehousetem;
    }

    public static class XmlQty{
        @ElementList(entry = "itemqty", inline = true, required = false)
        private List<XmlItemQty> itemqty;

        public List<XmlItemQty> getItemqty() {
            return itemqty;
        }

        public void setItemqty(List<XmlItemQty> itemqty) {
            this.itemqty = itemqty;
        }
    }

    public static class  XmlTem{
        @ElementList(entry = "pointtem", inline = true, required = false)
        private List<XmlPointTem> pointtem;

        public List<XmlPointTem> getPointtem() {
            return pointtem;
        }

        public void setPointtem(List<XmlPointTem> pointtem) {
            this.pointtem = pointtem;
        }
    }

    public static class XmlItemQty{

        @Element(name="itemname",required = false)
        private String itemname;
        @Element(name="lotno",required = false)
        private String lotno;
        @Element(name="storeqty",required = false)
        private String storeqty;
        @Element(name="orderqty",required = false)
        private String orderqty;
        @Element(name="transferqty",required = false)
        private String transferqty;

        public String getLotno() {
            return lotno;
        }

        public void setLotno(String lotno) {
            this.lotno = lotno;
        }

        public String getTransferqty() {
            return transferqty;
        }

        public void setTransferqty(String transferqty) {
            this.transferqty = transferqty;
        }

        public String getItemname() {
            return itemname;
        }

        public void setItemname(String itemname) {
            this.itemname = itemname;
        }

        public String getOrderqty() {
            return orderqty;
        }

        public void setOrderqty(String orderqty) {
            this.orderqty = orderqty;
        }

        public String getStoreqty() {
            return storeqty;
        }

        public void setStoreqty(String storeqty) {
            this.storeqty = storeqty;
        }
    }

    public static class XmlPointTem{
        @Element(name="pointstr",required = false)
        private String pointstr;
        @Element(name="value",required = false)
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPointstr() {
            return pointstr;
        }

        public void setPointstr(String pointstr) {
            this.pointstr = pointstr;
        }
    }
}
