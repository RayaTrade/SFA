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





    public boolean SavePreOrderHeader(final int countAllItems, final Activity activity, final Context context, final String CountryCode, final String CustomerNumber, final String Submitter, final String Long, final String Lat, final String Comment, final String DeliveryMethod, final String TransactionType, final String HeaderIdOffline, final String SyncType) {
        db_sync = new SyncDBHelper(context);

        String Transaction_url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavePreOrderHeader/"
                + CountryCode + "/" + CustomerNumber + "/" + Submitter + "/" + Long + "/" + Lat + "/" + Comment + "/" + DeliveryMethod + "/" + TransactionType + "/" + HeaderIdOffline;
        Transaction_url = Transaction_url.replaceAll(" ", "%20");

        StringRequest SaveRequest = new StringRequest(Request.Method.GET, Transaction_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("SFA_SavePreOrderHeaderResult");
                    if (!result.equals("")) {
                        transactionTenderList = new ArrayList<>();
                        itemsList = new ArrayList<>();
                        transactionTenderList.addAll(db_sync.getAllTransactionTenderOffline(HeaderIdOffline));
                        itemsList.addAll(db_sync.getAllTransactionItemsOffline_SyncBack(String.valueOf(HeaderIdOffline)));

                        if (db_sync.checkifSyncBackOfflineIDFound(HeaderIdOffline) == true) { //if user click try again
                            //SFA_InCompletedTransaction_Removed(context,OldHeaderID);
                            db_sync.UpdateifSyncBackOfflineIDFound(result, HeaderIdOffline, "Wait..");
                            Log.d("UPDATED", result + "->" + HeaderIdOffline);
                        } else {
                            String Type = "";
                            if (TransactionType.equals("1"))
                                Type = "Pre-Order";
                            else if (TransactionType.equals("2"))
                                Type = "Order";

                            db_sync.InsertSyncBackOffline(HeaderIdOffline, result, Type, "Error", CustomerNumber); //normal sync back
                        }

                        if (TransactionType.equals("1")) {
                            for (TransactionTender t : transactionTenderList) {
                                TransactionTenderFlag = SFA_SavePreOrderTender(activity, context, HeaderIdOffline, result, t.getTenderType(), t.getSubmitter(), t.getTotal());
                            }
                        }

                        int countofitems = db_sync.getcountofItems(HeaderIdOffline);
                        for (Items i : itemsList) {
                            counter++;
                            TransactionItemsFlag = SavePreOrderDetails_Service(countAllItems, counter, TransactionType, activity, context, HeaderIdOffline, result, i.getItemCode(), i.getQty(), i.getUnitPrice(), i.getDiscount(), i.getSubmitter(), i.getQtyinStock(), countofitems, SyncType);
                            if (SyncType.equals("Update")) {
                                ((SyncBackActivity) activity).RefreshList();
                                ((SyncBackActivity) activity).StartProgressloop();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    TransactionFlag = false;
                }
                TransactionFlag = true;


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                try {
                    dialogSync.dismiss();
                } catch (Exception e) {

                }
                TransactionFlag = false;
                Toast.makeText(context, "Error ! Check Your Internet Connection And Try again later", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(context).add(SaveRequest);


        return TransactionFlag;
    }

    public boolean SFA_SavePreOrderTender(final Activity activity, final Context context, final String HeaderIdOffline, final String HeaderID, String TenderTypeID, String Submitter, String Total) {
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavePreOrderTender/"
                + HeaderID + "/" + TenderTypeID + "/" + Submitter + "/" + Total;

        url = url.replaceAll(" ", "%20");


        StringRequest SaveRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("SFA_SavePreOrderTenderResult");
                    if (result.equals("1")) {
                        TransactionTenderFlag = true;
                        // Toast.makeText(context,"Tender Type Send",Toast.LENGTH_SHORT).show();
                    } else {
                        TransactionTenderFlag = false;
                        Toast.makeText(context, "Tender Type Not Send", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    TransactionTenderFlag = false;
                    e.printStackTrace();
                }
                TransactionTenderFlag = true;
                db_sync.deleteFromTransactionTenderOffline(HeaderIdOffline);
                //Toast.makeText(context,String.valueOf(TransactionTenderFlag),Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TransactionTenderFlag = false;
                Toast.makeText(context, "Please try Again with good internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(context).add(SaveRequest);
        return TransactionTenderFlag;
    }

    public boolean SavePreOrderDetails_Service(final int countAllItems, final int countofAllItems, final String TransactionType, final Activity activity, final Context context, final String HeaderIdOffline, final String HeaderID, final String Item_code, String Qty, String UnitPrice, String Discount, String Submitter, String QtyinStock, final int countofOfflineItems, final String SyncType) {
        String Details_url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavePreOrderDetails/"
                + HeaderID + "/" + Item_code + "/" + Qty + "/" + UnitPrice + "/" + Discount + "/" + Submitter + "/" + QtyinStock;
        Log.d("countAllItemsloop", String.valueOf(countAllItems));

        Details_url = Details_url.replaceAll(" ", "%20");

        StringRequest DetailRequest = new StringRequest(Request.Method.GET, Details_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("SFA_SavePreOrderDetailsResult");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject current = array.getJSONObject(i);
                            String Key = current.getString("Key");
                            ItemId_Online = Key;
                            String Value = current.getString("Value");

                            if (TransactionType.equals("2")) {
                                for (Serial serial : db_sync.getAllTransactionSerialsOfflineByHeaderID_ItemCode(HeaderIdOffline, Item_code)) {
                                    SFA_SavePreOrderDetails_serials(countofAllItems,countAllItems,SyncType,activity,Value,countofOfflineItems,HeaderIdOffline,context, HeaderID, Key, serial.SerialNumber);
                                }
                            }

                            if (Value.equals(String.valueOf(countofOfflineItems))) {

                                if (TransactionType.equals("1")) {
                                    db_sync.UpdateifSyncBackOfflineIDFound(HeaderID, HeaderIdOffline, "Saved");
                                    db_sync.deleteFromTransactionOffline(Integer.valueOf(HeaderIdOffline));

                                    db_sync.deleteFromTransactionTenderOffline(HeaderIdOffline);
                                    db_sync.deleteFromTransactionItemsOffline(HeaderIdOffline);

                                } else if (TransactionType.equals("2")) {
                                    // not do any thing
                                }
                            }

                        }
                        TransactionItemsFlag = true;

                    }
                } catch (JSONException e) {
                    TransactionItemsFlag = false;
                    e.printStackTrace();
                }
                TransactionItemsFlag = true;

                RenderUI(countofAllItems,countAllItems,SyncType,context,activity,HeaderID,HeaderIdOffline,TransactionType);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TransactionItemsFlag = false;
                db_sync.UpdateifSyncBackOfflineIDFound(HeaderID, HeaderIdOffline, "Error");
                Toast.makeText(context, "Please Check list", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(context).add(DetailRequest);

        return TransactionItemsFlag;
    }

    String Serial_item_id = "";

    public void SFA_SavePreOrderDetails_serials(final int countofAllItems,final int countAllItems,final String SyncType,final Activity activity,final String Value,final int countofOfflineItems,final String HeaderIdOffline,final Context context, final String OrderHeaderid, final String DetailsID, final String Serials) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String SerialsURL = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavePreOrderDetails_serials/" + OrderHeaderid + "/"
                        + DetailsID + "/" + Serials;

                Log.d("SerialsURL", SerialsURL);
                SerialsURL = SerialsURL.replaceAll(" ", "%20");

                StringRequest SerialRequest = new StringRequest(Request.Method.GET, SerialsURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("SFA_SavePreOrderDetails_serialsResult");
                            if (result.equals("1")) {
                                if (Value.equals(String.valueOf(countofOfflineItems))) {
                                    db_sync.deleteFromTransactionSerialsOfflineByHeaderId(OrderHeaderid);
                                    db_sync.deleteFromTransactionItemsOffline(OrderHeaderid);
                                    db_sync.UpdateifSyncBackOfflineIDFound(OrderHeaderid, HeaderIdOffline, "Saved");
                                    db_sync.deleteFromTransactionOffline(Integer.valueOf(HeaderIdOffline));
                                }
                            } else {
                                Toast.makeText(context, "Order Serials Not Saved " + Serial_item_id, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RenderUI(countofAllItems,countAllItems,SyncType,context,activity,OrderHeaderid,HeaderIdOffline,"2");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Please try Again Order Serials not Save", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });
                Volley.newRequestQueue(context).add(SerialRequest);
            }
        }, 500);
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
