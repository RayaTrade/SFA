package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ahmed_hasanein.sfa.R;

import java.util.List;

import Model.Items;
import Model.PreOrderHistoryDetail;

public class SyncBackItemAdapter extends RecyclerView.Adapter<SyncBackItemAdapter.SyncBackItemsHolder> {
    List<PreOrderHistoryDetail> itemsList;
    private Context context;

    public SyncBackItemAdapter( List<PreOrderHistoryDetail> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public SyncBackItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_view_item_sync_back,viewGroup,false);
        SyncBackItemsHolder holder = new SyncBackItemsHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SyncBackItemsHolder viewHolder, int i) {
        final PreOrderHistoryDetail items = itemsList.get(i);
        viewHolder.TxtViewItemCodeSyncBack.setText(items.getItemCode());
        viewHolder.TxtViewQtySyncBack.setText(items.getQty());
        viewHolder.TxtViewSyncBackTotal.setText(items.getTotal());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
    class SyncBackItemsHolder extends RecyclerView.ViewHolder{
        TextView TxtViewItemCodeSyncBack,TxtViewQtySyncBack,TxtViewSyncBackTotal;
        public SyncBackItemsHolder(@NonNull View itemView) {
            super(itemView);
            TxtViewItemCodeSyncBack = (TextView) itemView.findViewById(R.id.TxtViewItemCodeSyncBack);
            TxtViewQtySyncBack = (TextView) itemView.findViewById(R.id.TxtViewQtySyncBack);
            TxtViewSyncBackTotal = (TextView) itemView.findViewById(R.id.TxtViewSyncBackTotal);
        }
    }
}
