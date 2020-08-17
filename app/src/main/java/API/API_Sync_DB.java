package API;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Model.List_EndUserPrice;
import Model.Model;
import Model.Parsing_Json.SFA_GetItemDimensions_All;
import Model.Parsing_Json.SFA_GetPromotions;
import Model.Parsing_Json.SFA_GetPromotionsXQuery;
import Model.Parsing_Json.SFA_GetPromotionsXResult;
import Model.Parsing_Json.SFA_GetTruckType;
import Model.Parsing_Json.SFA_User_X_Subinventory;
import Model.Promotions;
import Model.Serial;
import Utility.Http;
import cz.msebera.android.httpclient.HttpResponse;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;
import Model.TruckType;
import Model.ProductDimension;
import Model.Subinventory;


public class API_Sync_DB {
    public SyncDBHelper db_sync;
    public SerialDBHelper db_serial;
    String Allow_Delivery_Method,AllowNegativeQty,Allow_StockTaking,Currency,GPSInterval,MobileVersion,ServerConfigID,UserID,MenuID,Customernumber,item_code_preOrder,item_code_order;
    boolean checklogin = true;
    boolean checkpermssion;
    boolean checkcustomer;
    boolean checkpreorder;
    boolean checkorder;
    boolean checkcategory;
    boolean checkbrand;
    boolean checkModel;
    boolean checkDeliveryMethod;
    boolean checkTenderType;
    boolean checkSalesReason;
    boolean checkEndUserPrice;
    boolean checkGetStockSerials;

