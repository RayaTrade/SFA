package com.example.ahmed_hasanein.sfa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import API.API_Online;
import API.API_Sync_Back;
import Adapter.DiliveryMethodAdapter;
import Adapter.SummaryAdapter;
import FillSpinners.DeliveryMethodsSpinner;
import FillSpinners.TenderTypeSpinner;
import Model.DeliveryMethod;
import Model.Header;
import Model.Parsing_Json.SFA_GetTruckType;
import Model.Product;
import Model.ProductDimension;
import Model.Promotions;
import Model.TenderType;
import Model.TruckType;
import Model.User;
import Utility.Connectivity;
import Utility.DialogHint;
import Utility.NetworkChangeReceiver;
import gps_tracking.LocationPoints;
import preview_database.DB.DealerDB.DealerDBHelper;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.ProductPreOrderDB.ProductDBHelper;
import preview_database.DB.StockTakingDB.StockTakingDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromDealerOrder;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromPreOrderPage;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromStockTaken;
import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;
import static com.example.ahmed_hasanein.sfa.LoginActivity.ServerConfigIDpref;
import static com.example.ahmed_hasanein.sfa.LoginActivity.emailpref;
import static com.example.ahmed_hasanein.sfa.MainActivity.C_BALANCE;
import static com.example.ahmed_hasanein.sfa.MainActivity.C_CREDIT_LIMIT;
import static com.example.ahmed_hasanein.sfa.MainActivity.C_OUTSTANDING;
import static com.example.ahmed_hasanein.sfa.MainActivity.C_RISKY_CHECKS;
import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerName;
import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerNumber;
import static com.example.ahmed_hasanein.sfa.MainActivity.CustomerPrice_list;
import static com.example.ahmed_hasanein.sfa.MainActivity.customerDueDateFrom;

