package com.example.ahmed_hasanein.sfa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Adapter.HistoryDetailAdapter;
import Model.Items;
import Model.PreOrderHistoryDetail;
import preview_database.DB.SyncDB.SyncDBHelper;

public class HistoryDetailActivity extends AppCompatActivity {

    Bundle extras;
    String url;
    List<PreOrderHistoryDetail> preOrderHistoryDetailList;
    ProgressBar mProgressBar;
    RecyclerView recyclerView;
    HistoryDetailAdapter adapter;
    TextView txtVNumberHistoryDetail;
    SyncDBHelper db_sync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        txtVNumberHistoryDetail = (TextView) findViewById(R.id.txtVNumberHistoryDetail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtVNumberHistoryDetail.setText("Version "+BuildConfig.VERSION_NAME+","+BuildConfig.VERSION_CODE); //set textview for version number

        extras = getIntent().getExtras();
        if(extras!=null){
            String id = extras.getString("PreOrderID");
            if(extras.getBoolean("OpenFromSyncBackDialog")==true){
                ///offline details of transaction //////////
                OfflineDetials(id);
                ///////////////////////////////////////////
            }else {
                ///online details of transaction //////////
                new API_Online().SFA_Search_PreOrdersItems(getBaseContext(),id,recyclerView,mProgressBar);
                getSupportActionBar().setTitle("Online Transaction # "+id);
                ///////////////////////////////////////////
            }
        }else{
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void OfflineDetials(String id){
        db_sync = new SyncDBHelper(getBaseContext());
        preOrderHistoryDetailList = new ArrayList<>();
        for (Items items:db_sync.getAllTransactionItemsOffline(id)) {
            PreOrderHistoryDetail preOrderHistoryDetail = new PreOrderHistoryDetail(items.getItemCode(),"","","",items.getQty(),"",items.getUnitPrice(),items.getSubinventory());
            preOrderHistoryDetailList.add(preOrderHistoryDetail);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        adapter = new HistoryDetailAdapter(preOrderHistoryDetailList,getBaseContext(),"Offline Page");
        recyclerView.setAdapter(adapter);
        mProgressBar.setVisibility(View.GONE);
        getSupportActionBar().setTitle("Offline Transaction # "+id);
    }

}
