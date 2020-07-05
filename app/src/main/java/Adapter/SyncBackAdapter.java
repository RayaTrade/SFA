package Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ahmed_hasanein.sfa.R;
import com.example.ahmed_hasanein.sfa.SynBackItemFragment;
import com.example.ahmed_hasanein.sfa.SyncBackActivity;
import java.util.ArrayList;
import java.util.List;

import API.API_Sync_Back;
import Model.Header;
import Model.SyncBack;
import preview_database.DB.SyncDB.SyncDBHelper;

public class SyncBackAdapter extends RecyclerView.Adapter<SyncBackAdapter.SyncBackHolder> {
    List<SyncBack> syncBackList;
    Context context;
    SyncDBHelper db_sync;
    FragmentManager fm;
    Activity activity;
    SynBackItemFragment synBackItemFragment;
    API_Sync_Back api_syncBack;
    List<Header> headerList;
    public static ProgressDialog dialogSync;
    public SyncBackAdapter(List<SyncBack> syncBackList, Context context,FragmentManager fm,Activity activity) {
        this.syncBackList = syncBackList;
        this.context = context;
        this.fm = fm;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SyncBackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_sync_back,viewGroup,false);
        SyncBackHolder holder = new SyncBackHolder(row);
        return holder;
    }
    public static ProgressDialog dialogitem;
    @Override
    public void onBindViewHolder(@NonNull final SyncBackHolder viewHolder, int i) {
        final SyncBack syncBack = syncBackList.get(i);

        db_sync = new SyncDBHelper(context);
        viewHolder.Txtsyncbacktype.setText(syncBack.getType());
        viewHolder.TxtsyncbackOfflineId.setText(syncBack.getId_Offline());
        viewHolder.TxtsyncbackItemSaved.setText(syncBack.getItemSaved());

        if(syncBack.getId_Online().equals("0")){
            viewHolder.TxtsyncbacknlineId.setText("Not Saved");
            viewHolder.TxtsyncbacknlineId.setTextColor(Color.RED);
            viewHolder.btnsyncback.setText("Try Again");
            if(syncBack.getItemSaved().equals("Error")){
                viewHolder.TxtsyncbackItemSaved.setTextColor(Color.RED);
            }
        }else{
            viewHolder.TxtsyncbacknlineId.setText(syncBack.getId_Online());
            viewHolder.TxtsyncbacknlineId.setTextColor(Color.parseColor("#32CD32"));
            if(syncBack.getItemSaved().equals("Saved")){
                viewHolder.TxtsyncbackItemSaved.setTextColor(Color.parseColor("#32CD32"));
                viewHolder.btnsyncback.setText("Show");
            }else if(syncBack.getItemSaved().equals("Error")){
                viewHolder.TxtsyncbackItemSaved.setTextColor(Color.RED);
                viewHolder.btnsyncback.setText("Try Again");
            }else{
                viewHolder.btnsyncback.setText("Try Again");
            }

        }
        viewHolder.TxtsyncbackCustNum.setText(syncBack.getCustNumber());
        viewHolder.btnsyncback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(syncBack.getId_Online().equals("0")||viewHolder.btnsyncback.getText().equals("Try Again")){
                    viewHolder.TxtsyncbacknlineId.setText("Sync..");
                    viewHolder.TxtsyncbacknlineId.setTextColor(Color.parseColor("#1565C0"));//blue
                    viewHolder.TxtsyncbackItemSaved.setText("Sync..");
                    viewHolder.TxtsyncbackItemSaved.setTextColor(Color.parseColor("#1565C0"));//blue
                    //sync again
                    dialogSync = ProgressDialog.show(activity, "",
                            "Sync Again. Please wait...", true);

                    api_syncBack = new API_Sync_Back();
                    db_sync = new SyncDBHelper(context);
                    headerList = new ArrayList<>();


                    new AsyncTask<String,Integer,String>(){
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            ((SyncBackActivity)activity).StartProgressloop();
                            if(!viewHolder.TxtsyncbacknlineId.getText().toString().equals("0")) {
                                api_syncBack.SFA_InCompletedTransaction_Removed(context, syncBack.getId_Online());
                            }
                        }
                        @Override
                        protected String doInBackground(String... strings) {
                            return null;
                        }
                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            headerList.addAll(db_sync.getAllTransactionsOfflineByItemID(Integer.valueOf(syncBack.getId_Offline())));
                            int countofAllItems = db_sync.getcountofAllItemsByHeaderID(syncBack.getId_Offline());
                            for (Header h:headerList) {
                           /*     api_syncBack.SavePreOrderHeader(countofAllItems,activity,context,h.getCountryCode(),h.getCustomerNumber(),h.getSubmitter()
                                        ,h.getLong(),h.getLat(),h.getComment(),h.getDeliveryMethod(),h.getTransactionType(),syncBack.getId_Online(),"Update");
                           */
                                api_syncBack.SavePreOrderHeader_OneBulk(countofAllItems,activity,context,h.getCountryCode(),h.getCustomerNumber(),h.getSubmitter()
                                        ,h.getLong(),h.getLat(),h.getComment(),h.getDeliveryMethod(),h.getTransactionType(),syncBack.getId_Online(),h.getPromoID(),"Update");


                            }
                            dialogSync.dismiss();

                        }
                    }.execute();
                }else{
                    synBackItemFragment=new SynBackItemFragment();
                    dialogitem = ProgressDialog.show(activity, "",
                            "Loading. Please wait...", true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            synBackItemFragment.GetDetails(viewHolder.TxtsyncbacknlineId.getText().toString(),context);
                            synBackItemFragment.show(fm,"");
                        }
                    },500);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return syncBackList.size();
    }
    class SyncBackHolder extends RecyclerView.ViewHolder{
        TextView Txtsyncbacktype,TxtsyncbackOfflineId,TxtsyncbacknlineId,TxtsyncbackItemSaved,TxtsyncbackCustNum;
        Button btnsyncback;
        public SyncBackHolder(@NonNull View itemView) {
            super(itemView);
            Txtsyncbacktype = (TextView) itemView.findViewById(R.id.Txtsyncbacktype);
            TxtsyncbackOfflineId = (TextView) itemView.findViewById(R.id.TxtsyncbackOfflineId);
            TxtsyncbacknlineId = (TextView) itemView.findViewById(R.id.TxtsyncbacknlineId);
            TxtsyncbackItemSaved = (TextView) itemView.findViewById(R.id.TxtsyncbackItemSaved);
            TxtsyncbackCustNum = (TextView) itemView.findViewById(R.id.TxtsyncbackCustNum);
            btnsyncback = (Button) itemView.findViewById(R.id.btnsyncback);
        }
    }

    public void updateList(List<SyncBack> newlist){
        syncBackList =new ArrayList<>();
        syncBackList.addAll(newlist);
        notifyDataSetChanged();
    }
}
