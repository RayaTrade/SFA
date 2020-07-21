package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Items {
    private int id;
    private String HeaderId;
    private String ItemCode;
    private String Qty;
    private String UnitPrice;
    private String Discount;
    private String Submitter;
    private String QtyinStock;


    private String Subinventory;


    //------------
    @SerializedName("ITEM_CODE")
    @Expose
    String ITEM_CODE;

    @SerializedName("ONHAND")
    @Expose
    String ONHAND;

    public String getITEM_CODE_Receipt() {
        return ITEM_CODE;
    }

    public void setITEM_CODE_Receipt(String ITEM_CODE) {
        this.ITEM_CODE = ITEM_CODE;
    }

    public String getONHAND_Receipt() {
        return ONHAND;
    }

    public void setONHAND_Receipt(String ONHAND) {
        this.ONHAND = ONHAND;
    }

    public String getSubinventory() {
        return Subinventory;
    }

    public void setSubinventory(String subinventory) {
        Subinventory = subinventory;
    }


    //-----------------

    public Items() {
    }

    public Items(int id, String headerId, String itemCode, String qty, String unitPrice, String discount, String submitter, String qtyinStock) {
        this.id = id;
        HeaderId = headerId;
        ItemCode = itemCode;
        Qty = qty;
        UnitPrice = unitPrice;
        Discount = discount;
        Submitter = submitter;
        QtyinStock = qtyinStock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getSubmitter() {
        return Submitter;
    }

    public void setSubmitter(String submitter) {
        Submitter = submitter;
    }

    public String getQtyinStock() {
        return QtyinStock;
    }

    public void setQtyinStock(String qtyinStock) {
        QtyinStock = qtyinStock;
    }
}
