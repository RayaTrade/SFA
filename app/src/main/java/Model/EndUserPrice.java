package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EndUserPrice {

    @SerializedName("CustomerName")
    @Expose
    public static String CustomerName;
    @SerializedName("CustomerNumber")
    @Expose
    public static String CustomerNumber;
    @SerializedName("ItemCode")
    @Expose
    public static String ItemCode;
    @SerializedName("Price")
    @Expose
   public static String Price;

}
