package Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import API.API_Online;
import Model.User;

public class ImageProcessClass {
    public static void Media_access_Permisionn(Activity activity, Context context)
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        7);

            }
        }
    }
   public static class AsyncCallWS_upload extends AsyncTask<Void,Void,Void> {
        File file;
        String imageName,OrderId,SubmitBy;
        Activity activity;
        String newFile_Name = null;
        public AsyncCallWS_upload(File file, String imageName, Activity activity,String OrderId,String SubmitBy)
        {
            this.file = file;
            this.imageName=imageName;
            this.activity = activity;
            this.OrderId = OrderId;
            this.SubmitBy = SubmitBy;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            newFile_Name = uploadFile(file,imageName,activity,OrderId,SubmitBy);
            return null;
        }

       @Override
       protected void onPostExecute(Void aVoid) {
          if(newFile_Name != null)
           new API_Online().SFA_UploadPicture(activity,OrderId,newFile_Name,SubmitBy);
          else
              Toast.makeText(activity.getBaseContext(),"Can`t reach to image ",Toast.LENGTH_SHORT).show();
       }


   }
    public static String  uploadFile(File file, String imageName, final Activity activity, final String OrderId, final String SubmitBy){


        final String FTP_HOST= "41.78.22.20";
        int port = 21;
        String FTP_USER="RayaFTP";
        String FTP_PASS="Raya@FTP";


        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.getDefault());
        String currentyear = year.format(new Date());

        SimpleDateFormat month = new SimpleDateFormat("MM", Locale.getDefault());
        String currentmonth = month.format(new Date());

        SimpleDateFormat day = new SimpleDateFormat("dd", Locale.getDefault());
        String currentday = day.format(new Date());




        try {


            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(FTP_HOST,port);
            ftpClient.login(FTP_USER, FTP_PASS);
            // remove all ip path till SFA Directory (ftp://41.78.22.20/SFA/SFA_NG/ to  SFA/SFA_NG/ )
            String Pathname = User.FTP_Path;
            Pathname = Pathname.substring(Pathname.indexOf("S"));
            Pathname.trim();
            ftpClient.changeWorkingDirectory(Pathname);



            boolean B_year=ftpClient.changeWorkingDirectory(currentyear);
            if(B_year)
                ftpClient.changeWorkingDirectory(currentyear);

            boolean B_month=ftpClient.changeWorkingDirectory(currentmonth);
            if(B_month)
             ftpClient.changeWorkingDirectory(currentmonth);

            boolean B_day=ftpClient.changeWorkingDirectory(currentday);
            if(B_day)
                ftpClient.changeWorkingDirectory(currentday);

            if(!B_year || !B_month || !B_day)
            {   if(!B_year)
                ftpClient.makeDirectory(currentyear);

                ftpClient.changeWorkingDirectory(currentyear);

                if(!B_month)
                ftpClient.makeDirectory(currentmonth);

                ftpClient.changeWorkingDirectory(currentmonth);

                if(!B_day)
                    ftpClient.makeDirectory(currentday);

                ftpClient.changeWorkingDirectory(currentday);
            }



            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
            BufferedInputStream buffIn = null;

            buffIn = new BufferedInputStream(new FileInputStream(file));

            ftpClient.enterLocalPassiveMode();
            final String newFile_Name = imageName+extension;
            ftpClient.storeFile(newFile_Name, buffIn);
            buffIn.close();
            ftpClient.logout();
            ftpClient.disconnect();

           return currentyear+"/"+currentmonth+"/"+currentday+"/"+newFile_Name;

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

    }
    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

}
