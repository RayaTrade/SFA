package Model.Parsing_Json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import Model.ProductDimension;

public class SFA_GetItemDimensions_All {


    public ArrayList<ProductDimension> getProductDimensions() {
        return productDimensions;
    }

    public void setProductDimensions(ArrayList<ProductDimension> productDimensions) {
        this.productDimensions = productDimensions;
    }

    @SerializedName("SFA_GetItemDimensions_AllResult")
    @Expose
    private ArrayList<ProductDimension> productDimensions = new ArrayList<ProductDimension>();
}
