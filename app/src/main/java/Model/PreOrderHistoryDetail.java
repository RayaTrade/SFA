package Model;

public class PreOrderHistoryDetail {
    public String ItemCode;
    public String Brand;
    public String Model;
    public String Description;
    public String Qty;
    public String Total;
    public String UnitPrice;
    public String Subinventory;

    public PreOrderHistoryDetail(String itemCode, String brand, String model, String description, String qty, String total, String unitPrice,String subinventory) {
        ItemCode = itemCode;
        Brand = brand;
        Model = model;
        Description = description;
        Qty = qty;
        Total = total;
        UnitPrice = unitPrice;
        Subinventory = subinventory;
    }

    public String getSubinventory() {
        return Subinventory;
    }

    public void setSubinventory(String subinventory) {
        Subinventory = subinventory;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
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
}
