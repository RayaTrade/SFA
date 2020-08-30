package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import Model.Customer;


public class SFA_Customers {

    public ArrayList<Model.Customer> getCustomer() {
        return Customer;
    }

    public void setCustomer(ArrayList<Model.Customer> customer) {
        Customer = customer;
    }

    @SerializedName("SFA_CustomersResult")
    @Expose
    private ArrayList<Customer> Customer = new ArrayList<Customer>();
}
