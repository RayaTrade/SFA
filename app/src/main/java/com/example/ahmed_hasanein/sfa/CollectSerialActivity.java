package com.example.ahmed_hasanein.sfa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Adapter.CollectSerialTableAdapter;
import Adapter.SerialAdapter;
import Model.Product;
import Model.Promotions;
import Model.Serial;
import Model.User;
import Utility.Connectivity;
import Utility.DialogHint;
import Utility.NetworkChangeReceiver;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static Utility.DialogHint.VisitDate;
import static com.example.ahmed_hasanein.sfa.LoginActivity.AllowNegativeQtypref;
import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;
import static com.example.ahmed_hasanein.sfa.LoginActivity.ServerConfigIDpref;
import static com.example.ahmed_hasanein.sfa.LoginActivity.emailpref;
import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerName;
import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerNumber;


public class CollectSerialActivity extends AppCompatActivity {

    static List<Product> productList;
    static RecyclerView recyclerView;
    ProgressBar mProgressBar;
    private static OrderDBHelper db_order;
    private SyncDBHelper db_sync;
    private SerialDBHelper db_serial;
    static  CollectSerialTableAdapter adapter;
    Button btnSaveSerials;
    ProgressDialog dialogSave;
    private List<Serial> serialList;
    private RecyclerView mRecyclerView,mRecyclerView_box;
    private SerialAdapter mAdapter,mAdapter_Box;
    TextView TxtCollectedSerials_Choose,TxtCollectedSerials_Total,TxtCollectedSerials_Date;
    LinearLayout TxtCollectedSerials_notFound;
    static TextView TxtCollConnectionType;
    public static String StringOfSerials = "";
    public ProgressDialog loadingProgressDialog;
    Bundle extras;
    String Long, Lat, CustomerNumber;
    Button dialogbtnFinsihOrder, dialogbtncloseOrder;
    EditText TxtCommentOrder;


    public static EditText getFilter() {
        return Filter;
    }

    public static void setFilter(EditText filter) {
        Filter = filter;
    }

