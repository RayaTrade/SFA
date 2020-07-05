package preview_database.DB.ProductOrderDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import Model.Product;

public class OrderDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Product_Order.db";
    public static final String CONTACTS_TABLE_NAME = "Orders";
    public static final String COLUMN_ORDER_SKU = "SKU";
    public static final String COLUMN_ORDER_category = "Category";
    public static final String COLUMN_ORDER_image_PATH = "image_path";
    public static final String COLUMN_ORDER_product_brand = "Brand";
    public static final String COLUMN_ORDER_product_model = "Model";
    public static final String COLUMN_ORDER_product_description = "Description";
    public static final String COLUMN_ORDER_product_QTY = "Quantity";
    public static final String COLUMN_ORDER_product_Customer_Number = "Customer_Number";
    public static final String COLUMN_ORDER_product_OnHand = "OnHand";
    public static final String COLUMN_ORDER_product_UnitPrice = "UnitPrice";
    public static final String COLUMN_ORDER_product_Total = "Total";
    public static final String COLUMN_ORDER_product_Collected_Serial = "C_Serial";
    public static final String COLUMN_ORDER_PRODUCT_product_VisitDate = "Visit_Date";

    public OrderDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 4);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table Orders " +
                        "(SKU text primary key, Category text NOT NULL,image_path text NOT NULL,Brand text NOT NULL" +
                        ",Model text NOT NULL,Description text NOT NULL,Quantity text NOT NULL,Customer_Number text NOT NULL" +
                        ",OnHand text NOT NULL,UnitPrice text NOT NULL,Total text NOT NULL,Visit_Date text NOT NULL,C_Serial text NOT NULL DEFAULT '0')"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Orders");
        onCreate(db);
    }
    public boolean insertItemOrder (String SKU, String Category, String image_path, String Brand, String Model
            , String Description, String Quantity, String Customer_Number, String OnHand, Float UnitPrice, String Total,String Visit_Date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SKU", SKU);
        contentValues.put("Category", Category);
        contentValues.put("image_path", image_path);
        contentValues.put("Brand", Brand);
        contentValues.put("Model", Model);
        contentValues.put("Description", Description);
        contentValues.put("Quantity", Quantity);
        contentValues.put("Customer_Number", Customer_Number);
        contentValues.put("OnHand", OnHand);
        contentValues.put("UnitPrice", UnitPrice);
        contentValues.put("Visit_Date", Visit_Date);
        contentValues.put("Total", Total);

        db.insert("Orders", null, contentValues);

        return true;
    }

    public Cursor getDatabySKU(String SKU) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Orders where SKU='"+SKU+"'", null );
        return res;
    }


    public boolean CheckSKUOrder(String SKU) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql ="SELECT SKU FROM Orders WHERE SKU='"+SKU+"'";
        cursor= db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }

    }

    public int numberOfRowsOrder(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateItemOrder (String SKU,String Quantity, String OnHand, Float UnitPrice, String Total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Quantity", Quantity);
        contentValues.put("OnHand", OnHand);
        contentValues.put("UnitPrice", UnitPrice);
        contentValues.put("Total", Total);
        db.update("Orders", contentValues, "SKU = ? ", new String[] { SKU } );
        return true;
    }

    public boolean updateCollectedSerial (String SKU,String C_Serial) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("C_Serial", C_Serial);
        db.update("Orders", contentValues, "SKU = ? ", new String[] { SKU } );

        return true;
    }

    public Integer deleteSKU (String SKU) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Orders",
                "SKU = ? ",
                new String[] { SKU });
    }


    public ArrayList<String> getAllSKU () {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Orders", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(COLUMN_ORDER_SKU)));
            res.moveToNext();
        }
        return array_list;
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Orders");
        db.close();
    }

    public List<Product> getAllOrder() {
        List<Product> orders = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM Orders";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Product order = new Product();
                    order.setSKU(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_SKU)));
                    order.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_category)));
                    order.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_brand)));
                    order.setModel(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_model)));
                    order.setQTY(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_QTY)));
                    order.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_image_PATH)));
                    order.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_description)));
                    order.setCustomer_number(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_Customer_Number)));
                    order.setOnHand(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_OnHand)));
                    order.setUnitPrice(Float.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_UnitPrice))));
                    order.setTotal(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_Total)));
                    order.setVisit_Date(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_PRODUCT_product_VisitDate)));
                    int i = Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_Collected_Serial)));
                    order.setCollectedSerials(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_product_Collected_Serial))));
                    orders.add(order);
                } while (cursor.moveToNext());

            }}catch (Exception e){
            e.printStackTrace();
        }

        // close db connection
        db.close();

        // return notes list
        return orders;
    }

    public String getVisitDatePendingOrder(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from Orders ORDER BY SKU ASC LIMIT 1";
        Cursor cursor =  db.rawQuery( query, null );
        String Visit_Date = "";
        try {
            if (cursor.moveToFirst()) {
                do {
                    Visit_Date = cursor.getString(cursor.getColumnIndex("Visit_Date"));
                } while (cursor.moveToNext());

            }}catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return Visit_Date;
    }
}
