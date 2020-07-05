package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import Model.Promotions;

public class SFA_GetPromotionsXQuery {


    public List<Promotions> getSFA_GetPromotionsXQueryResult() {
        return SFA_GetPromotionsXQueryResult;
    }

    public void setSFA_GetPromotionsXQueryResult(List<Promotions> SFA_GetPromotionsXQueryResult) {
        this.SFA_GetPromotionsXQueryResult = SFA_GetPromotionsXQueryResult;
    }

    @SerializedName("SFA_GetPromotionsXQueryResult")
    @Expose
    private List<Model.Promotions> SFA_GetPromotionsXQueryResult = new ArrayList<Promotions>();

}
