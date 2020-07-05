package PrinterHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.example.ahmed_hasanein.sfa.DeviceListActivity;
import com.example.ahmed_hasanein.sfa.R;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import Model.OrderReceipt;

public class PrinterHandler_Main extends AppCompatActivity {
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private static OutputStream outputStream;


  static   Activity activity;
    public PrinterHandler_Main(Activity activity){
        this.activity = activity;

    }
    public void scan(OrderReceipt orderReceipt){
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(activity, "Device Not Support", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices(orderReceipt);
                       /* Intent connectIntent = new Intent(activity,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);*/

                    }
                }
            }


/*
    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);

                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    run();
                   // Thread mBlutoothConnectThread = new Thread();
                    //mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(activity,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(activity, "Bluetooth activate denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }*/

    private void ListPairedDevices(OrderReceipt orderReceipt) {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
                mBluetoothDevice = mBluetoothAdapter
                        .getRemoteDevice(mDevice.getAddress());
                run(orderReceipt);
                break;
            }
        }
    }

    public void run(OrderReceipt orderReceipt) {
        try {

            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
            //print_Demo();
            print(orderReceipt);
            Log.v(TAG, "PairedDevices: " );
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
//            mBluetoothConnectProgressDialog.cancel();
            Toast.makeText(activity, "Connecting failed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(activity, "DeviceConnected", Toast.LENGTH_LONG).show();
        }
    };

    public void print(final OrderReceipt orderReceipt)
    {
        Thread t = new Thread() {
            public void run() {
                try {

                    OutputStream opstream = null;
                    try {
                        opstream = mBluetoothSocket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    outputStream = opstream;
                    outputStream = mBluetoothSocket.getOutputStream();
                    //byte[] printformat = new byte[]{0x1B,0x21,0x04};
                    //outputStream.write(printformat);


                    Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.print);
                    printPhoto(bitmap);

                    String date = new SimpleDateFormat("dd/mm/yyyy hh:mm").format(new Date());

                    printCustom("Order Number: "+orderReceipt.getOrderId(),1,0);
                    printCustom("Date: "+date+"",1,0);
                    printCustom(new String(new char[48]).replace("\0", "="),1,0);
                    printNewLine();

                    printCustom("Customer Name: "+orderReceipt.getCustomername(),1,0);
                    printCustom("Sales Name: "+orderReceipt.getSales_name().replace("@rayacorp.com",""),1,0);
                    printNewLine();
                    printNewLine();
                    //  printCustom("a b c d e f g h i j k l m n o p q r s t u v w x ",1,0); // 48
                    printCustom("Item                   Qty    Price   Total",1,0);
                    printNewLine();
                    for (int i=0;i<orderReceipt.getProducts().size();i++)
                    {
                        printText(Line(orderReceipt.getProducts().get(i).getSKU(),orderReceipt.getProducts().get(i).getQTY(),orderReceipt.getProducts().get(i).getUnitPrice(),orderReceipt.getProducts().get(i).getTotal()));
                        printNewLine();
                    }
                    printCustom(new String(new char[48]).replace("\0", "="),1,0);
                    printNewLine();
                    printCustom("Total= " +orderReceipt.getTotalAmount(),1,0);
                    printNewLine();
                   // printCustom("Promotion:" +" (promo)\n InLine \n 2 from 111LSMFEG2Z81G1 and 1 from 111INDI42WGV1 and ",1,0);

                    outputStream.flush();
                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    public void print_Demo()
    {
        Thread t = new Thread() {
            public void run() {
                try {

                    OutputStream opstream = null;
                    try {
                        opstream = mBluetoothSocket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    outputStream = opstream;
                    outputStream = mBluetoothSocket.getOutputStream();
                    byte[] printformat = new byte[]{0x1B,0x21,0x05};
                    outputStream.write(printformat);


                    Bitmap bitmap = BitmapFactory.decodeResource( activity.getResources(), R.drawable.print);
                    printPhoto(bitmap);
                    /*String date = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss").format(new Date());
                    printCustom(new String(new char[38]).replace("\0", "."),0,0);
                    printCustom("Order Number: 181818 ",1,0);
                    printCustom(new String(new char[38]).replace("\0", "."),0,0);
                    printCustom("Date: "+date+"",1,0);
                    printCustom(new String(new char[38]).replace("\0", "."),0,0);*/
                    printText("Customer Name: ");printText("Hamdy Saad Mohamed");
                    printNewLine();
                    printText("Sales Name: ");printText("Ahmed Ali Farouk");


                    // 0-20                21-25  27-36    37-
                    printCustom("Item          "+"QTY  "+"Price  "+"Total",1,0);
                    printCustom(new String(new char[38]).replace("\0", "."),0,0);
                    /*printNewLine();
                    printText("111LSMFEG2Z81G1 "+"   1"+"  200"+"       400 ");
                    printNewLine();
                    printText("111INDI42WGV1   "+"   1"+"  23999.85"+"  23999.85 ");
                    printNewLine();
                    printCustom(new String(new char[38]).replace("\0", "."),0,0);
                    printNewLine();
                    printCustom("Total= " +"24,399.85",1,0);
                    printNewLine();
                    printCustom("Promotion:" +" (promo)\n InLine \n 2 from 111LSMFEG2Z81G1 and 1 from 111INDI42WGV1 and ",1,0);
*/
                    outputStream.flush();
                    //printer specific code you can comment ==== > End
                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }


    public String Line(String item , String Qty , String price, String Total ){
        char[] Line = new char[64];
        int index = 0;
        for (int i=0; i <= 29 ;i++ )
        {

            try {
                Line[index]= item.charAt(i);
            }catch (Exception e){
                Line[index]= ' ';
            }
            index++;
        }
        index+=2;
        for (int i=0; i <= 5 ;i++ )
        {
            try {
                Line[index]= Qty.charAt(i);
            }catch (Exception e){
                Line[index]= ' ';
            }
            index++;
        }
        index+=2;

        for (int i=0; i <= 8 ;i++ )
        {
            try {
                Line[index]= price.charAt(i);
            }catch (Exception e){
                Line[index]= ' ';
            }
            index++;
        }
        index+=2;

        for (int i=0; i <= 8 ;i++ )
        {

            try {
                Line[index]= Total.charAt(i);
            }catch (Exception e){
                Line[index]= ' ';
            }
            index++;
        }

        String string = new String(Line).replace("\0", " ");
        return string;
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public void printPhoto(Bitmap bmp) {
        try {

            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

        //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print text
    private void printText(String msg) {
        try {
            byte[] cc = new byte[]{0x1B,0x21,0x03};
            outputStream.write(cc);
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}