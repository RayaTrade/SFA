package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
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
import com.example.ahmed_hasanein.sfa.MainActivity;
import com.example.ahmed_hasanein.sfa.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Model.Product;
import preview_database.DB.DealerDB.DealerDBHelper;

import static Adapter.CustomerAdapter.SelectedCustomerVisitDate;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromDealerOrder;
import static com.example.ahmed_hasanein.sfa.LoginActivity.AllowNegativeQtypref;

public class ProductDealerAdapter extends RecyclerView.Adapter<ProductDealerAdapter.ProductHolder> {
    List<Product> productList;
    private Context context;
    private Activity activity;
    private Product mProduct;
    boolean itemExist;
    Product product;
    String customer_name;
    String customer_number;

    public ProductDealerAdapter(List<Product> productList, Context context, String customer_name, String customer_number, Activity activity) {
        this.productList = productList;
        this.context = context;
        this.customer_name = customer_name;
        this.customer_number = customer_number;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_main_dealer,viewGroup,false);
        ProductDealerAdapter.ProductHolder holder = new ProductDealerAdapter.ProductHolder(row);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductHolder viewHolder,final int i) {
        product = productList.get(i);
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

        viewHolder.TxtDealerProductSKU.setText(product.SKU);
        viewHolder.TxtDealerProductDesc.setText(product.Description);
        viewHolder.Dealeronhandlbl.setText(product.OnHand);
        viewHolder.Dealercolorlbl.setText(product.Color);
        viewHolder.EditMainDealer_UnitPrice.setText(String.valueOf(product.UnitPrice));
        if(!product.image.equals("")){
            Picasso.get().load(product.image).error(R.drawable.g_icon).into(viewHolder.IMGDealerproduct);
        }

        viewHolder.EditMainDealer_Qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(viewHolder.EditMainDealer_Qty.length()>0&&viewHolder.EditMainDealer_Qty!=null) {
                    int Qty = Integer.parseInt(viewHolder.EditMainDealer_Qty.getText().toString());

                    if (viewHolder.EditMainDealer_Qty != null && viewHolder.EditMainDealer_Qty.length() > 0) {
                        float result=0;
                        String ed_user_unitprice = viewHolder.EditMainDealer_UnitPrice.getText().toString().trim();
                        if(TextUtils.isEmpty(ed_user_unitprice)) {
                            result = viewHolder.RowUnitPrice * Qty;
                        }else if(!TextUtils.isEmpty(ed_user_unitprice)){
                            String userUnitPrice = viewHolder.EditMainDealer_UnitPrice.getText().toString();
                            result = Float.valueOf(userUnitPrice) * Qty;
                        }
                        viewHolder.totalDealerpricelbl.setText(Float.toString(result));
                        if (!AllowNegativeQtypref.equals("True")||!(Qty <= Integer.parseInt(viewHolder.EditMainDealer_Qty.getText().toString()) &&AllowNegativeQtypref.equals("False"))) {
                            viewHolder.EditMainDealer_Qty.setTextColor(Color.RED);
                        }
                    }
                }else{
                    viewHolder.totalDealerpricelbl.setText("Total Price");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewHolder.EditMainDealer_UnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ed_user_unitprice = viewHolder.EditMainDealer_UnitPrice.getText().toString().trim();
                int Qty = 0;
                if (viewHolder.EditMainDealer_Qty != null && viewHolder.EditMainDealer_Qty.length() > 0) {
                    Qty = Integer.parseInt(viewHolder.EditMainDealer_Qty.getText().toString());
                }
                float result = 0;
                if(!ed_user_unitprice.isEmpty()){
                    result = Float.valueOf(viewHolder.EditMainDealer_UnitPrice.getText().toString()) * Qty;
                    viewHolder.totalDealerpricelbl.setText(Float.toString(result));
                }else if(viewHolder.EditMainDealer_Qty != null && viewHolder.EditMainDealer_Qty.length() > 0){
                    result = viewHolder.RowUnitPrice * Qty;
                    viewHolder.totalDealerpricelbl.setText(Float.toString(result));
                }else{
                    viewHolder.totalDealerpricelbl.setText("Total Price");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewHolder.AddDealerQTY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (viewHolder.EditMainDealer_Qty.length() > 0 && viewHolder.EditMainDealer_Qty != null) {
                        int Qty = Integer.parseInt(viewHolder.EditMainDealer_Qty.getText().toString());

                        if ((Qty <= Integer.parseInt(viewHolder.RowOnHand) && AllowNegativeQtypref.equals("False")) || AllowNegativeQtypref.equals("True")) {
                            btnAddItem(viewHolder.EditMainDealer_UnitPrice.getText().toString(),
                                    viewHolder.EditMainDealer_Qty.getText().toString(), i,
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
                                    viewHolder.totalDealerpricelbl.getText().toString()
                                    ,SelectedCustomerVisitDate);
                            viewHolder.EditMainDealer_Qty.setText("");
                            closeKeyboard();

                        } else {
                            Toast.makeText(context, "Quantity is more than on hand", Toast.LENGTH_LONG).show();
                        }
                    }

            }
        });

        viewHolder.TxtDealerProductSKU.setOnClickListener(new View.OnClickListener() {
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
                i.putExtra("onHand",viewHolder.Dealeronhandlbl.getText().toString());
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
        TextView TxtDealerProductSKU ,TxtDealerProductDesc,Dealeronhandlbl,Dealercolorlbl,totalDealerpricelbl;
        EditText EditMainDealer_Qty,EditMainDealer_UnitPrice;
        ImageView IMGDealerproduct,AddDealerQTY;
        String RowSKU,RowCategory,RowBrand,RowModel,RowDescription,Rowimage,RowOnHand,RowColor,RowCustomer_number;
        float RowUnitPrice;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            TxtDealerProductSKU = (TextView) itemView.findViewById(R.id.TxtDealerProductSKU);
            TxtDealerProductDesc = (TextView) itemView.findViewById(R.id.TxtDealerProductDesc);
            IMGDealerproduct = (ImageView) itemView.findViewById(R.id.IMGDealerproduct);
            EditMainDealer_Qty = (EditText) itemView.findViewById(R.id.EditMainDealer_Qty);
            EditMainDealer_UnitPrice = (EditText) itemView.findViewById(R.id.EditMainDealer_UnitPrice);
            AddDealerQTY = (ImageView) itemView.findViewById(R.id.AddDealerQTY);
            Dealeronhandlbl = (TextView) itemView.findViewById(R.id.Dealeronhandlbl);
            Dealercolorlbl= (TextView) itemView.findViewById(R.id.Dealercolorlbl);
            totalDealerpricelbl = (TextView) itemView.findViewById(R.id.totalDealerpricelbl);
        }
    }

    public void btnAddItem(final String EditUnitPrice,final String Qty, final int position,
                           String SKU,String Category,String Brand,String Model,String Description,String image,float UnitPrice,String OnHand,String Color,String Customer_number,String Total,String Visit_Date){

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
                        ,Customer_number
                        ,Total
                        ,Visit_Date,"","","","","");


               if(OpenfromDealerOrder==true){
                    AddOnDealerOrder(Qty,position,EditUnitPrice);
                }

            }else{
                Toast.makeText(context,"Please enter valid Quantity",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(context,"Please enter Quantity",Toast.LENGTH_LONG).show();
        }
    }

    public void AddOnDealerOrder(final String Qty , final int position,final String EditUnitPrice){
        final DealerDBHelper db_dealer = new DealerDBHelper(context);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                float unit_price = 0;
                if (!EditUnitPrice.equals("")) {
                    unit_price = Float.valueOf(EditUnitPrice);
                }
                if (db_dealer.CheckSKUDealerOrder(mProduct.getSKU())==false) {
                    itemExist= false;
                    if(EditUnitPrice.equals("")) {
                        db_dealer.insertItemDealerOrder(mProduct.getSKU(), mProduct.getCategory(), mProduct.getImage()
                                , mProduct.getBrand(), mProduct.getModel(), mProduct.getDescription()
                                , mProduct.getQTY(), mProduct.getCustomer_number(), mProduct.getOnHand()
                                , mProduct.getUnitPrice(), Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));
                    }else{
                        db_dealer.insertItemDealerOrder(mProduct.getSKU(), mProduct.getCategory(), mProduct.getImage()
                                , mProduct.getBrand(), mProduct.getModel(), mProduct.getDescription()
                                , mProduct.getQTY(), mProduct.getCustomer_number(), mProduct.getOnHand()
                                , unit_price, Float.toString(unit_price * Float.valueOf(Qty)));
                    }
                }else{
                    itemExist= true;
                    if(EditUnitPrice.equals("")) {
                        db_dealer.updateItemDealerOrder(mProduct.getSKU(), mProduct.getQTY(), mProduct.getOnHand()
                                , mProduct.getUnitPrice(), Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));
                    }else{
                        db_dealer.updateItemDealerOrder(mProduct.getSKU(), mProduct.getQTY(), mProduct.getOnHand()
                                , unit_price, Float.toString(unit_price * Float.valueOf(Qty)));
                    }
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

    public void updateList(List<Product> newlist){
        productList =new ArrayList<>();
        productList.addAll(newlist);
        notifyDataSetChanged();
    }
}