    public boolean LoginAPI(final Activity activity, final Context context, final String DeviceID, final String email, final String password) {

        db_sync = new SyncDBHelper(context);

        String url_login = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Login/"
                + DeviceID + "/" + email
                + "/" + password;

        StringRequest loginRequest = new StringRequest(Request.Method.GET, url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("SFA_LoginResult");
                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);
                        AllowNegativeQty = current.getString("AllowNegativeQty");
                        Currency = current.getString("Currency");
                        GPSInterval = current.getString("GPSInterval");
                        MobileVersion = current.getString("MobileVersion");
                        ServerConfigID = current.getString("ServerConfigID");
                        UserID = current.getString("UserID");
                        Allow_Delivery_Method = current.getString("Allow_Delivery_Method");
                        Allow_StockTaking = current.getString("Allow_StockTaking");
                    }
                    if(UserID!=""){
                        checklogin = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                db_sync.InsertLoginOffline(UserID,ServerConfigID,Currency,AllowNegativeQty,GPSInterval,MobileVersion,Allow_Delivery_Method,Allow_StockTaking);
//                try {
//                    ((SyncActivity)activity).changeLoginImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checklogin = false;
            }
        });
        Volley.newRequestQueue(context).add(loginRequest);

        return checklogin;
    }

    public boolean PermissionAPI(final Activity activity,final Context context,final String UserID) {

        db_sync = new SyncDBHelper(context);
        String urlpermssion = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_UserPermissions/"
                +UserID;

        StringRequest PermssionRequest = new StringRequest(Request.Method.GET, urlpermssion, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("SFA_UserPermissionsResult");

                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);
                        MenuID = current.getString("MenuID");
                        String MenuName = current.getString("MenuName");
                        db_sync.InsertPermssionsOffline(MenuID,MenuName);
                        if(MenuID!=""){
                            checkpermssion = true;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                try {
//                    ((SyncActivity)activity).changePermssionImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkpermssion = false;
            }
        });
        Volley.newRequestQueue(context).add(PermssionRequest);

        return checkpermssion;
    }

    public boolean CustomerAPI(final Activity activity,final Context context,final String DeviceID) {

        db_sync = new SyncDBHelper(context);
        //db_sync.deleteAllCustomersOffline();
        String url_customer = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Customers/"+DeviceID;

        StringRequest customerRequest = new StringRequest(Request.Method.GET, url_customer, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("SFA_CustomersResult");

                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Customer_Number", current.getString("Customer_Number"));
                        contentValues.put("Customer_Name", current.getString("Customer_Name"));
                        contentValues.put("PRICE_LIST", current.getString("PRICE_LIST"));
                        contentValues.put("DueDateFrom", current.getString("DueDateFrom"));
                        contentValues.put("ScheuleID",  current.getString("ScheuleID"));
                        contentValues.put("VisitID", current.getString("VisitID"));

                        contentValues.put("CUSTOMER_CLASS", current.getString("CUSTOMER_CLASS"));
                        contentValues.put("CREATION_DATE", current.getString("CREATION_DATE"));
                        contentValues.put("CUSTOMER_CATEGORY", current.getString("CUSTOMER_CATEGORY"));
                        contentValues.put("PRIMARY_SALESREP_NAME", current.getString("PRIMARY_SALESREP_NAME"));
                        contentValues.put("PAYMENT_TERM", current.getString("PAYMENT_TERM"));
                        contentValues.put("CITY", current.getString("CITY"));
                        contentValues.put("COUNTRY", current.getString("COUNTRY"));
                        contentValues.put("CREDIT_LIMIT", current.getString("CREDIT_LIMIT"));
                        contentValues.put("OVER_DRAFT", current.getString("OVER_DRAFT"));
                        contentValues.put("REGION", current.getString("REGION"));
                        contentValues.put("AREA", current.getString("AREA"));


                        db_sync.InsertCustomersOffline(contentValues);
                        if(Customernumber!=""){
                            checkcustomer = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                try {
//                    ((SyncActivity)activity).changeCustomerImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkcustomer = false;
            }
        });
        Volley.newRequestQueue(context).add(customerRequest);

        return checkcustomer;
    }

    public boolean TransactionTypesAPI(final Activity activity, final Context context, String ServerConfigID){

        String Transaction = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetTransactionTypes/"+ServerConfigID;;
        StringRequest TenderRequest = new StringRequest(Request.Method.GET, Transaction, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetTransactionTypesResult");
                    for(int i=0;i<array.length();i++){
                        JSONObject TenderTyperesult = array.getJSONObject(i);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Id", TenderTyperesult.getString("id"));
                        contentValues.put("T_Type", TenderTyperesult.getString("T_Type"));
                        db_sync.InsertTransactionTypesOffline(contentValues);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkTenderType = true;
//                try {
//                    ((SyncActivity)activity).changeTenderTypeImagetoTrue();
//                }catch (Exception e){
//                   // e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkTenderType=false;
            }

        });
        Volley.newRequestQueue(context).add(TenderRequest);
        return checkTenderType;
    }

    public boolean CustomerWithVisitAPI(final Activity activity,final Context context, String ServerConfigID, String Period, String Username) {

        db_sync = new SyncDBHelper(context);

        String url_customer = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Customers_WithVisit/"
                + ServerConfigID+"/"+Period+"/"+Username;

        StringRequest customerRequest = new StringRequest(Request.Method.GET, url_customer, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("SFA_Customers_WithVisitResult");

                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);
                        String name = current.getString("Customer_Name");
                        Customernumber = current.getString("Customer_Number");
                        String price = current.getString("PRICE_LIST");
                        String DueDateFrom = current.getString("DueDateFrom");
                        String DueDateTo = current.getString("DueDateTo");
                        String ScheuleID = current.getString("ScheuleID");
                        String VisitID = current.getString("VisitID");
                        db_sync.InsertCustomersWithVisitOffline(Customernumber,name,price,DueDateFrom,DueDateTo,ScheuleID,VisitID);
                        if(Customernumber!=""){
                            checkcustomer = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                try {
//                    ((SyncActivity)activity).changeCustomerImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkcustomer = false;
            }
        });
        Volley.newRequestQueue(context).add(customerRequest);

        return checkcustomer;
    }

    public boolean PreOrderOffline(final Activity activity,final Context context,String ServerConfigID,String customerNumber,String Category,String Brand,String Model,String UserName){
        db_sync = new SyncDBHelper(context);
        String url_preOrder = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Items_All_Test/"+ServerConfigID+"/"+customerNumber+"/"+Category+"/"+Brand+"/"+Model+"/"+UserName+"/"+"1/0";

        StringRequest preorderRequest = new StringRequest(Request.Method.GET, url_preOrder, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Items_All_TestResult");

                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ITEM_CODE", current.getString("ITEM_CODE"));
                        contentValues.put("CAT", current.getString("CAT"));
                        contentValues.put("BRAND", current.getString("BRAND"));
                        contentValues.put("MODEL", current.getString("MODEL"));
                        contentValues.put("DESCRIPTION", current.getString("DESCRIPTION"));
                        contentValues.put("BrandImage", current.getString("BrandImage"));
                        contentValues.put("ONHAND", current.getString("ONHAND"));
                        contentValues.put("UnitPrice", current.getString("UnitPrice"));
                        contentValues.put("ACC", current.getString("ACC"));
                        contentValues.put("STATUS", current.getString("STATUS"));
                        contentValues.put("COLOR", current.getString("COLOR"));
                        contentValues.put("TAX_CODE", current.getString("TAX_CODE"));
                        contentValues.put("TAX_RATE", current.getString("TAX_RATE"));
                        contentValues.put("SEGMENT1", current.getString("SEGMENT1"));
                        contentValues.put("MAIN_CAT", current.getString("MAIN_CAT"));
                        contentValues.put("INVENTORY_ITEM_ID", current.getString("INVENTORY_ITEM_ID"));
                        contentValues.put("CREATION_DATE", current.getString("CREATION_DATE"));
                        contentValues.put("Subinventory", current.getString("Subinventory"));

                        db_sync.InsertPreOrderOffline(contentValues);
                       // db_sync.InsertPreOrderOffline(item_code_preOrder,category,brand,model,description,image,ONHAND,UnitPrice,Color);
                        if(item_code_preOrder!=""){
                            checkpreorder = true;
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkpreorder=false;
            }

        });
        Volley.newRequestQueue(context).add(preorderRequest);
        return checkpreorder;
    }

    public boolean StockTakenOffline(final Activity activity,final Context context,String ServerConfigID,String customerNumber,String Category,String Brand,String Model,String UserName){
        db_sync = new SyncDBHelper(context);
        String url_preOrder = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Ora_StockItems_All/"+ServerConfigID+"/"+customerNumber+"/"+Category+"/"+Brand+"/"+Model+"/"+UserName+"/"+"3";

        StringRequest preorderRequest = new StringRequest(Request.Method.GET, url_preOrder, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Ora_StockItems_AllResult");

                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ITEM_CODE", current.getString("ITEM_CODE"));
                        contentValues.put("CAT", current.getString("CAT"));
                        contentValues.put("BRAND", current.getString("BRAND"));
                        contentValues.put("MODEL", current.getString("MODEL"));
                        contentValues.put("DESCRIPTION", current.getString("DESCRIPTION"));
                        contentValues.put("BrandImage", current.getString("BrandImage"));
                        contentValues.put("ONHAND", current.getString("ONHAND"));
                        contentValues.put("UnitPrice", current.getString("UnitPrice"));
                        contentValues.put("ACC", current.getString("ACC"));
                        contentValues.put("STATUS", current.getString("STATUS"));
                        contentValues.put("COLOR", current.getString("COLOR"));
                        contentValues.put("TAX_CODE", current.getString("TAX_CODE"));
                        contentValues.put("TAX_RATE", current.getString("TAX_RATE"));
                        contentValues.put("SEGMENT1", current.getString("SEGMENT1"));
                        contentValues.put("MAIN_CAT", current.getString("MAIN_CAT"));
                        contentValues.put("INVENTORY_ITEM_ID", current.getString("INVENTORY_ITEM_ID"));
                        contentValues.put("CREATION_DATE", current.getString("CREATION_DATE"));
                        contentValues.put("Subinventory", current.getString("Subinventory"));

                        db_sync.InsertStockTakenItemOffline(contentValues);
                       // db_sync.InsertPreOrderOffline(item_code_preOrder,category,brand,model,description,image,ONHAND,UnitPrice,Color);
                        if(item_code_preOrder!=""){
                            checkpreorder = true;
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkpreorder=false;
            }

        });
        Volley.newRequestQueue(context).add(preorderRequest);
        return checkpreorder;
    }

    public boolean OrderOffline(final Activity activity, final Context context, String ServerConfigID, String customerNumber, String Category, String Brand, String Model, String UserName){
         db_sync = new SyncDBHelper(context);

        String url_Order = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Items_All_Test/"+ServerConfigID+"/"+customerNumber+"/"+Category+"/"+Brand+"/"+Model+"/"+UserName+"/"+"2/0";

        StringRequest orderRequest = new StringRequest(Request.Method.GET, url_Order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Items_All_TestResult");
                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ITEM_CODE", current.getString("ITEM_CODE"));
                        contentValues.put("CAT", current.getString("CAT"));
                        contentValues.put("BRAND", current.getString("BRAND"));
                        contentValues.put("MODEL", current.getString("MODEL"));
                        contentValues.put("DESCRIPTION", current.getString("DESCRIPTION"));
                        contentValues.put("BrandImage", current.getString("BrandImage"));
                        contentValues.put("ONHAND", current.getString("ONHAND"));
                        contentValues.put("UnitPrice", current.getString("UnitPrice"));
                        contentValues.put("ACC", current.getString("ACC"));
                        contentValues.put("STATUS", current.getString("STATUS"));
                        contentValues.put("COLOR", current.getString("COLOR"));
                        contentValues.put("TAX_CODE", current.getString("TAX_CODE"));
                        contentValues.put("TAX_RATE", current.getString("TAX_RATE"));
                        contentValues.put("SEGMENT1", current.getString("SEGMENT1"));
                        contentValues.put("MAIN_CAT", current.getString("MAIN_CAT"));
                        contentValues.put("INVENTORY_ITEM_ID", current.getString("INVENTORY_ITEM_ID"));
                        contentValues.put("CREATION_DATE", current.getString("CREATION_DATE"));
                        contentValues.put("Subinventory", current.getString("Subinventory"));

                        db_sync.InsertOrderOffline(contentValues);

                        //db_sync.InsertOrderOffline(item_code_order,category,brand,model,description,image,ONHAND,UnitPrice,Color);
                    }
                }catch (JSONException e){
                    //e.printStackTrace();
                    checkorder = false;
                }
                checkorder = true;
