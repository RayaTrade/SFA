package gps_tracking;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ahmed_hasanein.sfa.LoginActivity;
import com.example.ahmed_hasanein.sfa.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import settings.Constants;


public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    SharedPreferences prefs;
    private static String submitter = LoginActivity.emailpref;
    private static String DeviceID = LoginActivity.DeviceIDpref;
    private static String GPS_StartTime = LoginActivity.GPS_StartTime;
    private static String GPS_EndTime = LoginActivity.GPS_EndTime;
    private static String GPS_Distance = LoginActivity.GPS_Distance;

    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest;


    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static Location Last_location = null;
    static SharedPreferences.Editor editor;
    Notification notification;
    //#region service
    @Override
    public void onCreate() {
        super.onCreate();


        String CHANNEL_ID = "my_channel";
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Location Detection",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        }

         notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Detection")
                .setSmallIcon(R.drawable.raya)
                .setContentText("Please keep GPS turn on with high accuracy").build();
        try {
           startForeground(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*
         * # change date 7/8/2019
         * © changed by Ahmed Ali
         * -- description: .........
         * PRIORITY_BALANCED_POWER_ACCURACY
         */


       /* if (!isGooglePlayServicesAvailable()) {
            mLocationClient.disconnect();
        }*/
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;

        prefs = getSharedPreferences("login_data", MODE_PRIVATE);
        //shared preferences data email and password
        submitter = prefs.getString("My_email", "");
        DeviceID = prefs.getString("My_DeviceID", "");
        GPS_StartTime = prefs.getString("GPS_StartTime", "");
        GPS_EndTime = prefs.getString("GPS_EndTime", "");
        GPS_Distance = prefs.getString("GPS_Distance", "");

        if(GPS_Distance.equals("") || GPS_Distance.equals(null))
            GPS_Distance ="100";

        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes,PRIORITY_LOW_POWER
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);
        mLocationRequest.setMaxWaitTime(Constants.MaxWaitTime);
        mLocationRequest.setPriority(priority);
        mLocationRequest.setSmallestDisplacement(Integer.parseInt(GPS_Distance));


        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationClient.connect();



        setOnetimeTimer(this.getApplicationContext());
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        // START_STICKY
        return START_REDELIVER_INTENT;
    }

    // destroy service
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            Intent myService = new Intent(this, LocationMonitoringService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                startForegroundService(myService);
            } else {

                startService(myService);
            }
        } catch (Exception e) {

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //statusCheck();
        return null;
    }

    //#endregion
    /*
     * # change date 3/9/2019
     * © changed by Ahmed Ali
     * -- description: .........
     *  calc_Time
     */
    public ArrayList<String> calc_Time(long current, long last, double distance) {
        ArrayList<String> List_of_Distance_Duration_speed = new ArrayList<>();
        long mills = last - current;

        double second = (double) (mills / 1000.0);
        double minutes = (second / 60);

        double speed = speed(distance, second);
        String time_minutes = String.valueOf(new DecimalFormat("##.##").format(minutes));

        List_of_Distance_Duration_speed.add(String.valueOf(distance)); // meter
        List_of_Distance_Duration_speed.add(time_minutes); // minutes
        List_of_Distance_Duration_speed.add(String.valueOf(speed));// K/h

        //String diff = hours + ":" + mins+":"+second +"\n"+ (speed) +"m/s"+"\n"+ (speed*3.6)+"K/H";

        return List_of_Distance_Duration_speed;

    }

    public Double speed(double distance, double second) {
        double speeds = 0;
        speeds = Double.parseDouble((new DecimalFormat("###.##").format((distance / second) * 3.6))); // K/h

        if (String.valueOf(speeds) == null || String.valueOf(speeds).equals("NaN"))
            return 0.0;

        return speeds;

    }

    //#region get location
    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        Log.d("Location_changed", location.toString());

        /*
         * # change date 25/8/2019
         * © changed by Ahmed Ali
         * -- description: .........
         *  onLocationChangedtime
         */

        if(amIonDate()) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
    }
    Last_location = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

    if (Last_location != null && !String.valueOf(location.getTime()).equals(String.valueOf(Last_location.getTime()))) {

        onlocationChange_operation(Last_location, location);
    } else {
        // work at first time to initalize
        Last_location = location;
        onlocationChange_operation(Last_location, location);
    }


}
    }
    //#endregion
    public void onlocationChange_operation(Location Last_location,Location location)
{
     {
        String onLocationChangedtime = Location_Date_Time(new Date(location.getTime()));

        double distance = Double.parseDouble(new DecimalFormat("###.##").format(Last_location.distanceTo(location)));
        ArrayList<String> List_of_Distance_Duration_speed =  calc_Time(location.getTime(),Last_location.getTime(),distance);


         if (submitter != null || DeviceID != null) {
            if (submitter == "" || DeviceID == "") {
                submitter = LoginActivity.emailpref;
                DeviceID = LoginActivity.DeviceIDpref;
            }
            try {
                CallServer(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), DeviceID, submitter, String.valueOf(onLocationChangedtime),List_of_Distance_Duration_speed.get(0),List_of_Distance_Duration_speed.get(1),List_of_Distance_Duration_speed.get(2));

            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getApplicationContext(), "Please switch on the GPS with high accuracy mode", Toast.LENGTH_SHORT).show();
        }


      /* if (location != null) { Log.d(TAG, "== location != null");//Send result to activities sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));*/


    }
}
    //#region Google API

    /** LOCATION CALLBACKS */

    @Override
    public void onConnected(Bundle dataBundle) {

        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
            Log.d("Sync_to_update_location", "location updated");
        } catch (Exception e) {

        }

        //statusCheck();
        Log.d(TAG, "Connected to Google API_Sync_DB");
    }

    /** Called by Location Services if the connection to the* location client drops because of an error.*/
    // just log
    @Override
    public void onConnectionSuspended(int i) {
        //statusCheck();
        Log.d(TAG, "Connection suspended");

    }

    // just log
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API_Sync_DB");
        //statusCheck();
    }

    //#endregion


