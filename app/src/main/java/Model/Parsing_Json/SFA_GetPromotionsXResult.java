package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import Model.Promotions;

public class SFA_GetPromotionsXResult {

    public List<Model.Promotions> getPromotions() {
        return Promotions;
    }

    public void setPromotions(List<Model.Promotions> promotions) {
        Promotions = promotions;
    }

    @SerializedName("SFA_GetPromotionsXResultResult")
    @Expose
    private List<Model.Promotions> Promotions = new ArrayList<Promotions>();
}
