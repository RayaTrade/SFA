package Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ahmed_hasanein.sfa.CollectSerialActivity;
import com.example.ahmed_hasanein.sfa.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Model.Product;
import Model.Serial;
import preview_database.DB.ProductOrderDB.OrderDBHelper;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

public class SerialAdapter extends RecyclerView.Adapter<SerialAdapter.ViewHolder> implements Filterable {

    private List<Serial> stList;
    private List<String> serials;
    private Context context;
    private Serial mserial;
    List<Product> productListDB;
    boolean itemExist;
    private SerialDBHelper mydb;
    private SyncDBHelper syncDBHelper;
    private OrderDBHelper db_order;
    private Activity activity;
    private int newCollectedCount=0;
    private String serialCustomerNumber = "";
    public SerialAdapter(){}
    public SerialAdapter(String serialCustomerNumber,List<Serial> serials, Context context, Activity activity) {
        this.stList = serials;
        this.context = context;
        this.activity = activity;
        this.serialCustomerNumber=serialCustomerNumber;
        mydb = new SerialDBHelper(context);
        db_order = new OrderDBHelper(context);
        try {
            this.newCollectedCount = mydb.CountOfSerialForItemCoed("0",serials.get(0).ItemCode);
        }catch (Exception e)
        {
            Log.v("Exception",e.getMessage());
            this.newCollectedCount = 0;
        }

    }
    ViewHolder viewHolder;
    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_serial, parent,false);
        viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {

        final int pos = position;

        viewHolder.BoxNumber.setText(stList.get(position).getBox_number());
        viewHolder.serialNumber.setText(stList.get(position).getSerialNumber());
        viewHolder.chkSelected.setChecked(stList.get(position).isSelected());
        viewHolder.chkSelected.setTag(stList.get(position));

        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                syncDBHelper = new SyncDBHelper(context);
                final Serial contact = (Serial) cb.getTag();
                contact.setSelected(cb.isChecked());
                stList.get(pos).setSelected(cb.isChecked());

//      Toast.makeText(v.getContext(),"Clicked on Checkbox: " + contact.SerialNumber + " is "+ cb.isChecked(), Toast.LENGTH_LONG).show();
                 if(cb.isChecked()){

                     mserial = new Serial(contact.ItemCode,contact.Model,contact.SerialNumber,contact.Box_number,false);

                     if(newCollectedCount == 0)
                     newCollectedCount= Integer.valueOf(GetLastCountCollectedBySerialNumber(contact.SerialNumber));

                     newCollectedCount += 1;
                     //insert serial for the first time only

                         boolean inserted = false;
                         inserted = mydb.insertSerials("0",contact.SerialNumber, contact.ItemCode, contact.Model);
                         if(inserted) {
                            //Toast.makeText(context, "done "+newCollectedCount,Toast.LENGTH_SHORT).show();
                             mydb.close();
                         }


                     //Update Collected Serial from order table
                     db_order.updateCollectedSerial(contact.ItemCode,String.valueOf(newCollectedCount));
                    //Update IsSelected Flag in DB
                     syncDBHelper.Update_StockSerialsStatusOffline(contact.SerialNumber,"true");

                     try {
                        ((CollectSerialActivity) activity).RefreshTable();
                     }catch (Exception ex)
                     {Log.v("Exc",ex.getMessage());}



                 }

                 else if(!cb.isChecked()) {
                    // int newCollectedCount = Integer.valueOf(GetLastCountCollectedBySerialNumber(contact.SerialNumber));

                     if (newCollectedCount != 0)
                         newCollectedCount -= 1;

                     //Update Collected Serial from order table
                     db_order.updateCollectedSerial(contact.ItemCode, String.valueOf(newCollectedCount));
                     //Update IsSelected Flag in DB
                     syncDBHelper.Update_StockSerialsStatusOffline(contact.SerialNumber, "false");

                     try {
                         ((CollectSerialActivity) activity).RefreshTable();
                     }catch (Exception ex)
                     {Log.v("Exc",ex.getMessage());}
                     //Delete serial if count now is 0

                     {  int deleted = 0;
                         deleted = mydb.deleteSerial("0",contact.SerialNumber);

                         if (deleted > 0) {
                            //Toast.makeText(context, "delete "+newCollectedCount,Toast.LENGTH_SHORT).show();
                             mydb.close();
                         }

                     }
                 }

            }
        });
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return stList.size();
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                stList = (List<Serial>) results.values;
                notifyDataSetChanged();
                try {
                    ((CollectSerialActivity) activity).RefreshTable();
                }
                catch (Exception ex)
                {Log.v("EX",ex.getMessage());
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                List<Serial> FilteredArrayNames = new ArrayList<>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString();
                if(!constraint.equals("")) {
                    for (int i = 0; i < stList.size(); i++) {
                        String dataserial = stList.get(i).getSerialNumber();
                        if (dataserial.equals(constraint.toString())) {
                            if(!stList.get(i).isSelected()) {
                                stList.get(i).setSelected(true);
                                setSerialSelected_Auto(stList.get(i));
                            }
                           // FilteredArrayNames.add(stList.get(i));

                        }
                    }

                    if (FilteredArrayNames.size() == 0) {
                        for (int i = 0; i < stList.size(); i++) {
                            String dataserial = stList.get(i).getBox_number();
                            if (dataserial.equals(constraint.toString())) {

                                if(!stList.get(i).isSelected()) {
                                    stList.get(i).setSelected(true);
                                    setSerialSelected_Auto(stList.get(i));
                                }
                               // FilteredArrayNames.add(stList.get(i));


                            }
                        }
                    }

/*
                    if(FilteredArrayNames.size() == )
                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;*/
                }



                results.count = stList.size();
                results.values = stList;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView serialNumber,BoxNumber;
        public CheckBox chkSelected;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
           BoxNumber = (TextView) itemLayoutView.findViewById(R.id.TxtBoxNumberTest);
            serialNumber = (TextView) itemLayoutView.findViewById(R.id.TxtSerialNumberTest);
            chkSelected = (CheckBox) itemLayoutView.findViewById(R.id.chkSerialSelected);
        }
    }

    //return C_serial for this item.
    public String GetLastCountCollectedBySerialNumber(String SerialNumber){
        Cursor cursor = mydb.getDatabySerialNumber("0",SerialNumber);
        String ItemCode="";
        if (cursor.moveToFirst()){
            do{
                ItemCode = cursor.getString(cursor.getColumnIndex("ItemCode"));
                // do what ever you want here
            }while(cursor.moveToNext());
        }
        cursor.close();

        Cursor cursor1 = db_order.getDatabySKU(ItemCode);
        String CollectedCount="";
        if (cursor1.moveToFirst()){
            do{
                CollectedCount = cursor1.getString(cursor1.getColumnIndex("C_Serial"));
                // do what ever you want here
            }while(cursor1.moveToNext());
        }
        cursor1.close();
        return (CollectedCount.equals(""))?"0":CollectedCount;
    }


    // method to access in activity after updating selection
    public List<Serial> getSeriallist() {
        return stList;
    }

    public void updateList(List<Serial> newlist){
        stList =new ArrayList<>();
        stList.addAll(newlist);

        try {
            this.newCollectedCount = mydb.CountOfSerialForItemCoed("0",newlist.get(0).ItemCode);
        }catch (Exception e)
        {
            Log.v("Exception",e.getMessage());
            this.newCollectedCount = 0;
        }
        notifyDataSetChanged();
    }

    public void setSerialSelected_Auto(Serial contact)
    {

        syncDBHelper = new SyncDBHelper(context);



            mserial = new Serial(contact.ItemCode,contact.Model,contact.SerialNumber,contact.Box_number,false);

            if(newCollectedCount == 0)
                newCollectedCount= Integer.valueOf(GetLastCountCollectedBySerialNumber(contact.SerialNumber));

            newCollectedCount += 1;
            //insert serial for the first time only

            boolean inserted = false;
            inserted = mydb.insertSerials("0",contact.SerialNumber, contact.ItemCode, contact.Model);
            if(inserted) {
                //Toast.makeText(context, "done "+newCollectedCount,Toast.LENGTH_SHORT).show();
                mydb.close();
            }


            //Update Collected Serial from order table
            db_order.updateCollectedSerial(contact.ItemCode,String.valueOf(newCollectedCount));
            //Update IsSelected Flag in DB
            syncDBHelper.Update_StockSerialsStatusOffline(contact.SerialNumber,"true");



    }

}
