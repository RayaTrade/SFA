package FillSpinners;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Model.User;


public class DeliveryMethodsSpinner {
    List<String> DeliveryMethodList;
    Context context;
    String result;
    public DeliveryMethodsSpinner(Context context) {
        this.context = context;
    }

    public boolean GetDeliveryMethods(final Spinner spinner, final Context context, final Activity activity) {
      return new API_Online().SFA_GetDeliveryMethods(spinner,context,activity);
    }
}
