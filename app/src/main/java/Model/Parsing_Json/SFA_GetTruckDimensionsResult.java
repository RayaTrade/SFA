package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import Model.TruckDimensions;


public class SFA_GetTruckDimensionsResult {

    @SerializedName("SFA_GetTruckDimensionsResult")
    @Expose
    private List<TruckDimensions> TruckDimensions = new ArrayList<TruckDimensions>();
}
