package Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ahmed_hasanein.sfa.R;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import API.API_Sync_Back;
import Model.Parsing_Json.SFA_GetTruckType;
import Model.TruckType;

public class DiliveryMethodAdapter extends ArrayAdapter<TruckType> {
    boolean offlineMode;
    public DiliveryMethodAdapter(Context context, List<TruckType> TruckType, boolean offlineMode) {
        super(context, R.layout.raw_truks, TruckType);
        this.offlineMode= offlineMode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TruckType truck = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.raw_truks, parent, false);
        }
        // Lookup view for data population
        TextView TrDesc = (TextView) convertView.findViewById(R.id.TruckDesc);
        EditText TrNumber= (EditText) convertView.findViewById(R.id.EditMain_Qty);
        TrNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!offlineMode)
                new API_Online().Trucks.getTruckType().get(position).setCount(Integer.parseInt(s.toString()));
                else
                new API_Sync_Back().Trucks.getTruckType().get(position).setCount(Integer.parseInt(s.toString()));

            }
        });
        // Populate the data into the template view using the data object
        TrDesc.setText(truck.getType());
        // Return the completed view to render on screen
        return convertView;
    }
}
