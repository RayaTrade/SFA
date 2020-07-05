package Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.ahmed_hasanein.sfa.CollectSerialActivity.changeTxtConnectionTypeCollectSerialActivity;
import static com.example.ahmed_hasanein.sfa.CustomerActivity.changeTxtConnectionTypeCustomerActivity;
import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;
import static com.example.ahmed_hasanein.sfa.MainActivity.changeTxtConnectionTypeMainActivity;
import static com.example.ahmed_hasanein.sfa.SummaryActivity.changeTxtConnectionTypeSummaryActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            if (isOnline(context)) {
                if(context.getClass().getSimpleName().equals("MainActivity")) {
                    changeTxtConnectionTypeMainActivity(true);
                    OfflineMode = false;
                }else if (context.getClass().getSimpleName().equals("CustomerActivity")){
                    changeTxtConnectionTypeCustomerActivity(true);
                    OfflineMode = false;
                }else if (context.getClass().getSimpleName().equals("SummaryActivity")){
                    changeTxtConnectionTypeSummaryActivity(true);
                    OfflineMode = false;
                }else if (context.getClass().getSimpleName().equals("CollectSerialActivity")){
                    changeTxtConnectionTypeCollectSerialActivity(true);
                    OfflineMode = false;
                }
            } else {
                if(context.getClass().getSimpleName().equals("MainActivity")) {
                    changeTxtConnectionTypeMainActivity(false);
                    //new DialogHint().Offline_Dialog(context);
                    OfflineMode = true;
                }else if (context.getClass().getSimpleName().equals("CustomerActivity")){
                    changeTxtConnectionTypeCustomerActivity(false);
                    //new DialogHint().Offline_Dialog(context);
                    OfflineMode = true;
                }else if (context.getClass().getSimpleName().equals("SummaryActivity")){
                    changeTxtConnectionTypeSummaryActivity(false);
                    //new DialogHint().Offline_Dialog(context);
                    OfflineMode = true;
                }else if (context.getClass().getSimpleName().equals("CollectSerialActivity")){
                    changeTxtConnectionTypeCollectSerialActivity(false);
                    //new DialogHint().Offline_Dialog(context);
                    OfflineMode = true;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    private boolean isOnline(Context context) {
        try {
            boolean checkConnectivity = new Connectivity().isConnectedFast(context);
            return checkConnectivity;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
