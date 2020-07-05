package Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ahmed_hasanein.sfa.CollectSerialActivity;
import com.example.ahmed_hasanein.sfa.R;

import java.util.ArrayList;
import java.util.List;

import Model.Product;


public class CollectSerialTableAdapter extends RecyclerView.Adapter<CollectSerialTableAdapter.Collect_Serial_TableHolder> {
    List<Product> productList;
    private Context context;
    private Activity activity;

    public CollectSerialTableAdapter(List<Product> productList, Context context, Activity activity) {
        this.productList = productList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CollectSerialTableAdapter.Collect_Serial_TableHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_collect_serial_table, viewGroup, false);
        CollectSerialTableAdapter.Collect_Serial_TableHolder holder = new CollectSerialTableAdapter.Collect_Serial_TableHolder(row);
        return holder;
    }

    int row_index;
    boolean clicked = false;

    @Override
    public void onBindViewHolder(@NonNull final Collect_Serial_TableHolder viewHolder, final int i) {
        final Product product = productList.get(i);
        viewHolder.RowSKU = product.SKU;
        viewHolder.RowModel = product.Model;
        viewHolder.RowQuantity = product.QTY;
        viewHolder.RowCollectedSerials = String.valueOf(product.CollectedSerials);

        viewHolder.TxtSKURow.setText(product.SKU);
        viewHolder.TxtModelRow.setText(product.Model);
        viewHolder.TxtQtyRow.setText(product.QTY);

        if (product.CollectedSerials < Integer.parseInt(product.QTY)) {
            viewHolder.TxtCollectedSerials.setTextColor(Color.parseColor("#FF8F00")); //yellow
        } else if (product.CollectedSerials == Integer.parseInt(product.QTY)) {
            viewHolder.TxtCollectedSerials.setTextColor(Color.parseColor("#33691E")); //green
        } else if (product.CollectedSerials >= Integer.parseInt(product.QTY)) {
            viewHolder.TxtCollectedSerials.setTextColor(Color.RED);
        }
        viewHolder.TxtCollectedSerials.setText(String.valueOf(product.CollectedSerials));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = i;
                notifyDataSetChanged();
                clicked = true;
               // ((CollectSerialActivity)activity).search(viewHolder.TxtSKURow.getText().toString());
            }
        });

        if (row_index == i && clicked) {
            viewHolder.row_LinearLayout.setBackground(context.getResources().getDrawable(R.drawable.table_border_selected));
            ((CollectSerialActivity) activity).search(viewHolder.TxtSKURow.getText().toString());
        } else {
            viewHolder.row_LinearLayout.setBackground(context.getResources().getDrawable(R.drawable.table_border));
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    class Collect_Serial_TableHolder extends RecyclerView.ViewHolder {
        LinearLayout row_LinearLayout;
        TextView TxtModelRow, TxtSKURow, TxtQtyRow, TxtCollectedSerials;
        String RowSKU;
        String RowModel;
        String RowQuantity;
        String RowCollectedSerials;

        public Collect_Serial_TableHolder(@NonNull View itemView) {
            super(itemView);
            row_LinearLayout = (LinearLayout) itemView.findViewById(R.id.row_layout_serial_Table);
            TxtSKURow = (TextView) itemView.findViewById(R.id.TxtSKURow);
            TxtModelRow = (TextView) itemView.findViewById(R.id.TxtModelRow);
            TxtQtyRow = (TextView) itemView.findViewById(R.id.TxtQtyRow);
            TxtCollectedSerials = (TextView) itemView.findViewById(R.id.TxtCollectedSerialRow);
            row_LinearLayout.setBackgroundColor(Color.parseColor("#DAE8FC"));
        }
    }

    public void updateList(List<Product> newlist) {
        productList = new ArrayList<>();
        productList.addAll(newlist);
        notifyDataSetChanged();
    }
}