//-------------------------------------------------------------------------------------------------------

    public void CallServer(final String Lat, final String Long, final String DeviceID, final String Submitter, String onLocationChangedtime, String distance, String time, String speed) {
        String urllocation;
        /*
         * # change date 25/8/2019
         * © changed by Ahmed Ali
         * -- description: .........
         *
         *  onLocationChangedtime
         */


        urllocation = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_GPSTracking/" + Lat + "/" + Long + "/" + DeviceID + "/" + Submitter+"/"+onLocationChangedtime+"/"+distance+"/"+time+"/"+speed;
        urllocation = urllocation.replaceAll(" ", "%20");
        StringRequest serverRequest = new StringRequest(Request.Method.GET, urllocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String result = object.getString("SFA_GPSTrackingResult");
                    if (result.equals("1")) {
                        // Toast.makeText(getApplicationContext(),"location sent "+Submitter,Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                   if(submitter.equals("admin@rayacorp.com") || submitter.equals("hamdy_smohamed@rayacorp.com") )
                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                Log.d("call_server_number", "1");
                Log.d("response", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Please, Check Internet Connection
                //Toast.makeText(getApplicationContext()," "+DeviceID+" "+submitter+" "+Lat+" "+Long,Toast.LENGTH_LONG).show();
            }

        });
        Log.d("serverRequest", serverRequest.toString());


        Volley.newRequestQueue(getApplicationContext()).add(serverRequest);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            return false;
        }
    }

    public void setOnetimeTimer(final Context context) {

        final Handler mHandler = new Handler();

        final Runnable mHandlerTask = new Runnable() {
            @Override
            public void run() {
                String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if(amIonDate()) {
                    //------------------------------------ Check GPS --------------------------------

                    if (locationProviders == null || locationProviders.equals("")) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    //------------------------------------ Battery --------------------------------
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = (level * 100) / (float) scale;
                    if (batteryPct <= 20) {


                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");

                        mBuilder.setContentTitle("Battery");
                        mBuilder.setSmallIcon(R.drawable.raya);
                        mBuilder.setContentText("Charge your phone");


                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            String channelId = "Your_channel_id";
                            NotificationChannel channel = null;
                            {
                                channel = new NotificationChannel(
                                        channelId,
                                        "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT
                                );
                            }
                            mNotificationManager.createNotificationChannel(channel);
                            mBuilder.setChannelId(channelId);
                        }

                        mNotificationManager.notify(0, mBuilder.build());

                        Log.v("Battery", String.valueOf(batteryPct));
                    }
                    //-------------------------------- Network Connection ---------------------------
                    if (!isNetworkAvailable()) {


                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context.getApplicationContext(), "notify_002");

                        mBuilder.setContentTitle("Network Failure");
                        mBuilder.setSmallIcon(R.drawable.raya);
                        mBuilder.setContentText("Open Your Network necessary");


                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            String channelId = "Your_channel_id2";
                            NotificationChannel channel = null;
                            {
                                channel = new NotificationChannel(
                                        channelId,
                                        "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT
                                );
                            }
                            mNotificationManager.createNotificationChannel(channel);
                            mBuilder.setChannelId(channelId);
                        }

                        mNotificationManager.notify(4, mBuilder.build());
                    }
                    //------------------------------- ----------------------------------------------


                 Log.v("at work","yes");


                }
                else
                {
                    Log.v("at work","No");
                }

                mHandler.postDelayed(this,10000 );
            }
        };
        mHandlerTask.run();

    }

    // convert location time format to server format
    public static String Location_Date_Time(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = sdf.format(d);
        Log.d("NMEAGPRMCDate", result);

        return result;
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean amIonDate()
    {

        Date currentTime = null, start_work_time = null,end_work_time=null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTimeString = sdf.format(new Date());
        try {
            currentTime = sdf.parse(currentTimeString);

            start_work_time = sdf.parse( GPS_StartTime);
            end_work_time = sdf.parse( GPS_EndTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if( currentTime.after(start_work_time) && currentTime.before(end_work_time))
        {
            if(currentTime.equals(start_work_time))
            {
                Intent myService = new Intent(this, LocationMonitoringService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(myService);
                }
                else
                {
                    startService(myService);

                }
            }

           return true;
        }
        else
        {
            return false;
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
    private void sendMessageToUI(String lat, String lng) {
        //statusCheck();
        Log.d(TAG, "Sending info..." + lat + "/" + lng);

      /*  Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/

    }
}
    /*

*/