//                try {
//                    ((SyncActivity)activity).changeOrderImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkorder=false;
            }

        });
        Volley.newRequestQueue(context).add(orderRequest);
        return checkorder;
    }

    String Catresult;
    public boolean CategoryOffline(final Activity activity, final Context context, String ServerConfigID){
        db_sync = new SyncDBHelper(context);

        //db_sync.deleteAllOrderOffline();
        String urlCat = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Category/"+ServerConfigID;

        StringRequest categoryRequest = new StringRequest(Request.Method.GET, urlCat, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_CategoryResult");

                    for(int i=0;i<array.length();i++){
                        Catresult = array.get(i).toString();
                        db_sync.InsertCategoryOffline(Catresult);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                checkcategory = true;
//                try {
//                    ((SyncActivity)activity).changeCategoryImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkcategory=false;
            }

        });
        Volley.newRequestQueue(context).add(categoryRequest);
        return checkcategory;
    }

    public void SFA_User_X_Subinventory(final Context context,String Username) {
        String url;
        db_sync = new SyncDBHelper(context);
        url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_User_X_Subinventory/" + Username+"/1";
        url = url.replaceAll(" ", "%20");

        StringRequest CategoryRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                SFA_User_X_Subinventory  sfa_user_x_subinventory = new Gson().fromJson(response, SFA_User_X_Subinventory.class);
                for (Subinventory sub : sfa_user_x_subinventory.getSubinventory()) {
                    db_sync.InsertUser_X_Subinventory(sub);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "the Subinventory not available , Please check your network !", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(CategoryRequest);
    }

    String Brandresult;
    public boolean BrandOffline(final Activity activity, final Context context, String ServerConfigID){
        db_sync = new SyncDBHelper(context);

        String urlBrand = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Brand/"+ServerConfigID+"/"+"All";


        StringRequest BrandRequest = new StringRequest(Request.Method.GET, urlBrand, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_BrandResult");

                    for(int i=0;i<array.length();i++){
                        Brandresult = array.get(i).toString();

                        if(Brandresult.equals("")){
                            Brandresult = "All";
                        }
                       db_sync.InsertBrandOffline(Brandresult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkbrand = true;
//                try {
//                    ((SyncActivity)activity).changeBrandImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkbrand=false;
            }

        });
        Volley.newRequestQueue(context).add(BrandRequest);
        return checkbrand;
    }
    String Modelresult;
    public boolean ModelOffline(final Activity activity, final Context context, String ServerConfigID){
        db_sync = new SyncDBHelper(context);

        String urlModel = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Model/"+ServerConfigID+"/"+"All"+"/"+"All";
        StringRequest ModelRequest = new StringRequest(Request.Method.GET, urlModel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_ModelResult");

                    for(int i=0;i<array.length();i++){
                        Modelresult = array.get(i).toString();

                        if(Modelresult.equals("")){
                            Modelresult = "All";
                        }
                       db_sync.InsertModelOffline(Modelresult);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkModel = true;
//                try {
//                    ((SyncActivity)activity).changeModelImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkModel=false;
            }

        });
        Volley.newRequestQueue(context).add(ModelRequest);
        return checkModel;
    }

    String DeliveryMethodresult;
    public boolean DeliveryMethodOffline(final Activity activity, final Context context, String ServerConfigID){
        String urlDeliveryMethods;

        urlDeliveryMethods = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetDeliveryMethods/"+ServerConfigID;
        StringRequest DeliveryRequest = new StringRequest(Request.Method.GET, urlDeliveryMethods, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetDeliveryMethodsResult");

                    for(int i=0;i<array.length();i++){
                        DeliveryMethodresult = array.get(i).toString();
                        db_sync.InsertDeliveryMethodOffline(DeliveryMethodresult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkDeliveryMethod = true;
//                try {
//                    ((SyncActivity)activity).changeDeliveryMethodImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkDeliveryMethod=false;
            }

        });
        Volley.newRequestQueue(context).add(DeliveryRequest);
        return checkDeliveryMethod;
    }
    String TenderTyperesult;

    public boolean TenderTypeOffline(final Activity activity, final Context context, String ServerConfigID){
        String urlTender;

        urlTender = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetTenderTypes/"+ServerConfigID;
        StringRequest TenderRequest = new StringRequest(Request.Method.GET, urlTender, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetTenderTypesResult");

                    for(int i=0;i<array.length();i++){
                        TenderTyperesult = array.get(i).toString();
                        db_sync.InsertTenderTypeOffline(TenderTyperesult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkTenderType = true;
//                try {
//                    ((SyncActivity)activity).changeTenderTypeImagetoTrue();
//                }catch (Exception e){
//                   // e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkTenderType=false;
            }

        });
        Volley.newRequestQueue(context).add(TenderRequest);
        return checkTenderType;
    }

    String SalesReasonresult;
    public boolean SalesReasonOffline(final Activity activity, final Context context, String Language){
        String urlReason;

        urlReason = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Visit_WithoutSalesReasons/"+Language;

        StringRequest ReasonRequest = new StringRequest(Request.Method.GET, urlReason, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Visit_WithoutSalesReasonsResult");

                    for(int i=0;i<array.length();i++){
                        SalesReasonresult = array.get(i).toString();
                        db_sync.InsertSalesReasonOffline(SalesReasonresult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkSalesReason = true;
//                try {
//                    ((SyncActivity)activity).changeTenderTypeImagetoTrue();
//                }catch (Exception e){
//                   // e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkSalesReason=false;
            }

        });
        Volley.newRequestQueue(context).add(ReasonRequest);
        return checkSalesReason;
    }
 // Used on Sync Activity and it not active
    public boolean EndUserPriceOffline(final Activity activity, final Context context, String ServerConfigID, String UserName){
        db_sync = new SyncDBHelper(context);

        //------ Need to Change ----
        UserName = "Ogidan-Segun@rayacorp.com";
        String url_EndUserPrice = "http://rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_EndUserPrice/"+ServerConfigID+"/"+UserName;

        StringRequest EndUserPriceRequest = new StringRequest(Request.Method.GET, url_EndUserPrice, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_EndUserPriceResult");
                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);

                        String CustomerName = current.getString("CustomerName");
                        String CustomerNumber = current.getString("CustomerNumber");
                        String ItemCode = current.getString("ItemCode");
                        String Price = current.getString("Price");

                        db_sync.InsertEndUserPriceOffline(CustomerName,CustomerNumber,ItemCode,Price);
                    }
                }catch (JSONException e){
                    checkEndUserPrice = false;
                }
                checkEndUserPrice = true;
