package com.example.ahmed_hasanein.sfa;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.SerialAdapter;
import Model.Header;
import Model.Items;
import Model.OrderReceipt;
import Model.Serial;
import Model.User;
import PrinterHandler.PrinterHandler_Main;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.LoginActivity.Currencypref;
import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerNumber;

public class FinishOfflineActivity extends AppCompatActivity {
    TextView OfflineOrderIDlbl,OfflinetotalOrderlbl,TextView_Promtion;
    Button OfflinebtnDone;
    Bundle extras;
    SerialAdapter  mAdapter ;
    List<Serial> serialList;
    RecyclerView mRecyclerView ;
    OrderReceipt orderReceipt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_offline);
        OfflineOrderIDlbl = (TextView) findViewById(R.id.OfflineOrderIDlbl);
        OfflinetotalOrderlbl = (TextView) findViewById(R.id.OfflinetotalOrderlbl);
        TextView_Promtion = findViewById(R.id.Promtion);
        OfflinebtnDone = (Button) findViewById(R.id.OfflinebtnDone);
        Button Printing = findViewById(R.id.Print_offline);
        orderReceipt = new OrderReceipt();
        String total="";
        extras = getIntent().getExtras();
        final String HeadrId = extras.getString("header");
        if (extras != null) {
            OfflineOrderIDlbl.setText(extras.getString("header"));
            TextView_Promtion.setText(extras.getString("prom"));
            total= extras.getString("total");
            if(Currencypref!=null){
                OfflinetotalOrderlbl.setText(total+" "+Currencypref);
            }else{
                OfflinetotalOrderlbl.setText(total);
            }
        }
        orderReceipt.setOrderId(extras.getString("header"));
        orderReceipt.setTotalAmount(total);
       orderReceipt = new SyncDBHelper(this).orderReceipt(orderReceipt);
        Printing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PrinterHandler_Main(FinishOfflineActivity.this).scan(orderReceipt);
            }
        });
        String TransactionType = extras.getString("TransactionType");
        final List<Items> Items_need_serila = new SyncDBHelper(getBaseContext()).getAll_Items_need_serila(HeadrId);
        int count =0;
        if(TransactionType.equals("2") && Items_need_serila.size() > 0)
        {
// get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            final View promptsView = li.inflate(R.layout.select_serials_layout, null);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final AlertDialog alertDialog = alertDialogBuilder.create();

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.enterserial);

            final ArrayList<String> serials = new ArrayList<>();
            final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, serials );;

            final TextView SerialHint = promptsView.findViewById(R.id.SerialHint);
            String Hint="";
            String items="";

            for(int i=0;i< Items_need_serila.size();i++)
            {
                Hint+="Item Code: "+Items_need_serila.get(i).getItemCode()+"  QTY:"+ Items_need_serila.get(i).getQty()+"\n";
                count+= Integer.parseInt(Items_need_serila.get(i).getQty());

                if(items.equals(""))
                    items+= "'"+Items_need_serila.get(i).getItemCode()+"'";
                else
                   items+=",'"+ Items_need_serila.get(i).getItemCode()+"'";

            }

            serialList= new SyncDBHelper(this).getserial(items);
            SerialHint.setText(Hint);


            mAdapter =  new SerialAdapter(SelectedCustomerNumber,serialList, getBaseContext(), FinishOfflineActivity.this);
             mRecyclerView = (RecyclerView) promptsView.findViewById(R.id.selected_serial);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(mAdapter);

            final int finalCount = count;

            final Button saveserial = promptsView.findViewById(R.id.saveserial);
            saveserial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedcount=0;
                    for(int i=0;i<serialList.size();i++)
                    {
                        if(serialList.get(i).isSelected)
                            selectedcount++;
                    }
                    if(selectedcount == finalCount)
                    {
                        for ( Items item: Items_need_serila)
                        {
                            String serials ="";
                            for(int i=0;i<serialList.size();i++) {
                                if(serialList.get(i).getItemCode().equals(item.getItemCode()) && serialList.get(i).isSelected)
                                {  if(serials.equals(""))
                                    serials =serialList.get(i).getSerialNumber();
                                    else
                                    serials+=";"+serialList.get(i).getSerialNumber();
                                }
                            }
                            new SerialDBHelper(getBaseContext()).update_HeaderId_Offline(HeadrId);

                            new SyncDBHelper(getBaseContext()).InsertTransactionSerialsOffline(HeadrId, "0", serials, item.getItemCode());

                        }
                        alertDialog.dismiss();
                        Toast.makeText(getBaseContext(),"Done",Toast.LENGTH_SHORT).show();
                    }
                    else
                    Toast.makeText(getBaseContext(),"select "+finalCount+" serials",Toast.LENGTH_SHORT).show();

                }
            });

            final Button clear = promptsView.findViewById(R.id.clear);

            Button ClearList = promptsView.findViewById(R.id.ClearList);
           /* ClearList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });*/

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userInput.getText().clear();

                    mAdapter =  new SerialAdapter(SelectedCustomerNumber,serialList, getBaseContext(), FinishOfflineActivity.this);
                    mRecyclerView = (RecyclerView) promptsView.findViewById(R.id.selected_serial);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.notifyDataSetChanged();

                }
            });



            userInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mAdapter.getFilter().filter(s.toString());

                    /*
                    if(!s.toString().equals(""))
                   {serials.add(s.toString());

                    if(serials.size() == finalCount)
                    saveserial.setEnabled(true);
                    else
                    {
                        saveserial.setEnabled(false);
                    }
                    myAdapter.notifyDataSetChanged();
                   }
                   */
                }
            });
/*
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    // edit text
                                    result.setText(userInput.getText());
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
*/
            // create alert dialog

            // show it
            alertDialog.show();
            alertDialog.setCancelable(false);
        }



        OfflinebtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
