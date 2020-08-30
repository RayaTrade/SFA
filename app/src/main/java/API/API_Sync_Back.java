package API;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ahmed_hasanein.sfa.CollectSerialActivity;
import com.example.ahmed_hasanein.sfa.FinishOnlineActivity;
import com.example.ahmed_hasanein.sfa.SyncBackActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Model.Items;
import Model.Parsing_Json.SFA_GetTruckType;
import Model.Product;
import Model.ProductDimension;
import Model.Serial;
import Model.TransactionTender;
import Utility.DialogHint;
import Utility.Http;
import cz.msebera.android.httpclient.HttpResponse;
import preview_database.DB.DealerDB.DealerDBHelper;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.ProductPreOrderDB.ProductDBHelper;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.DashboardActivity.dialogSync;


public class API_Sync_Back {
    private String ItemId_Online;
    private boolean TransactionFlag;
    private boolean TransactionTenderFlag;
    private boolean TransactionItemsFlag;
    private boolean TransactionInCompletedFlag;
    List<Items> itemsList;
    List<TransactionTender> transactionTenderList;
    SyncDBHelper db_sync;
    int counter = 1;
    boolean activityopen = false;

    List<Serial> serials = new ArrayList<>();
    static String Headerid;
    boolean saved = false;
    public static    ProductDimension[] productDimensions;
    public static SFA_GetTruckType Trucks;

    private JSONArray Convert_Array_to_Json_to (String HeaderID,Context context,String HeaderIdOffline,List<Items> productList,String TransactionType)
    {
        JSONArray array = new JSONArray();
        ArrayList<String> Serial_list = new ArrayList<>();
        CollectSerialActivity activity = new CollectSerialActivity();

        // adding productList
        for (Items product : productList) {
            JSONObject object = new JSONObject();
            try {
                object.put("SKU",product.getItemCode() );
                object.put("QTY" , product.getQty());
                object.put("QtyinStock",product.getQtyinStock());
                object.put("Discount",product.getDiscount());
                object.put("UnitPrice" , product.getUnitPrice());
                object.put("Subinventory" , product.getSubinventory());
                object.put("Inventory" , product.getInventory());
                if(TransactionType.equals("2")) {
                    try {
                        Serial_list = (new CollectSerialActivity().List_getSerialsByItemCode(HeaderID, context, product.getItemCode()));
                        Log.d("serials  ", serials + "product.getSKU  " + product.getItemCode());
                    } catch (Exception e) {

                    }
                }


                    object.put("Serial", new JSONArray(Serial_list));


                array.put(object);
            }catch (JSONException e) {
                e.printStackTrace();
                Log.d("serials  ", serials + "product.getSKU  " + product.getItemCode());

            }


        }


        return array;
    }

    private  Float Calcualte_Amount(List<Items> productList)
    {
        float total = 0;
        for (Items product : productList) {
            total += Float.valueOf(product.getUnitPrice());
        }
        return total;
    }



