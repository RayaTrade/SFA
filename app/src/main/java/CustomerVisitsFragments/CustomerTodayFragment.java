package CustomerVisitsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ahmed_hasanein.sfa.CustomerActivity;
import com.example.ahmed_hasanein.sfa.LoginActivity;
import com.example.ahmed_hasanein.sfa.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import API.API_Online;
import Model.Customer;
import Model.User;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;

public class CustomerTodayFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progress_bar_today_customer;
    public CustomerTodayFragment() {
    }

    public static CustomerTodayFragment newInstance(String param1, String param2) {
        CustomerTodayFragment fragment = new CustomerTodayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.navigation_today_customer, container, false);
        recyclerView = view.findViewById(R.id.TodayRecyclerView);
        progress_bar_today_customer = view.findViewById(R.id.progress_bar_today_customer);
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

                new API_Online().GetCustomersWithVisit(recyclerView, progress_bar_today_customer, getContext(), getActivity(), User.ServerConfigID, "0", User.Username);

            else
            {
                Intent backtologin = new Intent (getContext(), LoginActivity.class);
                startActivity(backtologin);
            }

        }else{ //offline
            List<Customer> customerList = new ArrayList<>();
            SyncDBHelper db_sync = new SyncDBHelper(getContext());
            if (db_sync.checkifCustomersWithVisitOfflineIsEmpty() == false) {
                String TodayDate = TodayDate();
                customerList.addAll(db_sync.getCustomersWithVisitByDueDateFromOffline(TodayDate));
                progress_bar_today_customer.setVisibility(View.GONE);
                ((CustomerActivity)getActivity()).RenderList(customerList,true,recyclerView);
                ((CustomerActivity)getActivity()).ChangeTotalVisitTxt(String.valueOf(customerList.size()));
            }
        }
    }
    public String TodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
        Date date = new Date();
        String TodayDate = dateFormat.format(date);
        return TodayDate;
    }

}
