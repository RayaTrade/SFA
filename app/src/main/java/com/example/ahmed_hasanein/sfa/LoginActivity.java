package com.example.ahmed_hasanein.sfa;

import android.Manifest;
import android.app.ActivityManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import API.API_Online;
import API.API_Sync_DB;
import Model.Permission;
import Model.User;
import Utility.Connectivity;
import Utility.DialogHint;
import preview_database.DB.SyncDB.SyncDBHelper;
import gps_tracking.LocationMonitoringService;

public class LoginActivity extends AppCompatActivity {
    String url;
    Button btnlogin, btnGetDeviceID, btnSyncOffline;
    EditText email, password;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView txtVNumberlogin;
    public List<Permission> permissionList;
    public static boolean preOrderPermission;
    public static boolean OrderPermission;
    public static boolean historyPermission;

    public static String emailpref;
    public static String passwordpref;
    public static String DeviceIDpref;
    public static String userIDpref;
    public static String ServerConfigIDpref;
    public static String AllowNegativeQtypref;
    public static String Currencypref;
    public static String LastLoginDatepref;
    public static String GPS_StartTime;
    public static String GPS_Distance;


    public static String GPS_EndTime;
    public static boolean OfflineMode;

    android.app.Dialog DialogVersionUpdate;
    SyncDBHelper db_sync;
    API_Sync_DB apiSyncDB;
    Connectivity connectivity;
    DialogHint dialogHint;
    String terms_conditions;
    {
        terms_conditions = "•\tThanks for using our products and services. The Services are provided by Raya, located at 26th of July Corridor, behind Dar El-Fouad Hospital, Giza Governorate, Egypt.\n" +
                "•\tBy using our Services, you are agreeing to these terms. Please read them carefully.\n" +
                "\n" +
                "•\tRaya’s privacy policies explain how we treat your personal data and protect your privacy when you use our Services. By using our Services, you agree that Raya can use such data in accordance with our privacy policies.\n" +
                "\n" +
                "\n" +
                "•\tDon’t misuse our Services. For example, don’t interfere with our Services or try to access them using a method other than the interface and the instructions that we provide. You may use our Services only as permitted by law, including applicable export and re-export control laws and regulations. We may suspend or stop providing our Services to you if you do not comply with our terms or policies or if we are investigating suspected misconduct.\n" +
                "\n" +
                "•\tRaya is committed to protect your privacy and develop a technology that gives you the most powerful services. This Statements of Privacy applies to the Raya SFA (Sales Force Automation) mobile application and governs data collection and usage. By using SFA mobile application, you consent to the data practices described in these statements.\n" +
                "\n" +
                "•\tCollection of your Personal Information:\n" +
                "1.\tRaya collects personally identifiable information, such as your e-mail address, name or telephone number or current location.\n" +
                "2.\tThere is also information about your device hardware and software that is automatically collected by SFA. This information can include your device ID, access times on mobile app and previous locations. This information is used by Raya for the operation of the service, to maintain quality of the service, and to provide general statistics regarding use of the SFA mobile application.\n" +
                "\n" +
                "•\tRaya encourages you to review the privacy statements of mobile applications you choose to link to from SFA so that you can understand how that mobile application collect, use and share your information. Raya is not responsible for the privacy statements or other content on mobile applications outside of Raya.\n" +
                "\n" +
                "•\tRaya collects and uses your personal information to operate the SFA mobile application and deliver the services you have requested.Raya also uses your personally identifiable information to inform you if there available updates from SFA mobile application. Raya may also contact you via telephone number to support issues that you face.\n" +
                "Raya does not sell any personal information.Raya may share data with trusted partners to help us perform statistical analysis. \n" +
                "\n" +
                "•\tRaya keeps track of the device and locations of yours based on your shift time. Locations of our customers visit secure and no one can access it if not authorized. This data is used to deliver customized content and enhance in performance to produce a good service.\n" +
                "\n" +
                "\n" +
                "•\tWe are constantly changing and improving our Services. We may add or remove functionalities or features, and we may suspend or stop a Service altogether.\n" +
                "\n" +
                "•\tRaya reserves all the rights, at its discretion, to change, modify, add, or remove portions of these Terms at any time by posting the amended Terms. Please check these Terms periodically for changes. Your continued use of the site or Services after the posting of changes constitutes your binding acceptance of such changes. In addition, when using any particular services, you may be subject to any posted guidelines, rules, product requirements or sometimes additional terms applicable to such services. All such guidelines, rules, product requirements or sometimes additional terms are hereby incorporated by reference into the Terms.\n";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Image view to show image after downloading
        my_image = (ImageView) findViewById(R.id.my_image);
        ImageView Terms_info_image = findViewById(R.id.Terms_info_image);
        final CheckBox TermsAndConditions_checkBox =findViewById(R.id.TermsAndConditions);
        dialogHint = new DialogHint();

        db_sync = new SyncDBHelper(getBaseContext());
        db_sync.deleteAllSyncBackOffline();
        apiSyncDB = new API_Sync_DB();
        //check connectivity
        connectivity = new Connectivity();
        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
        } else {
            OfflineMode = true;
        }


