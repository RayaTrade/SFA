package preview_database.DB.DealerDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import Model.Product;

public class DealerDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dealer.db";
    public static final String CONTACTS_TABLE_NAME = "dealer_order";
    public static final String COLUMN_DEALER_SKU = "SKU";
    public static final String COLUMN_DEALER_category = "Category";
    public static final String COLUMN_DEALER_image_PATH = "image_path";
    public static final String COLUMN_DEALER_product_brand = "Brand";
    public static final String COLUMN_DEALER_product_model = "Model";
    public static final String COLUMN_DEALER_product_description = "Description";
    public static final String COLUMN_DEALER_product_QTY = "Quantity";
    public static final String COLUMN_DEALER_product_Customer_Number = "Customer_Number";
    public static final String COLUMN_DEALER_product_OnHand = "OnHand";
    public static final String COLUMN_DEALER_product_UnitPrice = "UnitPrice";
    public static final String COLUMN_DEALER_product_Total = "Total";

    public DealerDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table dealer_order " +
                        "(SKU text primary key, Category text NOT NULL,image_path text NOT NULL,Brand text NOT NULL" +
                        ",Model text NOT NULL,Description text NOT NULL,Quantity text NOT NULL,Customer_Number text NOT NULL" +
                        ",OnHand text NOT NULL,UnitPrice text NOT NULL,Total text NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dealer_order");
        onCreate(db);
    }
    public boolean insertItemDealerOrder (String SKU, String Category, String image_path, String Brand, String Model
            , String Description, String Quantity, String Customer_Number, String OnHand, Float UnitPrice, String Total) {
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
        contentValues.put("Total", Total);

        db.insert("dealer_order", null, contentValues);

        return true;
    }


    public boolean CheckSKUDealerOrder(String SKU) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql ="SELECT SKU FROM dealer_order WHERE SKU='"+SKU+"'";
        cursor= db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }

    }

    public int numberOfRowsDealerOrder(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateItemDealerOrder (String SKU,String Quantity, String OnHand, Float UnitPrice, String Total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Quantity", Quantity);
        contentValues.put("OnHand", OnHand);
        contentValues.put("UnitPrice", UnitPrice);
        contentValues.put("Total", Total);
        db.update("dealer_order", contentValues, "SKU = ? ", new String[] { SKU } );
        return true;
    }


    public Integer deleteSKU (String SKU) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("dealer_order",
                "SKU = ? ",
                new String[] { SKU });
    }


    public ArrayList<String> getAllSKUDealerOrder () {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from dealer_order", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(COLUMN_DEALER_SKU)));
            res.moveToNext();
        }
        return array_list;
    }
    public void deleteAllDealerOrder(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from dealer_order");
        db.close();
    }

    public List<Product> getAllDealerOrder() {
        List<Product> orders = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM dealer_order";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Product dealer_order = new Product();
                    dealer_order.setSKU(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_SKU)));
                    dealer_order.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_category)));
                    dealer_order.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_brand)));
                    dealer_order.setModel(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_model)));
                    dealer_order.setQTY(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_QTY)));
                    dealer_order.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_image_PATH)));
                    dealer_order.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_description)));
                    dealer_order.setCustomer_number(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_Customer_Number)));
                    dealer_order.setOnHand(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_OnHand)));
                    dealer_order.setUnitPrice(Float.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_UnitPrice))));
                    dealer_order.setTotal(cursor.getString(cursor.getColumnIndex(COLUMN_DEALER_product_Total)));
                    orders.add(dealer_order);
                } while (cursor.moveToNext());

            }}catch (Exception e){
            e.printStackTrace();
        }

        // close db connection
        db.close();

        // return notes list
        return orders;
    }
}
