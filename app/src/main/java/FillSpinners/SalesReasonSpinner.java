package FillSpinners;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Spinner;

import java.util.List;

import API.API_Online;


public class SalesReasonSpinner {
    List<String> reasonList;
    Context context;
    String result;
    public SalesReasonSpinner(Context context) {
        this.context = context;
    }

    public boolean GetReason(final Spinner spinner, final Context context, final Activity activity, final String Language){
        return  new API_Online().SFA_Visit_WithoutSalesReasons(spinner,context,activity,Language);
    }

}
