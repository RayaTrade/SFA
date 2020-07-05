package preview_database.DB.SerialDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.Serial;

public class SerialDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "serial.db";
    public static final String CONTACTS_TABLE_NAME = "serial";
    public static final String CONTACTS_COLUMN_ItemCode = "ItemCode";
    public static final String CONTACTS_COLUMN_Model = "Model";
    public static final String CONTACTS_COLUMN_SerialNumber = "SerialNumber";

    private HashMap hp;

    public SerialDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table serial " +
                        "(SerialNumber text primary key, ItemCode text,Model text,HeaderID text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS serial");
        onCreate(db);
    }

    //done
    public boolean insertSerials (String HeaderID,String SerialNumber, String ItemCode, String Model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SerialNumber", SerialNumber);
        contentValues.put("ItemCode", ItemCode);
        contentValues.put("Model", Model);
        contentValues.put("HeaderID",HeaderID);

        db.insert("serial", null, contentValues);

        return true;
    }
    //done
    public Cursor getDatabySerialNumber(String HeaderID,String SerialNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from serial where SerialNumber='"+SerialNumber+"'" + " and HeaderID = '"+HeaderID+"'", null );
        return res;
    }

    //done
    public int CountOfSerialForItemCoed(String HeaderID,String ItemCode)
    {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT SerialNumber FROM serial WHERE HeaderID= '"+HeaderID+"' and "+"ItemCode='"+ItemCode+"'";
        cursor= db.rawQuery(sql,null);

        return cursor.getCount();
    }
    //done
    public boolean CheckSerialNumber(String SerialNumber) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql ="SELECT SerialNumber FROM serial WHERE SerialNumber='"+SerialNumber+"'";
        cursor= db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    //done
    public Cursor getDatabyItemCode(String HeaderID,String ItemCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from serial where HeaderID = '"+HeaderID+"' "+" and ItemCode='"+ItemCode+"'", null );
        return res;
    }

    //done
    public Integer deleteSerial (String HeaderID,String SerialNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("serial",
                "SerialNumber = ?  and HeaderID = ? ",
                new String[] { SerialNumber ,HeaderID});
    }

    public Integer deleteItemCode (String ItemCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("serial",
                "ItemCode = ? ",
                new String[] { ItemCode });
    }

    public void update_HeaderId_Offline(String HeaderID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HeaderID", HeaderID);
      int i =  db.update("serial", contentValues, "HeaderID = ? ", new String[] { "0" } );
    }

    //    public ArrayList<String> getAllSerials () {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from serial", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_SerialNumber)));
//            res.moveToNext();
//        }
//        return array_list;
//    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from serial");
        db.close();
    }

    public void deleteAll_HeaderID(String HeaderID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from serial where HeaderID='" +HeaderID+"'");
        db.close();
    }
    public List<Serial> getAllSerials() {
        List<Serial> serials = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM serial";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Serial serial = new Serial();
                    serial.setSerialNumber(cursor.getString(cursor.getColumnIndex("SerialNumber")));
                    serial.setItemCode(cursor.getString(cursor.getColumnIndex("ItemCode")));
                    serial.setModel(cursor.getString(cursor.getColumnIndex("Model")));
                    serial.setHeaderID(cursor.getString(cursor.getColumnIndex("HeaderID")));
                    serials.add(serial);
                } while (cursor.moveToNext());

            }}catch (Exception e){
            e.printStackTrace();
        }

        // close db connection
        db.close();

        // return notes list
        return serials;
    }
    public int numberOfRowsSerials(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateSerial (String SerialNumber, String ItemCode, String Model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SerialNumber", SerialNumber);
        contentValues.put("ItemCode", ItemCode);
        contentValues.put("Model", Model);
        db.update("serial", contentValues, "SerialNumber = ? ", new String[] { SerialNumber } );
        return true;
    }
}
