package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subinventory {
    @SerializedName("Subinventory")
    @Expose
    String Subinventory;

    public String getSubinventory() {
        return Subinventory;
    }

    public void setSubinventory(String subinventory) {
        Subinventory = subinventory;
    }

    public String getSubinventoryID() {
        return SubinventoryID;
    }

    public void setSubinventoryID(String subinventoryID) {
        SubinventoryID = subinventoryID;
    }

    @SerializedName("SubinventoryID")
    @Expose
    String SubinventoryID;

}
