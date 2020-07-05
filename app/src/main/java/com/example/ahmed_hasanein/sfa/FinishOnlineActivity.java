package com.example.ahmed_hasanein.sfa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import API.API_Online;
import Adapter.SerialAdapter;
import Model.Header;
import Model.Items;
import Model.OrderReceipt;
import Model.Product;
import Model.Serial;
import Model.Transaction_Items;
import Model.User;
import PrinterHandler.PrinterHandler_Main;
import preview_database.DB.SerialDB.SerialDBHelper;
import preview_database.DB.SyncDB.SyncDBHelper;

import static com.example.ahmed_hasanein.sfa.MainActivity.SelectedCustomerNumber;

public class FinishOnlineActivity extends AppCompatActivity {
    TextView OrderIDlbl,totalOrderlbl,PromotionDesc;
    Button btnDone;
    Bundle extras;
    SerialAdapter  mAdapter ;
    List<Serial> serialList;
    RecyclerView mRecyclerView ;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_online);
        OrderIDlbl = (TextView) findViewById(R.id.OrderIDlbl);
        totalOrderlbl = (TextView) findViewById(R.id.totalOrderlbl);
        PromotionDesc = (TextView) findViewById(R.id.PromotionDesc);
        btnDone = (Button) findViewById(R.id.btnDone);
        extras = getIntent().getExtras();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final OrderReceipt orderReceipt = new OrderReceipt().getOrderReceipt();

        Button Printing = findViewById(R.id.print_online);
        Printing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             try {
                 new PrinterHandler_Main(FinishOnlineActivity.this).scan(orderReceipt);
             }catch (Exception ex)
             {
                 Toast.makeText(getBaseContext(),"Printer Not Available",Toast.LENGTH_SHORT).show();
                 Log.v("EX-67",ex.getMessage());
             }
            }
        });



        OrderIDlbl.setText(orderReceipt.getOrderId());
        totalOrderlbl.setText(orderReceipt.getTotalAmount());

        if(!orderReceipt.getPromotionDesc().equals(""))
            PromotionDesc.setText(orderReceipt.getPromotionDesc());
        else
            PromotionDesc.setText("No Promotion for this order");

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



     if(orderReceipt.isOrder() && orderReceipt.getSerials().size() > 0)
        {

            LayoutInflater li = LayoutInflater.from(this);
            final View promptsView = li.inflate(R.layout.select_serials_layout, null);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);


            alertDialogBuilder.setView(promptsView);

            final AlertDialog alertDialog = alertDialogBuilder.create();

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.enterserial);


            final TextView SerialHint = promptsView.findViewById(R.id.SerialHint);
            String Hint="";
            for(int i=0;i< orderReceipt.getItems().size();i++)
            {
                Hint+="Item Code: "+orderReceipt.getItems().get(i).getSKU()+" --> QTY: "+ orderReceipt.getItems().get(i).getQTY()+"\n";
                count+= Integer.parseInt(orderReceipt.getItems().get(i).getQTY());
            }

            //serialList= new SyncDBHelper(this).getserial(items);
            serialList= orderReceipt.getSerials();
            SerialHint.setText(orderReceipt.getPromotionDesc());

            mAdapter =  new SerialAdapter(SelectedCustomerNumber,serialList, getBaseContext(), FinishOnlineActivity.this);
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
                        List<Product>  product = new ArrayList<>();
                        for ( Transaction_Items item: orderReceipt.getItems())
                        {
                            String serials ="";
                            for(int i=0;i<serialList.size();i++) {
                                if(serialList.get(i).getItemCode().equals(item.getQTY()) && serialList.get(i).isSelected)
                                {  if(serials.equals(""))
                                    serials =serialList.get(i).getSerialNumber();
                                    else
                                    serials+=";"+serialList.get(i).getSerialNumber();
                                }
                            }
                            new SerialDBHelper(getBaseContext()).update_HeaderId_Offline(orderReceipt.getOrderId());
                            new SyncDBHelper(getBaseContext()).InsertTransactionSerialsOffline(orderReceipt.getOrderId(), "0", serials, item.getSKU());
                            Product p = new Product();
                            p.setSKU(item.getSKU());
                            p.setQTY(item.getQTY());

                            product.add(p);
                        }
                        try {
                        alertDialog.dismiss();

                            ProgressDialog dialog = new ProgressDialog(FinishOnlineActivity.this);
                            dialog.setMessage("Saving serials, please wait.");
                            dialog.show();

                        new API_Online().SFA_PromotionOrderSerial(getApplicationContext(),dialog, orderReceipt.getOrderId(),product);

                        }catch (Exception e)
                        {
                            Log.v("Exception",e.getMessage());
                        }
                    }
                    else
                    Toast.makeText(getBaseContext(),"select "+finalCount+" serials",Toast.LENGTH_SHORT).show();

                }
            });

            final Button clear = promptsView.findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput.getText().clear();

                mAdapter =  new SerialAdapter(SelectedCustomerNumber,serialList, getBaseContext(), FinishOnlineActivity.this);
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


            }
        });

        // show it
        alertDialog.show();
        alertDialog.setCancelable(false);
    }



    }
}
