package preview_database.DB.SyncDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import CustomerVisitsFragments.CustomerMonthFragment;
import CustomerVisitsFragments.CustomerTodayFragment;
import Model.Brand;
import Model.Category;
import Model.Customer;
import Model.Model;
import Model.Product;
import Model.DeliveryMethod;
import Model.TenderType;
import Model.Permission;
import Model.Header;
import Model.*;

import Model.Items;
import Model.TransactionTender;
import Model.Serial;
import Model.StockSerial;
import Model.SyncBack;
import Model.Reason;

public class SyncDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "sync.db";

    public SyncDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 16);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table LoginOffline " +
                        "(UserID text primary key, ServerConfigID text,Currency text,AllowNegativeQty text,GPSInterval text" +
                        ",MobileVersion text" +",Allow_Delivery_Method text" +
                        ")"
        );
        db.execSQL(
                "create table PermssionsOffline " +
                        "(MenuID text primary key, MenuName text)"
        );

        db.execSQL(
                "create table Ora_Customers " +
                        "(Customer_Number text primary key, " +
                        "Customer_Name text," +
                        "PRICE_LIST text," +
                        "DueDateFrom datetime," +
                        "ScheuleID text," +
                        "VisitID text,"+
                        "CUSTOMER_CLASS text,"+
                        "CREATION_DATE text,"+
                        "CUSTOMER_CATEGORY text,"+
                        "PRIMARY_SALESREP_NAME text,"+
                        "PAYMENT_TERM text,"+
                        "CITY text,"+
                        "COUNTRY text,"+
                        "CREDIT_LIMIT text,"+
                        "OVER_DRAFT text,"+
                        "REGION text,"+
                        "AREA text)"


        );
        db.execSQL(
                "create table CustomersWithVisitOffline " +
                        "(VisitID text primary key, Customer_Name text,PRICE_LIST text,DueDateFrom datetime,DueDateTo datetime,ScheuleID text,Customer_Number text)"
        );

        db.execSQL(
                "create table PreOrderOffline " +
                        "(ITEM_CODE text primary key, " +
                        "CAT text," +
                        "BRAND text," +
                        "MODEL text," +
                        "DESCRIPTION text," +
                        "BrandImage text," +
                        "ONHAND text," +
                        "UnitPrice text," +
                        "COLOR text," +
                        "ACC text,"+
                        "STATUS text,"+
                        "TAX_CODE text,"+
                        "TAX_RATE text,"+
                        "SEGMENT1 text,"+
                        "MAIN_CAT text,"+
                        "INVENTORY_ITEM_ID text,"+
                        "CREATION_DATE text"+
                        ")"
        );

        db.execSQL(
                "create table OrderOffline " +
                        "(ITEM_CODE text primary key, " +
                        "CAT text," +
                        "BRAND text," +
                        "MODEL text," +
                        "DESCRIPTION text," +
                        "BrandImage text," +
                        "ONHAND text," +
                        "UnitPrice text," +
                        "COLOR text," +
                        "ACC text,"+
                        "STATUS text,"+
                        "TAX_CODE text,"+
                        "TAX_RATE text,"+
                        "SEGMENT1 text,"+
                        "MAIN_CAT text,"+
                        "INVENTORY_ITEM_ID text,"+
                        "CREATION_DATE text"+
                        ")"
        );

        db.execSQL(
                "create table CategoryOffline " +
                        "(Category text primary key)"
        );
        db.execSQL(
                "create table BrandOffline " +
                        "(Brand text primary key)"
        );
        db.execSQL(
                "create table ModelOffline " +
                        "(Model text primary key)"
        );
        db.execSQL(
                "create table DeliveryMethodOffline " +
                        "(DeliveryMethod text primary key)"
        );
        db.execSQL(
                "create table TenderTypes " +
                        "(Name text primary key)"
        );
        db.execSQL(
                "create table SalesReasonOffline " +
                        "(Reason text primary key)"
        );
        db.execSQL(
                "create table EndUserPriceOffline " +
                        "(CustomerName text,CustomerNumber text,ItemCode text,Price text)"
        );

        db.execSQL(
                "create table Ora_PriceList " +
                        "(PR_NAME text,ITEM_CODE text,PRICE text,END_USER_PR text)"
        );

        db.execSQL(
                "create table GetStockSerialsOffline " +
                        "(SerialNumber text primary key,ItemCode text,Model text,BoxNumber text,isSelected text)"
        );
        db.execSQL(
                "create table Transactions " +
                        "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "CountryCode text," +
                        "CustomerNumber text," +
                        "CustomerName text,"+
                        "Submitter text" +
                        ",Long text," +
                        "Lat text," +
                        "Comment text," +
                        "DeliveryMethod text," +
                        "TransactionType text," +
                        "CreateDate text,"+
                        "PromoID text," +
                        "PreOrder_Total_History text," +
                        "PreOrder_Total text)"
        );
        db.execSQL(
                "create table TransactionTypes " +
                        "(Id INTEGER PRIMARY KEY," +
                        "T_Type text)"
        );

        db.execSQL(
                "create table Promotions " +
                        "(PromoId INTEGER PRIMARY KEY," +
                        "ActiveDate text," +
                        "Country text," +
                        "EndDate text," +
                        "IsActive text," +
                        "PromDesc text," +
                        "PromNote text," +
                        "PromType text," +
                        "PromoName text," +
                        "Server text," +
                        "ServerConfigration text," +
                        "CreatedDate text" +
                        ")"
        );

        db.execSQL(
                "create table PromotionsXQuery " +
                        "(PromoId INTEGER PRIMARY KEY," +
                        "And_PromoQuery text)"
        );

        db.execSQL(
                "create table PromotionsXResult " +
                        "(PromoId INTEGER ," +
                        "PromoResult text)"
        );


        db.execSQL(
                "create table Transaction_Tender " +
                        "(Id INTEGER PRIMARY KEY AUTOINCREMENT,PreOrderID text,TenderTypeID text,Submitter text" +
                        ",Total text)"
        );
        db.execSQL(
                "create table Transaction_Items " +
                        "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "HeaderID text," +
                        "ITEM_CODE text," +
                        "Qty text" +
                        ",UnitPrice text," +
                        "Discount text," +
                        "CreateDate text," +
                        "Submitter text," +
                        "QtyinStock text," +
                        "Total text," +
                        "Total_History text)"
        );
        db.execSQL(
                "create table TransactionSerialsOffline " +
                        "(Id INTEGER PRIMARY KEY AUTOINCREMENT,Transaction_Header_ID text,Transaction_Items_ID text,ItemSerial text" +
                        ",Item_Code text)"
        );
        db.execSQL(
                "create table SyncBackOffline " +
                        "(Id INTEGER PRIMARY KEY AUTOINCREMENT,Id_Offline text,Id_Online text,Type text,ItemSaved text,CustomerNumber text)"
        );

        db.execSQL(
                "create table ItemDimensions " +
                        "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "BRAND text," +
                        "DESCRIPTION text," +
                        "HEIGHT text" +
                        ",INVENTORY_ITEM_ID text," +
                        "ITEMCODE text," +
                        "LENGTH text," +
                        "WEIGHT text," +
                        "size text," +
                        "WIDTH text )"
        );
        db.execSQL(
                "create table TruckType " +
                        "(ID text PRIMARY KEY," +
                        "Height text," +
                        "Type text," +
                        "Weight text" +
                        ",length text," +
                        "size text," +
                        "width text)"
        );

        db.execSQL(
                "create table Transaction_Truck " +
                        "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "TruckID text," +
                        "HeaderID text," +
                        "Height text," +
                        "Type text," +
                        "Weight text" +
                        ",length text," +
                        "size text," +
                        "count text," +
                        "width text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LoginOffline");
        db.execSQL("DROP TABLE IF EXISTS PermssionsOffline");
        db.execSQL("DROP TABLE IF EXISTS Ora_Customers");
        db.execSQL("DROP TABLE IF EXISTS CustomersWithVisitOffline");
        db.execSQL("DROP TABLE IF EXISTS PreOrderOffline");
        db.execSQL("DROP TABLE IF EXISTS OrderOffline");
        db.execSQL("DROP TABLE IF EXISTS CategoryOffline");
        db.execSQL("DROP TABLE IF EXISTS BrandOffline");
        db.execSQL("DROP TABLE IF EXISTS ModelOffline");
        db.execSQL("DROP TABLE IF EXISTS DeliveryMethodOffline");
        db.execSQL("DROP TABLE IF EXISTS TenderTypes");
        db.execSQL("DROP TABLE IF EXISTS SalesReasonOffline");
        db.execSQL("DROP TABLE IF EXISTS EndUserPriceOffline");
        db.execSQL("DROP TABLE IF EXISTS Ora_PriceList");
        db.execSQL("DROP TABLE IF EXISTS GetStockSerialsOffline");
        db.execSQL("DROP TABLE IF EXISTS Transactions");
        db.execSQL("DROP TABLE IF EXISTS TransactionTypes");
        db.execSQL("DROP TABLE IF EXISTS Transaction_Tender");
        db.execSQL("DROP TABLE IF EXISTS Transaction_Items");
        db.execSQL("DROP TABLE IF EXISTS TransactionSerialsOffline");
        db.execSQL("DROP TABLE IF EXISTS SyncBackOffline");
        db.execSQL("DROP TABLE IF EXISTS Promotions");
        db.execSQL("DROP TABLE IF EXISTS PromotionsXQuery");
        db.execSQL("DROP TABLE IF EXISTS PromotionsXResult");
        db.execSQL("DROP TABLE IF EXISTS ItemDimensions");
        db.execSQL("DROP TABLE IF EXISTS TruckType");
        db.execSQL("DROP TABLE IF EXISTS Transaction_Truck");

        onCreate(db);
    }

    //#region    ItemDimensions   TruckType Transaction_Truck
    public void deleteItemDimensionsOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from ItemDimensions");
        db.close();
    }
    public void deleteTruckTypeOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from TruckType");
        db.close();
    }
    public void deleteTransaction_TruckOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transaction_Truck");
        db.close();
    }

    public boolean InsertItemDimensionsOffline(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("ItemDimensions", null, contentValues);
        db.close();//
        return true;
    }

    public boolean InsertTransaction_Truck(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Transaction_Truck", null, contentValues);
        db.close();//
        return true;
    }

    public boolean InsertTruckTypeOffline(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("TruckType", null, contentValues);
        db.close();//
        return true;
    }

    public List<TruckType> getAllTruckTypeOffline(String HeaderID) {
        List<TruckType> truckTypes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Transaction_Truck where HeaderID ='"+HeaderID+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    TruckType truckType = new TruckType();
                    truckType.setID(cursor.getString(cursor.getColumnIndex("TruckID")));
                    truckType.setHeight(cursor.getString(cursor.getColumnIndex("Height")));
                    truckType.setType(cursor.getString(cursor.getColumnIndex("Type")));
                    truckType.setWeight(cursor.getString(cursor.getColumnIndex("Weight")));
                    truckType.setLength(cursor.getString(cursor.getColumnIndex("length")));
                    truckType.setSize(Double.parseDouble(cursor.getString(cursor.getColumnIndex("size"))));
                    truckType.setWidth(cursor.getString(cursor.getColumnIndex("width")));
                    truckType.setCount(Integer.parseInt(cursor.getString(cursor.getColumnIndex("count"))));
                    truckTypes.add(truckType);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return truckTypes;
    }


    public List<TruckType> getAllTruckTypeOffline() {
        List<TruckType> truckTypes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM TruckType";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    TruckType truckType = new TruckType();
                    truckType.setID(cursor.getString(cursor.getColumnIndex("ID")));
                    truckType.setHeight(cursor.getString(cursor.getColumnIndex("Height")));
                    truckType.setType(cursor.getString(cursor.getColumnIndex("Type")));
                    truckType.setWeight(cursor.getString(cursor.getColumnIndex("Weight")));
                    truckType.setLength(cursor.getString(cursor.getColumnIndex("length")));
                    truckType.setSize(Double.parseDouble(cursor.getString(cursor.getColumnIndex("size"))));
                    truckType.setWidth(cursor.getString(cursor.getColumnIndex("width")));
                    truckType.setCount(0);
                    truckTypes.add(truckType);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return truckTypes;
    }
    public List<ProductDimension> getItemDimensionsOffline(List<Product> productList) {
        List<ProductDimension> productDimensionList = new ArrayList<>();
        String item_code= "";
        for (Product product: productList) {
            if(item_code == "")
                item_code+="'"+product.getSKU()+"'";
            else
                item_code+=","+"'"+product.getSKU()+"'";

        }

        //String item_code="'AR12JPFTPWQNBT','PM6-GR'";
        String selectQuery = "SELECT  * FROM ItemDimensions where ITEMCODE in("+item_code+")";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {

                    ProductDimension Product = new ProductDimension();
                    Product.setBRAND(cursor.getString(cursor.getColumnIndex("BRAND")));
                    Product.setDESCRIPTION(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));
                    Product.setHEIGHT(cursor.getString(cursor.getColumnIndex("HEIGHT")));
                    Product.setINVENTORY_ITEM_ID(cursor.getString(cursor.getColumnIndex("INVENTORY_ITEM_ID")));
                    Product.setSize(Double.parseDouble(cursor.getString(cursor.getColumnIndex("size"))));
                    Product.setITEMCODE(cursor.getString(cursor.getColumnIndex("ITEMCODE")));
                    Product.setLENGTH(cursor.getString(cursor.getColumnIndex("LENGTH")));
                    Product.setWEIGHT(cursor.getString(cursor.getColumnIndex("WEIGHT")));
                    Product.setWIDTH(cursor.getString(cursor.getColumnIndex("WIDTH")));
                    productDimensionList.add(Product);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return productDimensionList;
    }

    //#endregion

    //#region  Promotions

    public String GetOrder_Price(String Header_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT PreOrder_Total FROM Transactions where Id='"+Header_ID+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                String PreOrder_Total = cursor.getString(cursor.getColumnIndex("PreOrder_Total"));
                return PreOrder_Total;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();


        return "0.0";
    }

    public List<Promotions> getAllPromotions() {
        List<Promotions> Promotions = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Promotions join PromotionsXQuery on Promotions.promoid = PromotionsXQuery.PromoId order by CreatedDate desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {


                    Promotions Promotion = new Promotions();
                    Promotion.setPromoId((cursor.getString(cursor.getColumnIndex("PromoId"))));
                    Promotion.setActiveDate(cursor.getString(cursor.getColumnIndex("ActiveDate")));
                    Promotion.setCountry(cursor.getString(cursor.getColumnIndex("Country")));
                    Promotion.setEndDate(cursor.getString(cursor.getColumnIndex("EndDate")));
                    Promotion.setIsActive(cursor.getString(cursor.getColumnIndex("IsActive")));
                    Promotion.setPromDesc(cursor.getString(cursor.getColumnIndex("PromDesc")));
                    Promotion.setPromNote(cursor.getString(cursor.getColumnIndex("PromNote")));
                    Promotion.setPromType(cursor.getString(cursor.getColumnIndex("PromType")));
                    Promotion.setPromoName(cursor.getString(cursor.getColumnIndex("PromoName")));
                    Promotion.setServer(cursor.getString(cursor.getColumnIndex("Server")));
                    Promotion.setAnd_PromoQuery(cursor.getString(cursor.getColumnIndex("And_PromoQuery")));

                    Promotions.add(Promotion);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return Promotions;
    }

    public int IsPromoValid(String promQuery,String headerid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = promQuery.replace("@id"," '"+headerid+"'");
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount=0;
        try {
            icount = mcursor.getInt(0);
        }
        catch (Exception ex)
        {
            return icount;
        }
        return icount;
    }

    public boolean Applly_Promo(String promId, Header Header_ID)
    {
        List<Promotions> Promotions = new ArrayList<>();
        String selectQuery = "SELECT  * FROM Promotions join PromotionsXResult on Promotions.promoid = PromotionsXResult.PromoId and Promotions.promoid="+promId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Promotions Promotion = new Promotions();
                    Promotion.setPromoResult(cursor.getString(cursor.getColumnIndex("PromoResult")));
                    Promotions.add(Promotion);
                } while (cursor.moveToNext());
                db.beginTransaction();
                for (Promotions prom : Promotions) {
                    try {
                        String id = "'"+Header_ID.getId()+"'";
                        String newResultQuery = prom.getPromoResult().replace("@id",id);

                        if(Header_ID.getTransactionType().equals("1"))
                            newResultQuery = newResultQuery.replace("@DBT","PreOrderOffline");
                        else
                            newResultQuery= newResultQuery.replace("@DBT","OrderOffline");

                        SQLiteStatement insert = db.compileStatement(newResultQuery);
                        int i = insert.executeUpdateDelete();
                        insert.clearBindings();
                        if(i == 0){
                            db.endTransaction();
                            db.close();
                            return  false;
                        }

                    }catch (Exception ex)
                    {
                        db.endTransaction();
                        db.close();
                        return  false;
                    }
                }
                try {
                    if(db.inTransaction())
                    db.setTransactionSuccessful();
                }
                catch (Exception e){}
                finally {

                    db.endTransaction();
                    db.close();
                }

            }
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }




        return  true;
    }

