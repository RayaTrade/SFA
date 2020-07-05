package API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ahmed_hasanein.sfa.BuildConfig;
import com.example.ahmed_hasanein.sfa.CollectSerialActivity;
import com.example.ahmed_hasanein.sfa.CustomerActivity;
import com.example.ahmed_hasanein.sfa.DashboardActivity;
import com.example.ahmed_hasanein.sfa.FinishOnlineActivity;
import com.example.ahmed_hasanein.sfa.LoginActivity;
import com.example.ahmed_hasanein.sfa.MainActivity;
import com.example.ahmed_hasanein.sfa.SummaryActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Adapter.HistoryAdapter;
import Adapter.HistoryDetailAdapter;
import Model.Customer;
import Model.OrderReceipt;
import Model.Parsing_Json.SFA_GetTruckType;
import Model.Permission;
import Model.PreOrderHistory;
import Model.PreOrderHistoryDetail;
import Model.Product;
import Model.ProductDimension;
import Model.Serial;
import Model.TruckType;
import Model.User;
import Utility.DialogHint;
import Utility.Http;
import cz.msebera.android.httpclient.HttpResponse;
import gps_tracking.LocationMonitoringService;
import preview_database.DB.DealerDB.DealerDBHelper;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.ProductPreOrderDB.ProductDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;
import settings.Constants;

public class API_Online extends LoginActivity{
    static String Headerid;
    boolean saved = false;
    List<Product> productList;
    List<Customer> customerList;
    List<PreOrderHistory> preOrderHistoryList;
    List<PreOrderHistoryDetail> preOrderHistoryDetailList;
    Customer customer;
    private ProductDBHelper db;
    private OrderDBHelper db_order;
    private SerialDBHelper db_serial;
    private SyncDBHelper db_sync;
    private DealerDBHelper db_dealer;
    private HistoryAdapter historyAdapter;
    private HistoryDetailAdapter historyDetailAdapter;
    static boolean HistoryCancel = false;

    public void SFA_Login(final ProgressDialog dialogLogin, final Context context, final Activity activity, final String email, final String password, final Button LoginBtn) {
        final SharedPreferences.Editor editor;
        SharedPreferences prefs;
        prefs = context.getSharedPreferences("login_data", MODE_PRIVATE);
/*
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Login/"
                + "356027082361034" + "/" + email
                + "/" + password;

*/

        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Login/"
                + User.DeviceID + "/" + email
                + "/" + password;


        url = url.replaceAll(" ", "%20");
        Log.d("LOGIN_URL", url);

        //save login data in shared preferances
        emailpref = email;
        DeviceIDpref = User.DeviceID;
        passwordpref= password;
        editor = prefs.edit();
        editor.putString("My_email", email);
        editor.putString("My_password", password);
        editor.putString("My_DeviceID", User.DeviceID);
      /*  try{
           editor.putString("Logs", new LogCat().CreateFile(activity,String.valueOf(email+"_"+DeviceIDpref+"_"+new Date().getDate())));
        }
        catch (Exception e)
        {}*/



        StringRequest loginRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("SFA_LoginResult");
//                    JSONArray array = jsonObject.getJSONArray("SFA_Login_TestResult");


                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject current = array.getJSONObject(i);
                            User.AllowNegativeQty = current.getString("AllowNegativeQty");
                            editor.putString("My_AllowNegativeQty", User.AllowNegativeQty);

                            User.Currency = current.getString("Currency");
                            editor.putString("My_Currency", User.Currency);

                            User.ServerConfigID = current.getString("ServerConfigID");
                            editor.putString("My_ServerConfigID", User.ServerConfigID);
                            ServerConfigIDpref= User.ServerConfigID;
                            User.UserID = current.getString("UserID");
                            editor.putString("My_UserID", User.UserID);
                            userIDpref=User.UserID;
                            User.GPS_StartTime = current.getString("GPS_StartTime");
                            editor.putString("GPS_StartTime", User.GPS_StartTime);

                            User.GPS_EndTime = current.getString("GPS_EndTime");
                            editor.putString("GPS_EndTime", User.GPS_EndTime);

                            User.GPS_Distance = current.getString("GPS_Distance");
                            editor.putString("GPS_Distance", User.GPS_Distance);

                            User.FTP_Path =  current.getString("FTP_Path");
                            User.Images_Path =  current.getString("Images_Path");
                            User.Allow_Delivery_Method =  Boolean.valueOf(current.getString("Allow_Delivery_Method"));
                            /*
                             * # change date 25/8/2019
                             * © changed by Ahmed Ali
                             * -- description: .........
                             *   User.MaxWaitTime = Integer.parseInt(current.getString("MaxWaitTime"));
                             *   User.FASTEST_LOCATION_INTERVAL = Integer.parseInt(current.getString("FASTEST_LOCATION_INTERVAL"));
                             */
                            User.GPSInterval = Integer.parseInt(current.getString("GPSInterval"));
                            User.MaxWaitTime = Integer.parseInt(current.getString("MaxWaitTime"));
                            User.FASTEST_LOCATION_INTERVAL = Integer.parseInt(current.getString("FASTEST_LOCATION_INTERVAL"));
                            User.MobileVersion = current.getString("MobileVersion");

                        }

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();

