package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Model.Customer;
import Model.User;
import Utility.DialogHint;

import com.example.ahmed_hasanein.sfa.MainActivity;
import com.example.ahmed_hasanein.sfa.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromDealerOrder;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromPreOrderPage;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromStockTaken;


public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerHolder> {
    List<Customer> customerList;
    private Context context;
    private Activity activity;
    String TransactionType;
    public static String SelectedCustomerVisitDate;

    public CustomerAdapter(List<Customer> customerList,Context context,Activity activity) {
        this.customerList = customerList;
        this.context = context;
        this.activity =activity;
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_customer, viewGroup, false);
        CustomerHolder holder = new CustomerHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerHolder customerHolder, int i) {
        final Customer customer = customerList.get(i);
        customerHolder.customerNumber.setText(customer.Number);
        customerHolder.customerName.setText(customer.Name);
        customerHolder.customer_PRICE_LIST.setText(customer.Price_list);
        SelectedCustomerVisitDate = customer.DueDateFrom;
        customerHolder.customer_DueDateFrom.setText(customer.DueDateFrom);
        customerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new DialogHint().GPS_Dialog(activity)){
                    return;
                }
                new DialogHint().getLocation(activity);

                if(OpenfromPreOrderPage == true) { //preorder
                    TransactionType = "1";
                }else if(OpenfromOrderPage==true){ //order
                    TransactionType = "2";
                }else if(OpenfromDealerOrder==true){ //dealer
                    TransactionType = "1";
                }
                else if(OpenfromStockTaken==true){ //StockTaken
                    TransactionType = "4";
                }
                if(OpenfromStockTaken == true){

                    if( User.Username == null)
                    {
                        new DialogHint().Session_End(activity,context);
                    }
                    else {
                        Intent i = new Intent(activity, MainActivity.class);
                        i.putExtra("customerName", customer.Name);
                        i.putExtra("customerNumber", customer.Number);
                        i.putExtra("customerDueDateFrom", customer.DueDateFrom);
                        i.putExtra("CustomerPrice_list", customer.Price_list);
                        i.putExtra("firstOpen", true);
                        i.putExtra("CREDIT_LIMIT", ConvertReformateNumber(customer.CREDIT_LIMIT));
                        i.putExtra("BALANCE", ConvertReformateNumber(customer.BALANCE));
                        i.putExtra("OUTSTANDING", ConvertReformateNumber(customer.OUTSTANDING));
                        i.putExtra("RISKY_CHECKS", ConvertReformateNumber(customer.RISKY_CHECKS));

                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(i);
                    }
                }
                else
                new DialogHint().Customer_Visit_Dialog(v,context,activity, User.ServerConfigID,customer.Price_list,customer.Name,customer.Number,customer.DueDateFrom,User.Username,customer.CREDIT_LIMIT,customer.BALANCE,customer.OUTSTANDING,customer.RISKY_CHECKS);
            }
        });
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

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    class CustomerHolder extends RecyclerView.ViewHolder {
        TextView customerNumber, customerName, customer_PRICE_LIST,customer_DueDateFrom;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            customerNumber = (TextView) itemView.findViewById(R.id.TxtCustomerNumber);
            customerName = (TextView) itemView.findViewById(R.id.TxtCustomerName);
            customer_PRICE_LIST = (TextView) itemView.findViewById(R.id.TxtCustomerPRICE_LIST);
            customer_DueDateFrom = (TextView) itemView.findViewById(R.id.TxtCustomerDueDateFrom);
        }
    }

    public void updateList(List<Customer> newlist) {
        customerList = new ArrayList<>();
        customerList.addAll(newlist);
        notifyDataSetChanged();
    }

}