    public void SavePreOrderHeader_OneBulk(final int countAllheaderList, final Activity activity, final Context context, final String CountryCode, final String CustomerNumber,
                                           final String Submitter, final String Long, final String Lat, final String Comment, final String DeliveryMethod, final String TransactionType,
                                           final String HeaderIdOffline,final String PromoID, final String SyncType) {

        if(Submitter == null || Submitter == "")
        {
            new DialogHint().Session_End(activity,context);
        }
        else {



            db_sync = new SyncDBHelper(context);
            final SerialDBHelper db_serial = new SerialDBHelper(context);
            int promoID= -1;

            if(PromoID != null)
            promoID = Integer.parseInt(PromoID);

            JSONObject object = new JSONObject();

            try {
                itemsList = new ArrayList<>();
                itemsList.addAll(db_sync.getAllTransactionItemsOffline(String.valueOf(HeaderIdOffline)));

                object.accumulate("ServerConfigID", CountryCode);
                object.accumulate("CustomerNumber", CustomerNumber);
                object.accumulate("Submitter", Submitter);
                object.accumulate("Long", Long);
                object.accumulate("Lat", Lat);
                object.accumulate("Comments", Comment);
                object.accumulate("DeliveryMethod", DeliveryMethod);
                object.accumulate("TransactionType", TransactionType);
                object.accumulate("OfflineID", HeaderIdOffline);
                object.accumulate("HeaderID", "");
                object.accumulate("Product_List", Convert_Array_to_Json_to(HeaderIdOffline,context,HeaderIdOffline, itemsList,TransactionType));
                object.accumulate("OpenFrom", "Offline to Online");
                object.accumulate("TenderType", (db_sync.getAllTransactionTenderOffline(HeaderIdOffline).size() > 0 )?db_sync.getAllTransactionTenderOffline(HeaderIdOffline).get(0).getTenderType() :"back to sales");
                object.accumulate("Amount", String.valueOf(Calcualte_Amount(itemsList)));
                object.accumulate("PromoID",promoID);
                SFA_GetTruckType trucks = new SFA_GetTruckType();
                trucks.setTruckType(db_sync.getAllTruckTypeOffline(HeaderIdOffline));
                JSONArray jsArray = new JSONArray();
                for (int i = 0; i < trucks.getTruckType().size(); i++) {
                    if(trucks.getTruckType().get(i).getCount() > 0)
                    {
                        String jsonInString = new Gson().toJson(trucks.getTruckType().get(i));
                        JSONObject mJSONObject = new JSONObject(jsonInString);
                        jsArray.put(mJSONObject);
                    }
                }
                object.accumulate("Trucks",jsArray );

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavaOrder_OneBulk";
           // String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavaOrder_OneBulk_PromotionTest";

            Log.d("HEADER_URL", url);


            new Http().httpPost(url, String.valueOf(object), new Http.RequestCallback() {
                @Override
                public void onError(Throwable exception) {
                    Toast.makeText(context, "Error ! Check Your Internet Connection And Try again later", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onResponseReceived(HttpResponse httpResponse, String response) {

                    try {
                        String result = Http.ReadResponse(httpResponse);
                         result = "15";
                        if (!result.equals("") || !result.equals(null)) {
                            saved = true;
                            //Headerid = result;

                            if (!result.equals("") || !result.equals(null)) {


                                db_serial.deleteAll_HeaderID(HeaderIdOffline);
                                db_sync.deleteFromTransactionTenderOffline(HeaderIdOffline);
                                db_sync.deleteFromTransactionOffline(Integer.valueOf(HeaderIdOffline));
                                db_sync.deleteFromTransactionItemsOffline(HeaderIdOffline);

                             //   if(HeaderIdOffline.equals(String.valueOf(countAllheaderList)))
                                RenderUI(countAllheaderList,countAllheaderList,SyncType,context,activity,Headerid,HeaderIdOffline,TransactionType);


                            } else {
                                Toast.makeText(context, "try again", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            saved = false;
                            Toast.makeText(context, "Order Not Saved!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Order Not Saved!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            });


        }
    }


    public void RenderUI(int countofAllItems,int countAllItems,String SyncType,final Context context,final Activity activity,String HeaderID,String HeaderIdOffline,String TransactionType){
        Log.d("countofAllItems",String.valueOf(countofAllItems));
        Log.d("countAllItems",String.valueOf(countAllItems));

        if ((countofAllItems >= countAllItems) && SyncType.equals("New")) {
            if (activityopen == false) {
                Intent intent = new Intent(context, SyncBackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("HeaderID",HeaderIdOffline);
                context.startActivity(intent);
                activity.finish();
            }
            activityopen = true;

        } else if ((countofAllItems >= countAllItems) && SyncType.equals("Update")) {
            db_sync.UpdateifSyncBackOfflineIDFound(HeaderID, HeaderIdOffline, "Saved");
            db_sync.deleteFromTransactionOffline(Integer.valueOf(HeaderIdOffline));
            if (TransactionType.equals("1")) {
                db_sync.deleteFromTransactionTenderOffline(HeaderIdOffline);
            } else if (TransactionType.equals("2")) {
                db_sync.deleteFromTransactionSerialsOfflineByHeaderId(HeaderIdOffline);
            }
           db_sync.deleteFromTransactionItemsOffline(HeaderIdOffline);

            if (SyncType.equals("Update")) {
                ((SyncBackActivity) activity).RefreshList();
            }
        }
    }

    //not used at current solution
    public boolean SFA_InCompletedTransaction_Removed(final Context context, final String HeaderID) {
        String InCompletedURL = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_InCompletedTransaction_Removed/" + HeaderID;
        Log.d("REMOVE_URL", InCompletedURL);

        InCompletedURL = InCompletedURL.replaceAll(" ", "%20");

        StringRequest InCompletedRequest = new StringRequest(Request.Method.GET, InCompletedURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("SFA_InCompletedTransaction_RemovedResult");
                    Log.d("REMOVED RESULT ID = ", HeaderID);
                    if (result.equals("1")) {
                        TransactionInCompletedFlag = true;
                    } else {
                        TransactionInCompletedFlag = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    TransactionInCompletedFlag = false;
                }
                TransactionInCompletedFlag = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                TransactionInCompletedFlag = false;
            }
        });
        Volley.newRequestQueue(context).add(InCompletedRequest);
        return TransactionInCompletedFlag;
    }

}