public void deletepromotion() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("delete from  Promotions");
    db.execSQL("delete from  PromotionsXResult");
    db.execSQL("delete from  PromotionsXQuery");
    db.close();
}
    public boolean InsertPromotions(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Promotions", null, contentValues);
        db.close();//
        return true;
    }

    public boolean InsertPromotionsXQuery(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("PromotionsXQuery", null, contentValues);
        db.close();//
        return true;
    }
    public boolean InsertPromotionsXResult(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("PromotionsXResult", null, contentValues);
        db.close();//
        return true;
    }

    //#endregion


    //#region Transactions

    public List<Serial> getserial( String items)
    {
        List<Serial> stockSerials = new ArrayList<>();

        String selectQuery = "SELECT  * FROM GetStockSerialsOffline  where ItemCode in("+items+") and isSelected = 'false' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Serial stockSerial = new Serial();
                    stockSerial.setSerialNumber(cursor.getString(cursor.getColumnIndex("SerialNumber")));
                    stockSerial.setItemCode(cursor.getString(cursor.getColumnIndex("ItemCode")));
                    stockSerial.setModel(cursor.getString(cursor.getColumnIndex("Model")));
                    stockSerial.setBox_number(cursor.getString(cursor.getColumnIndex("BoxNumber")));
                    stockSerial.setSelected(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("isSelected"))));

                    stockSerials.add(stockSerial);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return stockSerials;

    }

    public int getcountofTransaction() {
    SQLiteDatabase db = this.getWritableDatabase();
    String count = "SELECT count(*) FROM Transactions";
    Cursor mcursor = db.rawQuery(count, null);
    mcursor.moveToFirst();
    int icount = mcursor.getInt(0);
    return icount;
}

    public int getcountofPendingPreOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Transactions where TransactionType='1'";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount;
    }

    public int getcountofPendingOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Transactions where TransactionType='2'";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount;
    }

    public void deleteAllTransactionOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transactions");
        db.close();
    }

    public void deleteFromTransactionOffline(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transactions where Id=" + id);
        db.close();
    }


    public long InsertTransactionsOffline(String CountryCode, String CustomerNumber,String CustomerName,String Submitter, String Long, String Lat
            , String Comment, String DeliveryMethod, String TransactionType, String Total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CountryCode", CountryCode);
        contentValues.put("CustomerNumber", CustomerNumber);
        contentValues.put("CustomerName", CustomerName);
        contentValues.put("Submitter", Submitter);
        contentValues.put("Long", Long);
        contentValues.put("Lat", Lat);
        contentValues.put("Comment", Comment);
        contentValues.put("DeliveryMethod", DeliveryMethod);
        contentValues.put("TransactionType", TransactionType);
        contentValues.put("PreOrder_Total", Total);


        String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());

        contentValues.put("CreateDate", currentDate);


        long id = db.insert("Transactions", null, contentValues);
        db.close();
        return id;
    }

    public List<Header> getAllTransactionsOffline() {
        List<Header> headers = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Transactions";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Header header = new Header();
                    header.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    header.setCountryCode(cursor.getString(cursor.getColumnIndex("CountryCode")));
                    header.setCustomerNumber(cursor.getString(cursor.getColumnIndex("CustomerNumber")));
                    header.setSubmitter(cursor.getString(cursor.getColumnIndex("Submitter")));
                    header.setLong(cursor.getString(cursor.getColumnIndex("Long")));
                    header.setLat(cursor.getString(cursor.getColumnIndex("Lat")));
                    header.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                    header.setDeliveryMethod(cursor.getString(cursor.getColumnIndex("DeliveryMethod")));
                    header.setTransactionType(cursor.getString(cursor.getColumnIndex("TransactionType")));
                    header.setTotal(cursor.getString(cursor.getColumnIndex("PreOrder_Total")));
                    header.setPromoID(cursor.getString(cursor.getColumnIndex("PromoID")));

                    headers.add(header);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return headers;
    }

    public List<Header> getAllTransactionsOfflineByItemID(int Id) {
        List<Header> headers = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Transactions where Id=" + Id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Header header = new Header();
                    header.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    header.setCountryCode(cursor.getString(cursor.getColumnIndex("CountryCode")));
                    header.setCustomerNumber(cursor.getString(cursor.getColumnIndex("CustomerNumber")));
                    header.setCustomerName(cursor.getString(cursor.getColumnIndex("CustomerName")));
                    header.setSubmitter(cursor.getString(cursor.getColumnIndex("Submitter")));
                    header.setLong(cursor.getString(cursor.getColumnIndex("Long")));
                    header.setLat(cursor.getString(cursor.getColumnIndex("Lat")));
                    header.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                    header.setDeliveryMethod(cursor.getString(cursor.getColumnIndex("DeliveryMethod")));
                    header.setTransactionType(cursor.getString(cursor.getColumnIndex("TransactionType")));
                    header.setTotal(cursor.getString(cursor.getColumnIndex("PreOrder_Total")));
                    header.setPromoID(cursor.getString(cursor.getColumnIndex("PromoID")));

                    headers.add(header);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return headers;
    }

    public Header getAllTransactionsByID(int HeaderId) {
        Header header = new Header();

        String selectQuery = "SELECT  * FROM Transactions where Id=" + HeaderId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {

                    header.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    header.setCountryCode(cursor.getString(cursor.getColumnIndex("CountryCode")));
                    header.setCustomerNumber(cursor.getString(cursor.getColumnIndex("CustomerNumber")));
                    header.setCustomerName(cursor.getString(cursor.getColumnIndex("CustomerName")));
                    header.setSubmitter(cursor.getString(cursor.getColumnIndex("Submitter")));
                    header.setLong(cursor.getString(cursor.getColumnIndex("Long")));
                    header.setLat(cursor.getString(cursor.getColumnIndex("Lat")));
                    header.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                    header.setDeliveryMethod(cursor.getString(cursor.getColumnIndex("DeliveryMethod")));
                    header.setTransactionType(cursor.getString(cursor.getColumnIndex("TransactionType")));
                    header.setCreateDate(cursor.getString(cursor.getColumnIndex("CreateDate")));
                    header.setPromoID(cursor.getString(cursor.getColumnIndex("PromoID")));
                    header.setPreOrder_Total(cursor.getString(cursor.getColumnIndex("PreOrder_Total")));



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return header;
    }


    public List<Header> getAllTransactionsPreOrderOffline() {
        List<Header> headers = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Transactions where TransactionType='1'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Header header = new Header();
                    header.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    header.setCountryCode(cursor.getString(cursor.getColumnIndex("CountryCode")));
                    header.setCustomerNumber(cursor.getString(cursor.getColumnIndex("CustomerNumber")));
                    header.setSubmitter(cursor.getString(cursor.getColumnIndex("Submitter")));
                    header.setLong(cursor.getString(cursor.getColumnIndex("Long")));
                    header.setLat(cursor.getString(cursor.getColumnIndex("Lat")));
                    header.setComment(cursor.getString(cursor.getColumnIndex("Comment")));
                    header.setDeliveryMethod(cursor.getString(cursor.getColumnIndex("DeliveryMethod")));
                    header.setTransactionType(cursor.getString(cursor.getColumnIndex("TransactionType")));

                    headers.add(header);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return headers;
    }

    public boolean checkifTransactionsOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Transactions";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }


    //#endregion


    //#region TransactionItems

    public int getcountofAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Transaction_Items";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount;
    }

    public int getcountofAllItemsByHeaderID(String HeaderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Transaction_Items where HeaderID='" + HeaderID + "'";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount;
    }

    public int getcountofItems(String HeaderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Transaction_Items where HeaderID='" + HeaderID + "'";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount;
    }



    public void deleteFromTransactionItemsOffline(String HeaderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transaction_Items where HeaderID='" + HeaderID + "'");
        db.close();
    }

    public void deleteFromTransactionItemsOfflineByItemCode(String ItemCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transaction_Items where ITEM_CODE='" + ItemCode + "'");
        db.close();
    }


    public void deleteAllTransactionItemsOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transaction_Items");
        db.close();
    }


    public long InsertTransactionItemsOffline(String HeaderID, String ItemCode, String Qty, String UnitPrice, String Discount, String Submitter, String QtyinStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HeaderID", HeaderID);
        contentValues.put("ITEM_CODE", ItemCode);
        contentValues.put("Qty", Qty);
        contentValues.put("UnitPrice", UnitPrice);
        contentValues.put("Discount", Discount);
        contentValues.put("Submitter", Submitter);
        contentValues.put("QtyinStock", QtyinStock);
        contentValues.put("Total", String.valueOf( Double.parseDouble(UnitPrice) * Integer.parseInt(Qty) ));

        String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        contentValues.put("CreateDate", currentDate);


        long id = db.insert("Transaction_Items", null, contentValues);

        return id;
    }

    public OrderReceipt orderReceipt (OrderReceipt orderReceipt)
    {
        List<Transaction_Items> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM Transaction_Items where  HeaderID=" + orderReceipt.getOrderId();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Transaction_Items item = new Transaction_Items();

                    item.setSKU(cursor.getString(cursor.getColumnIndex("ITEM_CODE")));
                    item.setQTY(cursor.getString(cursor.getColumnIndex("Qty")));
                    item.setUnitPrice(cursor.getString(cursor.getColumnIndex("UnitPrice")));
                    item.setDiscount(cursor.getString(cursor.getColumnIndex("Discount")));
                    item.setTotal(cursor.getString(cursor.getColumnIndex("Total")));

                    items.add(item);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        orderReceipt.setProducts(items);
        List<Header> header = getAllTransactionsOfflineByItemID(Integer.parseInt(orderReceipt.getOrderId()));
        orderReceipt.setCustomername(header.get(0).getCustomerName());
        orderReceipt.setSales_name(header.get(0).getSubmitter());
        db.close();

        return orderReceipt;

    }

    public List<Items> getAllTransactionItemsOffline(final String HeaderID) {
        List<Items> items = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Transaction_Items where  HeaderID=" + HeaderID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Items item = new Items();
                    item.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    item.setHeaderId(cursor.getString(cursor.getColumnIndex("HeaderID")));
                    item.setItemCode(cursor.getString(cursor.getColumnIndex("ITEM_CODE")));
                    item.setQty(cursor.getString(cursor.getColumnIndex("Qty")));
                    item.setUnitPrice(cursor.getString(cursor.getColumnIndex("UnitPrice")));
                    item.setDiscount(cursor.getString(cursor.getColumnIndex("Discount")));
                    item.setSubmitter(cursor.getString(cursor.getColumnIndex("Submitter")));
                    item.setQtyinStock(cursor.getString(cursor.getColumnIndex("QtyinStock")));

                    items.add(item);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return items;
    }

    public List<Items> getAll_Items_need_serila(final String HeaderID) {
        List<Items> items = new ArrayList<>();

        String selectQuery = "SELECT  item_code,sum(Qty) as Qty FROM Transaction_Items where Submitter = 'Promotion' and  HeaderID=" + HeaderID+" group by item_code";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Items item = new Items();
                    item.setItemCode(cursor.getString(cursor.getColumnIndex("ITEM_CODE")));
                    item.setQty(cursor.getString(cursor.getColumnIndex("Qty")));

                    items.add(item);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return items;
    }


    public List<Items> getAllTransactionItemsOffline_SyncBack(final String HeaderID) {
        List<Items> items = new ArrayList<>();

       // String selectQuery = "SELECT  * FROM Transaction_Items where Submitter != 'Promotion' and HeaderID=" + HeaderID;
        String selectQuery = "SELECT  * FROM Transaction_Items where Submitter != 'Promotion' and HeaderID=" + HeaderID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Items item = new Items();
                    item.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    item.setHeaderId(cursor.getString(cursor.getColumnIndex("HeaderID")));
                    item.setItemCode(cursor.getString(cursor.getColumnIndex("ITEM_CODE")));
                    item.setQty(cursor.getString(cursor.getColumnIndex("Qty")));
                    item.setUnitPrice(cursor.getString(cursor.getColumnIndex("UnitPrice")));
                    item.setDiscount(cursor.getString(cursor.getColumnIndex("Discount")));
                    item.setSubmitter(cursor.getString(cursor.getColumnIndex("Submitter")));
                    item.setQtyinStock(cursor.getString(cursor.getColumnIndex("QtyinStock")));

                    items.add(item);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return items;
    }


    //#endregion

    //#region TransactionTypes

    public boolean InsertTransactionTypesOffline(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("TransactionTypes", null, contentValues);
        db.close();//
        return true;
    }

    public void deleteAllTransactionTypesOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from TransactionTypes");
        db.close();
    }


    //#endregion

    //#region Ora_Customers
    public boolean InsertCustomersOffline(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Ora_Customers", null, contentValues);
        db.close();//
        return true;
    }


    public List<Customer> getAllCustomerOffline() {
        List<Customer> customers = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Ora_Customers";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Customer customer = new Customer();
                    customer.setNumber(cursor.getString(cursor.getColumnIndex("Customer_Number")));
                    customer.setName(cursor.getString(cursor.getColumnIndex("Customer_Name")));
                    customer.setPrice_list(cursor.getString(cursor.getColumnIndex("PRICE_LIST")));
                    customer.setDueDateFrom(cursor.getString(cursor.getColumnIndex("DueDateFrom")));
                    customer.setScheuleID(cursor.getString(cursor.getColumnIndex("ScheuleID")));
                    customer.setVisitID(cursor.getString(cursor.getColumnIndex("VisitID")));

                    customer.setAREA(cursor.getString(cursor.getColumnIndex("AREA")));
                    customer.setCITY(cursor.getString(cursor.getColumnIndex("CITY")));
                    customer.setCOUNTRY(cursor.getString(cursor.getColumnIndex("CREATION_DATE")));
                    customer.setCREATION_DATE(cursor.getString(cursor.getColumnIndex("CREATION_DATE")));
                    customer.setCREDIT_LIMIT(cursor.getString(cursor.getColumnIndex("CREDIT_LIMIT")));
                    customer.setCUSTOMER_CATEGORY(cursor.getString(cursor.getColumnIndex("CUSTOMER_CATEGORY")));
                    customer.setCUSTOMER_CLASS(cursor.getString(cursor.getColumnIndex("CUSTOMER_CLASS")));
                    customer.setPAYMENT_TERM(cursor.getString(cursor.getColumnIndex("PAYMENT_TERM")));
                    customer.setREGION(cursor.getString(cursor.getColumnIndex("REGION")));
                    customer.setPRIMARY_SALESREP_NAME(cursor.getString(cursor.getColumnIndex("PRIMARY_SALESREP_NAME")));
                    customer.setOVER_DRAFT(cursor.getString(cursor.getColumnIndex("OVER_DRAFT")));

                    customers.add(customer);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return customers;
    }

    public boolean checkifCustomerOfflineisEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Ora_Customers";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllCustomersOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Ora_Customers");
        db.close();
    }

    //#endregion

    //#region TenderTypes
    public boolean InsertTenderTypeOffline(String TenderType) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("Name", TenderType);

    db.insert("TenderTypes", null, contentValues);
    db.close();
    return true;
}

    public boolean checkifTenderTypeOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM TenderTypes";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllTenderTypeOfflineOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from TenderTypes");
        db.close();
    }

    public List<TenderType> getAllTenderTypeOffline() {
        List<TenderType> tenderTypes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM TenderTypes";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    TenderType tenderType = new TenderType();
                    tenderType.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    tenderTypes.add(tenderType);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return tenderTypes;
    }

    //#endregion

    //#region Transaction_Tender

    public void deleteFromTransactionTenderOffline(String HeaderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transaction_Tender where PreOrderID='" + HeaderID + "'");
        db.close();
    }

    public void deleteAllTransactionTenderOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transaction_Tender");
        db.close();
    }


    public boolean InsertTransactionTenderOffline(String HeaderID, String TenderType, String Submitter, String Total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PreOrderID", HeaderID);
        contentValues.put("TenderTypeID", TenderType);
        contentValues.put("Submitter", Submitter);
        contentValues.put("Total", Total);

        db.insert("Transaction_Tender", null, contentValues);
        db.close();
        return true;
    }

    public List<TransactionTender> getAllTransactionTenderOffline(final String HeaderID) {
        List<TransactionTender> transactionTenders = new ArrayList<>();

        String selectQuery = "SELECT  * FROM Transaction_Tender where PreOrderID=" + HeaderID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    TransactionTender transactionTender = new TransactionTender();
                    transactionTender.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    transactionTender.setHeaderId(cursor.getString(cursor.getColumnIndex("PreOrderID")));
                    transactionTender.setTenderType(cursor.getString(cursor.getColumnIndex("TenderTypeID")));
                    transactionTender.setSubmitter(cursor.getString(cursor.getColumnIndex("Submitter")));
                    transactionTender.setTotal(cursor.getString(cursor.getColumnIndex("Total")));

                    transactionTenders.add(transactionTender);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return transactionTenders;
    }


    //#endregion

    public boolean checkiSyncBackOfflineAnyErrors() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM SyncBackOffline where Id_Online='0' OR ItemSaved='Error'";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean UpdateifSyncBackOfflineIDFound(String Id_Online, String Id_Offline, String ItemSaved) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Id_Online", Id_Online);
        contentValues.put("ItemSaved", ItemSaved);
        db.update("SyncBackOffline", contentValues, "Id_Offline='" + Id_Offline + "'", null);

        return true;
    }

    public boolean checkifSyncBackOfflineIDFound(String Id_Offline) {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM SyncBackOffline where Id_Offline='" + Id_Offline + "'";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return true;
        } else {
            return false;
        }
    }


    public void deleteAllSyncBackOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from SyncBackOffline");
        db.close();
    }

    public void deleteAllSyncBackOfflineByID(String Id_Offline) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from SyncBackOffline where Id_Offline='" + Id_Offline + "'");
        db.close();
    }

    public List<SyncBack> getAllSyncBackOffline() {
        List<SyncBack> syncBacks = new ArrayList<>();

        String selectQuery = "SELECT distinct Id_Offline,Id_Online,Type,ItemSaved,CustomerNumber FROM SyncBackOffline ORDER BY Id_Offline ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    SyncBack syncBack = new SyncBack();
                    syncBack.setId_Offline(cursor.getString(cursor.getColumnIndex("Id_Offline")));
                    syncBack.setId_Online(cursor.getString(cursor.getColumnIndex("Id_Online")));
                    syncBack.setType(cursor.getString(cursor.getColumnIndex("Type")));
                    syncBack.setItemSaved(cursor.getString(cursor.getColumnIndex("ItemSaved")));
                    syncBack.setCustNumber(cursor.getString(cursor.getColumnIndex("CustomerNumber")));
                    syncBacks.add(syncBack);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return syncBacks;
    }

    public boolean InsertSyncBackOffline(String Id_Offline, String Id_Online, String Type, String ItemSaved, String CustomerNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Id_Offline", Id_Offline);
        contentValues.put("Id_Online", Id_Online);
        contentValues.put("Type", Type);
        contentValues.put("ItemSaved", ItemSaved);
        contentValues.put("CustomerNumber", CustomerNumber);

        db.insert("SyncBackOffline", null, contentValues);
        db.close();
        return true;
    }

    public boolean InsertOra_PriceListOffline(String PR_NAME, String ITEM_CODE
            , String PRICE, String END_USER_PR) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PR_NAME", PR_NAME);
        contentValues.put("ITEM_CODE", ITEM_CODE);
        contentValues.put("PRICE", PRICE);
        contentValues.put("END_USER_PR", END_USER_PR);

        db.insert("Ora_PriceList", null, contentValues);
        db.close();
        return true;
    }


    public String GetOra_PriceListOffline_ItemPrice(String ITEM_CODE,String PR_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT END_USER_PR FROM Ora_PriceList where ITEM_CODE='"+ITEM_CODE+"' and PR_NAME='"+PR_NAME+"'";
        Cursor mcursor = db.rawQuery(selectQuery, null);
        mcursor.moveToFirst();
        try {
            int icount = mcursor.getInt(0);
            if (icount > 0) {

                if (mcursor.moveToFirst()) {
                    return mcursor.getString(mcursor.getColumnIndex("END_USER_PR"));
                }

            } else {
                return "0.0";
            }
        }catch (Exception e)
            {e.printStackTrace();
                return "0.0";
            }
        return "0.0";
    }


    public boolean InsertTransactionSerialsOffline(String Transaction_Header_ID, String Transaction_Items_ID
            , String ItemSerial, String Item_Code) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Transaction_Header_ID", Transaction_Header_ID);
        contentValues.put("Transaction_Items_ID", Transaction_Items_ID);
        contentValues.put("ItemSerial", ItemSerial);
        contentValues.put("Item_Code", Item_Code);

        db.insert("TransactionSerialsOffline", null, contentValues);
        db.close();
        return true;
    }


    public List<Serial> getAllTransactionSerialsOfflineByHeaderID(final String Transaction_Header_ID) {
        List<Serial> serials = new ArrayList<>();

        String selectQuery = "SELECT  * FROM TransactionSerialsOffline where Transaction_Header_ID='" + Transaction_Header_ID + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Serial serial = new Serial();
                    serial.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    serial.setHeaderID(cursor.getString(cursor.getColumnIndex("Transaction_Header_ID")));
                    serial.setItemID(cursor.getString(cursor.getColumnIndex("Transaction_Items_ID")));
                    serial.setSerialNumber(cursor.getString(cursor.getColumnIndex("ItemSerial")));
                    serial.setItemCode(cursor.getString(cursor.getColumnIndex("Item_Code")));

                    serials.add(serial);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return serials;
    }

    public void deleteFromTransactionSerialsOfflineByHeaderId(String Transaction_Header_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from TransactionSerialsOffline where Transaction_Header_ID='" + Transaction_Header_ID + "'");
        db.close();
    }

    public void deleteFromTransactionSerialsOfflineBySerial(String ItemSerial) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from TransactionSerialsOffline where ItemSerial='" + ItemSerial + "'");
        db.close();
    }

    public List<Serial> getAllTransactionSerialsOfflineByHeaderID_ItemCode(final String Transaction_Header_ID, final String Item_Code) {
        List<Serial> serials = new ArrayList<>();

        String selectQuery = "SELECT  * FROM TransactionSerialsOffline where Item_Code='" + Item_Code + "' AND Transaction_Header_ID='" + Transaction_Header_ID + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Serial serial = new Serial();
                    serial.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Id"))));
                    serial.setHeaderID(cursor.getString(cursor.getColumnIndex("Transaction_Header_ID")));
                    serial.setItemID(cursor.getString(cursor.getColumnIndex("Transaction_Items_ID")));
                    serial.setSerialNumber(cursor.getString(cursor.getColumnIndex("ItemSerial")));
                    serial.setItemCode(cursor.getString(cursor.getColumnIndex("Item_Code")));

                    serials.add(serial);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return serials;
    }

    public boolean InsertGetStockSerialsOffline(String SerialNumber, String ItemCode, String Model, String Box_number,String isSelected) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("SerialNumber", SerialNumber);
            contentValues.put("ItemCode", ItemCode);
            contentValues.put("Model", Model);
            contentValues.put("BoxNumber",Box_number);
            contentValues.put("isSelected", isSelected);

            db.insert("GetStockSerialsOffline", null, contentValues);

        }
        return true;
    }

    public void RemoveSelectedStockSerialsOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update GetStockSerialsOffline set isSelected='false'");
        db.close();
    }

    public void deleteAllGetStockSerialsOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from GetStockSerialsOffline");
        db.close();
    }

    public boolean checkifGetStockSerialsOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM GetStockSerialsOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void Update_StockSerialsStatusOffline(String serial,String isSelected ) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update GetStockSerialsOffline set isSelected ='"+isSelected+"' where SerialNumber='"+serial+"'");
        db.close();
    }

    public List<Serial> getAllGetStockSerialsOffline() {
        List<Serial> stockSerials = new ArrayList<>();

        String selectQuery = "SELECT  * FROM GetStockSerialsOffline where isSelected = 'false' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Serial stockSerial = new Serial();
                    stockSerial.setSerialNumber(cursor.getString(cursor.getColumnIndex("SerialNumber")));
                    stockSerial.setItemCode(cursor.getString(cursor.getColumnIndex("ItemCode")));
                    stockSerial.setModel(cursor.getString(cursor.getColumnIndex("Model")));
                    stockSerial.setBox_number(cursor.getString(cursor.getColumnIndex("BoxNumber")));
                    stockSerial.setSelected(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("isSelected"))));

                    stockSerials.add(stockSerial);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return stockSerials;
    }

    public List<Serial> getAllGetStockBoxesOffline() {
        List<Serial> stockSerials = new ArrayList<>();

        String selectQuery = "SELECT  DISTINCT  BoxNumber,ItemCode FROM GetStockSerialsOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Serial stockSerial = new Serial();
                    stockSerial.setItemCode(cursor.getString(cursor.getColumnIndex("ItemCode")));
                    stockSerial.setBox_number(cursor.getString(cursor.getColumnIndex("BoxNumber")));


                    stockSerials.add(stockSerial);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return stockSerials;
    }




    public boolean checkifModelOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM ModelOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean InsertEndUserPriceOffline(String CustomerName, String CustomerNumber, String ItemCode, String Price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CustomerName", CustomerName);
        contentValues.put("CustomerNumber", CustomerNumber);
        contentValues.put("ItemCode", ItemCode);
        contentValues.put("Price", Price);
        db.insert("EndUserPriceOffline", null, contentValues);
        db.close();
        return true;
    }

    public boolean checkifEndUserPriceOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM EndUserPriceOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean InsertSalesReasonOffline(String DeliveryMethod) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Reason", DeliveryMethod);

        db.insert("SalesReasonOffline", null, contentValues);
        db.close();//
        return true;
    }

    public boolean checkifSalesReasonOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM SalesReasonOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllReasonOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from SalesReasonOffline");
        db.close();
    }

    public List<Reason> getAllReasonOffline() {
        List<Reason> reasons = new ArrayList<>();

        String selectQuery = "SELECT  * FROM SalesReasonOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Reason reason = new Reason();
                    reason.setReason(cursor.getString(cursor.getColumnIndex("Reason")));
                    reasons.add(reason);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return reasons;
    }

    public void deleteAllEndUserPriceOfflineOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from EndUserPriceOffline");
        db.close();
    }

    public void deleteOra_PriceListOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Ora_PriceList");
        db.close();
    }


    public boolean InsertDeliveryMethodOffline(String DeliveryMethod) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DeliveryMethod", DeliveryMethod);

        db.insert("DeliveryMethodOffline", null, contentValues);
        db.close();//
        return true;
    }

    public boolean checkifDeliveryMethodOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM DeliveryMethodOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllDeliveryMethodOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from DeliveryMethodOffline");
        db.close();
    }

    public List<DeliveryMethod> getAllDeliveryMethodOffline() {
        List<DeliveryMethod> deliveryMethods = new ArrayList<>();

        String selectQuery = "SELECT  * FROM DeliveryMethodOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    DeliveryMethod deliveryMethod = new DeliveryMethod();
                    deliveryMethod.setName(cursor.getString(cursor.getColumnIndex("DeliveryMethod")));
                    deliveryMethods.add(deliveryMethod);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return deliveryMethods;
    }

    public boolean InsertModelOffline(String Model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Model", Model);

        db.insert("ModelOffline", null, contentValues);
        db.close();//
        return true;
    }

    public void deleteAllModelOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from ModelOffline");
        db.close();
    }

    public List<Model> getAllModelOffline() {
        List<Model> models = new ArrayList<>();

        String selectQuery = "SELECT  * FROM ModelOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Model model = new Model();
                    model.setName(cursor.getString(cursor.getColumnIndex("Model")));
                    models.add(model);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return models;
    }

    public boolean InsertBrandOffline(String Brand) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Brand", Brand);

        db.insert("BrandOffline", null, contentValues);
        db.close();//
        return true;
    }

    public boolean checkifBrandOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM BrandOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllBrandOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from BrandOffline");
        db.close();
    }

    public List<Brand> getAllBrandOffline() {
        List<Brand> brands = new ArrayList<>();

        String selectQuery = "SELECT  * FROM BrandOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Brand brand = new Brand();
                    brand.setName(cursor.getString(cursor.getColumnIndex("Brand")));
                    brands.add(brand);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return brands;
    }

    public boolean InsertCategoryOffline(String Category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Category", Category);

        db.insert("CategoryOffline", null, contentValues);
        db.close();//
        return true;
    }

    public boolean checkifCategoryOfflineEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM CategoryOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllCategoryOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from CategoryOffline");
        db.close();
    }

    public List<Category> getAllCategoryOffline() {
        List<Category> categories = new ArrayList<>();

        String selectQuery = "SELECT  * FROM CategoryOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setName(cursor.getString(cursor.getColumnIndex("Category")));
                    categories.add(category);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return categories;
    }

    public boolean InsertLoginOffline(String UserID, String ServerConfigID, String Currency, String AllowNegativeQty, String GPSInterval
            , String MobileVersion,String Allow_Delivery_Method) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UserID", UserID);
        contentValues.put("ServerConfigID", ServerConfigID);
        contentValues.put("Currency", Currency);
        contentValues.put("AllowNegativeQty", AllowNegativeQty);
        contentValues.put("GPSInterval", GPSInterval);
        contentValues.put("MobileVersion", MobileVersion);
        contentValues.put("Allow_Delivery_Method", Allow_Delivery_Method);

        db.insert("LoginOffline", null, contentValues);
        db.close();//
        return true;
    }

    public boolean checkifLoginOfflineisEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM LoginOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllLoginOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from LoginOffline");
        db.close();
    }

    public String getUserIDfromLoginOffline() {
        SQLiteDatabase db = this.getReadableDatabase();
        String USERID = "";
        Cursor res = db.rawQuery("select * from LoginOffline", null);
        if (res.moveToFirst()) {
            do {
                USERID = res.getString(res.getColumnIndex("UserID"));
            } while (res.moveToNext());
        }
        res.close();
        return USERID;
    }

    public String getServerConfigIDfromLoginOffline() {
        SQLiteDatabase db = this.getReadableDatabase();
        String ServerConfigID = "";
        Cursor res = db.rawQuery("select * from LoginOffline", null);
        if (res.moveToFirst()) {
            do {
                ServerConfigID = res.getString(res.getColumnIndex("ServerConfigID"));
            } while (res.moveToNext());
        }
        res.close();
        return ServerConfigID;
    }

    public boolean InsertPermssionsOffline(String MenuID, String MenuName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("MenuID", MenuID);
        contentValues.put("MenuName", MenuName);
        db.insert("PermssionsOffline", null, contentValues);

        return true;
    }

    public boolean checkifPermssionOfflineisEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM PermssionsOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllPermssionsOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from PermssionsOffline");
        db.close();
    }

    public List<Permission> getAllPermssionsOffline() {
        List<Permission> permissions = new ArrayList<>();

        String selectQuery = "SELECT  * FROM PermssionsOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Permission permission = new Permission();
                    permission.setMenuID(cursor.getString(cursor.getColumnIndex("MenuID")));
                    permission.setMenuName(cursor.getString(cursor.getColumnIndex("MenuName")));

                    permissions.add(permission);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return permissions;
    }

    public boolean checkifCustomersWithVisitOfflineIsEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM CustomersWithVisitOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllCustomersWithVisitOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from CustomersWithVisitOffline");
        db.close();
    }

    public boolean InsertCustomersWithVisitOffline(String Customer_Number, String Customer_Name, String PRICE_LIST, String DueDateFrom, String DueDateTo, String ScheuleID, String VisitID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Customer_Number", Customer_Number);
        contentValues.put("Customer_Name", Customer_Name);
        contentValues.put("PRICE_LIST", PRICE_LIST);
        contentValues.put("DueDateFrom", DueDateFrom);
        contentValues.put("DueDateTo", DueDateTo);
        contentValues.put("ScheuleID", ScheuleID);
        contentValues.put("VisitID", VisitID);

        db.insert("CustomersWithVisitOffline", null, contentValues);
        db.close();//
        return true;
    }

    public List<Customer> getCustomersWithVisitByDueDateFromOffline(final String DueDateFrom) {
        List<Customer> customers = new ArrayList<>();
        String selectQuery;
        if (DueDateFrom.equals(new CustomerTodayFragment().TodayDate())) {
            selectQuery = "SELECT  * FROM CustomersWithVisitOffline where DueDateFrom ='" + DueDateFrom + "'";
        }else if(DueDateFrom.equals(new CustomerMonthFragment().MonthDate())){
            selectQuery = "SELECT  * FROM CustomersWithVisitOffline";
        }else { //tomorrow & week
            selectQuery = "SELECT  * FROM CustomersWithVisitOffline where DueDateFrom <='" + DueDateFrom + "'";
        }
        Log.d("SQL", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Customer customer = new Customer();
                    customer.setNumber(cursor.getString(cursor.getColumnIndex("Customer_Number")));
                    customer.setName(cursor.getString(cursor.getColumnIndex("Customer_Name")));
                    customer.setPrice_list(cursor.getString(cursor.getColumnIndex("PRICE_LIST")));
                    customer.setDueDateFrom(cursor.getString(cursor.getColumnIndex("DueDateFrom")));
                    customer.setDueDateTo(cursor.getString(cursor.getColumnIndex("DueDateTo")));
                    customer.setScheuleID(cursor.getString(cursor.getColumnIndex("ScheuleID")));
                    customer.setVisitID(cursor.getString(cursor.getColumnIndex("VisitID")));
                    customers.add(customer);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return customers;
    }

    public List<Customer> getAllCustomersWithVisitOffline() {
        List<Customer> customers = new ArrayList<>();

        String selectQuery = "SELECT  * FROM CustomersWithVisitOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Customer customer = new Customer();
                    customer.setNumber(cursor.getString(cursor.getColumnIndex("Customer_Number")));
                    customer.setName(cursor.getString(cursor.getColumnIndex("Customer_Name")));
                    customer.setPrice_list(cursor.getString(cursor.getColumnIndex("PRICE_LIST")));
                    customer.setDueDateFrom(cursor.getString(cursor.getColumnIndex("DueDateFrom")));
                    customer.setDueDateTo(cursor.getString(cursor.getColumnIndex("DueDateTo")));
                    customer.setScheuleID(cursor.getString(cursor.getColumnIndex("ScheuleID")));
                    customer.setVisitID(cursor.getString(cursor.getColumnIndex("VisitID")));
                    customers.add(customer);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return customers;
    }


    public boolean InsertPreOrderOffline(String ITEM_CODE, String CAT, String BRAND, String MODEL,
                                         String DESCRIPTION, String BrandImage, String ONHAND, String UnitPrice, String COLOR) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ITEM_CODE", ITEM_CODE);
        contentValues.put("CAT", CAT);
        contentValues.put("BRAND", BRAND);
        contentValues.put("MODEL", MODEL);
        contentValues.put("DESCRIPTION", DESCRIPTION);
        contentValues.put("BrandImage", BrandImage);
        contentValues.put("ONHAND", ONHAND);
        contentValues.put("UnitPrice", UnitPrice);
        contentValues.put("COLOR", COLOR);

        db.insert("PreOrderOffline", null, contentValues);
        db.close();
        return true;
    }

    public boolean InsertPreOrderOffline(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("PreOrderOffline", null, contentValues);
        db.close();
        return true;
    }

    public boolean checkifPreorderOfflineisEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM PreOrderOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllPreOrderOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from PreOrderOffline");
        db.close();
    }

    public String GetUnitPriceForEndUser(final String SKU, final String CustomerNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String result = "";
        String query = "select * from EndUserPriceOffline where ItemCode = '" + SKU + "' and CustomerNumber='" + CustomerNumber + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("Price"));
        }
        cursor.close();
        return result;
    }

    public List<Product> getAllPreOrderOffline(final String CustomerNumber) {
        List<Product> preorders = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM PreOrderOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Product preorder = new Product();
                    preorder.setSKU(cursor.getString(cursor.getColumnIndex("ITEM_CODE")));
                    preorder.setCategory(cursor.getString(cursor.getColumnIndex("CAT")));
                    preorder.setBrand(cursor.getString(cursor.getColumnIndex("BRAND")));
                    preorder.setModel(cursor.getString(cursor.getColumnIndex("MODEL")));
                    preorder.setDescription(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));
                    preorder.setImage(cursor.getString(cursor.getColumnIndex("BrandImage")));
                    preorder.setOnHand(cursor.getString(cursor.getColumnIndex("ONHAND")));
                    preorder.setACC(cursor.getString(cursor.getColumnIndex("ACC")));
                    preorder.setSTATUS(cursor.getString(cursor.getColumnIndex("STATUS")));
                    preorder.setTAX_CODE(cursor.getString(cursor.getColumnIndex("TAX_CODE")));
                    preorder.setTAX_RATE(cursor.getString(cursor.getColumnIndex("TAX_RATE")));
                    preorder.setSEGMENT1(cursor.getString(cursor.getColumnIndex("SEGMENT1")));
                    preorder.setMAIN_CAT(cursor.getString(cursor.getColumnIndex("MAIN_CAT")));
                    preorder.setINVENTORY_ITEM_ID(cursor.getString(cursor.getColumnIndex("INVENTORY_ITEM_ID")));
                    preorder.setCREATION_DATE(cursor.getString(cursor.getColumnIndex("CREATION_DATE")));

                    String UnitPrice = GetUnitPriceForEndUser(preorder.getSKU(), CustomerNumber);
                    if (UnitPrice != "") {
                        preorder.setUnitPrice(Float.valueOf(UnitPrice));
                    }
                    preorder.setColor(cursor.getString(cursor.getColumnIndex("COLOR")));
                    preorders.add(preorder);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // close db connection
        db.close();

        // return notes list
        return preorders;
    }

    public boolean InsertOrderOffline(String ITEM_CODE, String CAT, String BRAND, String MODEL,
                                      String DESCRIPTION, String BrandImage, String ONHAND, String UnitPrice, String COLOR) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ITEM_CODE", ITEM_CODE);
        contentValues.put("CAT", CAT);
        contentValues.put("BRAND", BRAND);
        contentValues.put("MODEL", MODEL);
        contentValues.put("DESCRIPTION", DESCRIPTION);
        contentValues.put("BrandImage", BrandImage);
        contentValues.put("ONHAND", ONHAND);
        contentValues.put("UnitPrice", UnitPrice);
        contentValues.put("COLOR", COLOR);

        db.insert("OrderOffline", null, contentValues);
        db.close();//
        return true;
    }

    public boolean InsertOrderOffline( ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("OrderOffline", null, contentValues);
        db.close();//
        return true;
    }

    public boolean checkifOrderOfflineisEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM OrderOffline";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllOrderOffline() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from OrderOffline");
        db.close();
    }

    public List<Product> getAllOrderOffline(final String CustomerNumber) {
        List<Product> orders = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM OrderOffline";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Product order = new Product();
                    order.setSKU(cursor.getString(cursor.getColumnIndex("ITEM_CODE")));
                    order.setCategory(cursor.getString(cursor.getColumnIndex("CAT")));
                    order.setBrand(cursor.getString(cursor.getColumnIndex("BRAND")));
                    order.setModel(cursor.getString(cursor.getColumnIndex("MODEL")));
                    order.setDescription(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));
                    order.setImage(cursor.getString(cursor.getColumnIndex("BrandImage")));
                    order.setOnHand(cursor.getString(cursor.getColumnIndex("ONHAND")));

                    order.setACC(cursor.getString(cursor.getColumnIndex("ACC")));
                    order.setSTATUS(cursor.getString(cursor.getColumnIndex("STATUS")));
                    order.setTAX_CODE(cursor.getString(cursor.getColumnIndex("TAX_CODE")));
                    order.setTAX_RATE(cursor.getString(cursor.getColumnIndex("TAX_RATE")));
                    order.setSEGMENT1(cursor.getString(cursor.getColumnIndex("SEGMENT1")));
                    order.setMAIN_CAT(cursor.getString(cursor.getColumnIndex("MAIN_CAT")));
                    order.setINVENTORY_ITEM_ID(cursor.getString(cursor.getColumnIndex("INVENTORY_ITEM_ID")));
                    order.setCREATION_DATE(cursor.getString(cursor.getColumnIndex("CREATION_DATE")));

                    String UnitPrice = GetUnitPriceForEndUser(order.getSKU(), CustomerNumber);
                    if (UnitPrice != "") {
                        order.setUnitPrice(Float.valueOf(UnitPrice));
                    }
                    order.setUnitPrice(Float.valueOf(UnitPrice));
                    order.setColor(cursor.getString(cursor.getColumnIndex("COLOR")));
                    orders.add(order);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // close db connection
        db.close();

        // return notes list
        return orders;
    }
}
