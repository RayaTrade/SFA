package Model;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import preview_database.DB.SyncDB.SyncDBHelper;

public class Promotions {

    @SerializedName("ActiveDate")
    @Expose
    String ActiveDate ="";
    @SerializedName("Country")
    @Expose
    String Country ="";
    @SerializedName("EndDate")
    @Expose
    String EndDate ="";
    @SerializedName("IsActive")
    @Expose
    String IsActive ="";
    @SerializedName("PromDesc")
    @Expose
    String PromDesc ="";
    @SerializedName("PromNote")
    @Expose
    String PromNote ="";
    @SerializedName("PromType")
    @Expose
    String PromType ="";
    @SerializedName("PromoId")
    @Expose
    String PromoId ="";
    @SerializedName("PromoName")
    @Expose
    String PromoName ="";
    @SerializedName("Server")
    @Expose
    String Server ="";

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    @SerializedName("CreatedDate")
    @Expose
    String CreatedDate ="";

    public String getServerConfigration() {
        return ServerConfigration;
    }

    public void setServerConfigration(String serverConfigration) {
        ServerConfigration = serverConfigration;
    }

    @SerializedName("ServerConfigration")
    @Expose
    String ServerConfigration ="";




    @SerializedName("And_PromoQuery")
    @Expose
    String And_PromoQuery ="";


    @SerializedName("PromoResult")
    @Expose
    String PromoResult ="";



    public String getAnd_PromoQuery() {
        return And_PromoQuery;
    }

    public void setAnd_PromoQuery(String and_PromoQuery) {
        And_PromoQuery = and_PromoQuery;
    }

    public String getPromoResult() {
        return PromoResult;
    }

    public void setPromoResult(String promoResult) {
        PromoResult = promoResult;
    }


    public String getActiveDate() {
        return ActiveDate;
    }

    public void setActiveDate(String activeDate) {
        ActiveDate = activeDate;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getPromDesc() {
        return PromDesc;
    }

    public void setPromDesc(String promDesc) {
        PromDesc = promDesc;
    }

    public String getPromNote() {
        return PromNote;
    }

    public void setPromNote(String promNote) {
        PromNote = promNote;
    }

    public String getPromType() {
        return PromType;
    }

    public void setPromType(String promType) {
        PromType = promType;
    }

    public String getPromoId() {
        return PromoId;
    }

    public void setPromoId(String promoId) {
        PromoId = promoId;
    }

    public String getPromoName() {
        return PromoName;
    }

    public void setPromoName(String promoName) {
        PromoName = promoName;
    }

    public String getServer() {
        return Server;
    }

    public void setServer(String server) {
        Server = server;
    }

    public Promotions FindPromo(Context context, String HeaderID)
    {
        Header Transaction = new SyncDBHelper(context).getAllTransactionsByID(Integer.parseInt(HeaderID));

        List<Promotions> Promotions = new SyncDBHelper(context).getAllPromotions();
        for ( Promotions prom: Promotions) {
            if(CheckDate(prom))
            {
                if(new SyncDBHelper(context).IsPromoValid(prom.And_PromoQuery,HeaderID) > 0)
                {
                    if(new SyncDBHelper(context).Applly_Promo(prom.PromoId,Transaction))
                        return prom;
                }
            }
        }
            return null;
    }

    private boolean CheckDate(Promotions promotion)
    {
        String Now = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Date dateNow = null, Adate=null,Edate=null;
        try {
            dateNow =new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(Now);
            Adate=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(promotion.ActiveDate);
            Edate=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(promotion.EndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dateNow.after(Adate) && dateNow.before(Edate))
            return true;
        else
            return  false;
    }


}


