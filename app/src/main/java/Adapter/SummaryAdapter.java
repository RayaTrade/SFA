package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed_hasanein.sfa.DetailActivity;
import com.example.ahmed_hasanein.sfa.R;
import com.example.ahmed_hasanein.sfa.SummaryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Model.Product;
import Model.User;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.ProductPreOrderDB.ProductContract;

import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;
import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerNumber;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.SummaryHolder> {
    List<Product> productList;
    private Context context;
    private OrderDBHelper db_order;
    private SerialDBHelper db_serial;
    private  Activity activity;
    public SummaryAdapter(List<Product> productList, Context context, Activity activity) {
        this.productList = productList;
        this.context = context;
        this.activity = activity;

    }

    @NonNull
    @Override
    public SummaryAdapter.SummaryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_summary,viewGroup,false);
        SummaryAdapter.SummaryHolder holder = new SummaryAdapter.SummaryHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SummaryHolder viewHolder,final int i) {
        final Product product = productList.get(i);

        viewHolder.RowSKU=product.SKU;
        viewHolder.RowCategory = product.Category;
        viewHolder.RowBrand = product.Brand;
        viewHolder.RowModel = product.Model;
        viewHolder.RowDescription = product.Description;
        viewHolder.Rowimage = product.image;
        viewHolder.RowUnitPrice = product.UnitPrice;
        viewHolder.RowOnHand = product.OnHand;
        viewHolder.RowColor = product.Color;
        viewHolder.RowCustomer_number = product.Customer_number;
        viewHolder.TxtSummaryproductSKU.setText(product.SKU);
        viewHolder.TxtSummaryproductCategory.setText(product.Category);
        viewHolder.TxtSummaryProductSubinventory.setText(product.getSubinventory());
        viewHolder.TxSummarytproductQty.setText("Quantity: "+product.QTY);

        if( User.Allow_Delivery_Method) {
            if (product.getHEIGHT().equals(""))
                viewHolder.Product_warning.setVisibility(View.VISIBLE);
            else
                viewHolder.Product_warning.setVisibility(View.GONE);
        }
        else
            viewHolder.Product_warning.setVisibility(View.GONE);


//        if(!product.image.equals("")){
//            Picasso.get().load(product.image).error(R.drawable.g_icon).into(viewHolder.IMGSummaryproduct);
//        }

        Picasso.get().load(product.image).error(R.drawable.g_icon).into(viewHolder.IMGSummaryproduct);
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OpenfromOrderPage==false)
                    deleteitemFromPreOrder(product.getSKU(),product.getSubinventory(),i,v);
                else if (OpenfromOrderPage==true)
                    deleteitemFromOrder(product.getSKU(),i,v);
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,DetailActivity.class);
                i.putExtra("productSKU",viewHolder.RowSKU);
                i.putExtra("productCat",viewHolder.RowCategory);
                i.putExtra("productBrand",viewHolder.RowBrand);
                i.putExtra("productBrand",viewHolder.RowModel);
                i.putExtra("productDesc",viewHolder.RowDescription);
                i.putExtra("productImg",viewHolder.Rowimage);
                i.putExtra("productPrice",Float.toString(viewHolder.RowUnitPrice));
                i.putExtra("productQTY",viewHolder.TxSummarytproductQty.getText().toString());
                i.putExtra("customer_number",viewHolder.RowCustomer_number);
                i.putExtra("onHand",viewHolder.RowOnHand);
                i.putExtra("subinventory",product.getSubinventory());
                i.putExtra("openfromSummary",true);
                i.putExtra("Order",true);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                activity.finish();
            }
        });
    }

    public void deleteitemFromPreOrder(final String SKU,final String Subinventory , final int i, final View view){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                context.getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI,
                        ProductContract.ProductEntry.COLUMN_PRODUCT_SKU + " = '" + SKU+"' and "
                        + ProductContract.ProductEntry.COLUMN_PRODUCT_product_Subinventory + " = '"+Subinventory+ "'"
                        , null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(context,"Item Deleted !",Toast.LENGTH_SHORT).show();
                view.setVisibility(View.INVISIBLE);
                productList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i,productList.size());
                view.setVisibility(View.VISIBLE);

                Intent i = new Intent(context, SummaryActivity.class);
                i.putExtra("customerNumber", SelectedCustomerNumber);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(i);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void deleteitemFromOrder(final String SKU,final int i,final View view){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                db_order = new OrderDBHelper(context);
                db_serial = new SerialDBHelper(context);
                db_order.deleteSKU(SKU);
                db_serial.deleteItemCode(SKU);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(context,"Item Deleted !",Toast.LENGTH_SHORT).show();
                view.setVisibility(View.INVISIBLE);
                productList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i,productList.size());
                view.setVisibility(View.VISIBLE);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    class SummaryHolder extends RecyclerView.ViewHolder{
        TextView TxtSummaryproductSKU , TxtSummaryproductCategory,TxSummarytproductQty,TxtSummaryProductSubinventory;
        ImageView IMGSummaryproduct,Product_warning;
        ImageButton btnDelete;
        String RowSKU;
        String RowCategory;
        String RowBrand;
        String RowModel;
        String RowDescription;
        String Rowimage;
        float RowUnitPrice;
        String RowOnHand;
        String RowColor;
        String RowCustomer_number;
        public SummaryHolder(@NonNull View itemView) {
            super(itemView);
            TxtSummaryproductSKU = (TextView) itemView.findViewById(R.id.TxtSummaryProductSKU);
            TxtSummaryproductCategory = (TextView) itemView.findViewById(R.id.TxtSummaryCategory);
            TxSummarytproductQty = (TextView) itemView.findViewById(R.id.TxtSummaryProductQTY);
            TxtSummaryProductSubinventory = (TextView) itemView.findViewById(R.id.TxtSummaryProductSubinventory);
            IMGSummaryproduct = (ImageView) itemView.findViewById(R.id.IMGSummaryproduct);
            Product_warning = (ImageView) itemView.findViewById(R.id.warning);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
        }
    }
    public void updateList(List<Product> newlist){
        productList =new ArrayList<>();
        productList.addAll(newlist);
        notifyDataSetChanged();
    }
}