//                try {
//                    ((SyncActivity)activity).changeEndUserPriceImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkEndUserPrice=false;
            }

        });
        Volley.newRequestQueue(context).add(EndUserPriceRequest);
        return checkEndUserPrice;
    }

    public boolean SFA_Ora_PriceList(final Activity activity, final Context context, String ServerConfigID, String UserName){
        db_sync = new SyncDBHelper(context);

        String url_EndUserPrice = "http://rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Ora_PriceList/"+ServerConfigID+"/"+UserName;

        StringRequest EndUserPriceRequest = new StringRequest(Request.Method.GET, url_EndUserPrice, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Ora_PriceListResult");
                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);

                        String END_USER_PR = current.getString("END_USER_PR");
                        String ITEM_CODE = current.getString("ITEM_CODE");
                        String PRICE = current.getString("PRICE");
                        String PR_NAME = current.getString("PR_NAME");

                        db_sync.InsertOra_PriceListOffline(PR_NAME,ITEM_CODE,PRICE,END_USER_PR);
                    }
                }catch (JSONException e){
                    checkEndUserPrice = false;
                }
                checkEndUserPrice = true;
//                try {
//                    ((SyncActivity)activity).changeEndUserPriceImagetoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkEndUserPrice=false;
            }

        });
        Volley.newRequestQueue(context).add(EndUserPriceRequest);
        return checkEndUserPrice;
    }

    Serial serial;
    public boolean GetStockSerialsOffline(final Activity activity, final Context context,String UserName){
        db_sync = new SyncDBHelper(context);
        db_serial = new SerialDBHelper(context);
        String urlSerial;
        urlSerial = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetStockSerials/" + UserName;
        StringRequest SerialRequest = new StringRequest(Request.Method.GET, urlSerial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetStockSerialsResult");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String Box_number=current.getString("Box_number");
                        String SerialNumber = current.getString("SerialNumber");
                        String ItemCode = current.getString("ItemCode");
                        String Model = current.getString("Model");

                        if(db_serial.CheckSerialNumber(SerialNumber)){
                            serial = new Serial(ItemCode, Model, SerialNumber, Box_number,true);
                            serial.setSerialNumber(SerialNumber);
                            db_sync.InsertGetStockSerialsOffline(SerialNumber,ItemCode,Model,Box_number,"true");
                        }else{
                            serial = new Serial(ItemCode, Model, SerialNumber,Box_number, false);
                            serial.setSerialNumber(SerialNumber);
                            db_sync.InsertGetStockSerialsOffline(SerialNumber,ItemCode,Model,Box_number,"false");
                        }
                    }
                    checkGetStockSerials = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                    checkGetStockSerials = false;
                }

