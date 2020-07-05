package com.example.ahmed_hasanein.sfa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.SyncBackItemAdapter;
import Model.Items;
import Model.PreOrderHistoryDetail;

import static Adapter.SyncBackAdapter.dialogitem;

public class SynBackItemFragment extends DialogFragment {
    RecyclerView rv;
    SyncBackItemAdapter adapter;
    List<PreOrderHistoryDetail> itemsList;
    ProgressBar progress_bar_fragment_item;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_list_item_sync_back,container);
        rv= (RecyclerView) rootView.findViewById(R.id.recycler_item_sync_back);
        progress_bar_fragment_item = rootView.findViewById(R.id.progress_bar_fragment_item);
        return rootView;
    }
    public void GetDetails(String orderId, final Context context){
        String url = "http://www.rayatrade.com/RayaTradeWCFService/RayaService.svc/SFA_Search_PreOrdersItems/"+orderId;
        StringRequest historyRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("SFA_Search_PreOrdersItemsResult");
                    itemsList = new ArrayList<>();
                    for(int i=0;i<array.length();i++){
                        JSONObject current = array.getJSONObject(i);
                        String ItemCode = current.getString("ItemCode");
                        String Brand = current.getString("Brand");
                        String Model = current.getString("Model");
                        String Description = current.getString("Description");
                        String Qty = current.getString("Qty");
                        String Total = current.getString("Total");
                        String UnitPrice = current.getString("UnitPrice");
                        PreOrderHistoryDetail items = new PreOrderHistoryDetail(ItemCode,Brand,Model,Description,Qty,Total,UnitPrice);
                        itemsList.add(items);

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                //ADAPTER
                rv.setLayoutManager(new LinearLayoutManager(context));
                adapter=new SyncBackItemAdapter(itemsList,context);
                rv.setAdapter(adapter);
                dialogitem.dismiss();
                progress_bar_fragment_item.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"check internet connection",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                dialogitem.dismiss();
            }

        });
        Volley.newRequestQueue(context).add(historyRequest);
    }
}
