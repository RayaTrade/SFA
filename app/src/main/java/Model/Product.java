package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("ITEM_CODE")
    @Expose
    public String SKU;

    @SerializedName("CAT")
    @Expose
    public String Category;
    @SerializedName("BRAND")
    @Expose
    public String Brand;
    @SerializedName("MODEL")
    @Expose
    public String Model;
    /*@SerializedName("ONHAND")
    @Expose*/
    public String QTY;
    @SerializedName("DESCRIPTION")
    @Expose
    public String Description;
    @SerializedName("BrandImage")
    @Expose
    public String image;
    @SerializedName("UnitPrice")
    @Expose
    public float UnitPrice;
    @SerializedName("ONHAND")
    @Expose
    public String OnHand;
    @SerializedName("COLOR")
    @Expose
    public String Color;

    public String Total;

    public int CollectedSerials;

    @SerializedName("Customer_Number")
    @Expose
    public String Customer_number;


    @SerializedName("Subinventory")
    @Expose
    public String Subinventory;

    public String Visit_Date;
    @SerializedName("ACC")
    @Expose
    public String ACC;
    @SerializedName("STATUS")
    @Expose
    public String STATUS;
    @SerializedName("TAX_CODE")
    @Expose
    public String TAX_CODE ;
    @SerializedName("TAX_RATE")
    @Expose
    public String TAX_RATE ;
    @SerializedName("SEGMENT1")
    @Expose
    public String SEGMENT1 ;
    @SerializedName("MAIN_CAT")
    @Expose
    public String MAIN_CAT ;
    @SerializedName("INVENTORY_ITEM_ID")
    @Expose
    public String INVENTORY_ITEM_ID ;
    @SerializedName("CREATION_DATE")
    @Expose
    public String CREATION_DATE ;

    @SerializedName("LENGTH")
    @Expose
    public String LENGTH = "";
    @SerializedName("WEIGHT")
    @Expose
    public String WEIGHT = "";
    @SerializedName("WIDTH")
    @Expose
    public String WIDTH = "";
    @SerializedName("HEIGHT")
    @Expose
    public String HEIGHT = "";

    public String getInventoryType() {
        return InventoryType;
    }

    public void setInventoryType(String inventoryType) {
        InventoryType = inventoryType;
    }

    public String InventoryType = "";


    public String getSubinventory() {
        return Subinventory;
    }

    public void setSubinventory(String subinventory) {
        Subinventory = subinventory;
    }
    public String getLENGTH() {
        return LENGTH;
    }

    public void setLENGTH(String LENGTH) {
        this.LENGTH = LENGTH;
    }

    public String getWEIGHT() {
        return WEIGHT;
    }

    public void setWEIGHT(String WEIGHT) {
        this.WEIGHT = WEIGHT;
    }

    public String getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(String WIDTH) {
        this.WIDTH = WIDTH;
    }

    public String getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(String HEIGHT) {
        this.HEIGHT = HEIGHT;
    }


    public String getACC() {
        return ACC;
    }

    public void setACC(String ACC) {
        this.ACC = ACC;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getTAX_CODE() {
        return TAX_CODE;
    }

    public void setTAX_CODE(String TAX_CODE) {
        this.TAX_CODE = TAX_CODE;
    }

    public String getTAX_RATE() {
        return TAX_RATE;
    }

    public void setTAX_RATE(String TAX_RATE) {
        this.TAX_RATE = TAX_RATE;
    }

    public String getSEGMENT1() {
        return SEGMENT1;
    }

    public void setSEGMENT1(String SEGMENT1) {
        this.SEGMENT1 = SEGMENT1;
    }

    public String getMAIN_CAT() {
        return MAIN_CAT;
    }

    public void setMAIN_CAT(String MAIN_CAT) {
        this.MAIN_CAT = MAIN_CAT;
    }

    public String getINVENTORY_ITEM_ID() {
        return INVENTORY_ITEM_ID;
    }

    public void setINVENTORY_ITEM_ID(String INVENTORY_ITEM_ID) {
        this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
    }

    public String getCREATION_DATE() {
        return CREATION_DATE;
    }

    public void setCREATION_DATE(String CREATION_DATE) {
        this.CREATION_DATE = CREATION_DATE;
    }



    public Product(int collectedSerials) {
        CollectedSerials = collectedSerials;
    }

    public String getCustomer_number() {
        return Customer_number;
    }

    public void setCustomer_number(String customer_number) {
        Customer_number = customer_number;
    }

    public Product() {
    }

    public float getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        UnitPrice = unitPrice;
    }


    public Product(String SKU, String category, String brand,String model, String QTY, String description, String image, float unitPrice, String onHand, String color, String customer_number,String total,String visit_Date,
                    String ACC, String STATUS, String TAX_CODE ,String TAX_RATE , String SEGMENT1 , String MAIN_CAT, String INVENTORY_ITEM_ID , String CREATION_DATE,String subinventory ) {
        this.SKU = SKU;
        Category = category;
        Brand = brand;
        Model = model;
        this.QTY = QTY;
        Description = description;
        this.image = image;
        UnitPrice = unitPrice;
        OnHand = onHand;
        Color = color;
        Customer_number = customer_number;
        Total = total;
        Visit_Date = visit_Date;
         this.ACC=ACC;
         this.STATUS=STATUS;
        this.TAX_CODE=TAX_CODE ;
        this.TAX_RATE =TAX_RATE;
        this.SEGMENT1 =SEGMENT1;
        this.MAIN_CAT =MAIN_CAT;
        this.INVENTORY_ITEM_ID =INVENTORY_ITEM_ID ;
        this.CREATION_DATE =CREATION_DATE ;
        this.Subinventory = subinventory;
    }

    public Product(String SKU, String category, String brand,String model, String QTY, String description, String image, float unitPrice, String onHand, String color, String customer_number,String total,String visit_Date,
                   String  lENGTH ,  String  wEIGHT ,String wIDTH,String hEIGHT,String subinventory,String inventoryType) {
        this.SKU = SKU;
        Category = category;
        Brand = brand;
        Model = model;
        this.QTY = QTY;
        Description = description;
        this.image = image;
        UnitPrice = unitPrice;
        OnHand = onHand;
        Color = color;
        Customer_number = customer_number;
        Total = total;
        Visit_Date = visit_Date;
        LENGTH = lENGTH;
        WEIGHT =wEIGHT;
        WIDTH =wIDTH;
        HEIGHT = hEIGHT;
        Subinventory = subinventory;
        InventoryType = inventoryType;
    }
    public String getVisit_Date() {
        return Visit_Date;
    }

    public void setVisit_Date(String visit_Date) {
        Visit_Date = visit_Date;
    }

    public int getCollectedSerials() {
        return CollectedSerials;
    }

    public void setCollectedSerials(int collectedSerials) {
        CollectedSerials = collectedSerials;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getOnHand() {
        return OnHand;
    }

    public void setOnHand(String OnHand) {
        this.OnHand = OnHand;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