        email = (EditText) findViewById(R.id.EditEmail);
        password = (EditText) findViewById(R.id.EditPassword);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        txtVNumberlogin = (TextView) findViewById(R.id.txtVNumberlogin);
        btnGetDeviceID = (Button) findViewById(R.id.btnGetDeviceID);
        btnSyncOffline = (Button) findViewById(R.id.btnSyncOffline);
        DialogVersionUpdate = new android.app.Dialog(this);
        permissionList = new ArrayList<>();

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        prefs = getSharedPreferences("login_data", MODE_PRIVATE);
        txtVNumberlogin.setText("Version " + BuildConfig.VERSION_NAME + "," + BuildConfig.VERSION_CODE);//set version number for textview

        //shared preferences data email and password Device ID
        emailpref = prefs.getString("My_email", "");
        passwordpref = prefs.getString("My_password", "");
        DeviceIDpref = prefs.getString("My_DeviceID", "");
        userIDpref = prefs.getString("My_UserID", "");
        ServerConfigIDpref = prefs.getString("My_ServerConfigID", "");
        AllowNegativeQtypref = prefs.getString("My_AllowNegativeQty", "");
        Currencypref = prefs.getString("My_Currency", "");
        LastLoginDatepref = prefs.getString("My_LastLoginDate", "");

