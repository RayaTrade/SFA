package Utility;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class LogCat {

    private static String UserLogFile;

    public static String getUserLogFile() {
        return UserLogFile;
    }

    public static void setUserLogFile(String userLogFile) {
        UserLogFile = userLogFile;
    }

    public  String  CreateFile(Activity activity,String File_name)
    {
        File file = new File(activity.getFilesDir(), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, File_name);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("testtttt");
            writer.flush();
            writer.close();
            Toast.makeText(activity, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }
        return file.getName();
        /*

        try {
            File root = new File(activity.getFilesDir(), "AppLogs");
            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, File_name+".txt");
            setUserLogFile(File_name);

            try {
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("testtttt");
                writer.flush();
                writer.close();
            } catch (Exception e) { }

            return  gpxfile.getName();
        }catch (Exception e)
        {
            return  "";
        }*/

    }

public  void WriteLog(String Body)
{
    File root = new File(Environment.getExternalStorageDirectory(), "AppLogs");
    if (!root.exists()) {
        root.mkdirs();
    }
    try {
        File gpxfile = new File(root, getUserLogFile());
        FileWriter writer = new FileWriter(gpxfile);
        writer.append(Body);
        writer.flush();
        writer.close();
    } catch (Exception e) { }

}
}
