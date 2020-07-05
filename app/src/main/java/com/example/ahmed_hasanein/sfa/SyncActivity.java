package com.example.ahmed_hasanein.sfa;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import API.API_Sync_DB;
import preview_database.DB.SyncDB.SyncDBHelper;


public class SyncActivity extends AppCompatActivity {

    ProgressBar progress_bar_login_sync,progress_bar_Permissions_sync,progress_bar_customer_sync,progress_bar_preOrder_sync
            ,progress_bar_Order_sync,progress_bar_Category_sync,progress_bar_Brand_sync,progress_bar_Model_sync
            ,progress_bar_DeliveryMethod_sync,progress_bar_TenderType_sync,progress_bar_EndUserPrice_sync
            ,progress_bar_GetStockSerials_sync;

    ImageView IMG_login_right,IMG_Permissions_right,IMG_customer_right,IMG_preOrder_right,IMG_Order_right,
            IMG_login_error,IMG_Permissions_error,IMG_customer_error,IMG_preOrder_error,IMG_Order_error
            ,IMG_Category_right,IMG_Category_error,IMG_Brand_right,IMG_Brand_error,IMG_Model_right,IMG_Model_error
            ,IMG_DeliveryMethod_right,IMG_DeliveryMethod_error,IMG_TenderType_right,IMG_TenderType_error
            ,IMG_EndUserPrice_right,IMG_EndUserPrice_error,IMG_GetStockSerials_right,IMG_GetStockSerials_error;

    TextView TxtLoginSync,TxtPermissionsSync,TxtCustomerSync,TxtPreOrderSync,TxtOrderSync
            ,TxtCategorySync,TxtBrandSync,TxtModelSync,TxtDeliveryMethodSync,TxtTenderTypeSync,TxtEndUserPriceSync,TxtGetStockSerialsSync;

    SyncDBHelper db_sync;
    API_Sync_DB apiSyncDB;
    SharedPreferences prefs;
    public static String emailpref;
    public static String passwordpref;
    public static String DeviceIDpref;
    public static String userIDpref;
    public static String ServerConfigIDpref;
    boolean synclogin,syncPermission,syncCustomer,syncPreorder,syncOrder
            ,syncCategory,syncBrand,syncModel
            ,syncDeliveryMethod,syncTenderType,syncEndUserPrice,syncGetStockSerials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        TxtLoginSync = (TextView) findViewById(R.id.TxtLoginSync);
        TxtPermissionsSync = (TextView) findViewById(R.id.TxtPermissionsSync);
        TxtCustomerSync = (TextView) findViewById(R.id.TxtCustomerSync);
        TxtPreOrderSync = (TextView) findViewById(R.id.TxtPreOrderSync);
        TxtOrderSync = (TextView) findViewById(R.id.TxtOrderSync);
        TxtCategorySync = (TextView) findViewById(R.id.TxtCategorySync);
        TxtBrandSync = (TextView) findViewById(R.id.TxtBrandSync);
        TxtModelSync = (TextView) findViewById(R.id.TxtModelSync);
        TxtTenderTypeSync = (TextView) findViewById(R.id.TxtTenderTypeSync);
        TxtDeliveryMethodSync = (TextView) findViewById(R.id.TxtDeliveryMethodSync);
        TxtEndUserPriceSync = (TextView) findViewById(R.id.TxtEndUserPriceSync);
        TxtGetStockSerialsSync = (TextView) findViewById(R.id.TxtGetStockSerialsSync);

        progress_bar_GetStockSerials_sync = (ProgressBar) findViewById(R.id.progress_bar_GetStockSerials_sync);
        progress_bar_EndUserPrice_sync = (ProgressBar) findViewById(R.id.progress_bar_EndUserPrice_sync);
        progress_bar_TenderType_sync = (ProgressBar) findViewById(R.id.progress_bar_TenderType_sync);
        progress_bar_DeliveryMethod_sync = (ProgressBar) findViewById(R.id.progress_bar_DeliveryMethod_sync);
        progress_bar_Model_sync = (ProgressBar) findViewById(R.id.progress_bar_Model_sync);
        progress_bar_Brand_sync = (ProgressBar) findViewById(R.id.progress_bar_Brand_sync);
        progress_bar_Category_sync = (ProgressBar) findViewById(R.id.progress_bar_Category_sync);
        progress_bar_Order_sync = (ProgressBar) findViewById(R.id.progress_bar_Order_sync);
        progress_bar_preOrder_sync = (ProgressBar) findViewById(R.id.progress_bar_preOrder_sync);
        progress_bar_customer_sync = (ProgressBar) findViewById(R.id.progress_bar_customer_sync);
        progress_bar_Permissions_sync = (ProgressBar) findViewById(R.id.progress_bar_Permissions_sync);
        progress_bar_login_sync = (ProgressBar) findViewById(R.id.progress_bar_login_sync);

