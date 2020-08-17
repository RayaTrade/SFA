package com.example.ahmed_hasanein.sfa;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Adapter.ProductAdapter;
import Adapter.ProductDealerAdapter;
import FillSpinners.BrandSpinner;
import FillSpinners.CategorySpinner;
import FillSpinners.ModelSpinner;
import FillSpinners.SubinventorySpinner;
import Model.Brand;
import Model.Category;
import Model.Model;
import Model.Product;
import Model.User;
import Utility.Connectivity;
import Utility.CountCartDrawable;
import Utility.NetworkChangeReceiver;
import preview_database.DB.DealerDB.DealerDBHelper;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.ProductPreOrderDB.ProductDBHelper;
import preview_database.DB.StockTakingDB.StockTakingDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromDealerOrder;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromPreOrderPage;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromStockTaken;
import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;

import Model.Subinventory;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    TextView txtCustomerNumber, txtCustomerName, TxtMainTotalPrice, txtVNumbermain, TxtNoItemfound, TxtPageType, cart_item_notification, TxtbuttomSheetTitle, TxtButtonSheetVisitDate;
    LinearLayout LayoutItemSearchHint;
    /*
     * # change date 30/7/2019
     * © changed by Ahmed Ali
     * -- description: .........
     *  txtusername
     */
    static TextView TxtConnectionType,txtusername;
    ImageView btnRefresh;
    ImageView btnSearch;
    CardView btnSummary;
    private Spinner spinnerSubinventory,spinnerCategory, spinnerBrand, spinnerModel;
    String url;
    List<Product> productList;
    List<Product> productListDB;
    RecyclerView recyclerView;
    ProductAdapter adapter;
    ProductDealerAdapter productDealerAdapter;
    ProgressBar mProgressBar;
    Bundle extras;
    String Category;
    List<String> categoryList;
    List<String> SubinventoryList;
    String Brand;
    List<String> brandList;
    String Model;
    List<String> modelList;
    CategorySpinner categorySpinner;
    boolean openfromCustomerActivity = false;
    private TableLayout tableLayout;
    private ProductDBHelper db;
    private StockTakingDBHelper db_stocktaking;
    private OrderDBHelper db_order;
    private DealerDBHelper db_dealer;
    private SyncDBHelper db_sync;
    View tableRow;
    TextView sku;
    TextView qty;
    TextView price;
    TextView total;
    float totalprice = 0;
    boolean openfromOrder;
    public static String SelectedCustomerName, SelectedCustomerNumber,CustomerPrice_list,customerDueDateFrom;
    public static float TotalOfItems;
    Connectivity connectivity;
    private BroadcastReceiver mNetworkReceiver;
    Menu defaultMenu;
    CardView layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    Button btnbuttomSheetshowDetails, btnBottomSheetSummaryMain;
    public ProgressDialog DropDownListDialogMainActivity;
    static TextView LayoutMainOffline;
    LinearLayout LayoutWaitingMainList,Subinventory_Layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        txtCustomerNumber = (TextView) findViewById(R.id.TxtMainCustomerNumber);
        txtCustomerName = (TextView) findViewById(R.id.TxtMainCustomerName);
        /*
         * # change date 30/7/2019
         * © changed by Ahmed Ali
         * -- description: .........
         *  txtusername = (TextView) findViewById(R.id.txtusername);
         */
        TxtConnectionType = (TextView) findViewById(R.id.TxtConnectionType);
        txtusername = (TextView) findViewById(R.id.txtusername);
        TxtMainTotalPrice = (TextView) findViewById(R.id.TxtMainTotalPrice);
        btnSummary = (CardView) findViewById(R.id.btnSummary);
        btnRefresh = (ImageView) findViewById(R.id.btnRefresh);
        txtVNumbermain = (TextView) findViewById(R.id.txtVNumbermain);
        btnSearch = (ImageView) findViewById(R.id.btnSearch);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerSubinventory = (Spinner) findViewById(R.id.spinnerSubinventory);
        spinnerBrand = (Spinner) findViewById(R.id.spinnerBrand);
        spinnerModel = (Spinner) findViewById(R.id.spinnerModel);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        TxtNoItemfound = (TextView) findViewById(R.id.TxtNoItemfound);
        TxtPageType = (TextView) findViewById(R.id.TxtPageType);
        LayoutItemSearchHint = (LinearLayout) findViewById(R.id.LayoutItemSearchHint);
        cart_item_notification = (TextView) findViewById(R.id.cart_item_notification);
        layoutBottomSheet = (CardView) findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        TxtbuttomSheetTitle = (TextView) findViewById(R.id.TxtbuttomSheetTitle);
        btnbuttomSheetshowDetails = (Button) findViewById(R.id.btnbuttomSheetshowDetails);
        TxtButtonSheetVisitDate = (TextView) findViewById(R.id.TxtButtonSheetVisitDate);
        btnBottomSheetSummaryMain = (Button) findViewById(R.id.btnBottomSheetSummaryMain);
        LayoutMainOffline = (TextView) this.findViewById(R.id.marque_scrolling_text_main);
        LayoutWaitingMainList = (LinearLayout) findViewById(R.id.LayoutWaitingMainList);
        Subinventory_Layout = (LinearLayout) findViewById(R.id.Subinventory_Layout);
        txtVNumbermain.setText("Version " + BuildConfig.VERSION_NAME + "," + BuildConfig.VERSION_CODE); //set textview for version number
        mProgressBar.setVisibility(View.GONE);
        /*
         * # change date 30/7/2019
         * © changed by Ahmed Ali
         * -- description: .........
         *   txtusername.setText(User.Username);
         */
        txtusername.setText(User.Username.replace("@rayacorp.com",""));

        // toolbar fancy stuff
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product List");



        db = new ProductDBHelper(getBaseContext());
        db_stocktaking = new StockTakingDBHelper(getBaseContext());
        db_order = new OrderDBHelper(getBaseContext());
        db_dealer = new DealerDBHelper(getBaseContext());
        db_sync = new SyncDBHelper(getBaseContext());
        productListDB = new ArrayList<>();
        categorySpinner = new CategorySpinner(getBaseContext());
        //keyboard when open not change ui
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //check connectivity
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcast();

        /*List<Serial> serialList = new ArrayList<>();
        serialList.addAll(db_sync.getAllGetStockSerialsOffline());
        Toast.makeText(this,"SerialList"+serialList.size(),Toast.LENGTH_SHORT).show();*/

        extras = getIntent().getExtras();
        if (extras != null) {
            SelectedCustomerName = extras.getString("customerName");
            SelectedCustomerNumber = extras.getString("customerNumber");
            CustomerPrice_list = extras.getString("CustomerPrice_list");
            customerDueDateFrom = extras.getString("customerDueDateFrom");

            txtCustomerName.setText(SelectedCustomerName);
            txtCustomerNumber.setText(SelectedCustomerNumber);
            Category = extras.getString("category");
            Brand = extras.getString("brand");
            Model = extras.getString("model");
            TxtButtonSheetVisitDate.setText(customerDueDateFrom);
            openfromCustomerActivity = extras.getBoolean("firstOpen");
            openfromOrder = extras.getBoolean("Order");
        }
        if (Category == null) {
            Category = "All";
        }
        if (Brand == null) {
            Brand = "All";
        }
        if (Model == null) {
            Model = "All";
        }


        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
        } else {
            OfflineMode = true;
        }

        if (OfflineMode == true) {
            boolean checkifCategoryOfflineEmpty = db_sync.checkifCategoryOfflineEmpty();
            boolean checkifModelOfflineEmpty = db_sync.checkifModelOfflineEmpty();
            boolean checkifBrandOfflineEmpty = db_sync.checkifBrandOfflineEmpty();
            if ( checkifCategoryOfflineEmpty == false && checkifModelOfflineEmpty == false &&  checkifBrandOfflineEmpty== false) {
                TxtConnectionType.setTextColor(Color.RED);
                TxtConnectionType.setText("Offline");
                ///offline dropdown list /////////
                loadingDropDownlistsOffline();
                /////////////////////////////////
            } else {
                Toast.makeText(getBaseContext(), "Check internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            TxtConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtConnectionType.setText("Online");
            ///online dropdown list ////////////
            loadingDropDownlistsOnline();
            //////////////////////////////////
        }

        BuildTableOfItems(); //fill bottom table with last add item from sqlite db

        if (OpenfromPreOrderPage == true) {
            TxtPageType.setText("Pre-Order");
            TxtbuttomSheetTitle.setText("Pre-Order Details");
            cart_item_notification.setText(String.valueOf(db.getAllProduct().size()));
            Subinventory_Layout.setVisibility(View.VISIBLE);
        }else if(OpenfromStockTaken == true){
            TxtPageType.setText("Stock Taking");
            TxtbuttomSheetTitle.setText("Stock Taking Details");
            cart_item_notification.setText(String.valueOf(db_stocktaking.getAllStockTaking().size()));
            Subinventory_Layout.setVisibility(View.GONE);
        }
        else if (OpenfromOrderPage == true) {
            TxtPageType.setText("Order");
            TxtbuttomSheetTitle.setText("Order Details");
            cart_item_notification.setText(String.valueOf(db_order.getAllOrder().size()));
            Subinventory_Layout.setVisibility(View.GONE);
        } else if (OpenfromDealerOrder == true) {
            TxtPageType.setText("Dealer Order");
            TxtbuttomSheetTitle.setText("Dealer Details");
            cart_item_notification.setText(String.valueOf(db_dealer.getAllDealerOrder().size()));
            Subinventory_Layout.setVisibility(View.GONE);
        }


        DropDownChanged();  //when user select dropdown


        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SummaryActivity.class);
                i.putExtra("customerNumber", txtCustomerNumber.getText().toString());
                if (OpenfromOrderPage == true) {
                    i.putExtra("Order", true);
                }
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(i);
                finish();
            }
        });

        btnBottomSheetSummaryMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SummaryActivity.class);
                i.putExtra("customerNumber", txtCustomerNumber.getText().toString());
                if (OpenfromOrderPage == true) {
                    i.putExtra("Order", true);
                }
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutWaitingMainList.setVisibility(View.VISIBLE);
                if(OpenfromPreOrderPage == true)
                new SubinventorySpinner(getBaseContext()).GetUser_X_Subinventory(LayoutWaitingMainList,spinnerSubinventory,getBaseContext(),OfflineMode,OpenfromPreOrderPage);
                else
                categorySpinner.GetCategory(LayoutWaitingMainList, spinnerCategory, getBaseContext(), OfflineMode);
             }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerCategory == null && spinnerCategory.getSelectedItem() ==null ) {
                    TextView errorText = (TextView)spinnerCategory.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Category list not loaded");//changes the selected item text to this
                    return;
                }
                if(spinnerBrand == null && spinnerBrand.getSelectedItem() ==null ) {
                    TextView errorText = (TextView)spinnerBrand.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Brand list not loaded");//changes the selected item text to this
                    return;
                }
                if(spinnerModel == null && spinnerModel.getSelectedItem() ==null ) {
                    TextView errorText = (TextView)spinnerModel.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Model list not loaded");//changes the selected item text to this
                    return;
                }
                LayoutItemSearchHint.setVisibility(View.INVISIBLE);
                //check connectivity
                connectivity = new Connectivity();
                boolean checkConnectivity = new Connectivity().isConnectedFast(getBaseContext());
                if (checkConnectivity == true) {
                    OfflineMode = false; //online
                    TxtConnectionType.setTextColor(Color.parseColor("#32CD32"));
                    TxtConnectionType.setText("Online");
                } else {
                    OfflineMode = true; //offline
                    TxtConnectionType.setTextColor(Color.RED);
                    TxtConnectionType.setText("Offline");
                }
                String category = "All";
                String brand = "All";
                String model = "All";
                String SubinventoryID = "0";
                if (spinnerCategory != null && spinnerBrand != null && spinnerModel != null) {
                    try {
                        category = spinnerCategory.getSelectedItem().toString();
                        brand = spinnerBrand.getSelectedItem().toString();
                        model = spinnerModel.getSelectedItem().toString();

                        if(OpenfromPreOrderPage)
                         SubinventoryID = new SubinventorySpinner(getBaseContext()).find_SubinventoryID(spinnerSubinventory.getSelectedItem().toString());
                    } catch (Exception e) {
                        category = "All";
                        brand = "All";
                        model = "All";
                        SubinventoryID = "0";
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "please click refresh and try again ", Toast.LENGTH_SHORT).show();
                    }

                }
                mProgressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if (OpenfromPreOrderPage == true) {
                    if (OfflineMode == true) {
                        //////////////////////////////Offline Items/////////////////////////////////////////////
                        if (db_sync.checkifPreorderOfflineisEmpty() == false) {
                            productList = new ArrayList<>();
                            for (Product p : db_sync.getAllPreOrderOffline(SelectedCustomerNumber,spinnerSubinventory.getSelectedItem().toString(),category,brand,model)) {

                                Product product = new Product(p.SKU, p.Category, p.Brand, p.Model, "", p.Description, p.image, Float.valueOf(p.getUnitPrice()), p.OnHand, p.Color, SelectedCustomerNumber, "", p.Visit_Date,p.ACC,
                                        p.STATUS,p.TAX_CODE,p.TAX_RATE ,p.SEGMENT1 ,p.MAIN_CAT ,p.INVENTORY_ITEM_ID ,p.CREATION_DATE , p.Subinventory);
                                productList.add(product);
                            }
                            RenderList(productList);
                            SearchOffline(spinnerCategory.getSelectedItem().toString(), spinnerBrand.getSelectedItem().toString(), spinnerModel.getSelectedItem().toString());
                        } else {
                            Toast.makeText(getBaseContext(), "Check internet Connection", Toast.LENGTH_SHORT).show();
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////////
                    } else {
                        ////////////////Online Items////////////////////////////////////////////////////////////////
                        new API_Online().SFA_Items_All(getBaseContext(), MainActivity.this, category, txtCustomerNumber.getText().toString(), brand, model, "1",SubinventoryID); //preorder
                        ////////////////////////////////////////////////////////////////////////////////////////////
                    }
                } else if (OpenfromStockTaken == true) {
                    if (OfflineMode == true) {
                        //////////////////////////////Offline Items/////////////////////////////////////////////
                        if (db_sync.checkifPreorderOfflineisEmpty() == false) {
                            productList = new ArrayList<>();
                            for (Product p : db_sync.getAllStockTakenItemOffline(SelectedCustomerNumber,category,brand,model)) {

                                Product product = new Product(p.SKU, p.Category, p.Brand, p.Model, "", p.Description, p.image, Float.valueOf(p.getUnitPrice()), p.OnHand, p.Color, SelectedCustomerNumber, "", p.Visit_Date,p.ACC,
                                        p.STATUS,p.TAX_CODE,p.TAX_RATE ,p.SEGMENT1 ,p.MAIN_CAT ,p.INVENTORY_ITEM_ID ,p.CREATION_DATE , p.Subinventory);
                                productList.add(product);
                            }
                            RenderList(productList);
                            SearchOffline(spinnerCategory.getSelectedItem().toString(), spinnerBrand.getSelectedItem().toString(), spinnerModel.getSelectedItem().toString());
                        } else {
                            Toast.makeText(getBaseContext(), "Check internet Connection", Toast.LENGTH_SHORT).show();
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////////
                    } else {
                        ////////////////Online Items////////////////////////////////////////////////////////////////
                        new API_Online().SFA_Ora_StockItems_All(getBaseContext(), MainActivity.this, category, txtCustomerNumber.getText().toString(), brand, model, "4",SubinventoryID); //StockTaken
                        ////////////////////////////////////////////////////////////////////////////////////////////
                    }
                }

                else if (OpenfromOrderPage == true || openfromOrder == true) {
                    if (OfflineMode == true) {
                        productList = new ArrayList<>();
                        if (db_sync.checkifOrderOfflineisEmpty() == false) {
                            ///////////////////////////Offline Items//////////////////////////////////////////////////
                            for (Product p : db_sync.getAllOrderOffline(SelectedCustomerNumber)) {
                                Product product = new Product(p.SKU, p.Category, p.Brand, p.Model, "", p.Description, p.image, Float.valueOf(p.getUnitPrice()), p.OnHand, p.Color, SelectedCustomerNumber, "", p.Visit_Date,p.ACC,
                                        p.STATUS,p.TAX_CODE,p.TAX_RATE ,p.SEGMENT1 ,p.MAIN_CAT ,p.INVENTORY_ITEM_ID ,p.CREATION_DATE,p.Subinventory);
                                        productList.add(product);
                            }
                            RenderList(productList);
                            SearchOffline(spinnerCategory.getSelectedItem().toString(), spinnerBrand.getSelectedItem().toString(), spinnerModel.getSelectedItem().toString());
                            //////////////////////////////////////////////////////////////////////////////////////////
                        } else {
                            Toast.makeText(getBaseContext(), "Check internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //////////////////////////////Online Items///////////////////////////////////////////////////////
                        new API_Online().SFA_Items_All(getBaseContext(), MainActivity.this, category, txtCustomerNumber.getText().toString(), brand, model, "2", SubinventoryID); //order
                        /////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                } else if (OpenfromDealerOrder == true) {
                    if (OfflineMode == true) {
                        productList = new ArrayList<>();
                        if (db_sync.checkifPreorderOfflineisEmpty() == false) {
                            ///////////////////////////Offline Items//////////////////////////////////////////////////
                            for (Product p : db_sync.getAllPreOrderOffline(SelectedCustomerNumber,spinnerSubinventory.getSelectedItem().toString(),category,brand,model)) {
                                Product product = new Product(p.SKU, p.Category, p.Brand, p.Model, "", p.Description, p.image, Float.valueOf(p.getUnitPrice()), p.OnHand, p.Color, SelectedCustomerNumber, "", p.Visit_Date,p.ACC,
                                        p.STATUS,p.TAX_CODE,p.TAX_RATE ,p.SEGMENT1 ,p.MAIN_CAT ,p.INVENTORY_ITEM_ID ,p.CREATION_DATE , p.Subinventory);
                                productList.add(product);
                            }
                            RenderList(productList);
                            SearchOffline(spinnerCategory.getSelectedItem().toString(), spinnerBrand.getSelectedItem().toString(), spinnerModel.getSelectedItem().toString());
                        } else {
                            Toast.makeText(getBaseContext(), "Check internet Connection", Toast.LENGTH_SHORT).show();
                        }
                        //////////////////////////////////////////////////////////////////////////////////////////
                    } else {
                        //////////////////////////////Online Items///////////////////////////////////////////////////////
                        new API_Online().SFA_Items_All(getBaseContext(), MainActivity.this, category, txtCustomerNumber.getText().toString(), brand, model, "1", SubinventoryID); //dealer order
                        /////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                }
            }
        });


        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        btnbuttomSheetshowDetails.setText("Close");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        btnbuttomSheetshowDetails.setText("Show");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btnbuttomSheetshowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnbuttomSheetshowDetails.setText("Close");
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btnbuttomSheetshowDetails.setText("Show");
                }
            }
        });
    }

    public void RenderList(List<Product> productList) {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        if (OpenfromPreOrderPage) {
            adapter = new ProductAdapter(productList, getBaseContext(), txtCustomerName.getText().toString(), txtCustomerNumber.getText().toString(), MainActivity.this,OfflineMode,spinnerSubinventory.getSelectedItem().toString());
            recyclerView.setAdapter(adapter);
        }else if (OpenfromOrderPage) {
            adapter = new ProductAdapter(productList, getBaseContext(), txtCustomerName.getText().toString(), txtCustomerNumber.getText().toString(), MainActivity.this,OfflineMode,"");
            recyclerView.setAdapter(adapter);
        }else if (OpenfromStockTaken) {
            adapter = new ProductAdapter(productList, getBaseContext(), txtCustomerName.getText().toString(), txtCustomerNumber.getText().toString(), MainActivity.this,OfflineMode,"");
            recyclerView.setAdapter(adapter);
        } else if (OpenfromDealerOrder) {
            productDealerAdapter = new ProductDealerAdapter(productList, getBaseContext(), txtCustomerName.getText().toString(), txtCustomerNumber.getText().toString(), MainActivity.this);
            recyclerView.setAdapter(productDealerAdapter);
        }
        mProgressBar.setVisibility(View.GONE);
        if (productList.size() <= 0) {
            this.productList = productList;
            TxtNoItemfound.setVisibility(View.VISIBLE);
        } else {
            this.productList = productList;
            TxtNoItemfound.setVisibility(View.GONE);
        }
    }


    public void SearchOffline(String Category, String Brand, String Model) {
        try {
            List<Product> newList = new ArrayList<>();
            for (Product n : productList) {
                if (Category.equals("All")) {
                    Category = "";
                }
                if (Brand.equals("All")) {
                    Brand = "";
                }
                if (Model.equals("All")) {
                    Model = "";
                }
                if (n.getCategory().toLowerCase().contains(Category.toLowerCase()) && n.getBrand().toLowerCase().contains(Brand.toLowerCase()) && n.getModel().toLowerCase().contains(Model.toLowerCase())) {
                    newList.add(n);
                }
            }
            if (OpenfromDealerOrder == false)
                adapter.updateList(newList);
            else
                productDealerAdapter.updateList(newList);

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Not Found Try Online !", Toast.LENGTH_SHORT).show();
        }
    }

    public void DropDownChanged() {
        spinnerSubinventory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                  if(spinnerSubinventory != null)
                  {
                      categorySpinner.GetCategory(LayoutWaitingMainList, spinnerCategory, getBaseContext(), OfflineMode);
                  }
                }
                catch (Exception e)
                {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BrandSpinner brandSpinner = new BrandSpinner(getBaseContext());
                try {
                    if (spinnerCategory != null) {
                        brandSpinner.GetBrandsOnline(spinnerBrand, getBaseContext(), spinnerCategory.getSelectedItem().toString(), OfflineMode);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ModelSpinner modelSpinner = new ModelSpinner(getBaseContext());
                try {
                    if (spinnerCategory != null && spinnerBrand != null) {
                        modelSpinner.GetModelsOnline(spinnerModel, getBaseContext(), spinnerCategory.getSelectedItem().toString(), spinnerBrand.getSelectedItem().toString(), OfflineMode);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void BuildTableOfItems() {
        try {
            if (OpenfromPreOrderPage == true ) {
                db = new ProductDBHelper(this);
                productListDB.addAll(db.getAllProduct());
            } else if ( OpenfromStockTaken == true) {
                db_stocktaking = new StockTakingDBHelper(this);
                productListDB.addAll(db_stocktaking.getAllStockTaking());
            } else if (OpenfromOrderPage == true) {
                db_order = new OrderDBHelper(this);
                productListDB.addAll(db_order.getAllOrder());
            } else if (OpenfromDealerOrder == true) {
                db_dealer = new DealerDBHelper(this);
                productListDB.addAll(db_dealer.getAllDealerOrder());
            }
        } catch (Exception e) {

        }
        float totalprice = 0;
        for (final Product n : productListDB) {
            tableRow = LayoutInflater.from(this).inflate(R.layout.raw_main_table, null, false);
            sku = (TextView) tableRow.findViewById(R.id.txttablesku);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Highlight selected row
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        View row = tableLayout.getChildAt(i);
                        if (row == v) {
                            String rowSKU = n.SKU.toString();
                            Toast.makeText(getBaseContext(), rowSKU, Toast.LENGTH_SHORT).show();
                            row.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        } else {
                            row.setBackgroundResource(R.drawable.table_border);
                        }
                    }
                }
            });
            qty = (TextView) tableRow.findViewById(R.id.txttableQty);
            price = (TextView) tableRow.findViewById(R.id.txttableprice);
            total = (TextView) tableRow.findViewById(R.id.txttabletotal);
            sku.setText(n.SKU);
            qty.setText(n.QTY);
            price.setText(Float.toString(n.UnitPrice));
            total.setText(n.Total);

            totalprice = totalprice + Float.valueOf(n.Total);
            tableLayout.addView(tableRow);

        }
        tableLayout.refreshDrawableState();

        TxtMainTotalPrice.setText(Float.toString(totalprice));
        TotalOfItems = totalprice;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_product, menu);
        defaultMenu = menu;
        //search toolbar
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        //cart toolbar
        MenuItem CartItem = menu.findItem(R.id.action_cart);
        CartItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(getBaseContext(), SummaryActivity.class);
                i.putExtra("customerNumber", txtCustomerNumber.getText().toString());
                if (OpenfromOrderPage == true) {
                    i.putExtra("Order", true);
                }
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return false;
            }
        });
        return true;
    }


    public void setCount(Context context, String count) {

        MenuItem menuItem = defaultMenu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountCartDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountCartDrawable) {
            badge = (CountCartDrawable) reuse;
        } else {
            badge = new CountCartDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            if (OpenfromPreOrderPage == true ) {
                db = new ProductDBHelper(this);
                productListDB.addAll(db.getAllProduct());
                setCount(getBaseContext(), String.valueOf(db.getAllProduct().size()));
            } else if ( OpenfromStockTaken == true) {
                db_stocktaking = new StockTakingDBHelper(this);
                productListDB.addAll(db_stocktaking.getAllStockTaking());
                setCount(getBaseContext(), String.valueOf(db_stocktaking.getAllStockTaking().size()));
            } else if (OpenfromOrderPage == true) {
                db_order = new OrderDBHelper(this);
                productListDB.addAll(db_order.getAllOrder());
                setCount(getBaseContext(), String.valueOf(db_order.getAllOrder().size()));
            } else if (OpenfromDealerOrder == true) {
                db_dealer = new DealerDBHelper(this);
                productListDB.addAll(db_dealer.getAllDealerOrder());
                setCount(getBaseContext(), String.valueOf(db_dealer.getAllDealerOrder().size()));
            }
        } catch (Exception e) {

        }
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


    //this method set and update table and total of price
    //refresh table of items
    public void RefreshBottomSheetTable() {

        tableLayout.removeAllViews();

        productListDB = new ArrayList<>();
        try {
            if (OpenfromPreOrderPage == true ) {
                db = new ProductDBHelper(this);
                productListDB.addAll(db.getAllProduct());
                cart_item_notification.setText(String.valueOf(db.getAllProduct().size()));
                setCount(getBaseContext(), String.valueOf(db.getAllProduct().size()));
            } else if (OpenfromStockTaken == true) {
                db_stocktaking = new StockTakingDBHelper(this);
                productListDB.addAll(db_stocktaking.getAllStockTaking());
                cart_item_notification.setText(String.valueOf(db_stocktaking.getAllStockTaking().size()));
                setCount(getBaseContext(), String.valueOf(db_stocktaking.getAllStockTaking().size()));
            } else if (OpenfromOrderPage == true) {
                db_order = new OrderDBHelper(this);
                productListDB.addAll(db_order.getAllOrder());
                cart_item_notification.setText(String.valueOf(db_order.getAllOrder().size()));
                setCount(getBaseContext(), String.valueOf(db_order.getAllOrder().size()));
            } else if (OpenfromDealerOrder == true) {
                db_dealer = new DealerDBHelper(this);
                productListDB.addAll(db_dealer.getAllDealerOrder());
                cart_item_notification.setText(String.valueOf(db_dealer.getAllDealerOrder().size()));
                setCount(getBaseContext(), String.valueOf(db_dealer.getAllDealerOrder().size()));
            }
        } catch (Exception e) {

        }
        totalprice = 0;
        for (final Product n : productListDB) {
            tableRow = LayoutInflater.from(this).inflate(R.layout.raw_main_table, null, false);
            sku = (TextView) tableRow.findViewById(R.id.txttablesku);
            qty = (TextView) tableRow.findViewById(R.id.txttableQty);
            price = (TextView) tableRow.findViewById(R.id.txttableprice);
            total = (TextView) tableRow.findViewById(R.id.txttabletotal);
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Highlight selected row
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        View row = tableLayout.getChildAt(i);
                        if (row == v) {
                            String rowSKU = n.SKU.toString();
                            Toast.makeText(getBaseContext(), rowSKU, Toast.LENGTH_SHORT).show();
                            row.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        } else {
                            //Change this to your normal background color.
                            row.setBackgroundResource(R.drawable.table_border);
                        }
                    }
                }
            });
            sku.setText(n.SKU);
            qty.setText(n.QTY);
            price.setText(Float.toString(n.UnitPrice));
            total.setText(n.Total);

            totalprice = totalprice + Float.valueOf(n.Total);


            tableLayout.addView(tableRow);
        }

        TxtMainTotalPrice.setText(Float.toString(totalprice));
        tableLayout.refreshDrawableState();
        TotalOfItems = totalprice;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        RefreshBottomSheetTable();
    }

    public void loadingDropDownlistsOffline() {
        categoryList = new ArrayList<>();
        brandList = new ArrayList<>();
        modelList = new ArrayList<>();
        SubinventoryList = new ArrayList<>();

        for (Subinventory c : db_sync.getUser_X_Subinventory()) {
            SubinventoryList.add(c.getSubinventory());
        }

        spinnerSubinventory.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,  SubinventoryList));


        for (Category c : db_sync.getAllCategoryOffline()) {
            categoryList.add(c.getName());
        }
        spinnerCategory.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, categoryList));

        for (Brand b : db_sync.getAllBrandOffline()) {
            brandList.add(b.getName());
        }
        if (!brandList.contains("All")) {
            brandList.add(0, "All");
        }
        spinnerBrand.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, brandList));

        for (Model m : db_sync.getAllModelOffline()) {
            modelList.add(m.getName());
        }
        if (!modelList.contains("All")) {
            modelList.add(0, "All");
        }
        spinnerModel.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, modelList));
        LayoutWaitingMainList.setVisibility(View.GONE);
    }

    public void loadingDropDownlistsOnline() {
        if(OpenfromPreOrderPage == true)
            new SubinventorySpinner(getBaseContext()).GetUser_X_Subinventory(LayoutWaitingMainList,spinnerSubinventory,getBaseContext(),OfflineMode,OpenfromPreOrderPage);
        else
            categorySpinner.GetCategory(LayoutWaitingMainList, spinnerCategory, getBaseContext(), OfflineMode);
    }

    public static void changeTxtConnectionTypeMainActivity(boolean value) {
        if (value) {
            TxtConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtConnectionType.setText("Online");
            LayoutMainOffline.setVisibility(View.GONE);
        } else {
            TxtConnectionType.setTextColor(Color.RED);
            TxtConnectionType.setText("Offline");
            LayoutMainOffline.setVisibility(View.VISIBLE);
            LayoutMainOffline.setSelected(true);
        }
    }

    private void registerNetworkBroadcast() {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
}
