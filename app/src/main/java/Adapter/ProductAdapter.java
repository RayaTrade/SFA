package Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed_hasanein.sfa.DetailActivity;
import Model.Product;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.ProductPreOrderDB.ProductContract;
import preview_database.DB.SyncDB.SyncDBHelper;

import com.example.ahmed_hasanein.sfa.MainActivity;
import com.example.ahmed_hasanein.sfa.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static Adapter.CustomerAdapter.SelectedCustomerVisitDate;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;
import static com.example.ahmed_hasanein.sfa.LoginActivity.AllowNegativeQtypref;
import static com.example.ahmed_hasanein.sfa.MainActivity.CustomerPrice_list;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    List<Product> productList;
    private Context context;
    private Activity activity;
    private Product mProduct;
    boolean itemExist;
    Product product;
    String customer_name;
    String customer_number;
    SharedPreferences prefs;
    boolean OfflineMode = false;
    String Subinventory;
    public ProductAdapter(List<Product> productList, Context context,String customer_name,String customer_number,Activity activity,boolean OfflineMode,String Subinventory) {
        this.productList = productList;
        this.context = context;
        this.customer_name = customer_name;
        this.customer_number = customer_number;
        this.activity = activity;
        this.OfflineMode = OfflineMode;
        this.Subinventory = Subinventory;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_main,viewGroup,false);
        ProductAdapter.ProductHolder holder = new ProductAdapter.ProductHolder(row);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductHolder viewHolder,final int i) {
        product = productList.get(i);
        //****
        viewHolder.RowSKU=product.SKU;
        viewHolder.RowCategory = product.Category;
        viewHolder.RowBrand = product.Brand;
        viewHolder.RowModel = product.Model;
        viewHolder.RowDescription = product.Description;
        viewHolder.Rowimage = product.image;
        if(OfflineMode == true)
        {
            viewHolder.RowUnitPrice = Float.parseFloat(new SyncDBHelper(context).GetOra_PriceListOffline_ItemPrice(product.SKU,CustomerPrice_list));
        }
        else
        viewHolder.RowUnitPrice = product.UnitPrice;

        viewHolder.RowOnHand = product.OnHand;
        viewHolder.RowColor = product.Color;
        viewHolder.RowCustomer_number = product.Customer_number;

        viewHolder.TxtproductSKU.setText(product.SKU);
        viewHolder.TxtProductDesc.setText(product.Description);
        viewHolder.onhandlbl.setText(product.OnHand);
        viewHolder.colorlbl.setText(product.Color);
        if(!product.image.equals("")){
            Picasso.get().load(product.image).error(R.drawable.g_icon).into(viewHolder.IMGproduct);
        }

        viewHolder.EditMain_Qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(viewHolder.EditMain_Qty.length()>0&&viewHolder.EditMain_Qty!=null) {
                    int Qty = Integer.parseInt(viewHolder.EditMain_Qty.getText().toString());
                    prefs = context.getSharedPreferences("login_data", context.MODE_PRIVATE);
                    AllowNegativeQtypref = prefs.getString("My_AllowNegativeQty", "");
                    if (viewHolder.EditMain_Qty != null && viewHolder.EditMain_Qty.length() > 0) {
                        float result = viewHolder.RowUnitPrice * Qty;
                        viewHolder.totalpricelbl.setText(Float.toString(result));
                        if (!AllowNegativeQtypref.equals("True")||!(Qty <= Integer.parseInt(viewHolder.EditMain_Qty.getText().toString()) &&AllowNegativeQtypref.equals("False"))) {
                            viewHolder.EditMain_Qty.setTextColor(Color.RED);
                        }
                    }
                } else{
                    viewHolder.totalpricelbl.setText("Total Price");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewHolder.AddQTY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (viewHolder.EditMain_Qty.length() > 0 && viewHolder.EditMain_Qty != null) {
                        int Qty = Integer.parseInt(viewHolder.EditMain_Qty.getText().toString());

                        if ((Qty <= Integer.parseInt(viewHolder.RowOnHand) && AllowNegativeQtypref.equals("False")) || AllowNegativeQtypref.equals("True")) {
                            btnAddItem(viewHolder.EditMain_Qty.getText().toString(), i,
                                    viewHolder.RowSKU,
                                    viewHolder.RowCategory,
                                    viewHolder.RowBrand,
                                    viewHolder.RowModel,
                                    viewHolder.RowDescription,
                                    viewHolder.Rowimage,
                                    viewHolder.RowUnitPrice,
                                    viewHolder.RowOnHand,
                                    viewHolder.RowColor,
                                    viewHolder.RowCustomer_number,
                                    SelectedCustomerVisitDate,
                                    Subinventory
                                    );
                            viewHolder.EditMain_Qty.setText("");
                            closeKeyboard();

                        } else {
                            Toast.makeText(context, "Quantity is more than on hand", Toast.LENGTH_LONG).show();
                        }
                    }

            }
        });

        viewHolder.TxtproductSKU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,DetailActivity.class);
                i.putExtra("productSKU",viewHolder.RowSKU);
                i.putExtra("productCat",viewHolder.RowCategory);
                i.putExtra("productBrand",viewHolder.RowBrand);
                i.putExtra("productModel",viewHolder.RowModel);
                i.putExtra("productDesc",viewHolder.RowDescription);
                i.putExtra("productImg",viewHolder.Rowimage);
                i.putExtra("productPrice",Float.toString(viewHolder.RowUnitPrice));
                i.putExtra("customer_number",viewHolder.RowCustomer_number);
                i.putExtra("subinventory",Subinventory);
                i.putExtra("onHand",viewHolder.onhandlbl.getText().toString());
                i.putExtra("openfromSummary",false);
                i.putExtra("Order",true);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        });
    }

    private void closeKeyboard(){
        View view = this.activity.getCurrentFocus();
        if(view!=null){
            InputMethodManager inputMethodManager = (InputMethodManager)this.activity.getSystemService(context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    class ProductHolder extends RecyclerView.ViewHolder{
        TextView TxtproductSKU ,TxtProductDesc,onhandlbl,colorlbl,totalpricelbl;
        EditText EditMain_Qty;
        ImageView IMGproduct;
        ImageView AddQTY;
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
        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            TxtproductSKU = (TextView) itemView.findViewById(R.id.TxtProductSKU);
            TxtProductDesc = (TextView) itemView.findViewById(R.id.TxtProductDesc);
            IMGproduct = (ImageView) itemView.findViewById(R.id.IMGproduct);
            EditMain_Qty = (EditText) itemView.findViewById(R.id.EditMain_Qty);
            AddQTY = (ImageView) itemView.findViewById(R.id.AddQTY);
            onhandlbl = (TextView) itemView.findViewById(R.id.onhandlbl);
            colorlbl= (TextView) itemView.findViewById(R.id.colorlbl);
            totalpricelbl = (TextView) itemView.findViewById(R.id.totalpricelbl);
        }
    }

    public void btnAddItem(final String Qty, final int position,
                           String SKU,String Category,String Brand,String Model,String Description,String image,float UnitPrice,String OnHand,String Color,String Customer_number,String Visit_Date,String subinventory){

        if(!Qty.isEmpty()){
            if(Integer.parseInt(Qty)>=1){
                mProduct = new Product(SKU,
                        Category
                        ,Brand
                        ,Model
                        ,Qty
                        ,Description
                        ,image
                        ,UnitPrice
                        ,OnHand
                        ,Color
                        ,Customer_number,
                        ""
                        ,Visit_Date,"","","","",subinventory);

                if(OpenfromOrderPage==false) {
                    AddfromPreOrder(Qty, position);
                }else if(OpenfromOrderPage==true){
                    AddfromOrder(Qty,position);
                }

            }else{
                Toast.makeText(context,"Please enter valid Quantity",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(context,"Please enter Quantity",Toast.LENGTH_LONG).show();
        }
    }
    /*
     * # change date 7/7/2020
     * © changed by Ahmed Ali
     */
    public void AddfromPreOrder(final String Qty , final int position){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isAddPreOrder()) {
                    itemExist= false;
                    ContentValues productValues = new ContentValues();
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SKU,
                            mProduct.getSKU());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_category,
                            mProduct.getCategory());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_image_PATH,
                            mProduct.getImage());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_brand,
                            mProduct.getBrand());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_model,
                            mProduct.getModel());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_description,
                            mProduct.getDescription());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_QTY,
                            mProduct.getQTY());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_Customer_Number,
                            mProduct.getCustomer_number());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_OnHand,
                            mProduct.getOnHand());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_UnitPrice,
                            mProduct.getUnitPrice());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_Total,
                            Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));

                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_VisitDate,
                            mProduct.getVisit_Date());

                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_Subinventory,
                            mProduct.getSubinventory());

                    context.getContentResolver().insert(
                            ProductContract.ProductEntry.CONTENT_URI,
                            productValues
                    );
                }else{
                    itemExist= true;
                    ContentValues productValues = new ContentValues();
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_QTY,
                            mProduct.getQTY());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_OnHand,
                            mProduct.getOnHand());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_UnitPrice,
                            mProduct.getUnitPrice());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_Total,
                            Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));

                    context.getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI,
                            productValues,ProductContract.ProductEntry.COLUMN_PRODUCT_SKU + " = '" + mProduct.getSKU()+"'"
                            + " and "+ProductContract.ProductEntry.COLUMN_PRODUCT_product_Subinventory + " = '"+mProduct.getSubinventory()+"'"
                            ,null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(itemExist==false) {
                    Toast.makeText(context, "Item Added !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Item already exist Quantity updated now !", Toast.LENGTH_SHORT).show();
                }

                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,productList.size());
                ((MainActivity)activity).RefreshBottomSheetTable();

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void AddfromOrder(final String Qty , final int position){
        final OrderDBHelper db_order = new OrderDBHelper(context);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (db_order.CheckSKUOrder(mProduct.getSKU())==false) {
                    itemExist= false;
                    db_order.insertItemOrder(mProduct.getSKU(),mProduct.getCategory(),mProduct.getImage()
                            ,mProduct.getBrand(),mProduct.getModel(),mProduct.getDescription()
                            ,mProduct.getQTY(),mProduct.getCustomer_number(),mProduct.getOnHand()
                            ,mProduct.getUnitPrice(),Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)),mProduct.Visit_Date,mProduct.getSubinventory());
                }else{
                    itemExist= true;
                    db_order.updateItemOrder(mProduct.getSKU(),mProduct.getQTY(),mProduct.getOnHand()
                            ,mProduct.getUnitPrice(),Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(itemExist==false) {
                    Toast.makeText(context, "Item Added !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Item already exist Quantity updated now !", Toast.LENGTH_SHORT).show();
                }

                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,productList.size());
                ((MainActivity)activity).RefreshBottomSheetTable();

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /*
     * # change date 7/7/2020
     * © changed by Ahmed Ali
     */

    private boolean isAddPreOrder() {
        Cursor productCursor = context.getContentResolver().query(
                ProductContract.ProductEntry.CONTENT_URI,
                new String[]{ProductContract.ProductEntry.COLUMN_PRODUCT_SKU},
                ProductContract.ProductEntry.COLUMN_PRODUCT_SKU + " = '" + mProduct.getSKU()+"' and " + ProductContract.ProductEntry.COLUMN_PRODUCT_product_Subinventory + " = '"+ mProduct.getSubinventory()+"'",
                null,
                null);

        if (productCursor != null && productCursor.moveToFirst()) {
            productCursor.close();
            return true;
        } else {
            return false;
        }
    }
    public void updateList(List<Product> newlist){
        productList =new ArrayList<>();
        productList.addAll(newlist);
        notifyDataSetChanged();
    }
}