        IMG_GetStockSerials_right = (ImageView) findViewById(R.id.IMG_GetStockSerials_right);
        IMG_GetStockSerials_error = (ImageView) findViewById(R.id.IMG_GetStockSerials_error);
        IMG_EndUserPrice_right = (ImageView) findViewById(R.id.IMG_EndUserPrice_right);
        IMG_EndUserPrice_error = (ImageView) findViewById(R.id.IMG_EndUserPrice_error);
        IMG_TenderType_right = (ImageView) findViewById(R.id.IMG_TenderType_right);
        IMG_TenderType_error = (ImageView) findViewById(R.id.IMG_TenderType_error);
        IMG_DeliveryMethod_right = (ImageView) findViewById(R.id.IMG_DeliveryMethod_right);
        IMG_DeliveryMethod_error = (ImageView) findViewById(R.id.IMG_DeliveryMethod_error);
        IMG_Model_right = (ImageView) findViewById(R.id.IMG_Model_right);
        IMG_Model_error = (ImageView) findViewById(R.id.IMG_Model_error);
        IMG_Brand_right = (ImageView) findViewById(R.id.IMG_Brand_right);
        IMG_Brand_error = (ImageView) findViewById(R.id.IMG_Brand_error);
        IMG_Category_right = (ImageView) findViewById(R.id.IMG_Category_right);
        IMG_Category_error = (ImageView) findViewById(R.id.IMG_Category_error);
        IMG_login_right = (ImageView) findViewById(R.id.IMG_login_right);
        IMG_login_error = (ImageView) findViewById(R.id.IMG_login_error);
        IMG_Permissions_right = (ImageView) findViewById(R.id.IMG_Permissions_right);
        IMG_Permissions_error = (ImageView) findViewById(R.id.IMG_Permissions_error);
        IMG_customer_right = (ImageView) findViewById(R.id.IMG_customer_right);
        IMG_customer_error = (ImageView) findViewById(R.id.IMG_customer_error);
        IMG_preOrder_right = (ImageView) findViewById(R.id.IMG_preOrder_right);
        IMG_preOrder_error = (ImageView) findViewById(R.id.IMG_preOrder_error);
        IMG_Order_right = (ImageView) findViewById(R.id.IMG_Order_right);
        IMG_Order_error = (ImageView) findViewById(R.id.IMG_Order_error);
        prefs = getSharedPreferences("login_data", MODE_PRIVATE);
        emailpref = prefs.getString("My_email", "");
        passwordpref = prefs.getString("My_password", "");
        DeviceIDpref = prefs.getString("My_DeviceID","");
        userIDpref = prefs.getString("My_UserID","");
        ServerConfigIDpref = prefs.getString("My_ServerConfigID","");
        db_sync = new SyncDBHelper(getBaseContext());
        apiSyncDB = new API_Sync_DB();


