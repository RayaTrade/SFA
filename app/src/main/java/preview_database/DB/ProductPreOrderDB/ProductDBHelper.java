package preview_database.DB.ProductPreOrderDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Model.Product;

public class ProductDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "Product_PreOrder.db";

    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME
                + " (" +
                ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_SKU + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_category + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_image_PATH + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_brand + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_model + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_description + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_QTY + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_Customer_Number + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_OnHand + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_UnitPrice + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_Total + " TEXT NOT NULL, " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_product_VisitDate + " TEXT NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public List<Product> getAllProduct() {
        List<Product> products = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ProductContract.ProductEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setSKU(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SKU)));
                product.setCategory(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_category)));
                product.setBrand(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_brand)));
                product.setModel(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_model)));
                product.setQTY(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_QTY)));
                product.setImage(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_image_PATH)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_description)));
                product.setCustomer_number(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_Customer_Number)));
                product.setOnHand(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_OnHand)));
                product.setUnitPrice(Float.valueOf(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_UnitPrice))));
                product.setTotal(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_Total)));
                product.setVisit_Date(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_product_VisitDate)));
                products.add(product);
            } while (cursor.moveToNext());

        }}catch (Exception e){
            e.printStackTrace();
        }

        // close db connection
        db.close();

        // return notes list
        return products;
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ ProductContract.ProductEntry.TABLE_NAME);
        db.close();
    }

    public void deleteBySKU(String SKU){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ ProductContract.ProductEntry.TABLE_NAME+" Where SKU='"+SKU+"'");
        db.close();
    }

    public String getVisitDatePendingPreOrder(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from "+ ProductContract.ProductEntry.TABLE_NAME+ " ORDER BY _id ASC LIMIT 1";
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