//                try {
//                    ((SyncActivity)activity).changeGetStockSerialstoTrue();
//                }catch (Exception e){
//                    //e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               error.printStackTrace();
                checkGetStockSerials=false;
            }

        });
        Volley.newRequestQueue(context).add(SerialRequest);
    return checkGetStockSerials;
    }


    public boolean SFA_GetPromotions(final Activity activity, final Context context,String ServerConfigID){
        db_sync = new SyncDBHelper(context);
        db_serial = new SerialDBHelper(context);
        String urlSerial;
        urlSerial = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetPromotions/" + ServerConfigID;
        StringRequest SerialRequest = new StringRequest(Request.Method.GET, urlSerial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



               try {
                   SFA_GetPromotions Promotions = new Gson().fromJson(response,SFA_GetPromotions.class);

                   for ( Promotions prom : Promotions.getPromotions()) {
                       ContentValues contentValues = new ContentValues();
                       contentValues.put("ActiveDate", prom.getActiveDate());
                       contentValues.put("PromoId", prom.getPromoId());
                       contentValues.put("Country", prom.getCountry());
                       contentValues.put("EndDate", prom.getEndDate());
                       contentValues.put("IsActive", prom.getIsActive());
                       contentValues.put("PromDesc", prom.getPromDesc());
                       contentValues.put("PromNote", prom.getPromNote());
                       contentValues.put("PromType", prom.getPromType());
                       contentValues.put("PromoName", prom.getPromoName());
                       contentValues.put("Server", prom.getServer());
                       contentValues.put("ServerConfigration", prom.getServerConfigration());
                       contentValues.put("CreatedDate", prom.getCreatedDate());

                        new SyncDBHelper(context).InsertPromotions(contentValues);
                   }

                       checkGetStockSerials = true;
                }catch (Exception e){
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkGetStockSerials=false;
            }

        });
        Volley.newRequestQueue(context).add(SerialRequest);
        return checkGetStockSerials;
    }

    public boolean SFA_GetPromotionsXQuery(final Activity activity, final Context context,String ServerConfigID){
        db_sync = new SyncDBHelper(context);
        db_serial = new SerialDBHelper(context);
        String urlSerial;
        urlSerial = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetPromotionsXQuery/" + ServerConfigID;
        StringRequest SerialRequest = new StringRequest(Request.Method.GET, urlSerial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    SFA_GetPromotionsXQuery Promotions = new Gson().fromJson(response,SFA_GetPromotionsXQuery.class);

                    for ( Promotions prom : Promotions.getSFA_GetPromotionsXQueryResult()) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("PromoId", prom.getPromoId());
                        contentValues.put("And_PromoQuery", prom.getAnd_PromoQuery());

                        new SyncDBHelper(context).InsertPromotionsXQuery(contentValues);

                    }

                    checkGetStockSerials = true;
                }catch (Exception e){
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkGetStockSerials=false;
            }

        });
        Volley.newRequestQueue(context).add(SerialRequest);
        return checkGetStockSerials;
    }

    public boolean SFA_GetPromotionsXResult(final Activity activity, final Context context,String ServerConfigID){
        db_sync = new SyncDBHelper(context);
        db_serial = new SerialDBHelper(context);
        String urlSerial;
        urlSerial = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetPromotionsXResult/" + ServerConfigID;
        StringRequest SerialRequest = new StringRequest(Request.Method.GET, urlSerial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    SFA_GetPromotionsXResult Promotions = new Gson().fromJson(response,SFA_GetPromotionsXResult.class);

                    for ( Promotions prom : Promotions.getPromotions()) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("PromoId", prom.getPromoId());
                        contentValues.put("PromoResult", prom.getPromoResult());

                        new SyncDBHelper(context).InsertPromotionsXResult(contentValues);
                    }

                    checkGetStockSerials = true;
                }catch (Exception e){
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                checkGetStockSerials=false;
            }

        });
        Volley.newRequestQueue(context).add(SerialRequest);
        return checkGetStockSerials;
    }

    public boolean SFA_GetTruckType(final Activity activity, final Context context) {
        String urlSerial;
        urlSerial = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetTruckType";

        urlSerial = urlSerial.replaceAll(" ", "%20");

        StringRequest TrRequest = new StringRequest(Request.Method.GET, urlSerial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SFA_GetTruckType  Trucks = new Gson().fromJson(response, SFA_GetTruckType.class);
                for (TruckType Truck:Trucks.getTruckType()) {
                    Truck.setProSize(Truck);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ID", Truck.getID());
                    contentValues.put("Height", Truck.getHeight());
                    contentValues.put("Type", Truck.getType());
                    contentValues.put("Weight", Truck.getWeight());
                    contentValues.put("length",Truck.getLength());
                    contentValues.put("size", String.valueOf(Truck.getSize()));
                    contentValues.put("width", Truck.getWidth());

                    new SyncDBHelper(context).InsertTruckTypeOffline(contentValues);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(TrRequest);
        return  true;
    }
    public boolean SFA_GetItemDimensions(final Activity activity, final Context context) {
        String url;
        url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetItemDimensions_All";

        new Http().httpGet(url,activity, new Http.RequestCallback() {
            @Override
            public void onError(Throwable exception) {
                Toast.makeText(context,"Error to get product dimensions",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseReceived(HttpResponse httpResponse, String response2) {

                String response = new Http().ReadResponse(httpResponse);
                SFA_GetItemDimensions_All productDimensions = new Gson().fromJson(response, SFA_GetItemDimensions_All.class);
                for (ProductDimension product:productDimensions.getProductDimensions()) {
                    product.setProSize(product);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("BRAND", product.getBRAND());
                    contentValues.put("DESCRIPTION", product.getDESCRIPTION());
                    contentValues.put("HEIGHT", product.getHEIGHT());
                    contentValues.put("INVENTORY_ITEM_ID", product.getINVENTORY_ITEM_ID());
                    contentValues.put("ITEMCODE", product.getITEMCODE());
                    contentValues.put("LENGTH", product.getLENGTH());
                    contentValues.put("WEIGHT", product.getWEIGHT());
                    contentValues.put("size",String.valueOf(product.getSize()));
                    contentValues.put("WIDTH", product.getWIDTH());
                    new SyncDBHelper(context).InsertItemDimensionsOffline(contentValues);

                }

            }
        });
       return  true;
    }

}