                        editor.putString("My_LastLoginDate", dateFormat.format(date));
                        editor.apply();
                        Toast.makeText(context, "Getting Permissions...", Toast.LENGTH_SHORT).show();

                        Intent myService = new Intent(context, LocationMonitoringService.class);

                        //restart my service with new GPS Interval
                        boolean isMyServiceRunning = ((LoginActivity) activity).isMyServiceRunning(LocationMonitoringService.class);
                        if (isMyServiceRunning) {
                            context.stopService(myService);
                            /*
                             * # change date 25/8/2019
                             * © changed by Ahmed Ali
                             * -- description: .........
                             *  Constants.LOCATION_INTERVAL = User.GPSInterval;
                             *  Constants.FASTEST_LOCATION_INTERVAL = User.FASTEST_LOCATION_INTERVAL;
                             *  Constants.MaxWaitTime = User.MaxWaitTime;
                             */
                            Constants.LOCATION_INTERVAL = User.GPSInterval;
                            Constants.FASTEST_LOCATION_INTERVAL = User.FASTEST_LOCATION_INTERVAL;
                            Constants.MaxWaitTime = User.MaxWaitTime;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(myService);
                            } else {
                                context.startService(myService);
                            }
                        } else {
                            Constants.LOCATION_INTERVAL = User.GPSInterval;
                            Constants.FASTEST_LOCATION_INTERVAL = User.FASTEST_LOCATION_INTERVAL;
                            Constants.MaxWaitTime = User.MaxWaitTime;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(myService);
                            } else {
                                context.startService(myService);
                            }
                        }

                        //get permssion of this user
                        GetPermssionsOnline(User.UserID, context, activity, dialogLogin, email);
                        LoginBtn.setVisibility(View.GONE);

                    } else {
                        dialogLogin.dismiss();
                        Toast.makeText(context, "user not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    dialogLogin.dismiss();
                    e.printStackTrace();


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogLogin.dismiss();
                new DialogHint().showNetworkDialog(activity);
                error.printStackTrace();
                Toast.makeText(context, "login Error ! Check Your Internet Connection And Try again later", Toast.LENGTH_LONG).show();
            }
        });
        Volley.newRequestQueue(context).add(loginRequest);

    }


    public boolean GetPermssionsOnline(String UserID, final Context context, final Activity activity, final ProgressDialog dialogPermssion, final String email) {
        final List<Permission> permissionList = new ArrayList<>();
        final SyncDBHelper db_sync;
        db_sync = new SyncDBHelper(context);

        String urlpermssion = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_UserPermissions/"
                + UserID;
        urlpermssion = urlpermssion.replaceAll(" ", "%20");

        StringRequest PermssionRequest = new StringRequest(Request.Method.GET, urlpermssion, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("SFA_UserPermissionsResult");

                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject current = array.getJSONObject(i);
                            String MenuID = current.getString("MenuID");
                            String MenuName = current.getString("MenuName");
                            Permission permission = new Permission(MenuID, MenuName);
                            permissionList.add(permission);
                            List<String> permissionMenuNames = new ArrayList<>();
                            for (Permission p : permissionList) {
                                permissionMenuNames.add(p.getMenuName());
                            }
                            for (String p : permissionMenuNames) {
                                if (p.equals("PreOrder")) {
                                    preOrderPermission = true;
                                }
                                if (p.equals("History")) {
                                    historyPermission = true;
                                }
                                if (p.equals("Order")) {
                                    OrderPermission = true;
                                }
                            }
                        }
                        if (!User.MobileVersion.equals(BuildConfig.VERSION_NAME)) {
                            ((LoginActivity) activity).showUpdateDialog();
                        } else {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = new Date();
                            if ((!LastLoginDatepref.equals(dateFormat.format(date))) || (db_sync.checkifLoginOfflineisEmpty() == true
                                    || db_sync.checkifPermssionOfflineisEmpty() == true || db_sync.checkifCustomerOfflineisEmpty() == true
                                    || db_sync.checkifSalesReasonOfflineEmpty() == true
                                    || db_sync.checkifCategoryOfflineEmpty() == true || db_sync.checkifModelOfflineEmpty() == true
                                    || db_sync.checkifBrandOfflineEmpty() == true || db_sync.checkifPreorderOfflineisEmpty() == true
                                    || db_sync.checkifTenderTypeOfflineEmpty() == true || db_sync.checkifDeliveryMethodOfflineEmpty() == true)) {

                                //start sync with offline database///////////////////////////////////////

                                try {

                                    dialogPermssion.dismiss();
                                    DialogHint dialogHint = new DialogHint();
                                    dialogHint.Sync_Dialog(context,activity,LastLoginDatepref,User.DeviceID,preOrderPermission,OrderPermission,historyPermission,email);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //////////////////////////////////////////////////////////////////////////
                            } else {
                                Intent intent = new Intent(context, DashboardActivity.class);
                                intent.putExtra("lastsyncdate", LastLoginDatepref);
                                intent.putExtra("DeviceID", User.DeviceID);
                                intent.putExtra("preOrderPermission", preOrderPermission);
                                intent.putExtra("OrderPermission", OrderPermission);
                                intent.putExtra("historyPermission", historyPermission);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                User.Username = email;
                                dialogPermssion.dismiss();
                                context.startActivity(intent);
                                activity.finish();
                            }

                        }
                    } else {
                        Toast.makeText(context, "user not have any permissions", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogHint().showNetworkDialog(activity);
                Toast.makeText(context, "Permission Error ! Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(context).add(PermssionRequest);
        return preOrderPermission;
    }


    List<String> categoryList;

    public void SFA_Category(final LinearLayout LayoutWaitingMainList, final Spinner spinner, final Context context) {
        String urlCat;

        urlCat = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Category/" + User.ServerConfigID;
        urlCat = urlCat.replaceAll(" ", "%20");

        StringRequest CategoryRequest = new StringRequest(Request.Method.GET, urlCat, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_CategoryResult");
                    categoryList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        if (result.equals("")) {
                            result = "All";
                        }
                        categoryList.add(result);
                        LayoutWaitingMainList.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categoryList));
                } catch (Exception e) {
                    Toast.makeText(context, "this category not available", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "this category not available , Please check your network !", Toast.LENGTH_LONG).show();
                LayoutWaitingMainList.setVisibility(View.GONE);
            }

        });
        Volley.newRequestQueue(context).add(CategoryRequest);
    }

    List<String> BrandList;

    public void SFA_Brand(final Spinner spinner, final Context context, String Category) {
        if (Category == null) {
            Category = "All";
        }

        try {
            Category = URLEncoder.encode(Category, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(context, "can not user this category now !", Toast.LENGTH_SHORT).show();
        }

        String urlBrand = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Brand/" + User.ServerConfigID + "/" + Category;

        urlBrand = urlBrand.replaceAll(" ", "%20");

        StringRequest BrandRequest = new StringRequest(Request.Method.GET, urlBrand, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_BrandResult");
                    BrandList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        if (result.equals("")) {
                            result = "All";
                        }
                        BrandList.add(result);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!BrandList.contains("All")) {
                    BrandList.add(0, "All");
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BrandList));
                } catch (Exception e) {
                    Toast.makeText(context, "this brand not available", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "this brand not available , Please check your network !", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(BrandRequest);
    }

    List<String> modelList;

    public void SFA_Model(final Spinner spinner, final Context context, String Category, String Brand) {
        if (Category == null) {
            Category = "All";
        }
        if (Brand == null) {
            Brand = "All";
        }
        try {
            Category = URLEncoder.encode(Category, "UTF-8");
            Brand = URLEncoder.encode(Brand, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(context, "can not user this category or brand now !", Toast.LENGTH_SHORT).show();
        }

        String urlModel = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Model/" + User.ServerConfigID + "/" + Category + "/" + Brand;

        urlModel = urlModel.replaceAll(" ", "%20");

        StringRequest ModelRequest = new StringRequest(Request.Method.GET, urlModel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_ModelResult");
                    modelList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        if (result.equals("")) {
                            result = "All";
                        }
                        modelList.add(result);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!modelList.contains("All")) {
                    modelList.add(0, "All");
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, modelList));
                } catch (Exception e) {
                    Toast.makeText(context, "this model not available", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "this model not available , Please check your network !", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(ModelRequest);
    }

    List<String> DeliveryMethodList;

    public boolean SFA_GetDeliveryMethods(final Spinner spinner, final Context context, final Activity activity) {
        String urlDeliveryMethods;


        urlDeliveryMethods = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetDeliveryMethods/" + User.ServerConfigID;
        urlDeliveryMethods = urlDeliveryMethods.replaceAll(" ", "%20");
/*
        new Http().httpGet(urlDeliveryMethods, activity, new Http.RequestCallback() {
            @Override
            public void onError(Throwable exception) {

            }

            @Override
            public void onResponseReceived(HttpResponse httpResponse, String response2) {
                try {
                    String response = Http.ReadResponse(httpResponse);

                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetDeliveryMethodsResult");
                    DeliveryMethodList = new ArrayList<>();
                    Log.v("Delivery_Method",array.toString());
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        DeliveryMethodList.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DeliveryMethodList));
                    ((SummaryActivity) activity).RemoveLoadingLayout_EnableButton();
                    Log.v("Delivery Method",DeliveryMethodList.toString());
                } catch (Exception e) {
                    Toast.makeText(context, "Delivery Method not available", Toast.LENGTH_LONG).show();
                }
            }
        });
        */
        final StringRequest DeliveryRequest = new StringRequest(Request.Method.GET, urlDeliveryMethods, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetDeliveryMethodsResult");
                    DeliveryMethodList = new ArrayList<>();
                    Log.v("Delivery_Method",array.toString());
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        DeliveryMethodList.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DeliveryMethodList));
                    ((SummaryActivity) activity).RemoveLoadingLayout_EnableButton();
                    Log.v("Delivery Method",DeliveryMethodList.toString());
                } catch (Exception e) {
                    Toast.makeText(context, "Delivery Method not available", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //DropDownListDialogSummaryActivity.dismiss();
                Toast.makeText(context, "Delivery Method not available , Please check your network !", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(DeliveryRequest);

        if (spinner.getCount() < 1) {
            return false;
        } else {
            return true;
        }
    }

    List<String> reasonList;

    public boolean SFA_Visit_WithoutSalesReasons(final Spinner spinner, final Context context, final Activity activity, final String Language) {
        String urlReason;

        urlReason = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Visit_WithoutSalesReasons/" + Language;
        urlReason = urlReason.replaceAll(" ", "%20");


        StringRequest ReasonRequest = new StringRequest(Request.Method.GET, urlReason, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Visit_WithoutSalesReasonsResult");
                    reasonList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        if (result.equals("")) {
                            result = "All";
                        }
                        reasonList.add(result);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, reasonList));
                    ((CustomerActivity) activity).RemoveLoadingLayout_EnableButton();
                } catch (Exception e) {
                    Toast.makeText(context, "this reason not available", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "this reason not available , Please check your network !", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(ReasonRequest);
        if (spinner.getCount() < 1) {
            return false;
        } else {
            return true;
        }
    }

    List<String> TenderList;

    public boolean SFA_GetTenderTypes(final Spinner spinner, final Context context, final Activity activity) {
        String urlTender;

        urlTender = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetTenderTypes/" + User.ServerConfigID;

        urlTender = urlTender.replaceAll(" ", "%20");
/*
        new Http().httpGet(urlTender, activity, new Http.RequestCallback() {
            @Override
            public void onError(Throwable exception) {

            }

            @Override
            public void onResponseReceived(HttpResponse httpResponse, String response2) {
                try {
                    String response = Http.ReadResponse(httpResponse);

                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetTenderTypesResult");
                    Log.v("TenderTypes_Method",array.toString());

                    TenderList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        TenderList.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, TenderList));
                    ((SummaryActivity) activity).RemoveLoadingLayout_EnableButton();
                    Log.v("Tender Type Method",TenderList.toString());

                } catch (Exception e) {
                    Toast.makeText(context, "Tender Type not available", Toast.LENGTH_LONG).show();
                }
            }
        });*/
        StringRequest TenderRequest = new StringRequest(Request.Method.GET, urlTender, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetTenderTypesResult");
                    Log.v("TenderTypes_Method",array.toString());

                    TenderList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String result = array.get(i).toString();
                        TenderList.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //Setting adapter to show the items in the spinner
                    spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, TenderList));
                    ((SummaryActivity) activity).RemoveLoadingLayout_EnableButton();
                    Log.v("Tender Type Method",TenderList.toString());

                } catch (Exception e) {
                    Toast.makeText(context, "Tender Type not available", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Tender Type not available , Please check your network !", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(TenderRequest);
        if (spinner.getCount() < 1) {
            return false;
        } else {
            return true;
        }
    }

    public void SFA_Items_All(final Context context, final Activity activity, String Category, final String customerNumber, String Brand, String Model, String TransactionType) {
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Items_All/" + User.ServerConfigID + "/" + customerNumber + "/" + Category + "/" + Brand + "/" + Model + "/" + User.Username + "/" + TransactionType;

        url = url.replaceAll(" ", "%20");
        Log.d("PRODUCT_URL", url);


        StringRequest productRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Items_AllResult");
                    productList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String item_code = current.getString("ITEM_CODE");
                        String category = current.getString("CAT");
                        String brand = current.getString("BRAND");
                        String model = current.getString("MODEL");
                        String description = current.getString("DESCRIPTION");
                        String image = current.getString("BrandImage");
                        String ONHAND = current.getString("ONHAND");
                        String UnitPrice = current.getString("UnitPrice");
                        String Color = current.getString("COLOR");

                        String HEIGHT = current.getString("HEIGHT");
                        String WEIGHT = current.getString("WEIGHT");
                        String LENGTH = current.getString("LENGTH");
                        String WIDTH = current.getString("WIDTH");

                        Product product = new Product(item_code, category, brand, model, "", description, image, Float.valueOf(UnitPrice), ONHAND, Color, customerNumber, "", "",LENGTH,WEIGHT,WIDTH,HEIGHT);
                        productList.add(product);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((MainActivity) activity).RenderList(productList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                new DialogHint().showNetworkDialog(activity);
                Toast.makeText(context, "Get Products Error ! Try Again", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(productRequest);

     /*
     new Http().httpGet(url, activity, new Http.RequestCallback() {
         @Override
         public void onError(Throwable exception) {
           //  error.printStackTrace();
             new DialogHint().showNetworkDialog(activity);
             Toast.makeText(context, "Get Products Error ! Try Again", Toast.LENGTH_LONG).show();
         }

         @Override
         public void onResponseReceived(HttpResponse httpResponse, String respons) {

             try {
                 String response = Http.ReadResponse(httpResponse);

                 JSONObject object = new JSONObject(response);
                 JSONArray array = object.getJSONArray("SFA_Items_AllResult");
                 productList = new ArrayList<>();
                 for (int i = 0; i < array.length(); i++) {
                     JSONObject current = array.getJSONObject(i);
                     String item_code = current.getString("ITEM_CODE");
                     String category = current.getString("CAT");
                     String brand = current.getString("BRAND");
                     String model = current.getString("MODEL");
                     String description = current.getString("DESCRIPTION");
                     String image = current.getString("BrandImage");
                     String ONHAND = current.getString("ONHAND");
                     String UnitPrice = current.getString("UnitPrice");
                     String Color = current.getString("COLOR");

                     String HEIGHT = current.getString("HEIGHT");
                     String WEIGHT = current.getString("WEIGHT");
                     String LENGTH = current.getString("LENGTH");
                     String WIDTH = current.getString("WIDTH");

                     Product product = new Product(item_code, category, brand, model, "", description, image, Float.valueOf(UnitPrice), ONHAND, Color, customerNumber, "", "",LENGTH,WEIGHT,WIDTH,HEIGHT);
                     productList.add(product);
                 }
             } catch (JSONException e) {
                 e.printStackTrace();
             }
             ((MainActivity) activity).RenderList(productList);

         }
     });
   */
    }

    public void SFA_Customers(final RecyclerView recyclerView, final ProgressBar progressBar, final Context context, final Activity activity) {
        customerList = new ArrayList<>();
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Customers/" + User.DeviceID;
        url = url.replaceAll(" ", "%20");


        StringRequest customerRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_CustomersResult");
                    customerList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String name = current.getString("Customer_Name");
                        String number = current.getString("Customer_Number");
                        String price = current.getString("PRICE_LIST");
                        String DueDateFrom = current.getString("DueDateFrom");
                        String DueDateTo = current.getString("DueDateTo");
                        String ScheuleID = current.getString("ScheuleID");
                        String VisitID = current.getString("VisitID");
                        customer = new Customer(number, name, price, DueDateFrom, DueDateTo, ScheuleID, VisitID);
                        customer.setNumber(number);
                        customerList.add(customer);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((CustomerActivity) activity).RenderList(customerList, false, recyclerView);
                ((CustomerActivity) activity).ChangeTotalVisitTxt(String.valueOf(customerList.size()));
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //new DialogHint().showNetworkDialog(activity);
                Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(customerRequest);
    }

    public void SFA_Customers_Filter(final RecyclerView recyclerView, final ProgressBar progressBar, final Context context, final Activity activity,String Customername) {
        customerList = new ArrayList<>();
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Customers_Filter/" + User.DeviceID+"/"+Customername;
        url = url.replaceAll(" ", "%20");


        StringRequest customerRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Customers_FilterResult");
                    customerList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String name = current.getString("Customer_Name");
                        String number = current.getString("Customer_Number");
                        String price = current.getString("PRICE_LIST");
                        String DueDateFrom = current.getString("DueDateFrom");
                        String DueDateTo = current.getString("DueDateTo");
                        String ScheuleID = current.getString("ScheuleID");
                        String VisitID = current.getString("VisitID");
                        customer = new Customer(number, name, price, DueDateFrom, DueDateTo, ScheuleID, VisitID);
                        customer.setNumber(number);
                        customerList.add(customer);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((CustomerActivity) activity).RenderList(customerList, false, recyclerView);
                ((CustomerActivity) activity).ChangeTotalVisitTxt(String.valueOf(customerList.size()));
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //new DialogHint().showNetworkDialog(activity);
                Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(customerRequest);
    }

    public ProgressDialog dialogCustomersWithVisit;

    // need to modify by deserialize
    public void GetCustomersWithVisit(final RecyclerView recyclerView, final ProgressBar progressBar, final Context context, final Activity activity, String ServerConfigID, String Period, String Username) {
        customerList = new ArrayList<>();
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Customers_WithVisit/"
                + ServerConfigID + "/" + Period + "/" + Username;

        dialogCustomersWithVisit = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);

        url = url.replaceAll(" ", "%20");


        StringRequest customerRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Customers_WithVisitResult");
                    customerList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String name = current.getString("Customer_Name");
                        String number = current.getString("Customer_Number");
                        String price = current.getString("PRICE_LIST");
                        String DueDateFrom = current.getString("DueDateFrom");
                        String DueDateTo = current.getString("DueDateTo");
                        String ScheuleID = current.getString("ScheuleID");
                        String VisitID = current.getString("VisitID");
                        customer = new Customer(number, name, price, DueDateFrom, DueDateTo, ScheuleID, VisitID);
                        customer.setNumber(number);
                        customerList.add(customer);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "click again", Toast.LENGTH_LONG).show();
                }
                ((CustomerActivity) activity).RenderList(customerList, true, recyclerView);
                ((CustomerActivity) activity).ChangeTotalVisitTxt(String.valueOf(customerList.size()));
                dialogCustomersWithVisit.dismiss();
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
                dialogCustomersWithVisit.dismiss();
                new DialogHint().showNetworkDialog(activity);
            }

        });
        Volley.newRequestQueue(context).add(customerRequest);
    }


    //---------------------------

    /* # change date 14/11/2019
     * © changed by Ahmed Ali
     * -- description: .........
     *   Convert_Array_to_Json_to (Function)
     *   SavePreOrderHeader (Function Updated)
     *   SavePreOrderDetails_Service_Bulk(Function)
     */
    String Key="",count_of_online_items="";

    private JSONArray Convert_Array_to_Json_to (String HeaderId,Context context,List<Product> productList)
    {
        JSONArray array = new JSONArray();
        ArrayList<String> Serial_list = new ArrayList<>();

        CollectSerialActivity activity = new CollectSerialActivity();

       // adding productList
        for (Product product : productList) {
            JSONObject object = new JSONObject();
            try {

                        object.put("SKU",product.SKU );
                        object.put("QTY" , product.QTY);
                        object.put("QtyinStock",product.OnHand);
                        object.put("Discount","0");
                        object.put("UnitPrice" , product.UnitPrice);
                try {
                    Serial_list = ((CollectSerialActivity) activity).List_getSerialsByItemCode(HeaderId,context,product.SKU);
                        Log.d("serials  ", serials + "product.getSKU  " + product.SKU);
                    }catch (Exception e){
                    serials = "";
                    }


                    object.put("Serial", new JSONArray(Serial_list));


                array.put(object);
            }catch (JSONException e) {
                e.printStackTrace();
                Log.d("serials  ", serials + "product.getSKU  " + product.getSKU());

            }


        }


        return array;
    }

    private  Float Calcualte_Amount(List<Product> productList)
    {
        float total = 0;
        for (Product product : productList) {
            total += Float.valueOf(product.Total);
        }
        return total;
    }

    public void SFA_SavaOrder_OneBulk(final Context context, final Activity activity, final ProgressDialog dialogSave, final String OpenFrom, String CountryCode, final String CustomerName , String CustomerNumber, final String Submitter, String Long, String Lat, String Comment, final String DeliveryMethod, final String TenderType, String TransactionType, String OfflineID) {

        if(Submitter == null || Submitter == "")
        {
            dialogSave.dismiss();
            new DialogHint().Session_End(activity,context);
        }
            else {
            productList = new ArrayList<>();
            //fill productlist from DB (select item from user)
            if (OpenFrom.equals("preorder")) {
                db = new ProductDBHelper(context);
                productList.addAll(db.getAllProduct()); //preorder (product.db)
            } else if (OpenFrom.equals("order")) {
                db_order = new OrderDBHelper(context);
                db_serial = new SerialDBHelper(context);
                productList.addAll(db_order.getAllOrder());
            } else if (OpenFrom.equals("dealer")) {
                db_dealer = new DealerDBHelper(context);
                productList.addAll(db_dealer.getAllDealerOrder());
            }

            JSONObject object = new JSONObject();

            try {
                object.accumulate("ServerConfigID", CountryCode);
                object.accumulate("CustomerNumber", CustomerNumber);
                object.accumulate("Submitter", Submitter);
                object.accumulate("Long", Long);
                object.accumulate("Lat", Lat);
                object.accumulate("Comments", Comment);
                object.accumulate("DeliveryMethod", DeliveryMethod);
                object.accumulate("TransactionType", TransactionType);
                object.accumulate("OfflineID", OfflineID);
                object.accumulate("HeaderID", "");
                object.accumulate("Product_List", Convert_Array_to_Json_to("0",context,productList));
                object.accumulate("OpenFrom", OpenFrom);
                object.accumulate("TenderType", TenderType);
                object.accumulate("Amount", String.valueOf(Calcualte_Amount(productList)));
                object.accumulate("PromoID", 0);
                JSONArray jsArray = new JSONArray();
                for (int i = 0; i < Trucks.getTruckType().size(); i++) {
                    if(Trucks.getTruckType().get(i).getCount() > 0)
                    {
                        String jsonInString = new Gson().toJson(Trucks.getTruckType().get(i));
                        JSONObject mJSONObject = new JSONObject(jsonInString);
                        jsArray.put(mJSONObject);
                    }
                }
                object.accumulate("Trucks",jsArray );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavaOrder_OneBulk";

        //    String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavaOrder_OneBulk_PromotionTest";
            Log.d("HEADER_URL", url);

            new Http().httpPost(url, String.valueOf(object), new Http.RequestCallback() {
                @Override
                public void onError(Throwable exception) {
                    dialogSave.dismiss();
                    Toast.makeText(context, "Error ! Check Your Internet Connection And Try again later", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponseReceived(HttpResponse httpResponse, String response) {

                    try {
                        String result = Http.ReadResponse(httpResponse);

                        if (!result.equals("") || !result.equals(null)) {

                            OrderReceipt orderReceipt = new Gson().fromJson(result,OrderReceipt.class);
                            orderReceipt.setCustomername(CustomerName);
                            orderReceipt.setSales_name(Submitter);
                            saved = true;

                            Intent i = new Intent(context, FinishOnlineActivity.class);
                            if (result != null) {

                                new OrderReceipt().setOrderReceipt(orderReceipt);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialogSave.dismiss();

                                if (OpenFrom.equals("preorder")) {
                                    db.deleteAll();
                                } else if (OpenFrom.equals("order")) {
                                    db_order.deleteAll();
                                    db_serial.deleteAll();
                                } else if (OpenFrom.equals("dealer")) {
                                    db_dealer.deleteAllDealerOrder();
                                }

                                context.startActivity(i);
                                activity.finish();



                            } else {
                                dialogSave.dismiss();
                                Toast.makeText(context, "try again", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            saved = false;
                            dialogSave.dismiss();
                            Toast.makeText(context, "Order Not Saved!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        dialogSave.dismiss();
                        Toast.makeText(context, "Order Not Saved!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                }
            });
        }

    }

    public void SFA_PromotionOrderSerial(final Context context, final ProgressDialog dialog, String Header, List<Product> Items)
    {


        JSONObject object = new JSONObject();
        try {
            object.accumulate("HeaderID", Header);
            object.accumulate("Product_List", Convert_Array_to_Json_to(Header,context,Items));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        String  Url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavaOrder_PromotiosSerial";
        new Http().httpPost(Url,String.valueOf(object) , new Http.RequestCallback() {
            @Override
            public void onError(Throwable exception) {
                dialog.dismiss();
                Toast.makeText(context,"Faild to save serial",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseReceived(HttpResponse httpResponse, String response) {

                String result = Http.ReadResponse(httpResponse);

                if(Boolean.parseBoolean(result.replace("\n","")))
                    Toast.makeText(context,"Serial saved ",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context,"Failed to save serial",Toast.LENGTH_LONG).show();

                dialog.dismiss();

            }
        });

    }

    String OrderDetailsID = "";
    String serials = "";


    public void SFA_SavePreOrderTender(final Context context, String HeaderID, String TenderTypeID, String Submitter, float Amount) {
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_SavePreOrderTender/"
                + HeaderID + "/" + TenderTypeID + "/" + Submitter + "/" + Amount;

        url = url.replaceAll(" ", "%20");

        StringRequest SaveRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("SFA_SavePreOrderTenderResult");
                    if (result.equals("1")) {
                        saved = true;
                    } else {
                        saved = false;
                        Toast.makeText(context, "Tender Type Not Send", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Please try Again with good internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(context).add(SaveRequest);

    }

    public void SFA_Search_PreOrders(final Context context, final Activity activity, final RecyclerView recyclerView, final ProgressBar progressBar, final LinearLayout txtHintHistory) {

            String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Search_PreOrders/" + User.ServerConfigID + "/" + User.Username;

        url = url.replaceAll(" ", "%20");

        StringRequest historyRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Search_PreOrdersResult");
                    preOrderHistoryList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String ID = current.getString("SFA_PreOrderID");
                        String customerName = current.getString("CustomerName");
                        String customerNumber = current.getString("CustomerNumber");
                        String Status = current.getString("PreOrderStatus");
                        String Total = current.getString("PreOrder_Total");
                        PreOrderHistory preOrderHistory = new PreOrderHistory(ID, customerName, customerNumber, Status, Total);
                        preOrderHistoryList.add(preOrderHistory);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                historyAdapter = new HistoryAdapter(preOrderHistoryList, context, activity);
                recyclerView.setAdapter(historyAdapter);
                progressBar.setVisibility(View.GONE);
                if (preOrderHistoryList.size() <= 0) {
                    txtHintHistory.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "check internet connection", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }

        });
        Volley.newRequestQueue(context).add(historyRequest);
    }

    public void SFA_Search_PreOrdersItems(final Context context, String orderId, final RecyclerView recyclerView, final ProgressBar progressBar) {
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Search_PreOrdersItems/" + orderId;

        url = url.replaceAll(" ", "%20");

        StringRequest historyRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Search_PreOrdersItemsResult");
                    preOrderHistoryDetailList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String ItemCode = current.getString("ItemCode");
                        String Brand = current.getString("Brand");
                        String Model = current.getString("Model");
                        String Description = current.getString("Description");
                        String Qty = current.getString("Qty");
                        String Total = current.getString("Total");
                        String UnitPrice = current.getString("UnitPrice");
                        PreOrderHistoryDetail preOrderHistoryDetail = new PreOrderHistoryDetail(ItemCode, Brand, Model, Description, Qty, Total, UnitPrice);
                        preOrderHistoryDetailList.add(preOrderHistoryDetail);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                historyDetailAdapter = new HistoryDetailAdapter(preOrderHistoryDetailList, context, "Online Page");
                recyclerView.setAdapter(historyDetailAdapter);
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "check internet connection", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }

        });
        Volley.newRequestQueue(context).add(historyRequest);
    }

    String Serial_item_id = "";

    public void SFA_SavePreOrderDetails_serials(final Context context, final String OrderHeaderid, final String DetailsID, final String Serials) {
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
                            } else {
                                Toast.makeText(context, "Order Serials Not Saved " + Serial_item_id, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public boolean RequestForCancel(final String orderId, final Context context, final ProgressDialog dialog) {
        String urlCancel;
        urlCancel = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_RequestForCancel/" + orderId;

        urlCancel = urlCancel.replaceAll(" ", "%20");

        StringRequest cancelRequest = new StringRequest(Request.Method.GET, urlCancel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String result = object.getString("SFA_RequestForCancelResult");
                    if (result.equals("1")) {
                        dialog.dismiss();
                        HistoryCancel = true;
                    } else {
                        dialog.dismiss();
                        Toast.makeText(context, "Can not Cancel this order try again later !", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(context, "Please, Check Internet Connection", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(cancelRequest);

        return HistoryCancel;
    }

    private List<Serial> serialList;
    Serial serial;

    public void SFA_GetStockSerials(final Activity activity, final Context context, final RecyclerView recyclerView, final ProgressBar progressBar, final ProgressDialog dialog) {
        String urlSerial;
        urlSerial = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetStockSerials/" + User.Username;

        urlSerial = urlSerial.replaceAll(" ", "%20");

        StringRequest SerialRequest = new StringRequest(Request.Method.GET, urlSerial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_GetStockSerialsResult");
                    serialList = new ArrayList<>();
                    db_serial = new SerialDBHelper(context);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject current = array.getJSONObject(i);
                        String Box_number=current.getString("Box_number");
                        String SerialNumber = current.getString("SerialNumber");
                        String ItemCode = current.getString("ItemCode");
                        String Model = current.getString("Model");

                        if (db_serial.CheckSerialNumber(SerialNumber)) {
                            serial = new Serial(ItemCode, Model, SerialNumber, Box_number,true);
                            serial.setSerialNumber(SerialNumber);
                            serialList.add(serial);
                        } else {
                            serial = new Serial(ItemCode, Model, SerialNumber, Box_number,false);
                            serial.setSerialNumber(SerialNumber);
                            serialList.add(serial);
                        }

                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((CollectSerialActivity) activity).RenderList(serialList, recyclerView, context, activity, progressBar);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(SerialRequest);
    }

    public void SFA_UploadPicture(final Activity activity, String OrderId, String Image_Name, String SubmitBy)
    {
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_UploadPicture";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("OrderId",OrderId);
            jsonObject.accumulate("FTP_Path",User.FTP_Path + Image_Name);
            jsonObject.accumulate("Images_Path",User.Images_Path + Image_Name);
            jsonObject.accumulate("SubmitBy",SubmitBy);
            jsonObject.accumulate("Device","Mobile");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Http().httpPost(url, String.valueOf(jsonObject), new Http.RequestCallback() {
            @Override
            public void onError(Throwable exception) {
                Toast.makeText(activity.getBaseContext(),"Image not saved",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponseReceived(HttpResponse httpResponse, String response) {
                String result = new Http().ReadResponse(httpResponse);
                result = result.replace("\n","");
                result = result.replace("\"","");
                if(Boolean.parseBoolean(result))
                Toast.makeText(activity.getBaseContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();


            }
        });
    }
public static SFA_GetTruckType Trucks;
    public void SFA_GetTruckType(final Activity activity, final Context context) {
        String urlSerial;
        urlSerial = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetTruckType";

        urlSerial = urlSerial.replaceAll(" ", "%20");

        StringRequest TrRequest = new StringRequest(Request.Method.GET, urlSerial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Trucks = new Gson().fromJson(response,SFA_GetTruckType.class);
                for (TruckType Truck:Trucks.getTruckType()) {
                    Truck.setProSize(Truck);
                }

                Collections.sort(Trucks.getTruckType(), new Comparator<TruckType>() {
                    @Override
                    public int compare(TruckType o1, TruckType o2) {
                        return Double.valueOf(o1.getSize()).compareTo(Double.valueOf(o2.getSize()));
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }

        });
        Volley.newRequestQueue(context).add(TrRequest);
    }
    public static    ProductDimension[] productDimensions;

    public void SFA_GetItemDimensions(final Activity activity, final Context context, List<ProductDimension> list, final ProgressDialog  dialog) {
        String url;
        url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GetItemDimensions";
        String json = new Gson().toJson(list);
        Log.v("Json1",json);

        new Http().httpPost(url, json, new Http.RequestCallback() {
            @Override
            public void onError(Throwable exception) {
                dialog.dismiss();
                Log.v("Get Dim",exception.getMessage());
                Toast.makeText(context,"Check your network to get Product dimensions",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseReceived(HttpResponse httpResponse, String response2) {

                String response = new Http().ReadResponse(httpResponse);
                Log.v("Json2",response);
                Gson gson = new Gson();
                productDimensions = gson.fromJson(response, ProductDimension[].class);
                for (ProductDimension product:productDimensions) {
                    product.setProSize(product);
                }
                Collections.sort(Arrays.asList(productDimensions), new Comparator<ProductDimension>() {
                    @Override
                    public int compare(ProductDimension o1, ProductDimension o2) {
                        return Double.valueOf(o1.getSize()).compareTo(Double.valueOf(o2.getSize()));
                    }

                });
              //  new SummaryActivity().LoadingData();
            }
        });

    }

}
