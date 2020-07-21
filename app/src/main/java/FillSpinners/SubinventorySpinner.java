package FillSpinners;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.List;

import API.API_Online;
import Model.Parsing_Json.SFA_User_X_Subinventory;
import Model.Subinventory;


public class SubinventorySpinner {
    List<String> categoryList;
    Context context;
    String result;
    public SubinventorySpinner(Context context) {
        this.context = context;
    }

    public void GetUser_X_Subinventory(final LinearLayout LayoutWaitingMainList, final Spinner spinner, final Context context, boolean offlineMode,
                                       boolean OpenfromOrderPage,boolean OpenfromDealerOrder){
        try {
            if(!offlineMode){

                if(!OpenfromOrderPage && !OpenfromDealerOrder)
                    new API_Online().SFA_User_X_Subinventory(LayoutWaitingMainList,spinner,context,"1");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String find_SubinventoryID(String Wh)
    {
         SFA_User_X_Subinventory sfa_user_x_subinventories = new API_Online().sfa_user_x_subinventory;
        for ( Subinventory sub : sfa_user_x_subinventories.getSubinventory() ) {
            if(sub.getSubinventory().equals(Wh))
                return sub.getSubinventoryID();
        }
        return  "0";
    }

}
