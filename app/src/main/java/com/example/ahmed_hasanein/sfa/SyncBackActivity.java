package com.example.ahmed_hasanein.sfa;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.SyncBackAdapter;
import Model.SyncBack;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.HistoryActivity.dialogSync;

public class SyncBackActivity extends AppCompatActivity {
    RecyclerView recyclerSyncBack;
    List<SyncBack> syncBackList;
    SyncDBHelper db_sync;
    ImageView IMG_Sync_True,IMG_Sync_Warning;
    ProgressBar progress_bar_sync_back_logo;
    Button btnDoneSyncBack;
    TextView TxtSyncHint,txtVNumberSyncBack;
    FragmentManager fm;
    ProgressBar progress_bar_sync_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_back);
        recyclerSyncBack = (RecyclerView) findViewById(R.id.recyclerSyncBack);
        progress_bar_sync_back = (ProgressBar) findViewById(R.id.progress_bar_sync_back);
        IMG_Sync_True = (ImageView) findViewById(R.id.IMG_Sync_True);
        IMG_Sync_Warning = (ImageView) findViewById(R.id.IMG_Sync_Warning);
        progress_bar_sync_back_logo = (ProgressBar) findViewById(R.id.progress_bar_sync_back_logo);
        TxtSyncHint = (TextView) findViewById(R.id.TxtSyncHint);
        txtVNumberSyncBack = (TextView) findViewById(R.id.txtVNumberSyncBack);
        btnDoneSyncBack = (Button) findViewById(R.id.btnDoneSyncBack);
        txtVNumberSyncBack.setText("Version "+BuildConfig.VERSION_NAME+","+BuildConfig.VERSION_CODE);

        String HeaderIdOffline="";
        HeaderIdOffline = getIntent().getStringExtra("HeaderID");
        {

        }

        fm = getSupportFragmentManager();
        dialogSync.dismiss();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress_bar_sync_back_logo.setVisibility(View.GONE);
                progress_bar_sync_back.setVisibility(View.GONE);
                syncBackList = new ArrayList<>();
                db_sync = new SyncDBHelper(getBaseContext());
                syncBackList.addAll(db_sync.getAllSyncBackOffline());

                recyclerSyncBack.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                SyncBackAdapter adapter = new SyncBackAdapter(syncBackList,getBaseContext(),fm,SyncBackActivity.this);
                recyclerSyncBack.setAdapter(adapter);
                if(db_sync.checkiSyncBackOfflineAnyErrors()==false) {
                    IMG_Sync_True.setVisibility(View.VISIBLE);
                }else{
                    IMG_Sync_Warning.setVisibility(View.VISIBLE);
                }
                TxtSyncHint.setText("Transaction Synced Online");
            }
        },3000);



        btnDoneSyncBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db_sync.checkiSyncBackOfflineAnyErrors()==false) {
                    finish();
                }else{
                    Toast.makeText(getBaseContext(),"Please Check list before close",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void StartProgressloop(){
        IMG_Sync_True.setVisibility(View.GONE);
        IMG_Sync_Warning.setVisibility(View.GONE);
        progress_bar_sync_back_logo.setVisibility(View.VISIBLE);
    }
    public void RefreshList(){
        dialogSync.dismiss();
        syncBackList = new ArrayList<>();
        db_sync = new SyncDBHelper(getBaseContext());

        progress_bar_sync_back_logo.setVisibility(View.GONE);
        if(db_sync.checkiSyncBackOfflineAnyErrors()==false) {
            IMG_Sync_True.setVisibility(View.VISIBLE);
        }else{
            IMG_Sync_Warning.setVisibility(View.VISIBLE);
        }

        syncBackList.addAll(db_sync.getAllSyncBackOffline());

        recyclerSyncBack.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        SyncBackAdapter adapter = new SyncBackAdapter(syncBackList,getBaseContext(),fm,SyncBackActivity.this);
        recyclerSyncBack.setAdapter(adapter);
    }
}
