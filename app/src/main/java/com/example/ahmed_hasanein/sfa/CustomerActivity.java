package com.example.ahmed_hasanein.sfa;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Adapter.CustomerAdapter;
import FillSpinners.SalesReasonSpinner;
import Model.Customer;
import Model.Product;
import Model.Reason;
import Model.User;
import Utility.Connectivity;
import Utility.DialogHint;
import Utility.NetworkChangeReceiver;
import CustomerVisitsFragments.CustomerAllFragment;
import CustomerVisitsFragments.CustomerMonthFragment;
import CustomerVisitsFragments.CustomerTodayFragment;
import CustomerVisitsFragments.CustomerTomorrowFragment;
import CustomerVisitsFragments.CustomerWeekFragment;
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

public class CustomerActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    String url;
    CardView AddDearCustomerCardView;
    Dialog DialogAddCustomer;
    List<Customer> customerList;
    RecyclerView recyclerView;
    CustomerAdapter adapter;
    ProgressBar mProgressBar;
    Bundle extras;
    private ProductDBHelper db;
    private StockTakingDBHelper db_stock;
    private OrderDBHelper db_order;
    private SyncDBHelper db_sync;
    private DealerDBHelper db_dealer;
    Customer customer;
    /*
     * # change date 30/7/2019
     * © changed by Ahmed Ali
     * -- description: .........
     *  txtusername
     */
    TextView txthintCustomer, txtVNumberCustomer, TxtCustPageType, TxtCustTotal,txtusername;
    static TextView LayoutCustomerOffline;
    LinearLayout txtHintcustomers;
    static TextView TxtCustConnectionType;
    boolean openfromOrder, openFromDealerOrder;
    private BroadcastReceiver mNetworkReceiver;
    private ActionBar toolbar;
    BottomNavigationView navigation;
    TextView navigation_today;
    EditText Filter_Customer_editText;
    ImageView Filter_Customer_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        txthintCustomer = (TextView) findViewById(R.id.txthintCustomer);
        txtHintcustomers = (LinearLayout) findViewById(R.id.txtHintcustomers);
        txtVNumberCustomer = (TextView) findViewById(R.id.txtVNumberCustomer);
        TxtCustPageType = (TextView) findViewById(R.id.TxtCustPageType);
        Filter_Customer_editText = findViewById(R.id.Filter_Customer_editText);
        Filter_Customer_search = findViewById(R.id.Filter_Customer_search);
        /*
         * # change date 30/7/2019
         * © changed by Ahmed Ali
         * -- description: .........
         *  txtusername
         */
        TxtCustTotal = (TextView) findViewById(R.id.TxtCustTotal);
        txtusername= (TextView) findViewById(R.id.txtusername);
        TxtCustConnectionType = (TextView) findViewById(R.id.TxtCustConnectionType);
        LayoutCustomerOffline = (TextView) findViewById(R.id.marque_scrolling_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtVNumberCustomer.setText("Version " + BuildConfig.VERSION_NAME + "," + BuildConfig.VERSION_CODE); //set textview for version number
        /*
         * # change date 30/7/2019
         * © changed by Ahmed Ali
         * -- description: .........
         *  txtusername
         */
        txtusername.setText("UserName | "+User.Username);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationCounter();



        View activeLabel = navigation.findViewById(R.id.largeLabel);
        if (activeLabel != null && activeLabel instanceof TextView) {
            ((TextView) activeLabel).setPadding(0, 0, 0, 0);
            ((TextView) activeLabel).setGravity(View.TEXT_ALIGNMENT_CENTER);
        }
        getSupportActionBar().setTitle("Today Customer List");


        DialogAddCustomer = new Dialog(this);
        AddDearCustomerCardView = (CardView) findViewById(R.id.AddDealerCustomerCardView);
        AddDearCustomerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCustomerDialog();
            }
        });


        extras = getIntent().getExtras();
        if (extras != null) {
            openfromOrder = extras.getBoolean("Order");
            openFromDealerOrder = extras.getBoolean("DealerOrder");
            if (openfromOrder == true) {
                AddDearCustomerCardView.setVisibility(View.GONE);
                TxtCustPageType.setText("Order");
            } else if (OpenfromPreOrderPage == true) {
                AddDearCustomerCardView.setVisibility(View.GONE);
                TxtCustPageType.setText("Pre-Order");
            } else if (openFromDealerOrder == true) {
                TxtCustPageType.setText("Dealer Order");
            }else if(OpenfromStockTaken == true){
                AddDearCustomerCardView.setVisibility(View.GONE);
                TxtCustPageType.setText("Stock Taking");
            }
        } else {
            if (OpenfromOrderPage == true) {
                AddDearCustomerCardView.setVisibility(View.GONE);
                TxtCustPageType.setText("Order");
            } else if (OpenfromPreOrderPage == true) {
                AddDearCustomerCardView.setVisibility(View.GONE);
                TxtCustPageType.setText("Pre-Order");
            } else if (OpenfromDealerOrder == true) {
                TxtCustPageType.setText("Dealer Order");
            }else if(OpenfromStockTaken == true){
                TxtCustPageType.setText("Stock Taking");
                AddDearCustomerCardView.setVisibility(View.GONE);

            }
        }

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcast();
        boolean checkConnectivity = new Connectivity().isConnectedFast(this);
        if (checkConnectivity == true) {
            OfflineMode = false;
        } else {
            OfflineMode = true;
        }

        if (OfflineMode == true) {
            TxtCustConnectionType.setTextColor(Color.RED);
            TxtCustConnectionType.setText("Offline");

        } else if (OfflineMode == false) {
            TxtCustConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtCustConnectionType.setText("Online");
        }
        ///load customer today fragment//////////////////
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFragment(new CustomerTodayFragment());
            }
        },0);
        ////////////////////////////////////////////////

        Filter_Customer_editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                txtHintcustomers.setVisibility(View.GONE);
                return false;
            }
        });
        Filter_Customer_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Filter_Customer_editText.getText().toString();
                loadFragment(new CustomerAllFragment(name));

            }
        });
    }


    public void RenderList(List<Customer> customerList, boolean isFragment, RecyclerView recyclerView) {
        txtHintcustomers.setVisibility(View.INVISIBLE);
        this.customerList = customerList;
        if (OpenfromPreOrderPage == true)
            customerList = pendingPreOrderSelectedCustomer(customerList);
        else if (OpenfromOrderPage == true)
            customerList = pendingOrderSelectedCustomer(customerList);
        else if (OpenfromDealerOrder == true)
            customerList = pendingDealerOrderSelectedCustomer(customerList);
        else if (OpenfromStockTaken == true)
            customerList = pendingStockTakenSelectedCustomer(customerList);
        ///////////////////////////////////////////////////////////////////////////////////

        if (isFragment) {
            recyclerView.setLayoutManager(new GridLayoutManager(CustomerActivity.this, 2));
            adapter = new CustomerAdapter(customerList, CustomerActivity.this, this);
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
            adapter = new CustomerAdapter(customerList, getBaseContext(), this);
        }

        recyclerView.setAdapter(adapter);
        mProgressBar.setVisibility(View.GONE);
        if (customerList.size() <= 0) {
            txtHintcustomers.setVisibility(View.VISIBLE);
        }
    }

    private void BottomNavigationCounter() {
        db_sync = new SyncDBHelper(getBaseContext());

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navigation.getChildAt(0);

        //today
        View TodayNav = bottomNavigationMenuView.getChildAt(0);
        BottomNavigationItemView todayNavItemView = (BottomNavigationItemView) TodayNav;

        View TodayNavBadge = LayoutInflater.from(this)
                .inflate(R.layout.navigation_bottom_counter, todayNavItemView, true);

        TextView TodayTxt = TodayNavBadge.findViewById(R.id.TxtNavBadge);
        String TodayDate = new CustomerTodayFragment().TodayDate();
        String TodayCount = String.valueOf(db_sync.getCustomersWithVisitByDueDateFromOffline(TodayDate).size());
        if (!TodayCount.equals("0"))
            TodayTxt.setText(TodayCount);
        else
            TodayTxt.setVisibility(View.INVISIBLE);
        //tomorrow
        View tomorrowNav = bottomNavigationMenuView.getChildAt(1);
        BottomNavigationItemView tomorrowItemView = (BottomNavigationItemView) tomorrowNav;

        View tomorrowNavBadge = LayoutInflater.from(this)
                .inflate(R.layout.navigation_bottom_counter, tomorrowItemView, true);

        TextView tomorrowTxt = tomorrowNavBadge.findViewById(R.id.TxtNavBadge);
        String TomorrowDate = new CustomerTomorrowFragment().TomorrowDate();
        String TomorrowCount = String.valueOf(db_sync.getCustomersWithVisitByDueDateFromOffline(TomorrowDate).size());
        if (!TomorrowCount.equals("0"))
            tomorrowTxt.setText(TomorrowCount);
        else
            tomorrowTxt.setVisibility(View.INVISIBLE);
        //week
        View weekNav = bottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView weekNavItemView = (BottomNavigationItemView) weekNav;

        View weekNavBadge = LayoutInflater.from(this)
                .inflate(R.layout.navigation_bottom_counter, weekNavItemView, true);

        TextView weekTxt = weekNavBadge.findViewById(R.id.TxtNavBadge);
        String WeekDate = new CustomerWeekFragment().WeekDate();
        String WeekCount = String.valueOf(db_sync.getCustomersWithVisitByDueDateFromOffline(WeekDate).size());
        if (!WeekCount.equals("0"))
            weekTxt.setText(WeekCount);
        else
            weekTxt.setVisibility(View.INVISIBLE);
        //month
        View monthNav = bottomNavigationMenuView.getChildAt(3);
        BottomNavigationItemView monthNavItemView = (BottomNavigationItemView) monthNav;

        View monthNavBadge = LayoutInflater.from(this)
                .inflate(R.layout.navigation_bottom_counter, monthNavItemView, true);

        TextView MonthTxt = monthNavBadge.findViewById(R.id.TxtNavBadge);
        String MonthDate = new CustomerMonthFragment().MonthDate();
        String Monthcount = String.valueOf(db_sync.getCustomersWithVisitByDueDateFromOffline(MonthDate).size());
        if (!Monthcount.equals("0"))
            MonthTxt.setText(Monthcount);
        else
            MonthTxt.setVisibility(View.INVISIBLE);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_today:
                    navigation.getMenu().getItem(0).setCheckable(true);
                    txtHintcustomers.setVisibility(View.INVISIBLE);
                    TxtCustTotal.setText("Total Visits");
                    getSupportActionBar().setTitle("Today Customer List");
                    clearRecycleView();
                    try {
                        fragment = new CustomerTodayFragment();
                        loadFragment(fragment);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;

                case R.id.navigation_tomorrow:
                    navigation.getMenu().getItem(1).setCheckable(true);
                    txtHintcustomers.setVisibility(View.INVISIBLE);
                    TxtCustTotal.setText("Total Visits");
                    getSupportActionBar().setTitle("Tomorrow Customer List");
                    clearRecycleView();
                    try {
                        fragment = new CustomerTomorrowFragment();
                        loadFragment(fragment);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;

                case R.id.navigation_week:
                    navigation.getMenu().getItem(2).setCheckable(true);
                    txtHintcustomers.setVisibility(View.INVISIBLE);
                    TxtCustTotal.setText("Total Visits");
                    getSupportActionBar().setTitle("Week Customer List");
                    clearRecycleView();
                    try {
                        fragment = new CustomerWeekFragment();
                        loadFragment(fragment);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;

                case R.id.navigation_month:
                    navigation.getMenu().getItem(3).setCheckable(true);
                    txtHintcustomers.setVisibility(View.INVISIBLE);
                    TxtCustTotal.setText("Total Visits");
                    getSupportActionBar().setTitle("Month Customer List");
                    clearRecycleView();
                    try {
                        fragment = new CustomerMonthFragment();
                        loadFragment(fragment);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
            }
            return false;
        }
    };

    public void clearRecycleView() {
        if (customerList != null) {
            final int size = customerList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    customerList.remove(0);
                }
                recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
                adapter = new CustomerAdapter(customerList, getBaseContext(), this);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    public List<Customer> pendingPreOrderSelectedCustomer(List<Customer> customerList) {
        //check if user make pervious order
        List<Product> productList;
        List<Customer> PendingcustomerList = new ArrayList<>();
        String customerVisitID = "";
        String customerNumber = "";
        productList = new ArrayList<>();
        db = new ProductDBHelper(getBaseContext());
        if (db != null) {
            productList.addAll(db.getAllProduct());
            if (productList.size() > 0) {
                for (Product n : productList) {
                    if (productList != null) {
                        customerNumber = n.Customer_number;
                        customerVisitID = db.getVisitDatePendingPreOrder();
                        customerVisitID = n.Visit_Date;
                        txthintCustomer.setText("You have Pre Order not finished or canceled ! \n Visit Date : " + customerVisitID);
                        txthintCustomer.setVisibility(View.VISIBLE);
                    }
                }
                String customerName = "";
                String customerPriceList = "";
                String customerDueDateFrom = "";
                String customerDueDateTo = "";
                String customerScheuleID = "";
                String customerBalance = "";
                String customerOutstanding = "";
                String customerCreditLimit ="";
                String customerRISKY_CHECKS = "";
                if (customerNumber != null || !customerNumber.equals("")) {
                    for (Customer c : customerList) {
                        if (c.getNumber().equals(customerNumber)) {
                            customerName = c.Name;
                            customerPriceList = c.Price_list;
                            customerDueDateFrom = c.DueDateFrom;
                            customerDueDateTo = c.DueDateTo;
                            customerScheuleID = c.ScheuleID;
                            customerVisitID = c.VisitID;
                            customerBalance = c.BALANCE;
                            customerCreditLimit = c.CREDIT_LIMIT;
                            customerOutstanding = c.OUTSTANDING;
                            customerRISKY_CHECKS = c.RISKY_CHECKS;
                        }
                    }
                    if (!customerName.equals("")) {
                        customer = new Customer(customerNumber, customerName, customerPriceList, customerDueDateFrom, customerDueDateTo, customerScheuleID, customerVisitID,customerBalance,customerCreditLimit,customerOutstanding,customerRISKY_CHECKS);
                        if (!customerNumber.equals("")) {
                            PendingcustomerList.add(customer);
                        }
                    }
                }
            } else {
                PendingcustomerList = customerList; //no pending customer selected (get normal customer list)
            }
        }
        return PendingcustomerList;
    }

    public List<Customer> pendingStockTakenSelectedCustomer(List<Customer> customerList) {
        //check if user make pervious order
        List<Product> productList;
        List<Customer> PendingcustomerList = new ArrayList<>();
        String customerVisitID = "";
        String customerNumber = "";
        productList = new ArrayList<>();
        db_stock = new StockTakingDBHelper(getBaseContext());
        if (db_stock != null) {
            productList.addAll(db_stock.getAllStockTaking());
            if (productList.size() > 0) {
                for (Product n : productList) {
                    if (productList != null) {
                        customerNumber = n.Customer_number;
                      //  customerVisitID = db_stock.getVisitDatePendingPreOrder();
                        customerVisitID = n.Visit_Date;
                        txthintCustomer.setText("You have Stock Taking not finished or canceled ! \n Visit Date : " + customerVisitID);
                        txthintCustomer.setVisibility(View.VISIBLE);
                    }
                }
                String customerName = "";
                String customerPriceList = "";
                String customerDueDateFrom = "";
                String customerDueDateTo = "";
                String customerScheuleID = "";
                String customerBalance = "";
                String customerOutstanding = "";
                String customerCreditLimit ="";
                String customerRISKY_CHECKS = "";
                if (customerNumber != null || !customerNumber.equals("")) {
                    for (Customer c : customerList) {
                        if (c.getNumber().equals(customerNumber)) {
                            customerName = c.Name;
                            customerPriceList = c.Price_list;
                            customerDueDateFrom = c.DueDateFrom;
                            customerDueDateTo = c.DueDateTo;
                            customerScheuleID = c.ScheuleID;
                            customerVisitID = c.VisitID;
                            customerBalance = c.BALANCE;
                            customerCreditLimit = c.CREDIT_LIMIT;
                            customerOutstanding = c.OUTSTANDING;
                            customerRISKY_CHECKS = c.RISKY_CHECKS;
                        }
                    }
                    if (!customerName.equals("")) {
                        customer = new Customer(customerNumber, customerName, customerPriceList, customerDueDateFrom, customerDueDateTo, customerScheuleID, customerVisitID,customerBalance,customerCreditLimit,customerOutstanding,customerRISKY_CHECKS);
                        if (!customerNumber.equals("")) {
                            PendingcustomerList.add(customer);
                        }
                    }
                }
            } else {
                PendingcustomerList = customerList; //no pending customer selected (get normal customer list)
            }
        }
        return PendingcustomerList;
    }

    public List<Customer> pendingOrderSelectedCustomer(List<Customer> customerList) {
        //check if user make pervious order
        List<Product> orderList;
        List<Customer> PendingcustomerList = new ArrayList<>();
        String customerVisitID = "";
        String customerNumber = "";
        orderList = new ArrayList<>();
        db_order = new OrderDBHelper(getBaseContext());
        if (db_order != null) {
            orderList.addAll(db_order.getAllOrder());
            if (orderList.size() > 0) {
                for (Product o : orderList) {
                    if (orderList != null) {
                        customerNumber = o.Customer_number;
                        customerVisitID = db_order.getVisitDatePendingOrder();
                        customerVisitID = o.Visit_Date;
                        txthintCustomer.setText("You have Order not finished or canceled ! \n Visit Date : " + customerVisitID);
                        txthintCustomer.setVisibility(View.VISIBLE);
                    }
                }
                String customerName = "";
                String customerPriceList = "";
                String customerDueDateFrom = "";
                String customerDueDateTo = "";
                String customerScheuleID = "";

                String customerBalance = "";
                String customerOutstanding = "";
                String customerCreditLimit ="";
                String customerRISKY_CHECKS = "";
                if (customerNumber != null || !customerNumber.equals("")) {
                    for (Customer c : customerList) {
                        if (c.getNumber().equals(customerNumber)) {
                            customerName = c.Name;
                            customerPriceList = c.Price_list;
                            customerDueDateFrom = c.DueDateFrom;
                            customerDueDateTo = c.DueDateTo;
                            customerScheuleID = c.ScheuleID;
                            customerVisitID = c.VisitID;

                            customerBalance = c.BALANCE;
                            customerCreditLimit = c.CREDIT_LIMIT;
                            customerOutstanding = c.OUTSTANDING;
                            customerRISKY_CHECKS = c.RISKY_CHECKS;
                        }
                    }
                    if (!customerName.equals("")) {
                        customer = new Customer(customerNumber, customerName, customerPriceList, customerDueDateFrom, customerDueDateTo, customerScheuleID, customerVisitID,customerBalance,customerCreditLimit,customerOutstanding,customerRISKY_CHECKS);
                        if (!customerNumber.equals("")) {
                            PendingcustomerList.add(customer);
                        }
                    }
                }
            } else {
                PendingcustomerList = customerList; //no pending customer selected (get normal customer list)
            }
        }
        return PendingcustomerList;
    }

    public List<Customer> pendingDealerOrderSelectedCustomer(List<Customer> customerList) {
        //check if user make pervious order
        List<Product> dealerOrderList;
        List<Customer> PendingcustomerList = new ArrayList<>();
        String customerNumber = "";
        dealerOrderList = new ArrayList<>();
        db_dealer = new DealerDBHelper(getBaseContext());
        if (db_dealer != null) {
            dealerOrderList.addAll(db_dealer.getAllDealerOrder());
            if (dealerOrderList.size() > 0) {
                for (Product o : dealerOrderList) {
                    if (dealerOrderList != null) {
                        customerNumber = o.Customer_number;
                        txthintCustomer.setText("You have Dealer Order not finished or canceled !");
                        txthintCustomer.setVisibility(View.VISIBLE);
                    }
                }
                String customerName = "";
                String customerPriceList = "";
                String customerDueDateFrom = "";
                String customerDueDateTo = "";
                String customerScheuleID = "";
                String customerVisitID = "";
                String customerBalance = "";
                String customerOutstanding = "";
                String customerCreditLimit ="";
                String customerRISKY_CHECKS = "";
                if (customerNumber != null || !customerNumber.equals("")) {
                    for (Customer c : customerList) {
                        if (c.getNumber().equals(customerNumber)) {
                            customerName = c.Name;
                            customerPriceList = c.Price_list;
                            customerDueDateFrom = c.DueDateFrom;
                            customerDueDateTo = c.DueDateTo;
                            customerScheuleID = c.ScheuleID;
                            customerVisitID = c.VisitID;
                            customerBalance = c.BALANCE;
                            customerCreditLimit = c.CREDIT_LIMIT;
                            customerOutstanding = c.OUTSTANDING;
                            customerRISKY_CHECKS = c.RISKY_CHECKS;
                        }
                    }
                    customer = new Customer(customerNumber, customerName, customerPriceList, customerDueDateFrom, customerDueDateTo, customerScheuleID, customerVisitID,customerBalance,customerCreditLimit,customerOutstanding,customerRISKY_CHECKS);
                    customerList = new ArrayList<>();
                    if (!customerNumber.equals("")) {
                        customerList.add(customer);
                    }
                }
            } else {
                PendingcustomerList = customerList; //no pending customer selected (get normal customer list)
            }
        }
        return PendingcustomerList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_customer, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem action_showAll = menu.findItem(R.id.action_showAll);
        action_showAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                navigation.getMenu().getItem(0).setCheckable(false);
                navigation.getMenu().getItem(1).setCheckable(false);
                navigation.getMenu().getItem(2).setCheckable(false);
                navigation.getMenu().getItem(3).setCheckable(false);
                txtHintcustomers.setVisibility(View.INVISIBLE);
                TxtCustTotal.setText("Total Visits");

                getSupportActionBar().setTitle("Customers List");
                Fragment fragment;
                clearRecycleView();
                try {
                    fragment = new CustomerAllFragment();
                    loadFragment(fragment);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return true;
            }
        });
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    //search
    @Override
    public boolean onQueryTextChange(String s) {
        try {
            String userinput = s.toLowerCase();
            List<Customer> newList = new ArrayList<>();

            for (Customer n : customerList) {
                if (n.getName().toLowerCase().contains(userinput) || n.getNumber().toLowerCase().contains(userinput) || n.getPrice_list().toLowerCase().contains(userinput) || n.getDueDateFrom().toLowerCase().contains(userinput)) {
                    newList.add(n);
                }
            }
            adapter.updateList(newList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Please Wait until finish loading !", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public static void changeTxtConnectionTypeCustomerActivity(boolean value) {
        if (value) {
            TxtCustConnectionType.setTextColor(Color.parseColor("#32CD32"));
            TxtCustConnectionType.setText("Online");
            LayoutCustomerOffline.setVisibility(View.GONE);
        } else {
            TxtCustConnectionType.setTextColor(Color.RED);
            TxtCustConnectionType.setText("Offline");
            LayoutCustomerOffline.setSelected(true);
            LayoutCustomerOffline.setVisibility(View.VISIBLE);
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


    /////////////////////////////////////////Open Sales Reason Dialog////////////////////////////////////////////////
    AlertDialog alertDialog;
    Button dialogbtncloseReason, dialogbtnFinsihReason;
    EditText TxtCommentReason;
    Spinner spinnerReason;
    SalesReasonSpinner salesReasonSpinner;
    LinearLayout LayoutWaitingReasonList;
    public void SalesReasonPopup(final Context context, final Activity activity, final String CountryCode, final String CustomerName, final String CustomerNumber, final String Submitter, final String Long, final String Lat, final String DeliveryMethod, final String TransactionType, final String OfflineID) {

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.popup_without_sales, viewGroup, false);
        dialogbtncloseReason = (Button) dialogView.findViewById(R.id.dialogbtncloseReason);
        dialogbtnFinsihReason = (Button) dialogView.findViewById(R.id.dialogbtnFinsihReason);
        TxtCommentReason = (EditText) dialogView.findViewById(R.id.TxtCommentReason);
        spinnerReason = (Spinner) dialogView.findViewById(R.id.spinnerReason);
        LayoutWaitingReasonList = (LinearLayout) dialogView.findViewById(R.id.LayoutWaitingReasonList);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setView(dialogView);

        dialogbtnFinsihReason.setEnabled(false);
        loadingSalesReasonDropDownLists(context, spinnerReason); //load dropdown

        dialogbtncloseReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        dialogbtnFinsihReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(TxtCommentReason.getText())) {
                    TxtCommentReason.setError("Comment is Required !");
                    return;
                }
                if(spinnerReason == null && spinnerReason.getSelectedItem() ==null ) {
                    TextView errorText = (TextView)spinnerReason.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Error ! please restart dialog");//changes the selected item text to this
                    return;
                }

                new DialogHint().getLocation(activity);
                if (spinnerReason.getSelectedItem().toString().equals(R.array.Reason_spinner) || spinnerReason.getSelectedItem().toString().equals("") || spinnerReason == null) {
                    Toast.makeText(getBaseContext(), "Please wait until loading reason list", Toast.LENGTH_LONG).show();
                }
                ProgressDialog dialogSave = ProgressDialog.show(activity, "",
                        "Saving. Please wait...", true);

              //  new API_Online().SavePreOrderHeader(context, activity, dialogSave, "without_sales", CountryCode, CustomerNumber, Submitter, Long, Lat, TxtCommentReason.getText().toString(), DeliveryMethod, "", TransactionType, OfflineID);
                String Comment_Reason = spinnerReason.getSelectedItem().toString() +","+ TxtCommentReason.getText().toString();
              //  new API_Online().SavePreOrderHeader(context, activity, dialogSave, "without_sales", CountryCode, CustomerNumber, Submitter, Long, Lat, Comment_Reason, DeliveryMethod, "", TransactionType, OfflineID);
                new API_Online().SFA_SavaOrder_OneBulk(context, activity, dialogSave, "without_sales", CountryCode, CustomerName,CustomerNumber, Submitter, Long, Lat, Comment_Reason, DeliveryMethod, "", TransactionType, OfflineID);
                alertDialog.dismiss();


            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void RemoveLoadingLayout_EnableButton(){
        LayoutWaitingReasonList.setVisibility(View.GONE);
        dialogbtnFinsihReason.setEnabled(true);
    }

    public void loadingSalesReasonDropDownLists(final Context context, final Spinner spinnerReason) {
        salesReasonSpinner = new SalesReasonSpinner(context);
        List<String> SalesReasonList;
        if (OfflineMode == true) {
            db_sync = new SyncDBHelper(context);
            if (db_sync.checkifSalesReasonOfflineEmpty() == false) {
                SalesReasonList = new ArrayList<>();

                for (Reason r : db_sync.getAllReasonOffline()) {
                    SalesReasonList.add(r.getReason());
                }
                spinnerReason.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SalesReasonList));
                if(spinnerReason!=null||!spinnerReason.getSelectedItem().equals("")) {
                    RemoveLoadingLayout_EnableButton();
                }
            } else {
                Toast.makeText(context, "check internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                salesReasonSpinner.GetReason(spinnerReason, context,CustomerActivity.this, "EN");
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error ! Try open with good internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void showAddCustomerDialog() {
        DialogAddCustomer.setContentView(R.layout.popup_dealer_add_customer);
        DialogAddCustomer.show();
    }

    public void ChangeTotalVisitTxt(String totalVisit) {
        TxtCustTotal.setText("Total Visits: " + totalVisit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        this.finish();
        return;
    }
}
