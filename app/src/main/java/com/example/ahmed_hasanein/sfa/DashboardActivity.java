package com.example.ahmed_hasanein.sfa;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import Model.User;
import PrinterHandler.PrinterHandler_Main;
import Utility.Connectivity;
import Utility.DialogHint;
import preview_database.DB.SyncDB.SyncDBHelper;
import gps_tracking.LocationMonitoringService;

import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;

public class DashboardActivity extends AppCompatActivity {
    Bundle extras;
    CardView preOrderCardView, HistoryCardView, OrderCardView, DealersCardView,StockTakenCardView;
    boolean preOrderPermission;
    boolean OrderPermission;
    boolean historyPermission;
    boolean StockTakenServerPermission;
    String lastsyncdate;
    TextView txtVNumberDashboard, Txt_OfflineTransaction_notification;
    public static boolean OpenfromOrderPage = false;
    public static boolean OpenfromPreOrderPage = false;
    public static boolean OpenfromDealerOrder = false;
    public static boolean OpenfromStockTaken = false;
    SyncDBHelper db_sync;
    public static ProgressDialog dialogSync;
    Connectivity connectivity;
    TextView badge_notification_pending_sync;
    Button btnOfflineHistory, btnOnlineHistory;
    DialogHint dialogHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        preOrderCardView = (CardView) findViewById(R.id.preOrderCardView);
        HistoryCardView = (CardView) findViewById(R.id.HistoryCardView);
        txtVNumberDashboard = (TextView) findViewById(R.id.txtVNumberDashboard);
        OrderCardView = (CardView) findViewById(R.id.OrderCardView);
        StockTakenCardView = (CardView) findViewById(R.id.StockTakenCardView);
        Txt_OfflineTransaction_notification = (TextView) findViewById(R.id.Txt_OfflineTransaction_notification);

        txtVNumberDashboard.setText("Version " + BuildConfig.VERSION_NAME + "," + BuildConfig.VERSION_CODE); //set textview for version number

        extras = getIntent().getExtras();
        if (extras != null) {
            lastsyncdate = extras.getString("lastsyncdate");
            preOrderPermission = extras.getBoolean("preOrderPermission");
            OrderPermission = extras.getBoolean("OrderPermission");
            historyPermission = extras.getBoolean("historyPermission");
            StockTakenServerPermission = extras.getBoolean("StockTakenServerPermission");
            if (preOrderPermission) {
                preOrderCardView.setVisibility(View.VISIBLE);
            }
            if (historyPermission) {
                HistoryCardView.setVisibility(View.VISIBLE);
            }
            if (OrderPermission) {
                OrderCardView.setVisibility(View.VISIBLE);
            }
            if(StockTakenServerPermission)
            {
                StockTakenCardView.setVisibility(View.VISIBLE);
            }
        }

        db_sync = new SyncDBHelper(getBaseContext());
        dialogHint = new DialogHint();

        CounterOfOfflineTransactions(); // red circle counter offline transaction need to sync online

