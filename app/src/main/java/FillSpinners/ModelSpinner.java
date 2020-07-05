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
import Model.User;

public class ModelSpinner {
    String urlModel;
    List<String> modelList;
    Context context;
    String result;

    public ModelSpinner(Context context) {
        this.context = context;
    }

    public void GetModelsOnline(final Spinner spinner, final Context context, String Category, String Brand, boolean OfflineMode) {
        try {
            if (!OfflineMode) {
                new API_Online().SFA_Model(spinner, context, Category, Brand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
