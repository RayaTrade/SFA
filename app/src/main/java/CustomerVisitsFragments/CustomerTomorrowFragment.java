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

public class CustomerTomorrowFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progress_bar_tomorrow_customer;
    private List<Customer> customerList;
    private CustomerAdapter adapter;

    public CustomerTomorrowFragment() {
    }

    public static CustomerTomorrowFragment newInstance(String param1, String param2) {
        CustomerTomorrowFragment fragment = new CustomerTomorrowFragment();
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
        View view =  inflater.inflate(R.layout.navigation_tomorrow_customer, container, false);
        recyclerView = view.findViewById(R.id.TomorrowRecyclerView);
        progress_bar_tomorrow_customer = view.findViewById(R.id.progress_bar_tomorrow_customer);
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
                new API_Online().GetCustomersWithVisit(recyclerView, progress_bar_tomorrow_customer, getContext(), getActivity(), User.ServerConfigID, "1", User.Username);
            else
            {
                Intent backtologin = new Intent (getContext(), LoginActivity.class);
                startActivity(backtologin);
            }
        }else{ //offline

            List<Customer> customerList = new ArrayList<>();
            SyncDBHelper db_sync = new SyncDBHelper(getContext());
            if (db_sync.checkifCustomersWithVisitOfflineIsEmpty() == false) {
                String TomorrowDate = TomorrowDate();
                customerList.addAll(db_sync.getCustomersWithVisitByDueDateFromOffline(TomorrowDate));
                progress_bar_tomorrow_customer.setVisibility(View.GONE);
                ((CustomerActivity)getActivity()).RenderList(customerList,true,recyclerView);
                ((CustomerActivity)getActivity()).ChangeTotalVisitTxt(String.valueOf(customerList.size()));
            }
        }
    }
    public String TomorrowDate(){
        DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
        Date date = new Date();
        String TomorrowDate = dateFormat.format(date);
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(dateFormat.parse(TomorrowDate));
            c.add(Calendar.DATE, 1);  // number of days to add
            TomorrowDate = dateFormat.format(c.getTime());  // dt is now the new date
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"Error ! Please check Internet Connection",Toast.LENGTH_LONG).show();
        }
        return TomorrowDate;
    }
}
