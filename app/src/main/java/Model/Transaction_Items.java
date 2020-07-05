package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction_Items {

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    @SerializedName("Discount")
    @Expose
    String Discount = "";

    @SerializedName("QTY")
    @Expose
    String QTY = "";

    @SerializedName("SKU")
    @Expose
    String SKU = "";

    @SerializedName("Total")
    @Expose
    String Total = "";

    @SerializedName("UnitPrice")
    @Expose
    String UnitPrice = "";

}
