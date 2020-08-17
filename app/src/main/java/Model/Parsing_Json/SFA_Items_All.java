package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import Model.Product;
import Model.Subinventory;

public class SFA_Items_All {

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @SerializedName("SFA_Items_All_TestResult")
    @Expose

    private List<Product> products = new ArrayList<Product>();

    public List<Product> getOraproducts() {
        return Oraproducts;
    }

    public void setOraproducts(List<Product> oraproducts) {
        Oraproducts = oraproducts;
    }

    @SerializedName("SFA_Ora_StockItems_AllResult")
    @Expose
    private List<Product> Oraproducts = new ArrayList<Product>();

}