    static EditText Filter;
    TextView serialCustomerName, serialCustomerNumber;
    DialogHint dialogHint;
    Connectivity connectivity;
    //List<Serial> serialListDB;
    SharedPreferences prefs;
    private BroadcastReceiver mNetworkReceiver;
    ImageView IMG_Serial_bg;
    AlertDialog.Builder DialogFinsih;
    AlertDialog dialog;
    static TextView LayoutCustomerOffline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_serial);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCollectedSerial);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_barCollectedSerial);
        btnSaveSerials = (Button) findViewById(R.id.btnSaveSerials);
        TxtCollectedSerials_Choose = (TextView) findViewById(R.id.TxtCollectedSerials_Choose);
        TxtCollectedSerials_notFound = (LinearLayout) findViewById(R.id.TxtCollectedSerials_notFound);
        serialCustomerName = (TextView) findViewById(R.id.serialCustomerName);
        serialCustomerNumber = (TextView) findViewById(R.id.serialCustomerNumber);
        TxtCollConnectionType = (TextView) findViewById(R.id.TxtCollConnectionType);
        Filter = findViewById(R.id.Filter);
        IMG_Serial_bg = (ImageView) findViewById(R.id.IMG_Serial_bg);
        LayoutCustomerOffline = (TextView) findViewById(R.id.marque_scrolling_text_collect);
        productList = new ArrayList<>();
        Button ClearFilter = findViewById(R.id.ClearFilter);
        ClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filter.getText().clear();
            }
        });
       // serialListDB = new ArrayList<>();
        Filter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= 11) {
                    Filter.setRawInputType(InputType.TYPE_CLASS_TEXT);
                    Filter.setTextIsSelectable(true);
                } else {
                    Filter.setRawInputType(InputType.TYPE_NULL);
                    Filter.setFocusable(true);
                }
                return false;
            }
        });

        Filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                try {
                    mAdapter.getFilter().filter(cs.toString());

                }catch (Exception ex)
                {Log.v("Filter",ex.getMessage());}
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {
                    Filter.getText().clear();
            }
        });


        db_order = new OrderDBHelper(this);
        db_serial = new SerialDBHelper(this);

        DialogFinsih = new AlertDialog.Builder(CollectSerialActivity.this);

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcast();

        //check connectivity
        connectivity = new Connectivity();
        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
            TxtCollConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtCollConnectionType.setText("Online");
        } else {
            OfflineMode = true;
            TxtCollConnectionType.setTextColor(Color.RED);
            TxtCollConnectionType.setText("Offline");
        }

        StringOfSerials = "";
        dialogHint = new DialogHint();

        productList.addAll(db_order.getAllOrderTocollectserial());

        try {
            serialCustomerName.setText(SelectedCustomerName);
            serialCustomerNumber.setText(SelectedCustomerNumber);
        }catch (Exception e){
            Toast.makeText(getBaseContext(),"Error ! please restart application",Toast.LENGTH_SHORT).show();
        }


        //set table of items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CollectSerialTableAdapter(productList, getBaseContext(), this);
        recyclerView.setAdapter(adapter);
        //end of table

        //get server  config id from shared SharedPreferences
        prefs = getSharedPreferences("login_data", MODE_PRIVATE);
        AllowNegativeQtypref = prefs.getString("My_AllowNegativeQty", "");


        if (OfflineMode == true) {
            db_sync = new SyncDBHelper(getBaseContext());
            serialList = new ArrayList<>();
            serialList.addAll(db_sync.getAllGetStockSerialsOffline());



            Log.d("SERIAL_LIST", serialList.toString());
            mRecyclerView.setHasFixedSize(true);

            mAdapter = new SerialAdapter(SelectedCustomerNumber,serialList, getBaseContext(), CollectSerialActivity.this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(mAdapter);



            mProgressBar.setVisibility(View.GONE);
        } else {
            loadingProgressDialog = ProgressDialog.show(CollectSerialActivity.this, "",
                    "Loading. Please wait...", true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new API_Online().SFA_GetStockSerials(CollectSerialActivity.this, getBaseContext(),mRecyclerView, mProgressBar, loadingProgressDialog);
                }
            }, 2000);
        }


        btnSaveSerials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productList.size() < 1) {
                    Toast.makeText(getBaseContext(), "no item added !", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!dialogHint.GPS_Dialog(CollectSerialActivity.this)) {
                    return;
                }

                int CheckCounter = 0; //counter for check quantity and serials collected if equal
                for (Product p : productList) {
                    if (Integer.parseInt(p.QTY) == p.CollectedSerials) {
                        CheckCounter += 1;
                    }
                }
                if (CheckCounter != db_order.numberOfRowsOrder()) {
                    dialogHint.showSerialNotColletedAlert(CollectSerialActivity.this);
                    return;
                } else {
                    showSaveDialog();
                    dialogbtncloseOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialogbtnFinsihOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (TextUtils.isEmpty(TxtCommentOrder.getText())) {
                                TxtCommentOrder.setError("Comment is Required !");
                                return;
                            }
                            dialogHint.getLocation(CollectSerialActivity.this);

                            AlertDialog dialog = DialogFinsih.create();
                            dialog.dismiss();

                            if (OfflineMode == true) { //offline
                               if(emailpref == null)
                               {
                                   new DialogHint().Session_End(CollectSerialActivity.this,getBaseContext());
                               }
                               else {
                                   db_sync = new SyncDBHelper(getBaseContext());
                                   db_order = new OrderDBHelper(getBaseContext());
                                   db_serial = new SerialDBHelper(getBaseContext());
                                  // serialListDB.addAll(db_serial.getAllSerials());

                                   float total = 0;
                                   for (Product product : productList) {
                                       total += Float.valueOf(product.Total);
                                   }

                                   String Headerid = String.valueOf(db_sync.InsertTransactionsOffline(ServerConfigIDpref, SelectedCustomerNumber, SelectedCustomerName,emailpref, Long, Lat, TxtCommentOrder.getText().toString(), "Order", "2", String.valueOf(total)));
                                   db_sync.InsertTransactionTenderOffline(Headerid, "Cash      ", emailpref, String.valueOf(total));
                                   //update Header Flag to Real Header Flag
                                   db_serial.update_HeaderId_Offline(Headerid);

                                   String DetailId = "";
                                   for (Product product : productList) {
                                       DetailId = String.valueOf(db_sync.InsertTransactionItemsOffline(Headerid, product.SKU, product.QTY, String.valueOf(product.UnitPrice), "0", emailpref, product.OnHand,product.Subinventory));

                                       String serials = getSerialsByItemCode(getBaseContext(),Headerid ,product.SKU);
                                       db_sync.InsertTransactionSerialsOffline(Headerid, DetailId, serials, product.SKU);
                                   }
                                   Promotions prom = new Promotions().FindPromo(getBaseContext(),Headerid);
                                   String total_order =db_sync.GetOrder_Price(Headerid);

                                   Intent i = new Intent(getBaseContext(), FinishOfflineActivity.class);
                                   i.putExtra("header", Headerid);
                                   i.putExtra("total", total_order);
                                   i.putExtra("TransactionType", "2");
                                   if(prom != null)
                                       i.putExtra("prom", prom.getPromNote());
                                   else
                                       i.putExtra("prom", "No Promotion");

                                   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                   startActivity(i);
                                   finish();
                                   try {
                                       db_order.deleteAll();
                                   } catch (Exception e) {
                                       dialogSave.dismiss();
                                       Toast.makeText(getBaseContext(), "please Delete your orders manual !", Toast.LENGTH_LONG).show();
                                   }

                               }
                            } else {
                                OnlineSaveOrder();//online
                            }

                        }
                    });
                }
            }
        });
    }

    public void OnlineSaveOrder() {
        dialogSave = ProgressDialog.show(CollectSerialActivity.this, "",
                "Saving. Please wait...", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
       // Changed 14-11-2019 Ahmed Ali
               /* new API_Online().SavePreOrderHeader(getBaseContext(), CollectSerialActivity.this
                        , dialogSave, "order", User.ServerConfigID, SelectedCustomerNumber, User.Username, dialogHint.Long, dialogHint.Lat
                        , TxtCommentOrder.getText().toString(), "Order", "2", "2", "Online");

             new API_Online().SavePreOrderHeader(getBaseContext(), CollectSerialActivity.this
                        , dialogSave, "order", User.ServerConfigID, SelectedCustomerNumber, User.Username, dialogHint.Long, dialogHint.Lat
                        , TxtCommentOrder.getText().toString(), "Order", "2", "2", "Online");
*/

                new API_Online().SFA_SavaOrder_OneBulk(getBaseContext(), CollectSerialActivity.this
                        , dialogSave, "order", User.ServerConfigID, SelectedCustomerName,SelectedCustomerNumber, User.Username, dialogHint.Long, dialogHint.Lat
                        , TxtCommentOrder.getText().toString(), "Order", "Transfer  ", "2", "Online");



            }
        }, 4000);






    }

    public List<Serial> RenderList( List<Serial> serialList, final RecyclerView mRecyclerView, final Context context, final Activity activity, final ProgressBar progressBar) {
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new SerialAdapter(SelectedCustomerNumber,serialList, context, activity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
        this.serialList = serialList;

        return serialList;
    }

    private void showSaveDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_finish_order,null);
        dialogbtncloseOrder = (Button) view.findViewById(R.id.dialogbtncloseOrder);
        dialogbtnFinsihOrder = (Button) view.findViewById(R.id.dialogbtnFinsihOrder);
        TxtCommentOrder = (EditText) view.findViewById(R.id.TxtCommentOrder);
        TxtCollectedSerials_Total = (TextView) view.findViewById(R.id.TxtCollectedSerials_Total);
        TxtCollectedSerials_Date = (TextView) view.findViewById(R.id.TxtCollectedSerials_Date);
        try {
            float totalprice = 0;
            for(Product p:productList) {
                totalprice = totalprice + Float.valueOf(p.Total);
            }
            TxtCollectedSerials_Total.setText(Float.toString(totalprice));
            TxtCollectedSerials_Date.setText(VisitDate);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Error ! please restart application",Toast.LENGTH_SHORT).show();
        }
        DialogFinsih.setView(view);
        dialog = DialogFinsih.create();
        dialog.show();

    }


    public static void changeTxtConnectionTypeCollectSerialActivity(boolean value) {
        if (value) {
            TxtCollConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtCollConnectionType.setText("Online");
            LayoutCustomerOffline.setVisibility(View.GONE);
        } else {
            TxtCollConnectionType.setTextColor(Color.RED);
            TxtCollConnectionType.setText("Offline");
            LayoutCustomerOffline.setVisibility(View.VISIBLE);
            LayoutCustomerOffline.setSelected(true);
        }
    }

    /*
     * # change date 29/12/2019
     * © changed by Ahmed Ali
     * -- description: .........
     * Prepare list of serials to put it at json array object
     *  Use IN case offline and back online or online submit Direct
     * online always take header id 0
     */


    public ArrayList<String> List_getSerialsByItemCode(String HeaderID,Context context,String ItemCode) {
        ArrayList<String> serials = new ArrayList<>();
        Cursor cursor;
        String SerialNumber = "";
        OrderDBHelper orderDBHelper = new OrderDBHelper(context);
        SyncDBHelper syncDBHelper = new SyncDBHelper(context);
        db_serial = new SerialDBHelper(context);



        cursor = db_serial.getDatabyItemCode(HeaderID,ItemCode);
        if (cursor.moveToFirst()) {
            do {

                    SerialNumber = cursor.getString(cursor.getColumnIndex("SerialNumber"));
                    serials.add(SerialNumber);
                    db_serial.deleteSerial(HeaderID,SerialNumber);
                    //syncDBHelper.Update_StockSerialsStatusOffline(SerialNumber, "false");

            } while (cursor.moveToNext());


                // update serial flag to false to avoid see it checked when select the same item in another order.
            if(serials.size() > 0)
                orderDBHelper.updateCollectedSerial(ItemCode,"0");


        }
        cursor.close();

        return serials;
    }

    /*
     * # change date 29/12/2019
     * © changed by Ahmed Ali
     * -- description: .........
     * Use only when save order offline not important so much but helpful
     * offline take a header id 0 for a temp value till create order
     */
   public String getSerialsByItemCode(Context context, String HeaderID,String ItemCode) {
       Cursor cursor;
        String SerialNumber = "";
        db_serial = new SerialDBHelper(context);

        /* test serials
       List<Serial> DB_serials = new ArrayList<>();
       DB_serials = db_serial.getAllSerials();*/

        SyncDBHelper syncDBHelper = new SyncDBHelper(context);
        OrderDBHelper orderDBHelper = new OrderDBHelper(context);
        cursor = db_serial.getDatabyItemCode(HeaderID,ItemCode);
        if (cursor.moveToFirst()) {
            do {
                String DB_SerialNumber = cursor.getString(cursor.getColumnIndex("SerialNumber"));
                if (SerialNumber == "") {
                    SerialNumber = DB_SerialNumber;
                    //db_serial.deleteSerial(SerialNumber);
                } else {
                    SerialNumber = SerialNumber + ";" + DB_SerialNumber;
                    //db_serial.deleteSerial(cursor.getString(cursor.getColumnIndex("SerialNumber")));
                }

                // update serial flag to false to avoid see it checked when select the same item in another order.
                //syncDBHelper.Update_StockSerialsStatusOffline(DB_SerialNumber, "false");

            } while (cursor.moveToNext());

            if(!SerialNumber.equals("")) {
                orderDBHelper.updateCollectedSerial(ItemCode, String.valueOf(0));
            }

        }
        cursor.close();

        return (SerialNumber.equals(""))? null : SerialNumber;
    }

    public void search(String s) {
        try {
            List<Serial> newList = new ArrayList<>();
            for (Serial n : serialList) {
                if (n.getItemCode().equals(s)) {
                    newList.add(n);
                }
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            TxtCollectedSerials_Choose.setVisibility(View.GONE);
            IMG_Serial_bg.setVisibility(View.GONE);

              mAdapter.updateList(newList);

            if (newList.size() <= 0) {
                TxtCollectedSerials_notFound.setVisibility(View.VISIBLE);
            } else {
                TxtCollectedSerials_notFound.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Please select item again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void RefreshTable() {
        productList = new ArrayList<>();
        productList.addAll(db_order.getAllOrderTocollectserial());
        adapter = new CollectSerialTableAdapter(productList, getBaseContext(), this);
        recyclerView.setAdapter(adapter);
    }

    private void registerNetworkBroadcast() {
        try {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
