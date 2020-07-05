package FillSpinners;

import android.content.Context;
import android.widget.ArrayAdapter;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Model.Brand;
import Model.User;
import preview_database.DB.SyncDB.SyncDBHelper;

public class BrandSpinner {
    String urlBrand;
    Context context;
    String result;

    public BrandSpinner(Context context) {
        this.context = context;
    }

    public void GetBrandsOnline(final Spinner spinner, final Context context,String Category,boolean OfflineMode){
        try {
            if(!OfflineMode) {
                new API_Online().SFA_Brand(spinner, context, Category);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
