package FillSpinners;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.List;

import API.API_Online;


public class InventorySpinner {
    List<String> categoryList;
    Context context;
    String result;
    public InventorySpinner(Context context) {
        this.context = context;
    }

    public void GetInventory(final Spinner spinner, final Context context, boolean OfflineMode){
        try {
            if(!OfflineMode){
                new API_Online().SFA_GetInventoryTypes(spinner,context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
