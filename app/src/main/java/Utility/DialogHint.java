package Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed_hasanein.sfa.CollectSerialActivity;
import com.example.ahmed_hasanein.sfa.CustomerActivity;
import com.example.ahmed_hasanein.sfa.DashboardActivity;
import com.example.ahmed_hasanein.sfa.HistoryActivity;
import com.example.ahmed_hasanein.sfa.LoginActivity;
import com.example.ahmed_hasanein.sfa.MainActivity;
import com.example.ahmed_hasanein.sfa.R;
import com.example.ahmed_hasanein.sfa.SplashActivity;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import API.API_Online;
import FillSpinners.SalesReasonSpinner;
import Model.Reason;
import Model.User;
import preview_database.DB.SyncDB.SyncDBHelper;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.ahmed_hasanein.sfa.LoginActivity.OfflineMode;

public class DialogHint  {
    public static String Long, Lat;
    private SyncDBHelper db_sync;
    public static String VisitDate;


    public void Offline_Dialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_alert_offline_mode);
        builder.setMessage(R.string.msg_alert_offline_mode);
        String positiveText = context.getString(R.string.btn_label_ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void Sync_Dialog(final Context context, final Activity activity, final String LastLoginDatepref, final String DeviceID, final boolean preOrderPermission, final boolean OrderPermission, final boolean historyPermission, final String email, final boolean allow_StockTaking) {
       final Dialog dialog =   new Dialog((activity));
        dialog.setContentView(R.layout.warning_dialog);

        TextView warning_dialog_title = (TextView) dialog.findViewById(R.id.warning_dialog_title);
        TextView warning_question = (TextView) dialog.findViewById(R.id.warning_question);
        TextView warning_hint = (TextView) dialog.findViewById(R.id.warning_hint);
        warning_question.setText("Do you want to sync now or later?" );
        warning_hint.setText("Please note that if you choose later you will work online only and can't complete any actions when you go offline.");
        warning_dialog_title.setText("Sync");

        Button warning_SyncNow_button = dialog.findViewById(R.id.warning_SyncNow_button);
        Button warning_SyncLater_button = dialog.findViewById(R.id.warning_SyncLater_button);
        warning_SyncNow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                ((LoginActivity) activity).startSync();
            }
        });


        warning_SyncLater_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.putExtra("lastsyncdate", LastLoginDatepref);
                intent.putExtra("DeviceID", DeviceID);
                intent.putExtra("preOrderPermission", preOrderPermission);
                intent.putExtra("OrderPermission", OrderPermission);
                intent.putExtra("historyPermission", historyPermission);
                intent.putExtra("StockTakenServerPermission", (allow_StockTaking));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                User.Username = email;
                context.startActivity(intent);
                activity.finish();
            }
        });





        dialog.show();
        dialog.setCanceledOnTouchOutside(false);


    }


    public void Customer_Visit_Dialog(final View v, final Context context, final Activity activity, final String CountryCode, final String CustomerPrice_list, final String CustomerName, final String CustomerNumber, final String DueDateFrom, final String Submitter, final String CREDIT_LIMIT, final String BALANCE, final String OUTSTANDING, final String RISKY_CHECKS) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
     //   builderSingle.setMessage("Please select visit type");

        builderSingle.setTitle("Please select visit type");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1);
        arrayAdapter.add("With Sales");
        arrayAdapter.add("Without Sales");
        arrayAdapter.add("Complaint");


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

              if(strName.equals("With Sales"))
              {
                  dialog.dismiss();
                  VisitDate = DueDateFrom;
                  if(Submitter == null || User.Username == null)
                  {
                      new DialogHint().Session_End(activity,context);
                  }
                  else {
                      Intent i = new Intent(activity, MainActivity.class);
                      i.putExtra("customerName", CustomerName);
                      i.putExtra("customerNumber", CustomerNumber);
                      i.putExtra("customerDueDateFrom", DueDateFrom);
                      i.putExtra("CustomerPrice_list", CustomerPrice_list);
                      i.putExtra("CREDIT_LIMIT", ConvertReformateNumber(CREDIT_LIMIT));
                      i.putExtra("BALANCE", ConvertReformateNumber(BALANCE));
                      i.putExtra("OUTSTANDING", ConvertReformateNumber(OUTSTANDING));
                      i.putExtra("RISKY_CHECKS", ConvertReformateNumber(RISKY_CHECKS));
                      i.putExtra("firstOpen", true);
                      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                      activity.startActivity(i);
                  }

              }else if(strName.equals("Without Sales"))
              {
                  dialog.dismiss();
                  if(Submitter == null || User.Username == null)
                  {
                      new DialogHint().Session_End(activity,context);
                  }
                  else {
                      new CustomerActivity().SalesReasonPopup(context, activity, CountryCode, CustomerName, CustomerNumber, Submitter, Long, Lat, "By Raya", "3", "Online");
                  }
              }else if(strName.equals("Complaint"))
              {
                  dialog.dismiss();

                  final Dialog Complaintdialog = new Dialog(activity);

                  Complaintdialog.setContentView(R.layout.complaint_layout);
                  Complaintdialog.setTitle("Complaint");

                  // set the custom dialog components - text, image and button
                  TextView Com_Customenumber = (TextView) Complaintdialog.findViewById(R.id.Com_Customenumber);
                  Com_Customenumber.setText("Customer Number: "+CustomerNumber);

                  TextView Com_Customename = (TextView) Complaintdialog.findViewById(R.id.Com_Customename);
                  Com_Customename.setText("Customer Name: "+CustomerName);

                  final EditText Com_Text = (EditText) Complaintdialog.findViewById(R.id.Com_text);

                  Button SubmitButton = (Button) Complaintdialog.findViewById(R.id.Com_Submit);
                  ImageButton CancelButton =  Complaintdialog.findViewById(R.id.Com_exit);
                  // if button is clicked, close the custom dialog
                  SubmitButton.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ProgressDialog   dialog = new ProgressDialog(activity);
                          dialog.setMessage("Submit Complaint");
                            dialog.show();
                          try {
                             JSONObject object = new JSONObject();
                             object.accumulate("CustomerNumber", CustomerNumber);
                             object.accumulate("CustomerName", CustomerName);
                             object.accumulate("Submitter", Submitter);
                             object.accumulate("Body", Com_Text.getText().toString());
                             new API_Online().SubmitComplaint(object.toString(),dialog,activity);
                         }catch (Exception e){

                         }
                          Complaintdialog.dismiss();
                      }
                  });
                  CancelButton.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Complaintdialog.dismiss();
                          }
                      });


                  Complaintdialog.show();

              }


            }
        });

        final AlertDialog dialog = builderSingle.create();

        dialog.show();
      /*  final AlertDialog.Builder builder = new AlertDialog.Builder( v.getContext());
      //  final AlertDialog.Builder ReasonDialog = new AlertDialog.Builder((Activity) v.getContext());
        builder.setMessage("Please select visit type");

        builder.setTitle("Visit");


        builder.setPositiveButton("With Sales",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        VisitDate = DueDateFrom;
                        if(Submitter == null || User.Username == null)
                        {
                            new DialogHint().Session_End(activity,context);
                        }
                        else {
                            Intent i = new Intent(activity, MainActivity.class);
                            i.putExtra("customerName", CustomerName);
                            i.putExtra("customerNumber", CustomerNumber);
                            i.putExtra("customerDueDateFrom", DueDateFrom);
                            i.putExtra("CustomerPrice_list", CustomerPrice_list);
                            i.putExtra("firstOpen", true);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(i);
                        }
                    }
                });

        builder.setNegativeButton("Without Sales",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(Submitter == null || User.Username == null)
                        {
                            new DialogHint().Session_End(activity,context);
                        }
                        else {
                            new CustomerActivity().SalesReasonPopup(context, activity, CountryCode, CustomerName, CustomerNumber, Submitter, Long, Lat, "By Raya", "3", "Online");
                        }
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.show();*/
 //       Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
 //       Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
 //       positiveButton.setTextSize(13);
  //      negativeButton.setTextSize(13);
    }

    private String ConvertReformateNumber(String number){
        if(number.equals(""))
        {
            return "0.00";
        }
        else {
            Float litersOfPetrol = Float.parseFloat(number);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setGroupingUsed(true);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(2);
            number = df.format(litersOfPetrol);
            return number;
        }
    }
    public boolean GPS_Dialog(final Activity activity) {
        //check location turn on
        LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.title_alert_no_gps);
            builder.setMessage(R.string.msg_alert_no_gps);
            String positiveText = activity.getString(R.string.btn_label_ok);
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(final Activity activity) {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                activity.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            double longitude = location.getLatitude();
            double latitude = location.getLongitude();
            Lat = String.valueOf(latitude);
            Long = String.valueOf(longitude);
        } catch (NullPointerException e) {

        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation(final Activity activity) {
        LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location == null) {
                getLastLocation(activity); // get last known location

            } else {
                try {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Lat = String.valueOf(latitude);
                    Long = String.valueOf(longitude);

                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(), "please check your GPS before save", Toast.LENGTH_LONG).show();
        }
    }

    public void showNetworkDialog(Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Internet");
        alertDialog.setMessage("Please check Internet Connection");
        alertDialog.setIcon(activity.getResources().getDrawable(R.drawable.warning));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showAtlestFirstLoginDialog(Activity activity, String Message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Login");
        alertDialog.setMessage(Message);
        alertDialog.setIcon(activity.getResources().getDrawable(R.drawable.warning));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showSyncBackDialog(final Activity activity) {
        AlertDialog.Builder DialogTransaction = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Sync");
        alertDialog.setMessage("Please Sync Back your Transaction");
        alertDialog.setCancelable(false);
        alertDialog.setIcon(activity.getResources().getDrawable(R.drawable.warning));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Review Transactions", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(activity, HistoryActivity.class);
                intent.putExtra("OpenFromSyncBackDialog", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void showSerialNotColletedAlert(Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Warning !");
        alertDialog.setMessage("Please check collected Item serial's equal quantity");
        alertDialog.setIcon(activity.getResources().getDrawable(R.drawable.warning));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void allertdialog(Activity activity,String header ,String body,boolean warning)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(header);
        alertDialog.setMessage(body);

        if(warning)
        alertDialog.setIcon(activity.getResources().getDrawable(R.drawable.warning));

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void Session_End(final Activity activity, final Context context)
    {


        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setIcon(R.drawable.warning);
        alertDialog.setTitle("User Session End");
        alertDialog.setMessage("Your session of using app finished please try to use app again."+"\n"
                +"*Note: Your transactions in app saved.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                        Intent intent = new Intent(activity, SplashActivity.class);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());

                    }
                });
        alertDialog.show();
        alertDialog.setCancelable(false);

    }



    public void UploadAttachment(final Activity activity, final Context context, final String preOrderHistory_ID, final String preOrderHistory_customerNumber, final String UserName, final Intent data)
    {

        try {


            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_submit_image);
            TextView preOrderHistory_ID_view = dialog.findViewById(R.id.preOrderHistory_ID);
            TextView preOrderHistory_customerNumber_view = dialog.findViewById(R.id.preOrderHistory_customerNumber);
            final ImageView image = dialog.findViewById(R.id.U_imageView);
            Button Upload_Image = dialog.findViewById(R.id.Upload_Image);


            ImageProcessClass imageProcessClass = new ImageProcessClass();
            Uri uri = data.getData();
            final String picturePath = imageProcessClass.getPath(context, uri);
            final File imgFile = new  File(picturePath);
            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                image.setImageBitmap(myBitmap);

            };

            preOrderHistory_ID_view.setText("Order ID: "+ preOrderHistory_ID);
            preOrderHistory_customerNumber_view.setText("Customer Number: "+ preOrderHistory_customerNumber);

            Upload_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMddHHmmss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    String imagename=preOrderHistory_ID+currentDateandTime;
                    new ImageProcessClass.AsyncCallWS_upload(imgFile,imagename,activity,preOrderHistory_ID,UserName).execute();
                    dialog.dismiss();

                }
            });

            dialog.show();

        }
        catch (Exception e)
        {}



    }





}
