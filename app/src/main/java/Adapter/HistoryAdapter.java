package Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ahmed_hasanein.sfa.HistoryActivity;
import com.example.ahmed_hasanein.sfa.HistoryDetailActivity;
import com.example.ahmed_hasanein.sfa.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import API.API_Online;
import Model.PreOrderHistory;
import Utility.DialogHint;
import Utility.ImageProcessClass;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static Utility.ImageProcessClass.Media_access_Permisionn;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.PreOrderHolder> {
    List<PreOrderHistory> preOrderHistoryList;
    private Context context;
    private Activity activity;
    public static ProgressDialog dialogCancel;
    static  boolean cancel=false;
    SyncDBHelper db_sync;
    SerialDBHelper serialDBHelper;
    public HistoryAdapter(List<PreOrderHistory> preOrderHistoryList, Context context, Activity activity) {
        this.preOrderHistoryList = preOrderHistoryList;
        this.context = context;
        this.activity = activity;
    }

    public HistoryAdapter() {
    }

    @NonNull
    @Override
    public PreOrderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_history,viewGroup,false);
        PreOrderHolder holder = new PreOrderHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PreOrderHolder preOrderHolder, final int i) {
        final PreOrderHistory preOrderHistory = preOrderHistoryList.get(i);
        preOrderHolder.TxtPreOrderId.setText(preOrderHistory.ID);
        preOrderHolder.TxtPreOrderCustomerNumber.setText(preOrderHistory.CustomerNumber);
        preOrderHolder.TxtPreOrderTotal.setText(preOrderHistory.Total);
        preOrderHolder.TxtPreOrderStatus.setText(preOrderHistory.Status);

        final String id = preOrderHistory.ID;
        final String status = preOrderHistory.Status;
        if(status.equals("New")||status.equals("new")||status.equals("NEW")){
            {  preOrderHolder.btnDeleteHistory.setVisibility(View.VISIBLE);
                preOrderHolder.view3.setVisibility(View.VISIBLE);
                preOrderHolder.btnUploadreceipt.setVisibility(View.VISIBLE);
            }
        }else if(status.equals("Offline")||status.equals("offline")){
            preOrderHolder.btnDeleteHistory.setText("Delete");
            preOrderHolder.btnDeleteHistory.setVisibility(View.VISIBLE);
            preOrderHolder.view3.setVisibility(View.GONE);
            preOrderHolder.btnUploadreceipt.setVisibility(View.GONE);
        }

        preOrderHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("Offline")||status.equals("offline")){
                    Intent i = new Intent(activity, HistoryDetailActivity.class);
                    i.putExtra("PreOrderID", preOrderHistory.ID);
                    i.putExtra("OpenFromSyncBackDialog", true);
                    //i.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    activity.startActivity(i);
                }else {
                    Intent i = new Intent(activity, HistoryDetailActivity.class);
                    i.putExtra("PreOrderID", preOrderHistory.ID);
                   // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(i);
                }
            }
        });

        preOrderHolder.btnUploadreceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HistoryActivity().setPreOrderHistory_ID(preOrderHistory.ID);
                new HistoryActivity().setPreOrderHistory_customerNumber(preOrderHistory.CustomerNumber);
                new ImageProcessClass().Media_access_Permisionn(activity,context);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);


            }
        });

        preOrderHolder.btnDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("Offline") || status.equals("offline")) {
                    db_sync = new SyncDBHelper(context);
                    serialDBHelper = new SerialDBHelper(context);
                    try {
                        db_sync.deleteFromTransactionOffline(Integer.valueOf(id));
                        db_sync.deleteFromTransactionItemsOffline(id);
                        db_sync.deleteFromTransactionTenderOffline(id);

                        preOrderHistoryList.remove(i);
                        notifyItemRemoved(i);
                        notifyItemRangeChanged(i,preOrderHistoryList.size());
                        Toast.makeText(context,"Deleted !",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    try {
                        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                        alertDialog.setMessage("Are You Sure Cancel this Order ?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        HistoryAdapter historyAdapter = new HistoryAdapter();
                                        if (preOrderHistory.ID != null) {
                                            dialogCancel = ProgressDialog.show(activity, "",
                                                    "Canceling. Please wait...", true);

                                            //historyAdapter.RequestForCancel(preOrderHistory.ID, context);
                                            cancel = new API_Online().RequestForCancel(preOrderHistory.ID, context,dialogCancel);

                                            if (cancel) {
                                                preOrderHolder.btnDeleteHistory.setVisibility(View.INVISIBLE);
                                                preOrderHolder.TxtPreOrderStatus.setText("request sent !");
                                            } else {
                                                if (!preOrderHolder.TxtPreOrderStatus.getText().equals("Request For Cancel"))
                                                    preOrderHolder.TxtPreOrderStatus.setText("not canceled !");
                                            }

                                        } else {
                                            Toast.makeText(context, "Can not Delete this order !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        alertDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return preOrderHistoryList.size();
    }
    class PreOrderHolder extends RecyclerView.ViewHolder{
        TextView TxtPreOrderId , TxtPreOrderCustomerNumber,TxtPreOrderTotal,TxtPreOrderStatus;
        Button btnDeleteHistory,btnUploadreceipt;
        View view3;


        public PreOrderHolder(@NonNull View itemView) {
            super(itemView);
            TxtPreOrderId = (TextView) itemView.findViewById(R.id.TxtPreOrderId);
            TxtPreOrderCustomerNumber = (TextView) itemView.findViewById(R.id.TxtPreOrderCustomerNumber);
            TxtPreOrderTotal = (TextView) itemView.findViewById(R.id.TxtPreOrderTotal);
            TxtPreOrderStatus = (TextView) itemView.findViewById(R.id.TxtPreOrderStatus);
            btnDeleteHistory = (Button) itemView.findViewById(R.id.btnDeleteHistory);
            btnUploadreceipt = (Button) itemView.findViewById(R.id.btnUploadreceipt);
            view3 = itemView.findViewById(R.id.view3);

        }
    }

//    public void RequestForCancel(final String orderId, final Context context){
//        String urlCancel;
//
//        urlCancel = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_RequestForCancel/"+orderId;
//        StringRequest cancelRequest = new StringRequest(Request.Method.GET, urlCancel, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject object = new JSONObject(response);
//                    String result = object.getString("SFA_RequestForCancelResult");
//                    if (result.equals("1")) {
//                        dialogCancel.dismiss();
//                        cancel=true;
//                    }else{
//                        dialogCancel.dismiss();
//                        Toast.makeText(context,"Can not Cancel this order try again later !",Toast.LENGTH_LONG).show();
//                    }
//                }catch (JSONException e){
//                    dialogCancel.dismiss();
//                    Toast.makeText(context,"Error",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dialogCancel.dismiss();
//                Toast.makeText(context,"Please, Check Internet Connection",Toast.LENGTH_LONG).show();
//            }
//
//        });
//        Volley.newRequestQueue(context).add(cancelRequest);
//    }
}
