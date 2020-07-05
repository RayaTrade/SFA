package settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;

import gps_tracking.LocationMonitoringService;

public class GPSCheck extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Intent serviceIntent = new Intent(context, LocationMonitoringService.class);
                context.startForegroundService(serviceIntent);
            }else {
                Intent serviceIntent = new Intent(context, LocationMonitoringService.class);
                context.startService(serviceIntent);
            }
        }

        check(context);
    }

    public void check(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(gps_enabled && network_enabled)
        {
            //Toast.makeText(context, "Thanks :)", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(context, "Please switch on the GPS with high accuracy mode", Toast.LENGTH_SHORT).show();
        }
    }
}
