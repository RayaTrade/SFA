package com.example.ahmed_hasanein.sfa;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import Model.Product;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.ProductPreOrderDB.ProductContract;
import preview_database.DB.StockTakingDB.StockTakingDBHelper;

import static Adapter.CustomerAdapter.SelectedCustomerVisitDate;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromStockTaken;
import static com.example.ahmed_hasanein.sfa.LoginActivity.AllowNegativeQtypref;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;
import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerNumber;

public class DetailActivity extends AppCompatActivity {
    TextView TxtSKU, TxtCat, TxtBrand, TxtModel, TxtDesc, TxtQTY, TxtCustomerID, TxtOnHandDetail, TxtUnitPrice;
    ImageView ToolbarimageView;
    EditText Edit_Qty;
    Button btnAdditem, btnEditQty;
    private Product mProduct;
    Bundle extras;
    String imagepath;
    boolean itemExist;
    SharedPreferences prefs;
    String subinventory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TxtSKU = (TextView) findViewById(R.id.TxtSKU);
        TxtCat = (TextView) findViewById(R.id.TxtCat);
       // TxtSubinventory = (TextView) findViewById(R.id.TxtCat);
        TxtBrand = (TextView) findViewById(R.id.TxtBrand);
        TxtModel = (TextView) findViewById(R.id.TxtModel);
        TxtDesc = (TextView) findViewById(R.id.TxtDesc);
        TxtQTY = (TextView) findViewById(R.id.TxtQTY);
        TxtCustomerID = (TextView) findViewById(R.id.TxtCustomerNumberDetail);
        TxtOnHandDetail = (TextView) findViewById(R.id.TxtOnHandDetail);
        TxtUnitPrice = (TextView) findViewById(R.id.TxtUnitPrice);
        ToolbarimageView = (ImageView) findViewById(R.id.imageViewCollapsing);
        Edit_Qty = (EditText) findViewById(R.id.Edit_Qty);
        btnAdditem = (Button) findViewById(R.id.btnAdditem);
        btnEditQty = (Button) findViewById(R.id.btnEditQty);
        TextView lblQty = (TextView) findViewById(R.id.lblQty);
        TextView lblCustomerID = (TextView) findViewById(R.id.lblCustomerID);
        View lineView = (View) findViewById(R.id.lineQty);

        extras = getIntent().getExtras();
        prefs = getSharedPreferences("login_data", MODE_PRIVATE);
        AllowNegativeQtypref = prefs.getString("My_AllowNegativeQty", "");