        GPS_StartTime = prefs.getString("GPS_StartTime", "");
        GPS_EndTime = prefs.getString("GPS_EndTime", "");
        GPS_Distance= prefs.getString("GPS_Distance", "");
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );


        if (prefs != null) {
            try {
                email.setText(emailpref);
                password.setText(passwordpref);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "please enter email & password", Toast.LENGTH_LONG).show();
            }
        }


        btnSyncOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    dialogHint.showNetworkDialog(LoginActivity.this);
                    return;
                } else if (emailpref == null || passwordpref == null || DeviceIDpref == null || userIDpref == null || ServerConfigIDpref == null || userIDpref == null) {
                    dialogHint.showAtlestFirstLoginDialog(LoginActivity.this, "Please login first time online");
                } else {
                    Intent intent = new Intent(getBaseContext(), SyncActivity.class);
                    startActivity(intent);
                }
            }
        });

        // onclick on info image
        Terms_info_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new DialogHint().allertdialog(LoginActivity.this,"Terms and Conditions: ",terms_conditions);
            }
        });


        // onclick on checkbox
        TermsAndConditions_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(TermsAndConditions_checkBox.isChecked())
                    btnlogin.setVisibility(View.VISIBLE);
                else
                    btnlogin.setVisibility(View.INVISIBLE);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void LoginClicked(View view) {
        //check connectivity
        connectivity = new Connectivity();
        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
        } else {
            OfflineMode = true;
        }

        if (TextUtils.isEmpty(email.getText())) {
            email.setError("email is Required !");
            return;
        } else if (TextUtils.isEmpty(password.getText())) {
            password.setError("password is Required !");
            return;
        } else {
            if (OfflineMode == true) {
                //new sync daily
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                boolean checkifLoginOfflineisEmpty =db_sync.checkifLoginOfflineisEmpty();
                boolean checkifPermssionOfflineisEmpty =db_sync.checkifPermssionOfflineisEmpty();
                if ((emailpref != null || passwordpref != null || ServerConfigIDpref != null || DeviceIDpref != null || userIDpref != null || ServerConfigIDpref != null)
                        && ( checkifLoginOfflineisEmpty == false && checkifPermssionOfflineisEmpty == false)
                        && LastLoginDatepref.equals(dateFormat.format(date))) {
                    loginOffline(); //offline
                } else {
                    dialogHint.showAtlestFirstLoginDialog(LoginActivity.this, "Please Check Internet Connection"); //must login online
                }
            } else {//online
                if (dialogHint.GPS_Dialog(LoginActivity.this))
                    loginOnline();
            }
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static ProgressDialog dialog;

    public void loginOffline() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("DeviceID", DeviceIDpref);

        List<String> Permissionslist = new ArrayList<>();
        db_sync = new SyncDBHelper(getBaseContext());

        for (Permission p : db_sync.getAllPermssionsOffline()) { //get user permission from offline table
            Permissionslist.add(p.getMenuName());
        }
        for (String p : Permissionslist) {
            if (p.equals("PreOrder")) {
                preOrderPermission = true;
            }
            if (p.equals("Order")) {
                OrderPermission = true;
            }
            if (p.equals("History")) {
                historyPermission = true;
            }
        }
        intent.putExtra("preOrderPermission", preOrderPermission);
        intent.putExtra("OrderPermission", OrderPermission);
        intent.putExtra("historyPermission", historyPermission);
        User.Username = emailpref;
        startActivity(intent);
        finish();
    }


    public void loginOnline() {
        int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            Toast.makeText(getBaseContext(), "please click allow to continue", Toast.LENGTH_SHORT).show();
            btnlogin.setEnabled(true);

        } else {

            TelephonyManager telephonyManager;
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            User.DeviceID = telephonyManager.getDeviceId();
            String ANDROID_ID = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (User.DeviceID == null || User.DeviceID.equals("")) {
                User.DeviceID = ANDROID_ID;
            }
            dialog = ProgressDialog.show(LoginActivity.this, "",
                    "Loading. Please wait...", true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isNetworkAvailable()) {
                        btnlogin.setEnabled(true);
                        Toast.makeText(getBaseContext(), "please check internet connection", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else if (isNetworkAvailable()) {
                        new API_Online().SFA_Login(dialog, getBaseContext(), LoginActivity.this, email.getText().toString(), password.getText().toString(), btnlogin);

                    } else {
                        Toast.makeText(getBaseContext(), "This device is not allow to login please send device id to admin !", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }, 500);
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void btnDeviceID(View view) {

        int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            Toast.makeText(getBaseContext(), "please click allow to continue", Toast.LENGTH_SHORT).show();
        } else {
            try {
                TelephonyManager telephonyManager;
                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                User.DeviceID = telephonyManager.getDeviceId();

                String ANDROID_ID = Settings.Secure.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                if (User.DeviceID == null || User.DeviceID.equals("")) {
                    User.DeviceID = ANDROID_ID;
                }
                btnGetDeviceID.setText(User.DeviceID);
                ClipboardManager _clipboard = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
                _clipboard.setText(User.DeviceID);
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, User.DeviceID);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } catch (Exception ex) {
                }
            }
            catch (Exception ex){
                String x = Settings.Secure.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
               User.DeviceID = x;
               btnGetDeviceID.setText(User.DeviceID);
            }

        }
    }

    public void showUpdateDialog() {
        DialogVersionUpdate.setContentView(R.layout.popup_updaterequired);
        TextView TxtcurrentVersion = (TextView) DialogVersionUpdate.findViewById(R.id.TxtcurrentVersion);
        TextView TxtUpdateVersion = (TextView) DialogVersionUpdate.findViewById(R.id.TxtUpdateVersion);
        Button dialogbtnVersionOk = (Button) DialogVersionUpdate.findViewById(R.id.dialogbtnVersionOk);

        TxtcurrentVersion.setText("Your version : " + BuildConfig.VERSION_NAME);
        TxtUpdateVersion.setText("Update To version : " + User.MobileVersion);

        DialogVersionUpdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogVersionUpdate.show();
        DialogVersionUpdate.setCanceledOnTouchOutside(false);
        dialogbtnVersionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            dialog.dismiss();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Sync. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    private ProgressDialog pDialog;
    ImageView my_image;
    public static final int progress_bar_type = 0;

    public  void startSync() {

        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog(progress_bar_type);

                // code will executed before task start (main thread)
                db_sync.deleteAllLoginOffline();
                db_sync.deleteAllPermssionsOffline();
                db_sync.deleteAllCustomersOffline();
                db_sync.deleteAllCustomersWithVisitOffline();
                db_sync.deleteAllPreOrderOffline();
                db_sync.deleteAllOrderOffline();
                db_sync.deleteAllCategoryOffline();
                db_sync.deleteAllBrandOffline();
                db_sync.deleteAllModelOffline();
                db_sync.deleteAllDeliveryMethodOffline();
                db_sync.deleteAllTenderTypeOfflineOffline();
                db_sync.deleteAllEndUserPriceOfflineOffline();
                db_sync.deleteAllGetStockSerialsOffline();
                db_sync.deleteAllTransactionTypesOffline();

            }

            @Override
            protected String doInBackground(String... params) {
                // task will done in background
                //apiSyncDB = new API_Sync_DB();
                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.LoginAPI(LoginActivity.this, getBaseContext(), DeviceIDpref, emailpref, passwordpref);
                return null;

            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(5);

                pDialog.setMessage("Sync Permissions");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncPermssions(userIDpref);
                    }
                }, 2000);
            }

        }.execute();
    }

    public void SyncPermssions(final String userID) {
        new AsyncTask<String, Integer, String>() {
            //String userID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllPermssionsOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.PermissionAPI(LoginActivity.this, getBaseContext(), userID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(5);
                pDialog.setMessage("Sync Customers...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncTruckType(userIDpref);
                    }
                }, 2000);

            }
        }.execute();
    }

    public void SyncTruckType(final String userID) {
        new AsyncTask<String, Integer, String>() {
            //String userID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteTruckTypeOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.SFA_GetTruckType(LoginActivity.this,getBaseContext());

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(10);
                pDialog.setMessage("Sync TruckType...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncSyncItemDimensions(userIDpref);
                    }
                }, 2000);

            }
        }.execute();
    }

    public void SyncSyncItemDimensions(final String userID) {
        new AsyncTask<String, Integer, String>() {
            //String userID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteItemDimensionsOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.SFA_GetItemDimensions(LoginActivity.this,getBaseContext());

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(13);
                pDialog.setMessage("Sync ItemDimensions...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncCustomer();
                    }
                }, 3000);

            }
        }.execute();
    }

    public void SyncCustomer() {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllCustomersOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());
                apiSyncDB.CustomerAPI(LoginActivity.this, getBaseContext(), DeviceIDpref);
                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(15);
                pDialog.setMessage("Sync Pre-order items...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TransactionTypes(ServerConfigIDpref);
                    }
                }, 2000);
            }

        }.execute();
    }



    public void TransactionTypes(final String ServerConfigIDpref) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllTransactionTypesOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());
                apiSyncDB.TransactionTypesAPI(LoginActivity.this, getBaseContext(), ServerConfigIDpref);
                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(20);
                pDialog.setMessage("Sync Transaction Types ...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncPreOrder(ServerConfigIDpref);
                    }
                }, 2000);
            }

        }.execute();
    }

    public void SyncPreOrder(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllPreOrderOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.PreOrderOffline(LoginActivity.this, getBaseContext(), ServerConfigID, "All", "All", "All", "All", emailpref);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(25);
                pDialog.setMessage("Sync Order items...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncOrder(ServerConfigIDpref);
                    }
                }, 2000);

            }


        }.execute();
    }

    public void SyncOrder(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllOrderOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.OrderOffline(LoginActivity.this, getBaseContext(), ServerConfigID, "All", "All", "All", "All", emailpref);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(35);
                pDialog.setMessage("Sync Category...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncCategory(ServerConfigIDpref);
                    }
                }, 2000);
            }

        }.execute();
    }

    public void SyncCategory(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllCategoryOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.CategoryOffline(LoginActivity.this, getBaseContext(), ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(40);
                pDialog.setMessage("Sync Brands...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncBrand(ServerConfigIDpref);
                    }
                }, 2000);

            }

        }.execute();
    }

    public void SyncBrand(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllBrandOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.BrandOffline(LoginActivity.this, getBaseContext(), ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(45);
                pDialog.setMessage("Sync Models....");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncModel(ServerConfigIDpref);
                    }
                }, 2000);

            }


        }.execute();
    }

    public void SyncModel(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllModelOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.ModelOffline(LoginActivity.this, getBaseContext(), ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setMessage("Sync Delivery Method....");
                pDialog.setProgress(50);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncDeliveryMethod(ServerConfigIDpref);
                    }
                }, 2000);
            }

        }.execute();
    }

    public void SyncDeliveryMethod(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllDeliveryMethodOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.DeliveryMethodOffline(LoginActivity.this, getBaseContext(), ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(55);
                pDialog.setMessage("Sync Tender Type...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncTenderType(ServerConfigIDpref);
                    }
                }, 2000);

            }

        }.execute();
    }

    public void SyncTenderType(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllTenderTypeOfflineOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.TenderTypeOffline(LoginActivity.this, getBaseContext(), ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setMessage("Sync Price...");
                pDialog.setProgress(60);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncEndUserPrice(ServerConfigIDpref, emailpref);
                    }
                }, 2000);

            }

        }.execute();
    }

    public void SyncEndUserPrice(final String ServerConfigID, final String Username) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllEndUserPriceOfflineOffline();
                db_sync.deleteOra_PriceListOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

               // apiSyncDB.EndUserPriceOffline(LoginActivity.this, getBaseContext(), ServerConfigID, Username);
                 apiSyncDB.SFA_Ora_PriceList(LoginActivity.this, getBaseContext(), ServerConfigID, Username);


                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setProgress(70);
                pDialog.setMessage("Sync Customer Visits....");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncCustomerWithVisit(ServerConfigIDpref, emailpref);
                    }
                }, 2000);
            }


        }.execute();
    }

    public void SyncCustomerWithVisit(final String ServerConfigID, final String Username) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllCustomersWithVisitOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.CustomerWithVisitAPI(LoginActivity.this, getBaseContext(), ServerConfigID, "30", Username);//month

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setMessage("Sync Sales Reason...");
                pDialog.setProgress(80);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncSalesReason(ServerConfigID);
                    }
                }, 2000);
            }


        }.execute();
    }

    public void SyncSalesReason(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllReasonOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.SalesReasonOffline(LoginActivity.this, getBaseContext(), "EN");//english

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setMessage("Sync Stock Serials...");
                pDialog.setProgress(90);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SFA_GetPromotions(ServerConfigID);

                    }
                }, 2000);
            }


        }.execute();
    }

    public void SFA_GetPromotions(final String ServerConfigID) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deletepromotion();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.SFA_GetPromotions(LoginActivity.this, getBaseContext(), ServerConfigID);
                apiSyncDB.SFA_GetPromotionsXQuery(LoginActivity.this, getBaseContext(), ServerConfigID);
                apiSyncDB.SFA_GetPromotionsXResult(LoginActivity.this, getBaseContext(), ServerConfigID);

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.setMessage("Sync Get Promotions...");
                pDialog.setProgress(95);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SyncGetStockSerials(emailpref);
                    }
                }, 2000);
            }


        }.execute();
    }


    public void SyncGetStockSerials(final String Username) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                db_sync.deleteAllGetStockSerialsOffline();
            }

            @Override
            protected String doInBackground(String... params) {

                db_sync = new SyncDBHelper(getBaseContext());

                apiSyncDB.GetStockSerialsOffline(LoginActivity.this, getBaseContext(), Username);
                pDialog.setProgress(99);
 //               pDialog.setMessage("Sync Completed !");

                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getBaseContext(),"finsih",Toast.LENGTH_SHORT).show();
                dismissDialog(progress_bar_type);
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("DeviceID", User.DeviceID);
                intent.putExtra("preOrderPermission", preOrderPermission);
                intent.putExtra("OrderPermission", OrderPermission);
                intent.putExtra("historyPermission", historyPermission);
                User.Username = email.getText().toString();
//                dialog.dismiss();
                startActivity(intent);
                finish();
            }


        }.execute();
    }

}
