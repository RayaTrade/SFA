package FillSpinners;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Model.User;


public class CategorySpinner {
    List<String> categoryList;
    Context context;
    String result;
    public CategorySpinner(Context context) {
        this.context = context;
    }

    public void GetCategory(final LinearLayout LayoutWaitingMainList, final Spinner spinner, final Context context, boolean OfflineMode){
        try {
            if(!OfflineMode){
                new API_Online().SFA_Category(LayoutWaitingMainList,spinner,context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
