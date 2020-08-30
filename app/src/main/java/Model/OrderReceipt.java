package Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrderReceipt  {

    @SerializedName("OrderId")
    @Expose
    String OrderId = "";

    @SerializedName("IsOrder")
    @Expose
    boolean IsOrder = false;

    public boolean isOrderSaved() {
        return IsOrderSaved;
    }

    public void setOrderSaved(boolean orderSaved) {
        IsOrderSaved = orderSaved;
    }

    @SerializedName("IsOrderSaved")
    @Expose
    boolean IsOrderSaved = false;

    @SerializedName("PromotionDesc")
    @Expose
    String PromotionDesc = "";

    @SerializedName("PromotionID")
    @Expose
    String PromotionID = "";

    @SerializedName("TotalAmount")
    @Expose
    String TotalAmount = "";

    @SerializedName("serials")
    @Expose
    List<Serial> serials;

    public List<Transaction_Items> getItems() {
        return Promotion_Items;
    }

    public void setItems(List<Transaction_Items> items) {
        Promotion_Items = items;
    }

    public List<Transaction_Items> getProducts() {
        return Products;
    }

    public void setProducts(List<Transaction_Items> products) {
        Products = products;
    }

    @SerializedName("Products")
    @Expose
    List<Transaction_Items>Products;

    @SerializedName("Promotion_Items")
    @Expose
    List<Transaction_Items>Promotion_Items;

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getSales_name() {
        return sales_name;
    }

    public void setSales_name(String sales_name) {
        this.sales_name = sales_name;
    }

    public String customername="" ;
    public String sales_name="" ;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public boolean isOrder() {
        return IsOrder;
    }

    public void setOrder(boolean order) {
        IsOrder = order;
    }

    public String getPromotionDesc() {
        return PromotionDesc;
    }

    public void setPromotionDesc(String promotionDesc) {
        PromotionDesc = promotionDesc;
    }

    public String getPromotionID() {
        return PromotionID;
    }

    public void setPromotionID(String promotionID) {
        PromotionID = promotionID;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        TotalAmount = totalAmount;
    }

    public List<Serial> getSerials() {
        return serials;
    }

    public void setSerials(List<Serial> serials) {
        this.serials = serials;
    }



    public static OrderReceipt getOrderReceipt() {
        return orderReceipt;
    }

    public static void setOrderReceipt(OrderReceipt orderReceipt) {
        OrderReceipt.orderReceipt = orderReceipt;
    }

    public static OrderReceipt orderReceipt= new OrderReceipt();

}