        if (extras != null) {
            TxtSKU.setText(extras.getString("productSKU"));
             subinventory = extras.getString("subinventory");
           // TxtCat.setText(extras.getString("productCat"));
            TxtBrand.setText(extras.getString("productBrand"));
            TxtModel.setText(extras.getString("productModel"));
            TxtDesc.setText(extras.getString("productDesc"));
            TxtUnitPrice.setText(extras.getString("productPrice"));

            if(!extras.getString("productImg").equals(""))
                Picasso.get().load(extras.getString("productImg")).error(R.drawable.g_icon).into(ToolbarimageView);
            else
                Picasso.get().load(R.drawable.g_icon).error(R.drawable.g_icon).into(ToolbarimageView);


          //  Picasso.get().load(extras.getString("productImg")).error(R.drawable.g_icon).into(ToolbarimageView);
            TxtQTY.setText(extras.getString("productQTY"));
            TxtCustomerID.setText(extras.getString("customer_number"));
            TxtOnHandDetail.setText(extras.getString("onHand"));

            imagepath = extras.getString("productImg");

            if (extras.getBoolean("openfromSummary")) {
                Edit_Qty.setVisibility(View.GONE);
                btnAdditem.setVisibility(View.GONE);
                showEditItemDialog(DetailActivity.this);
            } else {
                TxtQTY.setVisibility(View.GONE);
                lblQty.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
                btnEditQty.setVisibility(View.GONE);
            }
        }


    }

    private void showEditItemDialog(final Context c) {
        final EditText taskEditText = new EditText(c);
        taskEditText.setRawInputType(Configuration.KEYBOARD_12KEY);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Edit Quantity")
                .setView(taskEditText)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(OpenfromStockTaken){
                            add_Edit(taskEditText.getText().toString());
                        }
                        else if (Float.valueOf(TxtUnitPrice.getText().toString()) >= 1.0) {
                            int Qty = Integer.parseInt(taskEditText.getText().toString());

                             if (!taskEditText.getText().toString().isEmpty() && Qty >= 1) {

                                if (AllowNegativeQtypref.equals("True") || (Qty <= Integer.parseInt(TxtOnHandDetail.getText().toString()) && AllowNegativeQtypref.equals("False"))) {

                                    add_Edit(taskEditText.getText().toString());
                                } else {
                                    Toast.makeText(getBaseContext(), "Quantity is more than on hand", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "Please enter valid Quantity", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "UnitPrice is less than 1", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public void btnAddItem(View view) {
        add_Edit(Edit_Qty.getText().toString());
    }

    public void add_Edit(String EditText) {

        if (!EditText.isEmpty()) {
            int Qty = Integer.parseInt(EditText);
            float UnitPrice = 0;

            if (OpenfromStockTaken) {
                mProduct = new Product(TxtSKU.getText().toString(),
                        TxtCat.getText().toString()
                        , TxtBrand.getText().toString()
                        , TxtModel.getText().toString()
                        , EditText
                        , TxtDesc.getText().toString()
                        , imagepath
                        , UnitPrice
                        , TxtOnHandDetail.getText().toString()
                        , ""
                        , extras.getString("customer_number")
                        , ""
                        , SelectedCustomerVisitDate, "", "", "", "", subinventory);
                AddfromStockTaking(EditText);
            }
            else
            {
                if (!TxtUnitPrice.getText().toString().equals("") && Qty >= 1) {
                UnitPrice = Float.parseFloat(TxtUnitPrice.getText().toString());
            }
                if (AllowNegativeQtypref.equals("True") || (Qty <= Integer.parseInt(TxtOnHandDetail.getText().toString()) && AllowNegativeQtypref.equals("False"))) {
                if (Integer.parseInt(EditText) >= 1) {
                    mProduct = new Product(TxtSKU.getText().toString(),
                            TxtCat.getText().toString()
                            , TxtBrand.getText().toString()
                            , TxtModel.getText().toString()
                            , EditText
                            , TxtDesc.getText().toString()
                            , imagepath
                            , UnitPrice
                            , TxtOnHandDetail.getText().toString()
                            , ""
                            , extras.getString("customer_number")
                            , ""
                            , SelectedCustomerVisitDate, "", "", "", "", subinventory);
                }

                if (OpenfromOrderPage == false) {
                    AddfromPreOrder(EditText);
                } else if (OpenfromOrderPage == true) {
                    AddfromOrder(EditText);
                }
            } else {
                Toast.makeText(getBaseContext(), "Quantity is more than on hand", Toast.LENGTH_LONG).show();
            }
            }
        } else {
            Toast.makeText(getBaseContext(), "Please enter valid Quantity", Toast.LENGTH_LONG).show();
        }

    }

    /*
     * # change date 7/7/2020
     * © changed by Ahmed Ali
     */

    public void AddfromPreOrder(final String Qty) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isAddPreOrder()) {
                    itemExist = false;
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

                    getContentResolver().insert(
                            ProductContract.ProductEntry.CONTENT_URI,
                            productValues
                    );


                } else {
                    itemExist = true;
                    ContentValues productValues = new ContentValues();
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_QTY,
                            mProduct.getQTY());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_OnHand,
                            mProduct.getOnHand());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_UnitPrice,
                            mProduct.getUnitPrice());
                    productValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_product_Total,
                            Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));

                    getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI,
                            productValues,ProductContract.ProductEntry.COLUMN_PRODUCT_SKU + " = '" + mProduct.getSKU()+"'"
                                    + " and "+ProductContract.ProductEntry.COLUMN_PRODUCT_product_Subinventory + " = '"+mProduct.getSubinventory()+"'"
                            ,null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Intent i = new Intent(getBaseContext(), SummaryActivity.class);
                i.putExtra("customerNumber", extras.getString("customer_number"));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void AddfromOrder(final String Qty) {
        final OrderDBHelper db_order = new OrderDBHelper(getBaseContext());
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (db_order.CheckSKUOrder(mProduct.getSKU()) == false) {
                    itemExist = false;
                    db_order.insertItemOrder(mProduct.getSKU(), mProduct.getCategory(), mProduct.getImage()
                            , mProduct.getBrand(), mProduct.getModel(), mProduct.getDescription()
                            , mProduct.getQTY(), mProduct.getCustomer_number(), mProduct.getOnHand()
                            , mProduct.getUnitPrice(), Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)),mProduct.Visit_Date,mProduct.getSubinventory());
                } else {
                    itemExist = true;
                    db_order.updateItemOrder(mProduct.getSKU(), mProduct.getQTY(), mProduct.getOnHand()
                            , mProduct.getUnitPrice(), Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (itemExist == false) {
                    Toast.makeText(getBaseContext(), "Item Added !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Item already exist Quantity updated now !", Toast.LENGTH_SHORT).show();
                }

                Intent i = new Intent(getBaseContext(), SummaryActivity.class);
                i.putExtra("customerNumber", extras.getString("customer_number"));
                i.putExtra("Order", true);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void AddfromStockTaking(final String Qty){
        final StockTakingDBHelper stockTakingDBHelper = new StockTakingDBHelper(getBaseContext());
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (stockTakingDBHelper.CheckSKUOrder(mProduct.getSKU())==false) {
                    itemExist= false;
                    stockTakingDBHelper.insertItemStockTaking(mProduct.getSKU(),mProduct.getCategory(),mProduct.getImage()
                            ,mProduct.getBrand(),mProduct.getModel(),mProduct.getDescription()
                            ,mProduct.getQTY(),mProduct.getCustomer_number(),mProduct.getOnHand()
                            ,mProduct.getUnitPrice(),Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)),mProduct.Visit_Date,mProduct.getSubinventory());
                }else{
                    itemExist= true;
                    stockTakingDBHelper.updateItemOrder(mProduct.getSKU(),mProduct.getQTY(),mProduct.getOnHand()
                            ,mProduct.getUnitPrice(),Float.toString(mProduct.UnitPrice * Float.valueOf(Qty)));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (itemExist == false) {
                    Toast.makeText(getBaseContext(), "Item Added !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Item already exist Quantity updated now !", Toast.LENGTH_SHORT).show();
                }
                Intent i = new Intent(getBaseContext(), SummaryActivity.class);
                i.putExtra("customerNumber", extras.getString("customer_number"));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean isAddPreOrder() {
        Cursor productCursor = getContentResolver().query(
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

    public void btnEditQuantity(View view) {
        showEditItemDialog(DetailActivity.this);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(this , SummaryActivity.class);
        i.putExtra("customerNumber", SelectedCustomerNumber);
        if (OpenfromOrderPage == true) {
            i.putExtra("Order", true);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
        this.finish();

        return;
    }


}