        startSync();


    }


    public void startSync(){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // code will executed before task start (main thread)
                progress_bar_login_sync.setVisibility(View.VISIBLE);
                db_sync.deleteAllLoginOffline();
                db_sync.deleteAllPermssionsOffline();
                db_sync.deleteAllCustomersOffline();
                db_sync.deleteAllPreOrderOffline();
                db_sync.deleteAllOrderOffline();
                db_sync.deleteAllCategoryOffline();
                db_sync.deleteAllBrandOffline();
                db_sync.deleteAllModelOffline();
                db_sync.deleteAllDeliveryMethodOffline();
                db_sync.deleteAllTenderTypeOfflineOffline();
                db_sync.deleteAllEndUserPriceOfflineOffline();
                db_sync.deleteAllGetStockSerialsOffline();

 //               db_sync.deleteAllTransactionOffline();
//                db_sync.deleteAllTransactionItemsOffline();
//                db_sync.deleteAllTransactionTenderOffline();
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                synclogin =  apiSyncDB.LoginAPI(SyncActivity.this,getBaseContext(),DeviceIDpref,emailpref,passwordpref);
                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_login_sync.setVisibility(View.GONE);

                if(db_sync.checkifLoginOfflineisEmpty()==true){
                    IMG_login_error.setVisibility(View.VISIBLE);
                }

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncPermssions(userIDpref);
                    }
                }, 2000);
            }

        }.execute();
    }

    public void SyncPermssions(final String userID){
        new AsyncTask<String, Integer, String>() {
            //String userID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllPermssionsOffline();

                // code will executed before task start (main thread)
                progress_bar_Permissions_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncPermission = apiSyncDB.PermissionAPI(SyncActivity.this,getBaseContext(),userID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_Permissions_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncCustomer();
                    }
                }, 2000);


                if(db_sync.checkifPermssionOfflineisEmpty()==true){
                    IMG_Permissions_error.setVisibility(View.VISIBLE);
                }

            }
        }.execute();
    }

    public void SyncCustomer(){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllCustomersOffline();
                // code will executed before task start (main thread)
                progress_bar_customer_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncCustomer = apiSyncDB.CustomerAPI(SyncActivity.this,getBaseContext(),DeviceIDpref);
                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_customer_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncPreOrder(ServerConfigIDpref);
                    }
                }, 2000);

                if(db_sync.checkifCustomerOfflineisEmpty()==true){
                    IMG_customer_error.setVisibility(View.VISIBLE);
                }
            }

        }.execute();
    }
    public void SyncPreOrder(final String ServerConfigID){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllPreOrderOffline();
                // code will executed before task start (main thread)
                progress_bar_preOrder_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncPreorder = apiSyncDB.PreOrderOffline(SyncActivity.this,getBaseContext(),ServerConfigID,"All","All","All","All",emailpref);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_preOrder_sync.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncOrder(ServerConfigIDpref);
                    }
                }, 2000);

                if(db_sync.checkifPreorderOfflineisEmpty()==true){
                    IMG_preOrder_error.setVisibility(View.VISIBLE);
                }

            }



        }.execute();
    }

    public void SyncOrder(final String ServerConfigID){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllOrderOffline();
                // code will executed before task start (main thread)
                progress_bar_Order_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncOrder = apiSyncDB.OrderOffline(SyncActivity.this,getBaseContext(),ServerConfigID,"All","All","All","All",emailpref);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_Order_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncCategory(ServerConfigIDpref);
                    }
                }, 2000);


                if(db_sync.checkifOrderOfflineisEmpty()==true){
                    IMG_Order_error.setVisibility(View.VISIBLE);
                }

            }

        }.execute();
    }

    public void SyncCategory(final String ServerConfigID){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllCategoryOffline();
                // code will executed before task start (main thread)
                progress_bar_Category_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncCategory = apiSyncDB.CategoryOffline(SyncActivity.this,getBaseContext(),ServerConfigID);


                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_Category_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncBrand(ServerConfigIDpref);
                    }
                }, 2000);


                if(db_sync.checkifCategoryOfflineEmpty()==true){
                    IMG_Category_error.setVisibility(View.VISIBLE);
                }

            }

        }.execute();
    }

    public void SyncBrand(final String ServerConfigID){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllBrandOffline();
                // code will executed before task start (main thread)
                progress_bar_Brand_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncBrand = apiSyncDB.BrandOffline(SyncActivity.this,getBaseContext(),ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_Brand_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncModel(ServerConfigIDpref);
                    }
                }, 2000);


                if(db_sync.checkifModelOfflineEmpty()==true){
                    IMG_Brand_error.setVisibility(View.VISIBLE);
                }

            }


        }.execute();
    }

    public void SyncModel(final String ServerConfigID){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllModelOffline();
                // code will executed before task start (main thread)
                progress_bar_Model_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncModel = apiSyncDB.ModelOffline(SyncActivity.this,getBaseContext(),ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_Model_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncDeliveryMethod(ServerConfigIDpref);
                    }
                }, 2000);


                if(db_sync.checkifModelOfflineEmpty()==true){
                    IMG_Model_error.setVisibility(View.VISIBLE);
                }

            }

        }.execute();
    }

    public void SyncDeliveryMethod(final String ServerConfigID){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllDeliveryMethodOffline();
                // code will executed before task start (main thread)
                progress_bar_DeliveryMethod_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncDeliveryMethod = apiSyncDB.DeliveryMethodOffline(SyncActivity.this,getBaseContext(),ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_DeliveryMethod_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncTenderType(ServerConfigIDpref);
                    }
                }, 2000);


                if(db_sync.checkifDeliveryMethodOfflineEmpty()==true){
                    IMG_DeliveryMethod_error.setVisibility(View.VISIBLE);
                }

            }

        }.execute();
    }

    public void SyncTenderType(final String ServerConfigID){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllTenderTypeOfflineOffline();
                // code will executed before task start (main thread)
                progress_bar_TenderType_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncTenderType = apiSyncDB.TenderTypeOffline(SyncActivity.this,getBaseContext(),ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_TenderType_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncEndUserPrice(ServerConfigIDpref,emailpref);
                    }
                }, 2000);


                if(db_sync.checkifTenderTypeOfflineEmpty()==true){
                    IMG_TenderType_error.setVisibility(View.VISIBLE);
                }

            }

        }.execute();
    }

    public void SyncEndUserPrice(final String ServerConfigID,final String Username){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllEndUserPriceOfflineOffline();
                // code will executed before task start (main thread)
                progress_bar_EndUserPrice_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncEndUserPrice = apiSyncDB.EndUserPriceOffline(SyncActivity.this,getBaseContext(),ServerConfigID,Username);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_EndUserPrice_sync.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        SyncGetStockSerials(emailpref);
                    }
                }, 2000);

                if(db_sync.checkifEndUserPriceOfflineEmpty()==true){
                    IMG_EndUserPrice_error.setVisibility(View.VISIBLE);
                }

            }


        }.execute();
    }

    public void SyncGetStockSerials(final String Username){
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllGetStockSerialsOffline();
                // code will executed before task start (main thread)
                progress_bar_GetStockSerials_sync.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                syncGetStockSerials = apiSyncDB.GetStockSerialsOffline(SyncActivity.this,getBaseContext(),Username);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // code executed after task finish hide progress and change text
                progress_bar_GetStockSerials_sync.setVisibility(View.GONE);

                if(db_sync.checkifGetStockSerialsOfflineEmpty()==true){
                    IMG_GetStockSerials_error.setVisibility(View.VISIBLE);
                }

            }


        }.execute();
    }


    public void changeLoginImagetoTrue(){
       IMG_login_error.setVisibility(View.GONE);
       IMG_login_right.setVisibility(View.VISIBLE);
   }

    public void changePermssionImagetoTrue(){
        IMG_Permissions_error.setVisibility(View.GONE);
        IMG_Permissions_right.setVisibility(View.VISIBLE);
    }
    public void changeCustomerImagetoTrue(){
        IMG_customer_error.setVisibility(View.GONE);
        IMG_customer_right.setVisibility(View.VISIBLE);
    }
    public void changePreOrderImagetoTrue(){
        IMG_preOrder_error.setVisibility(View.GONE);
        IMG_preOrder_right.setVisibility(View.VISIBLE);
    }
    public void changeOrderImagetoTrue(){
        IMG_Order_error.setVisibility(View.GONE);
        IMG_Order_right.setVisibility(View.VISIBLE);
    }
    public void changeCategoryImagetoTrue(){
        IMG_Category_error.setVisibility(View.GONE);
        IMG_Category_right.setVisibility(View.VISIBLE);
    }
    public void changeBrandImagetoTrue(){
        IMG_Brand_error.setVisibility(View.GONE);
        IMG_Brand_right.setVisibility(View.VISIBLE);
    }
    public void changeModelImagetoTrue(){
        IMG_Model_error.setVisibility(View.GONE);
        IMG_Model_right.setVisibility(View.VISIBLE);
    }
    public void changeDeliveryMethodImagetoTrue(){
        IMG_DeliveryMethod_error.setVisibility(View.GONE);
        IMG_DeliveryMethod_right.setVisibility(View.VISIBLE);
    }
    public void changeTenderTypeImagetoTrue(){
        IMG_TenderType_error.setVisibility(View.GONE);
        IMG_TenderType_right.setVisibility(View.VISIBLE);
    }
    public void changeEndUserPriceImagetoTrue(){
        IMG_EndUserPrice_error.setVisibility(View.GONE);
        IMG_EndUserPrice_right.setVisibility(View.VISIBLE);
    }
    public void changeGetStockSerialstoTrue(){
        IMG_GetStockSerials_error.setVisibility(View.GONE);
        IMG_GetStockSerials_right.setVisibility(View.VISIBLE);
    }
}