        //check connectivity
        connectivity = new Connectivity();
        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
        } else {
            OfflineMode = true;
        }

        try {
            if (db_sync.checkifTransactionsOfflineEmpty() == false) {
                if (OfflineMode == false) {
                    try {
                        dialogHint.showSyncBackDialog(DashboardActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (OfflineMode == true) {
                    showCounterHintPopup();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error !", Toast.LENGTH_SHORT).show();
        }
        boolean isMyServiceRunning =isMyServiceRunning(LocationMonitoringService.class);
        if (!isMyServiceRunning)
               restartServices();

       // new PrinterHandler_Main(DashboardActivity.this).scan(null);

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
    public void showCounterHintPopup() {
        final AlertDialog.Builder CounterHintPopoup = new AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater factory = LayoutInflater.from(DashboardActivity.this);
        final View view = factory.inflate(R.layout.popup_offline_counter_hint, null);
        TextView offlineTransactionTxtCounter = (TextView) view.findViewById(R.id.popup_notification_counter);
        db_sync = new SyncDBHelper(getBaseContext());
        offlineTransactionTxtCounter.setText(String.valueOf(db_sync.getcountofTransaction()));
        CounterHintPopoup.setView(view);
        CounterHintPopoup.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                dlg.dismiss();
            }
        });

        CounterHintPopoup.show();
    }
    public void CounterOfOfflineTransactions(){
        //set count of notification
        try {
            db_sync = new SyncDBHelper(getBaseContext());
            String count_preOrder = String.valueOf(db_sync.getcountofTransaction());
            if (count_preOrder.equals("0"))
                Txt_OfflineTransaction_notification.setVisibility(View.GONE);

            Txt_OfflineTransaction_notification.setText(count_preOrder);
            Txt_OfflineTransaction_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                    intent.putExtra("OpenFromOfflineIcon", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Txt_OfflineTransaction_notification.setVisibility(View.GONE);
        }
    }

    public void restartServices() {
        if (User.GPSInterval != 0) {
            //set GPSInterval to dialog
            //restart dialog

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(getApplicationContext(), LocationMonitoringService.class));
            } else {
                stopService(new Intent(this, LocationMonitoringService.class));
                startService(new Intent(this, LocationMonitoringService.class));
            }
        }
    }

    public void PreOrderClicked(View view) {
        if (extras != null) {
            Intent i = new Intent(DashboardActivity.this, CustomerActivity.class);
            i.putExtra("Order", false);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            OpenfromOrderPage = false;
            OpenfromDealerOrder = false;
            OpenfromStockTaken = false;
            OpenfromPreOrderPage = true;
            startActivity(i);
        }

    }

    public void PreOrderHistoryClick(View view) {
        //check connectivity
        connectivity = new Connectivity();
        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
        } else {
            OfflineMode = true;
        }

        db_sync = new SyncDBHelper(getBaseContext());
        int CountOfflineTransaction = db_sync.getcountofTransaction();

        if (CountOfflineTransaction > 0) {
            initiatePopupOfflineSync(String.valueOf(CountOfflineTransaction));
        } else {
            if (OfflineMode == false) {
                Intent i = new Intent(DashboardActivity.this, HistoryActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(getBaseContext(), "check internet connection and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void OrderClicked(View view) {
        if (extras != null) {
            Intent i = new Intent(DashboardActivity.this, CustomerActivity.class);
            i.putExtra("Order", true);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            OpenfromOrderPage = true;
            OpenfromStockTaken = false;
            OpenfromPreOrderPage = false;
            OpenfromDealerOrder = false;
            startActivity(i);
        }
    }

    public void StockTakenClicked(View view) {
        if (extras != null) {
            Intent i = new Intent(DashboardActivity.this, CustomerActivity.class);
            i.putExtra("Order", false);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            OpenfromOrderPage = false;
            OpenfromPreOrderPage = false;
            OpenfromDealerOrder = false;
            OpenfromStockTaken = true;

            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void DealersClicked(View view) {
        Intent i = new Intent(DashboardActivity.this, DealerDashboardActivity.class);
        i.putExtra("DealerOrder", true);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        OpenfromDealerOrder = true;
        OpenfromOrderPage = false;
        startActivity(i);
    }
    AlertDialog alertDialog;
    public void initiatePopupOfflineSync(String badge) {
        final AlertDialog.Builder PendingTransactionPopup = new AlertDialog.Builder(DashboardActivity.this);
        alertDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.popup_pending_offline_sync, null);
        btnOfflineHistory = (Button) view.findViewById(R.id.btnOfflineHistory);
        btnOnlineHistory = (Button) view.findViewById(R.id.btnOnlineHistory);
        badge_notification_pending_sync = (TextView) view.findViewById(R.id.badge_notification_pending_sync);
        badge_notification_pending_sync.setText(badge);


        btnOnlineHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectivity = new Connectivity();
                boolean checkConnectivity = new Connectivity().isConnectedFast(getBaseContext());

                if (checkConnectivity) {
                    Intent i = new Intent(DashboardActivity.this, HistoryActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(), "check internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnOfflineHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
                intent.putExtra("OpenFromSyncBackDialog", true);
                startActivity(intent);
            }
        });

        PendingTransactionPopup.setView(view);
        alertDialog = PendingTransactionPopup.create();
        alertDialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            CounterOfOfflineTransactions();
            alertDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
