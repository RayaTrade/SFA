package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductDimension {

    public String getBRAND() {
        return BRAND;
    }

    public void setBRAND(String BRAND) {
        this.BRAND = BRAND;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(String HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

    public String getINVENTORY_ITEM_ID() {
        return INVENTORY_ITEM_ID;
    }

    public void setINVENTORY_ITEM_ID(String INVENTORY_ITEM_ID) {
        this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
    }

    public String getITEMCODE() {
        return ITEMCODE;
    }

    public void setITEMCODE(String ITEMCODE) {
        this.ITEMCODE = ITEMCODE;
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

    @SerializedName("BRAND")
    @Expose
    String    BRAND;
    @SerializedName("DESCRIPTION")
    @Expose
    String    DESCRIPTION;
    @SerializedName("HEIGHT")
    @Expose
    String    HEIGHT;

    @SerializedName("INVENTORY_ITEM_ID")
    @Expose
    String    INVENTORY_ITEM_ID;
    @SerializedName("ITEMCODE")
    @Expose
    String    ITEMCODE;
    @SerializedName("LENGTH")
    @Expose
    String    LENGTH;
    @SerializedName("WEIGHT")
    @Expose
    String    WEIGHT;
    @SerializedName("WIDTH")
    @Expose
    String    WIDTH;

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    double size = 0.0;
    public void setProSize(ProductDimension product)
    {
        product.size = (Double.parseDouble(product.HEIGHT)/100)  * (Double.parseDouble(product.WIDTH)/100) * (Double.parseDouble(product.LENGTH)/100);
    }
}
