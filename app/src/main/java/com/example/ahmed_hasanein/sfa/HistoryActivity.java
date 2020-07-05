package com.example.ahmed_hasanein.sfa;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import API.API_Online;
import API.API_Sync_Back;
import Adapter.HistoryAdapter;
import Model.Header;
import Model.PreOrderHistory;
import Model.User;
import Utility.Connectivity;
import Utility.DialogHint;
import Utility.ImageProcessClass;
import preview_database.DB.SyncDB.SyncDBHelper;

public class HistoryActivity extends AppCompatActivity {

    String url;
    List<PreOrderHistory> preOrderHistoryList;
    RecyclerView recyclerView;
    HistoryAdapter adapter;
    ProgressBar mProgressBar;
    TextView txtVNumberHistory;
    LinearLayout txtHintHistory;
    Bundle extras;
    boolean OpenFromSyncBackDialog=false,OpenFromOfflineIcon=false;
    SyncDBHelper db_sync;
    Button btnDoneSyncHistory;
    API_Sync_Back api_syncBack;
    List<Header> headerList;
    public static ProgressDialog dialogSync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        txtHintHistory = (LinearLayout) findViewById(R.id.txtHintHistory);
        txtVNumberHistory = (TextView) findViewById(R.id.txtVNumberHistory);
        btnDoneSyncHistory = (Button) findViewById(R.id.btnDoneSyncHistory);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtVNumberHistory.setText("Version "+BuildConfig.VERSION_NAME+","+BuildConfig.VERSION_CODE); //set textview for version number

        extras = getIntent().getExtras();
        if(extras!=null){
            OpenFromSyncBackDialog = extras.getBoolean("OpenFromSyncBackDialog");
            OpenFromOfflineIcon = extras.getBoolean("OpenFromOfflineIcon");
            if(OpenFromSyncBackDialog==true||OpenFromOfflineIcon==true) {
                //////offline transaction list ////////
                OfflineTransaction();
                //////////////////////////////////////
            }
        }else{
            ///////online transaction list //////////
            getSupportActionBar().setTitle("Online History");
            new API_Online().SFA_Search_PreOrders(getBaseContext(),HistoryActivity.this,recyclerView,mProgressBar,txtHintHistory);
            ///////////////////////////////////////
        }

        btnDoneSyncHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkConnectivity = new Connectivity().isConnectedFast(getBaseContext());
                if (preOrderHistoryList.size() >= 1) {
                    if(checkConnectivity==false){
                        Toast.makeText(getBaseContext(),"Check internet connection",Toast.LENGTH_SHORT).show();
                    }else {
                        dialogSync = ProgressDialog.show(HistoryActivity.this, "",
                                "Sync.. Please wait...", true);

                        api_syncBack = new API_Sync_Back();
                        db_sync = new SyncDBHelper(getBaseContext());
                        headerList = new ArrayList<>();


                        new AsyncTask<String, Integer, String>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                headerList.addAll(db_sync.getAllTransactionsOffline());
                                // Every one loop create a new ordere
                                for (Header h : headerList) {
                                    //int countofAllItems = db_sync.getcountofAllItemsByHeaderID(String.valueOf(h.getId()));
                                 /*   api_syncBack.SavePreOrderHeader(countofAllItems, HistoryActivity.this, getBaseContext(), h.getCountryCode(), h.getCustomerNumber(), h.getSubmitter()
                                            , h.getLong(), h.getLat(), h.getComment(), h.getDeliveryMethod(), h.getTransactionType(), String.valueOf(h.getId()), "New");

                               */


                                    api_syncBack.SavePreOrderHeader_OneBulk(headerList.size(), HistoryActivity.this, getBaseContext(), h.getCountryCode(), h.getCustomerNumber(), h.getSubmitter()
                                            , h.getLong(), h.getLat(), h.getComment(), h.getDeliveryMethod(), h.getTransactionType(), String.valueOf(h.getId()), h.getPromoID(),"New");


                                }
                            }

                            @Override
                            protected String doInBackground(String... strings) {
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                            }
                        }.execute();
                    }
                }else{
                    Toast.makeText(getBaseContext(),"No Transaction Found",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void OfflineTransaction(){
        getSupportActionBar().setTitle("Offline History");

        preOrderHistoryList = new ArrayList<>();
        db_sync = new SyncDBHelper(getBaseContext());
        for (Header header : db_sync.getAllTransactionsOffline()) {
            PreOrderHistory preOrderHistory = new PreOrderHistory(String.valueOf(header.getId()), "", header.getCustomerNumber(), "Offline", header.getTotal());
            preOrderHistoryList.add(preOrderHistory);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        adapter = new HistoryAdapter(preOrderHistoryList,getBaseContext(),HistoryActivity.this);
        recyclerView.setAdapter(adapter);
        mProgressBar.setVisibility(View.GONE);
        if(preOrderHistoryList.size()<=0){
            txtHintHistory.setVisibility(View.VISIBLE);
        }

        btnDoneSyncHistory.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPause() {
        super.onPause();
       // finish();
    }


    public static final int PICK_IMAGE = 1;



    private static String preOrderHistory_ID,preOrderHistory_customerNumber;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {

            new DialogHint().UploadAttachment(this,getBaseContext(),getPreOrderHistory_ID(),getPreOrderHistory_customerNumber(), User.Username,data);
        }
        else
            Toast.makeText(this,"Failed to select image",Toast.LENGTH_SHORT).show();
    }



    public static String getPreOrderHistory_ID() {
        return preOrderHistory_ID;
    }

    public static void setPreOrderHistory_ID(String preOrderHistory_ID) {
        HistoryActivity.preOrderHistory_ID = preOrderHistory_ID;
    }

    public static String getPreOrderHistory_customerNumber() {
        return preOrderHistory_customerNumber;
    }

    public static void setPreOrderHistory_customerNumber(String preOrderHistory_customerNumber) {
        HistoryActivity.preOrderHistory_customerNumber = preOrderHistory_customerNumber;
    }
}
