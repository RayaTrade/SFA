package CustomerVisitsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ahmed_hasanein.sfa.CustomerActivity;
import com.example.ahmed_hasanein.sfa.LoginActivity;
import com.example.ahmed_hasanein.sfa.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import API.API_Online;
import Adapter.CustomerAdapter;
import Model.Customer;
import Model.User;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;

public class CustomerWeekFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progress_bar_week_customer;
    private List<Customer> customerList;
    private CustomerAdapter adapter;

    public CustomerWeekFragment() {
    }

    public static CustomerWeekFragment newInstance(String param1, String param2) {
        CustomerWeekFragment fragment = new CustomerWeekFragment();
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
        View view =  inflater.inflate(R.layout.navigation_week_customer, container, false);
        recyclerView = view.findViewById(R.id.WeekRecyclerView);
        progress_bar_week_customer = view.findViewById(R.id.progress_bar_week_customer);
        customerList = new ArrayList<>();
        ViewFragmentCustomer();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ViewFragmentCustomer();
    }
    public void ViewFragmentCustomer(){
        if(!OfflineMode) {//online
            if(User.ServerConfigID != null && User.Username != null)
                new API_Online().GetCustomersWithVisit(recyclerView, progress_bar_week_customer, getContext(), getActivity(), User.ServerConfigID, "7", User.Username);
            else
            {
                Intent backtologin = new Intent (getContext(), LoginActivity.class);
                startActivity(backtologin);
            }
        }else{
            List<Customer> customerList = new ArrayList<>();
            SyncDBHelper db_sync = new SyncDBHelper(getContext());
            if (db_sync.checkifCustomersWithVisitOfflineIsEmpty() == false) {
                String WeekDate = WeekDate();
                customerList.addAll(db_sync.getCustomersWithVisitByDueDateFromOffline(WeekDate));
                progress_bar_week_customer.setVisibility(View.GONE);
                ((CustomerActivity)getActivity()).RenderList(customerList,true,recyclerView);
                ((CustomerActivity)getActivity()).ChangeTotalVisitTxt(String.valueOf(customerList.size()));
            }
        }
    }
    public String WeekDate(){
        DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
        Date date = new Date();
        String WeekDate = dateFormat.format(date);
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(dateFormat.parse(WeekDate));
            c.add(Calendar.DATE, 7);  // number of days to add next week
            WeekDate = dateFormat.format(c.getTime());  // dt is now the new date
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"Error ! Please check Internet Connection",Toast.LENGTH_LONG).show();
        }
        return WeekDate;
    }
}
