package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed_hasanein.sfa.R;

import java.util.List;

import Model.PreOrderHistoryDetail;
import preview_database.DB.SyncDB.SyncDBHelper;

public class HistoryDetailAdapter extends RecyclerView.Adapter<HistoryDetailAdapter.OrderHistoryDetailHolder> {
    List<PreOrderHistoryDetail> preOrderHistoryDetailList;
    private Context context;
    private String pageType;
    SyncDBHelper db_sync;

    public HistoryDetailAdapter(List<PreOrderHistoryDetail> preOrderHistoryDetailList, Context context,String pageType) {
        this.preOrderHistoryDetailList = preOrderHistoryDetailList;
        this.context = context;
        this.pageType = pageType;
    }

    @NonNull
    @Override
    public OrderHistoryDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_order_history_detail,viewGroup,false);
        OrderHistoryDetailHolder holder = new OrderHistoryDetailHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHistoryDetailHolder viewHolder,final int i) {
        final PreOrderHistoryDetail preOrderHistoryDetail = preOrderHistoryDetailList.get(i);
        viewHolder.TxtItemCodeHistory.setText(preOrderHistoryDetail.ItemCode);
        if(pageType.equals("Online Page")){
            viewHolder.btnDeleteHistoryDetail.setVisibility(View.GONE);
        }else{
            viewHolder.btnDeleteHistoryDetail.setVisibility(View.VISIBLE);
        }
        if(preOrderHistoryDetail.Brand.equals("")){//open from offline review
            viewHolder.LinearBrand.setVisibility(View.GONE);
        }else{
            viewHolder.TxtPreOrderHistoryBrand.setText(preOrderHistoryDetail.Brand);
        }

        if(preOrderHistoryDetail.Model.equals("")){//open from offline review
            viewHolder.LinearModel.setVisibility(View.GONE);
        }else{
            viewHolder.TxtPreOrderhistoryModel.setText(preOrderHistoryDetail.Model);
        }

        if(preOrderHistoryDetail.Description.equals("")){//open from offline review
            viewHolder.LinearDescription.setVisibility(View.GONE);
        }else{
            viewHolder.TxtPreOrderhistoryDescription.setText(preOrderHistoryDetail.Description);
        }

        viewHolder.TxtPreOrderhistoryQty.setText(preOrderHistoryDetail.Qty);

        if(preOrderHistoryDetail.Total.equals("")){ //open from offline review
            viewHolder.LinearTotal.setVisibility(View.GONE);
        }else{
            viewHolder.TxtPreOrderhistoryTotal.setText(preOrderHistoryDetail.Total);
        }

        viewHolder.TxtPreOrderhistoryUnitPrice.setText(preOrderHistoryDetail.UnitPrice);

        viewHolder.btnDeleteHistoryDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    db_sync = new SyncDBHelper(context);
                    db_sync.deleteFromTransactionItemsOfflineByItemCode(preOrderHistoryDetail.ItemCode);
                    preOrderHistoryDetailList.remove(i);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i,preOrderHistoryDetailList.size());
                    Toast.makeText(context,"Deleted !",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return preOrderHistoryDetailList.size();
    }
    class OrderHistoryDetailHolder extends RecyclerView.ViewHolder{
        TextView TxtItemCodeHistory , TxtPreOrderHistoryBrand,TxtPreOrderhistoryModel
        ,TxtPreOrderhistoryDescription,TxtPreOrderhistoryQty,TxtPreOrderhistoryTotal,TxtPreOrderhistoryUnitPrice;

        LinearLayout LinearBrand,LinearModel,LinearDescription,LinearTotal;

        Button btnDeleteHistoryDetail;

        public OrderHistoryDetailHolder(@NonNull View itemView) {
            super(itemView);
            TxtItemCodeHistory = (TextView) itemView.findViewById(R.id.TxtItemCodeHistory);
            TxtPreOrderHistoryBrand = (TextView) itemView.findViewById(R.id.TxtPreOrderHistoryBrand);
            TxtPreOrderhistoryModel = (TextView) itemView.findViewById(R.id.TxtPreOrderhistoryModel);
            TxtPreOrderhistoryDescription = (TextView) itemView.findViewById(R.id.TxtPreOrderhistoryDescription);
            TxtPreOrderhistoryQty = (TextView)itemView.findViewById(R.id.TxtPreOrderhistoryQty);
            TxtPreOrderhistoryTotal = (TextView) itemView.findViewById(R.id.TxtPreOrderhistoryTotal);
            TxtPreOrderhistoryUnitPrice = (TextView) itemView.findViewById(R.id.TxtPreOrderhistoryUnitPrice);

            btnDeleteHistoryDetail = (Button) itemView.findViewById(R.id.btnDeleteHistoryDetail);

            LinearBrand = (LinearLayout) itemView.findViewById(R.id.LinearBrand);
            LinearModel = (LinearLayout) itemView.findViewById(R.id.LinearModel);
            LinearDescription = (LinearLayout) itemView.findViewById(R.id.LinearDescription);
            LinearTotal = (LinearLayout) itemView.findViewById(R.id.LinearTotal);
        }
    }

}
