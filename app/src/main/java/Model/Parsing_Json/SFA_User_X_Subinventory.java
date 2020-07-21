package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import Model.Subinventory;

public class SFA_User_X_Subinventory {
    public List<Model.Subinventory> getSubinventory() {
        return Subinventory;
    }

    public void setSubinventory(List<Model.Subinventory> subinventory) {
        Subinventory = subinventory;
    }

    @SerializedName("SFA_User_X_SubinventoryResult")
    @Expose
    private List<Subinventory> Subinventory = new ArrayList<Subinventory>();
}
