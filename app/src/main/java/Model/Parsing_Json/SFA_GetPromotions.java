package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import Model.Promotions;

public class SFA_GetPromotions {
    public List<Model.Promotions> getPromotions() {
        return Promotions;
    }

    public void setPromotions(List<Model.Promotions> promotions) {
        Promotions = promotions;
    }

    @SerializedName("SFA_GetPromotionsResult")
    @Expose
    private List<Promotions> Promotions = new ArrayList<Promotions>();
}
