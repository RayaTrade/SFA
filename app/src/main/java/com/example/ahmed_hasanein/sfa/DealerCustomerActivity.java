package com.example.ahmed_hasanein.sfa;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Adapter.CustomerAdapter;
import Model.Customer;
import Model.User;

import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;

public class DealerCustomerActivity extends AppCompatActivity {
    CardView AddDearCustomerCardView;
    Dialog DialogAddCustomer;
    List<Customer> customerList;
    RecyclerView recyclerView;
    CustomerAdapter adapter;
    ProgressBar mProgressBar;
    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_customer);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDealerCustomer);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_customer_dealer);
        Toolbar toolbar = findViewById(R.id.toolbarDealerCustomer);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setTitle("Dealer Customer List");
        new API_Online().SFA_Customers(recyclerView,mProgressBar,getApplicationContext(),this);

        DialogAddCustomer = new Dialog(this);
        AddDearCustomerCardView = (CardView) findViewById(R.id.AddDealerCustomerCardView);
        AddDearCustomerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCustomerDialog();
            }
        });
    }

    private void showAddCustomerDialog() {
        DialogAddCustomer.setContentView(R.layout.popup_dealer_add_customer);
        DialogAddCustomer.show();
    }

}
