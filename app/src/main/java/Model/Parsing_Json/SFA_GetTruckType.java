package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import Model.TruckType;

public class SFA_GetTruckType {


    public List<Model.TruckType> getTruckType() {
        return TruckType;
    }

    public void setTruckType(List<Model.TruckType> truckType) {
        TruckType = truckType;
    }

    @SerializedName("SFA_GetTruckTypeResult")
    @Expose
    private List<TruckType> TruckType = new ArrayList<TruckType>();
}
