package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class List_EndUserPrice
{
    @SerializedName("SFA_EndUserPriceResult")
    @Expose
   public static List<EndUserPrice>endUserPrices;
}