public class SummaryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    String url;
    static List<Product> productList;
    RecyclerView recyclerView;
    ProgressBar mProgressBar;
  public   static TextView SummaryCustomerNumber, txtVNumberSummary, SummaryCustomerName,Recomendtext,Trucktext,TruckSpacetext,TruckNumtext
          ,TxtBALANCE,TxtRISKY_CHECKS,TxtCREDIT_LIMIT,TxtOUTSTANDING;
    static LinearLayout txtHintsummary;
    CardView delivery_layout;
    static TextView TxtSummConnectionType;
    private ProductDBHelper db;
    private StockTakingDBHelper db_Stock;
    private OrderDBHelper db_order;
    private SerialDBHelper db_serial;
    private SyncDBHelper db_sync;
    private DealerDBHelper db_dealer;
    SummaryAdapter adapter;
    boolean saved = false;
    static String Headerid;
    String Long;
    String Lat;
    private LocationPoints locationPoints;
    Bundle extras;
    AlertDialog.Builder DialogFinsih;
    AlertDialog dialog;
    Button dialogbtnclose, dialogbtnFinsih, btnCollectSerial, btnSavePreOrder;
    RadioGroup rgTander;
    RadioButton rbcash, rbtransfer;
    private RadioButton radioButton;
    List<String> DeliveryTypelist;
    List<String> TenderTypeList;
    public static ProgressDialog dialogSave, DropDownLoading;
    EditText TxtComment;
    TenderTypeSpinner tenderTypeSpinner;
    private Spinner spinnerTenderType, spinnerDeliveryMethods;
    DeliveryMethodsSpinner deliveryMethodsSpinner;
    boolean openfromOrder;
    Connectivity connectivity;
    private BroadcastReceiver mNetworkReceiver;
    static TextView LayoutSummaryOffline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSummary);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_barSummary);
        SummaryCustomerNumber = (TextView) findViewById(R.id.SummaryCustomerNumber);
        SummaryCustomerName = (TextView) findViewById(R.id.SummaryCustomerName);
        TxtSummConnectionType = (TextView) findViewById(R.id.TxtSummConnectionType);
        txtHintsummary = (LinearLayout) findViewById(R.id.txtHintsummary);
        delivery_layout = findViewById(R.id.delivery_layout);
        txtVNumberSummary = (TextView) findViewById(R.id.txtVNumberSummary);
        btnCollectSerial = (Button) findViewById(R.id.btnCollectSerial);
        btnSavePreOrder = (Button) findViewById(R.id.btnSavePreOrder);
        LayoutSummaryOffline = (TextView) findViewById(R.id.marque_scrolling_text_summary);
        DialogFinsih = new AlertDialog.Builder(SummaryActivity.this);

        Recomendtext = (TextView) findViewById(R.id.Recomendtext);
        Trucktext = (TextView) findViewById(R.id.Trucktext);
        TruckSpacetext = (TextView) findViewById(R.id.TruckSpacetext);
        TruckNumtext = (TextView) findViewById(R.id.TruckNumtext);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSummary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TxtBALANCE = (TextView) findViewById(R.id.C_BALANCE);
        TxtRISKY_CHECKS = (TextView) findViewById(R.id.C_RISKY_CHECKS);
        TxtCREDIT_LIMIT = (TextView) findViewById(R.id.C_CREDIT_LIMIT);
        TxtOUTSTANDING = (TextView) findViewById(R.id.C_OUTSTANDING);

        TxtBALANCE.setText("BALANCE: "+  (C_BALANCE));
        TxtRISKY_CHECKS.setText("RISKY CHECKS: "+ (C_RISKY_CHECKS));
        TxtCREDIT_LIMIT.setText("CREDIT LIMIT: "+ (C_CREDIT_LIMIT));
        TxtOUTSTANDING.setText("OUTSTANDING: "+ (C_OUTSTANDING));

        txtVNumberSummary.setText("Version " + BuildConfig.VERSION_NAME + "," + BuildConfig.VERSION_CODE); //set textview for version number
        SummaryCustomerName.setText(SelectedCustomerName);
        SummaryCustomerNumber.setText(SelectedCustomerNumber);

        db_sync = new SyncDBHelper(getBaseContext());
        //check connectivity
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcast();
        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
            TxtSummConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtSummConnectionType.setText("Online");

           if( User.Allow_Delivery_Method) {
               if (!OfflineMode)
                   new API_Online().SFA_GetTruckType(this, this.getBaseContext());
               else
                   LoadingData();
           }
           else
           {
               delivery_layout.setVisibility(View.GONE);
           }
        } else {
            OfflineMode = true;
            TxtSummConnectionType.setTextColor(Color.RED);
            TxtSummConnectionType.setText("Offline");
        }

        extras = getIntent().getExtras();
        if (extras != null) {
            openfromOrder = extras.getBoolean("Order");
            if (OpenfromOrderPage == true || openfromOrder == true) {
                btnCollectSerial.setVisibility(View.VISIBLE);
                btnSavePreOrder.setVisibility(View.GONE);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbarSummary);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setTitle("Summary of Items");

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        productList = new ArrayList<>();
        Fillitem();

      /*  delivery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryDialog();
            }
        });*/

    }
    ProductDimension[] productDimensions;

    public void SystemRecomendTruck(ProductDimension[] productDimensions, SFA_GetTruckType trucks){
        try {
            if(productDimensions != null ) {
                double productSize = 0.0;
                double productweight = 0.0;
                int count = 0;

                for (ProductDimension product : productDimensions) {

                    Product p = new Product();
                    p.setSKU(product.getITEMCODE());
                    int index = findindex(productList, p);

                    if (index >= 0) {
                        productSize += product.getSize() * Double.parseDouble(productList.get(index).QTY);
                        productweight += Double.parseDouble(product.getWEIGHT()) * Double.parseDouble(productList.get(index).QTY);

                        productList.get(index).setHEIGHT(product.getHEIGHT());
                        productList.get(index).setWIDTH(product.getWIDTH());
                        productList.get(index).setWEIGHT(product.getWEIGHT());
                        productList.get(index).setLENGTH(product.getLENGTH());
                    }

                    count++;
                }

                String text = "";
                count = 0;
                for (TruckType truck : trucks.getTruckType()) {
                    if ((Double.parseDouble(truck.getWeight())) >= productweight) {
                        if (truck.getSize() >= productSize) {

                            Trucktext.setText(truck.getType().toString());
                            TruckSpacetext.setText(String.valueOf(new DecimalFormat("##.##").format(100 - (productSize / truck.getSize()) * 100)) + "%");
                            text = "1 Car";
                            TruckNumtext.setText(text);
                            if (productList.size() != productDimensions.length) {
                                Recomendtext.setTextColor(Color.RED);
                                text = "Note that not all items dimensions calculate.";
                                Recomendtext.setText(text);
                            }
                            truck.setCount(1);
                            break;
                        }
                    }
                    count++;

                }
                if(text.equals(""))
                {
                    int lastTruck = trucks.getTruckType().size() -1;
                    Trucktext.setText(trucks.getTruckType().get(lastTruck  ).getType());
                    double car_size = trucks.getTruckType().get(lastTruck).getSize();
                    double prduct_size = productSize;

                     int cars  = (int) (  prduct_size/car_size ) +1 ;
                    TruckSpacetext.setText(String.valueOf(new DecimalFormat("##.##").format(100 - (productSize /  (cars * trucks.getTruckType().get(lastTruck).getSize())) * 100)) + "%");
                    TruckNumtext.setText(String.valueOf( cars ) + " Cars");
                    if (productList.size() != productDimensions.length) {
                        Recomendtext.setTextColor(Color.RED);
                        text = "Note that not all items dimensions calculate.";
                        Recomendtext.setText(text);
                    }
                    trucks.getTruckType().get(lastTruck).setCount(cars);
                }
            }
            else
            {
                Trucktext.setText("Any");
                TruckSpacetext.setText("Any");
                TruckNumtext.setText("Any");
                Recomendtext.setTextColor(Color.RED);
                String text = "Note that not all items dimensions calculate.";
                Recomendtext.setText(text);
            }
            if(Prodialog != null)
            Prodialog.dismiss();
        }
        catch (Exception ex)
        {
            Log.v("EX-280",ex.getMessage());
        }
}

    private int findindex(List<Product> productlist,Product product)
    {
        int count = 0;

    // For every Tool (t) in the list of Tools (tools)
        for(Product p : productlist){

            // Check your condition e.g: if owned is true
            if(p.getSKU().equals(product.getSKU())){
                return count;// put getters here for the values to sum up
            }
            count ++;
        }
        return -1;
    }
    public void LoadingData(){
        SFA_GetTruckType Trucks = new SFA_GetTruckType();
        if(!OfflineMode)
        {
            Trucks = new API_Online().Trucks;
            productDimensions = new API_Online().productDimensions;
        }
        else
        {
            Trucks.setTruckType(db_sync.getAllTruckTypeOffline());
            List<ProductDimension> productDimensionList =  db_sync.getItemDimensionsOffline(productList);
            productDimensions =  productDimensionList.toArray(new ProductDimension[0]);
            new API_Sync_Back().productDimensions = productDimensions;
            new API_Sync_Back().Trucks = Trucks;
        }
                if(OpenfromPreOrderPage)
                    SystemRecomendTruck(productDimensions, Trucks);

    }

   static ProgressDialog  Prodialog;
    public void Fillitem() {
        if (OpenfromPreOrderPage) { //preorder
            db = new ProductDBHelper(this);
            productList.addAll(db.getAllProduct());
            delivery_layout.setVisibility(View.VISIBLE);
            Prodialog = ProgressDialog.show(SummaryActivity.this, "",
                    "Loading. Please wait...", true);
            List<ProductDimension> products = new ArrayList<>();
            for (Product p: productList) {
                ProductDimension product = new ProductDimension();
                product.setITEMCODE(p.SKU);
                products.add(product);
            }

            if( User.Allow_Delivery_Method ) {
               if (!OfflineMode)
                   new API_Online().SFA_GetItemDimensions(this, getBaseContext(), products, Prodialog);
               else
                   LoadingData();
           }
            else
            {
                delivery_layout.setVisibility(View.GONE);
            }

        } else if (OpenfromStockTaken == true) { // Stock Taking
            db_Stock = new StockTakingDBHelper(this);
            productList.addAll(db_Stock.getAllStockTaking());
            delivery_layout.setVisibility(View.GONE);
        }else if (OpenfromOrderPage == true) { //order
            db_order = new OrderDBHelper(this);
            db_serial = new SerialDBHelper(this);
            productList.addAll(db_order.getAllOrder());
            delivery_layout.setVisibility(View.GONE);
        } else if (OpenfromDealerOrder == true) { //dealer
            db_dealer = new DealerDBHelper(this);
            productList.addAll(db_dealer.getAllDealerOrder());
        }

        final Activity activity = this;
        int timedelay = (productList.size() > 4)? productList.size() * 1000: 4000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                LoadingData();
                adapter = new SummaryAdapter(productList, getBaseContext(),activity);
                recyclerView.setAdapter(adapter);
                if (productList.size() <= 0) {
                    txtHintsummary.setVisibility(View.VISIBLE);
                }

                /* if(User.Allow_Delivery_Method)
                delivery_layout.setVisibility( View.VISIBLE);
                else
                delivery_layout.setVisibility(View.GONE);*/

                mProgressBar.setVisibility(View.GONE);
                if(Prodialog != null && Prodialog.isShowing())
                    Prodialog.dismiss();

            }
        }, timedelay);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_summary, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        try {
            String userinput = s.toLowerCase();
            List<Product> newList = new ArrayList<>();
            for (Product n : productList) {
                if (n.getSKU().toLowerCase().contains(userinput) || n.getCategory().toLowerCase().contains(userinput)) {
                    newList.add(n);
                }
            }
            adapter.updateList(newList);
            return true;
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Please Wait until finish loading !", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    LinearLayout LayoutWaitingPreOrderList;

    public void initiateDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_finish_preorder, null);
        dialogbtnclose = (Button) view.findViewById(R.id.dialogbtnclose);
        dialogbtnFinsih = (Button) view.findViewById(R.id.dialogbtnFinsih);
        TxtComment = (EditText) view.findViewById(R.id.TxtComment);
        spinnerTenderType = (Spinner) view.findViewById(R.id.spinnerTenderType);
        spinnerDeliveryMethods = (Spinner) view.findViewById(R.id.spinnerDeliveryMethods);
        LayoutWaitingPreOrderList = (LinearLayout) view.findViewById(R.id.LayoutWaitingPreOrderList);
        LinearLayout Pre_OrderMethods = view.findViewById(R.id.Pre_OrderMethods);
        DialogFinsih.setView(view);
        dialogbtnFinsih.setEnabled(false);
        dialog = DialogFinsih.create();
        if(OpenfromStockTaken)
        {
            Pre_OrderMethods.setVisibility(View.GONE);
        }
        dialog.show();
    }

    public void btnSaveProducts(final View view) {
        float limit =Float.parseFloat(C_CREDIT_LIMIT.replace(",", ""));
        float balance =Float.parseFloat(C_BALANCE.replace(",", ""));
        float risk =Float.parseFloat(C_RISKY_CHECKS.replace(",", ""));

     double Order_Indicator =  (limit) - (balance + risk);
        double Total_order =0.0;
        if (OpenfromPreOrderPage) {
            db = new ProductDBHelper(this);
            Total_order = Calcualte_Amount(db.getAllProduct()); //preorder (product.db)
        } else if (OpenfromOrderPage) {
            db_order = new OrderDBHelper(this);
            Total_order = Calcualte_Amount(db_order.getAllOrder());
        }
        if(Order_Indicator < Total_order)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Order Indicator");
            String Message = "This order may be not proceed due to user Limit Kindly check with finance";
            Message += "\n"+"Your Total order is "+ConvertReformateNumber(String.valueOf(Total_order));
            Message += "\n"+"Your Customer Limit is "+ConvertReformateNumber(String.valueOf(Order_Indicator));
            alertDialog.setMessage(Message);
                alertDialog.setIcon(this.getResources().getDrawable(R.drawable.warning));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            btnSaveProductsAction(view);
                        }
                    });
            alertDialog.show();
        }
        else {
            btnSaveProductsAction(view);
        }

    }
    private String ConvertReformateNumber(String number){
        if(number.equals(""))
        {
            return "0.00";
        }
        else {
            Float litersOfPetrol = Float.parseFloat(number);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setGroupingUsed(true);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(2);
            number = df.format(litersOfPetrol);
            return number;
        }
    }

    private  Float Calcualte_Amount(List<Product> productList)
    {
        float total = 0;
        for (Product product : productList) {
            total += Float.valueOf(product.Total);
        }
        return total;
    }

    public void btnSaveProductsAction(View view) {
        //List<TruckType> x =  new API_Online().Trucks.getTruckType();
        if (!new DialogHint().GPS_Dialog(this)) {
            return;
        }

        //check connectivity
        connectivity = new Connectivity();
        boolean checkConnectivity = new Connectivity().isConnectedFast(getBaseContext());
        if (checkConnectivity == true) {
            OfflineMode = false;
            TxtSummConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtSummConnectionType.setText("Online");
        } else {
            OfflineMode = true;
            TxtSummConnectionType.setTextColor(Color.RED);
            TxtSummConnectionType.setText("Offline");
        }

        if (productList.size() > 0) {
            getLocation();
            tenderTypeSpinner = new TenderTypeSpinner(getBaseContext());
            deliveryMethodsSpinner = new DeliveryMethodsSpinner(getBaseContext());

            // open dialog

            initiateDialog();
            loadingDropDownlists();

            dialogbtnclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialogbtnFinsih.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(TxtComment.getText())) {
                        TxtComment.setError("Comment is Required !");
                        return;
                    }
                    if (!new DialogHint().GPS_Dialog(SummaryActivity.this)) {
                        return;
                    }

                    if( (spinnerTenderType == null && spinnerTenderType.getSelectedItem() ==null) && !OpenfromStockTaken ) {
                        TextView errorText = (TextView)spinnerTenderType.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Error ! please restart dialog");//changes the selected item text to this
                        return;
                    }

                    if( (spinnerDeliveryMethods == null && spinnerDeliveryMethods.getSelectedItem() ==null) && !OpenfromStockTaken ) {
                        TextView errorText = (TextView)spinnerDeliveryMethods.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Error ! please restart dialog");//changes the selected item text to this
                        return;
                    }

                    //check connectivity
                    connectivity = new Connectivity();
                    boolean checkConnectivity = new Connectivity().isConnectedFast(getBaseContext());
                    if (checkConnectivity == true) {
                        OfflineMode = false;
                        TxtSummConnectionType.setTextColor(Color.parseColor("#32CD32"));
                        TxtSummConnectionType.setText("Online");
                    } else {
                        OfflineMode = true;
                        TxtSummConnectionType.setTextColor(Color.RED);
                        TxtSummConnectionType.setText("Offline");
                    }

                    if (OfflineMode == true) { //offline
                        //save transaction offline////
                        OfflineSave();
                        /////////////////////////////
                    } else { //online
                        //save transaction online////
                        OnlineSave();
                        ////////////////////////////
                    }
                }
            });
        } else {
            if (!new DialogHint().GPS_Dialog(this)) {
            } else {
                Toast.makeText(getBaseContext(), "list is empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    int counterList = 0;

    public void RemoveLoadingLayout_EnableButton() {
        counterList++;
        if (counterList >= 2) {
            LayoutWaitingPreOrderList.setVisibility(View.GONE);
            dialogbtnFinsih.setEnabled(true);
        }
    }


    public void OfflineSave() {
        if(emailpref == null)
        {
            new DialogHint().Session_End(SummaryActivity.this,getBaseContext());
        }
        else {
            db_sync = new SyncDBHelper(getBaseContext());
            float total = 0;
            for (Product product : productList) {
                total += Float.valueOf(product.Total);
            }

            if(OpenfromPreOrderPage)
            Headerid = String.valueOf(db_sync.InsertTransactionsOffline(ServerConfigIDpref, SelectedCustomerNumber, SelectedCustomerName,emailpref, Long, Lat, TxtComment.getText().toString(), spinnerDeliveryMethods.getSelectedItem().toString(), "1", String.valueOf(total)));
            else if(OpenfromStockTaken)
            Headerid = String.valueOf(db_sync.InsertTransactionsOffline(ServerConfigIDpref, SelectedCustomerNumber, SelectedCustomerName,emailpref, Long, Lat, TxtComment.getText().toString(), spinnerDeliveryMethods.getSelectedItem().toString(), "4", String.valueOf(total)));

            db_sync.InsertTransactionTenderOffline(Headerid, spinnerTenderType.getSelectedItem().toString(), emailpref, String.valueOf(total));

            for (Product product : productList) {
                db_sync.InsertTransactionItemsOffline(Headerid, product.SKU, product.QTY, String.valueOf(product.UnitPrice), "0", emailpref, product.OnHand,product.Subinventory,product.getInventoryType());
            }

            SFA_GetTruckType trucks = new API_Sync_Back().Trucks;
            for ( TruckType Truck: trucks.getTruckType() ) {
                if(Truck.getCount() > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("TruckID", Truck.getID());
                    contentValues.put("Height", Truck.getHeight());
                    contentValues.put("Type", Truck.getType());
                    contentValues.put("Weight", Truck.getWeight());
                    contentValues.put("length", Truck.getLength());
                    contentValues.put("size", String.valueOf(Truck.getSize()));
                    contentValues.put("width", Truck.getWidth());
                    contentValues.put("count", Truck.getCount());
                    contentValues.put("HeaderID", Headerid);
                    new SyncDBHelper(getBaseContext()).InsertTransaction_Truck(contentValues);

                }

            }
            new API_Sync_Back().Trucks.setTruckType(db_sync.getAllTruckTypeOffline());

            Promotions prom = new Promotions().FindPromo(getBaseContext(),Headerid);

            String total_order =db_sync.GetOrder_Price(Headerid);

            Intent i = new Intent(getBaseContext(), FinishOfflineActivity.class);
            i.putExtra("header", Headerid);
            i.putExtra("total",total_order );
            i.putExtra("TransactionType", "2");

            if(prom != null)
                i.putExtra("prom", prom.getPromNote());

            else
                i.putExtra("prom", "No Promotion");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            try {
                if(OpenfromPreOrderPage)
                db.deleteAll();
                else if(OpenfromStockTaken)
                    db_Stock.deleteAllStockTaking();
            } catch (Exception e) {
                dialogSave.dismiss();
                Toast.makeText(getBaseContext(), "please Delete your orders manual !", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void OnlineSave() {
        dialog.dismiss();

        if ( ((!spinnerDeliveryMethods.getSelectedItem().toString().equals("Delivery Method List Loading...") || !spinnerDeliveryMethods.getSelectedItem().toString().equals("") || spinnerDeliveryMethods != null)
                && (!spinnerTenderType.getSelectedItem().toString().equals("Tender Type List Loading......") || !spinnerTenderType.getSelectedItem().toString().equals("") || spinnerTenderType != null) ) || OpenfromStockTaken ) {
            dialogSave = ProgressDialog.show(SummaryActivity.this, "",
                    "Saving. Please wait...", true);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                  if(OpenfromPreOrderPage)
                   new API_Online().SFA_SavaOrder_OneBulk(getApplicationContext(), SummaryActivity.this, dialogSave, "preorder", User.ServerConfigID, SelectedCustomerName,SummaryCustomerNumber.getText().toString(), User.Username, Long, Lat, TxtComment.getText().toString(), spinnerDeliveryMethods.getSelectedItem().toString(), spinnerTenderType.getSelectedItem().toString(), "1", "Online");
                  else if(OpenfromStockTaken)
                      new API_Online().SFA_SavaOrder_OneBulk(getApplicationContext(), SummaryActivity.this, dialogSave, "StockTaking", User.ServerConfigID, SelectedCustomerName,SummaryCustomerNumber.getText().toString(), User.Username, Long, Lat, TxtComment.getText().toString(), "No Delivery Methods", "No Tender Type", "4", "Online");

                    } catch (Exception e) {
                        dialogSave.dismiss();
                        Toast.makeText(getBaseContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }, 4000);
        }
        else {
            Toast.makeText(getBaseContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    //this method for get last location if can not detect location on getlocation() method
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            double longitude = location.getLatitude();
            double latitude = location.getLongitude();
            Lat = String.valueOf(latitude);
            Long = String.valueOf(longitude);
        } catch (NullPointerException e) {

        }

    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location == null) {
                getLastLocation(); // get last known location

            } else {
                try {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Lat = String.valueOf(latitude);
                    Long = String.valueOf(longitude);
                } catch (Exception e) {

                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "please check your GPS before save", Toast.LENGTH_LONG).show();
        }
    }


    public void btnCancelProducts(View view) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(SummaryActivity.this).create();
            alertDialog.setTitle("Warning !");
            alertDialog.setIcon(getResources().getDrawable(R.drawable.warning));
            alertDialog.setMessage("Are You Sure Delete All Orders ?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (OpenfromPreOrderPage) {
                                db.deleteAll();
                            } else if (OpenfromStockTaken) {
                                db_Stock.deleteAllStockTaking();
                            } else if (OpenfromOrderPage == true) {
                                db_order.deleteAll();
                                db_serial.deleteAll();
                                db_sync.RemoveSelectedStockSerialsOffline();
                            } else if (OpenfromDealerOrder == true) {
                                db_dealer.deleteAllDealerOrder();
                            }
                            Intent intent = new Intent(getBaseContext(), CustomerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Order not Canceled !", Toast.LENGTH_LONG).show();
        }

    }


    public void btnCollectSerial(View view) {
        getLocation();
        Intent intent = new Intent(getApplicationContext(), CollectSerialActivity.class);
        intent.putExtra("Long", Long);
        intent.putExtra("Lat", Lat);
        intent.putExtra("CustomerNumber", SummaryCustomerNumber.getText());
        startActivity(intent);
    }

    public void loadingDropDownlists() {

        if (OfflineMode == true) {
            if (db_sync.checkifDeliveryMethodOfflineEmpty() == false && db_sync.checkifTenderTypeOfflineEmpty() == false) {
                TxtSummConnectionType.setTextColor(Color.RED);
                TxtSummConnectionType.setText("Offline");
                DeliveryTypelist = new ArrayList<>();
                TenderTypeList = new ArrayList<>();
                db_sync = new SyncDBHelper(getBaseContext());

                for (DeliveryMethod d : db_sync.getAllDeliveryMethodOffline()) {
                    DeliveryTypelist.add(d.getName());
                }
                spinnerDeliveryMethods.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, DeliveryTypelist));
                if(spinnerDeliveryMethods!=null||!spinnerDeliveryMethods.getSelectedItem().equals("")) {
                    RemoveLoadingLayout_EnableButton();
                }
                for (TenderType t : db_sync.getAllTenderTypeOffline()) {
                    TenderTypeList.add(t.getName());
                }
                spinnerTenderType.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, TenderTypeList));
                if(spinnerTenderType!=null||!spinnerTenderType.getSelectedItem().equals("")) {
                    RemoveLoadingLayout_EnableButton();
                }
                dialog.show();
            } else {
                Toast.makeText(getBaseContext(), "check internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {

            final boolean checktenderspinner = tenderTypeSpinner.GetTenderType(spinnerTenderType, getBaseContext(),SummaryActivity.this);
            final boolean checkdeliveryspinner = false; //deliveryMethodsSpinner.GetDeliveryMethods(spinnerDeliveryMethods, getBaseContext(),SummaryActivity.this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (checktenderspinner == false) {
                        try {
                            tenderTypeSpinner.GetTenderType(spinnerTenderType, getBaseContext(),SummaryActivity.this);
                        } catch (Exception e) {
                            tenderTypeSpinner.GetTenderType(spinnerTenderType, getBaseContext(),SummaryActivity.this);
                        }
                    }
                    if (checkdeliveryspinner == false) {
                        try {
                            deliveryMethodsSpinner.GetDeliveryMethods(spinnerDeliveryMethods, getBaseContext(),SummaryActivity.this);
                        } catch (Exception e) {
                            deliveryMethodsSpinner.GetDeliveryMethods(spinnerDeliveryMethods, getBaseContext(),SummaryActivity.this);
                        }
                    }
                }
            },1500);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterNetworkChanges();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);


        i.putExtra("customerName", SelectedCustomerName);
        i.putExtra("customerNumber", SelectedCustomerNumber);
        i.putExtra("customerDueDateFrom", customerDueDateFrom);
        i.putExtra("CustomerPrice_list", CustomerPrice_list);
        i.putExtra("CREDIT_LIMIT", C_CREDIT_LIMIT);
        i.putExtra("BALANCE", C_BALANCE);
        i.putExtra("OUTSTANDING", C_OUTSTANDING);
        i.putExtra("RISKY_CHECKS", C_RISKY_CHECKS);
        i.putExtra("firstOpen", true);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);
        finish();
        return;
    }

    public static void changeTxtConnectionTypeSummaryActivity(boolean value) {
        if (value) {
            TxtSummConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtSummConnectionType.setText("Online");
            LayoutSummaryOffline.setVisibility(View.GONE);
        } else {
            TxtSummConnectionType.setTextColor(Color.RED);
            TxtSummConnectionType.setText("Offline");
            LayoutSummaryOffline.setVisibility(View.VISIBLE);
            LayoutSummaryOffline.setSelected(true);
        }
    }

    private void registerNetworkBroadcast() {
        try {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
