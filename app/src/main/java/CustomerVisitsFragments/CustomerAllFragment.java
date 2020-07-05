package CustomerVisitsFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.ahmed_hasanein.sfa.CustomerActivity;
import com.example.ahmed_hasanein.sfa.LoginActivity;
import com.example.ahmed_hasanein.sfa.R;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Adapter.CustomerAdapter;
import Model.Customer;
import Model.User;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;

@SuppressLint("ValidFragment")
public class CustomerAllFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progress_bar_all_customer;
    private List<Customer> customerList;
    private CustomerAdapter adapter;
    String name="";
    public CustomerAllFragment() {
    }
    public CustomerAllFragment(String name) {
        this.name = name;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.navigation_all_customer, container, false);
        recyclerView = view.findViewById(R.id.AllCustomerRecyclerView);
        progress_bar_all_customer = view.findViewById(R.id.progress_bar_all_customer);
        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(customerList, getActivity(),getActivity());
        ViewFragmentCustomer();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewFragmentCustomer();
    }
    public void ViewFragmentCustomer(){
        if(!OfflineMode) { //online
            if(User.ServerConfigID != null && User.Username != null)
                if(name.equals(""))
                new API_Online().SFA_Customers(recyclerView, progress_bar_all_customer, getContext(), getActivity());
                else
                 new API_Online().SFA_Customers_Filter(recyclerView, progress_bar_all_customer, getContext(), getActivity(),name);

            else
            {
                Intent backtologin = new Intent (getContext(), LoginActivity.class);
                startActivity(backtologin);
            }
        }else{
            List<Customer> customerList = new ArrayList<>();
            SyncDBHelper db_sync = new SyncDBHelper(getContext());
            if (db_sync.checkifCustomerOfflineisEmpty() == false) {
                customerList.addAll(db_sync.getAllCustomerOffline());
                progress_bar_all_customer.setVisibility(View.GONE);
                ((CustomerActivity)getActivity()).RenderList(customerList,true,recyclerView);
            }
        }
    }
}
