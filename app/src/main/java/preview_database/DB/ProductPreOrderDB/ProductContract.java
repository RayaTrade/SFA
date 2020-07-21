package preview_database.DB.ProductPreOrderDB;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {
    public static final String CONTENT_AUTHORITY = "com.raya.sfa";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "Product_PreOrder";

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String TABLE_NAME = "PreOrders";
        public static final String COLUMN_PRODUCT_SKU = "SKU";
        public static final String COLUMN_PRODUCT_category = "Category";
        public static final String COLUMN_PRODUCT_image_PATH = "image_path";
        public static final String COLUMN_PRODUCT_product_brand = "Brand";
        public static final String COLUMN_PRODUCT_product_model = "Model";
        public static final String COLUMN_PRODUCT_product_description = "Description";
        public static final String COLUMN_PRODUCT_product_QTY = "Quantity";
        public static final String COLUMN_PRODUCT_product_Customer_Number = "Customer_Number";
        public static final String COLUMN_PRODUCT_product_OnHand = "OnHand";
        public static final String COLUMN_PRODUCT_product_UnitPrice = "UnitPrice";
        public static final String COLUMN_PRODUCT_product_Total = "Total";
        public static final String COLUMN_PRODUCT_product_VisitDate = "Visit_Date";
        public static final String COLUMN_PRODUCT_product_Subinventory = "Subinventory";

        public static Uri buildPRODUCTUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] PRODUCT_COLUMNS = {
                COLUMN_PRODUCT_SKU,
                COLUMN_PRODUCT_category,
                COLUMN_PRODUCT_image_PATH,
                COLUMN_PRODUCT_product_brand,
                COLUMN_PRODUCT_product_model,
                COLUMN_PRODUCT_product_description,
                COLUMN_PRODUCT_product_QTY,
                COLUMN_PRODUCT_product_Customer_Number,
                COLUMN_PRODUCT_product_OnHand,
                COLUMN_PRODUCT_product_UnitPrice,
                COLUMN_PRODUCT_product_Total,
                COLUMN_PRODUCT_product_VisitDate,
                COLUMN_PRODUCT_product_Subinventory
        };

        public static final int COL_PRODUCT_SKU = 0;
        public static final int COL_PRODUCT_category = 1;
        public static final int COL_PRODUCT_image_PATH = 2;
        public static final int COL_PRODUCT_product_brand = 3;
        public static final int COL_PRODUCT_product_model = 4;
        public static final int COL_PRODUCT_product_description = 5;
        public static final int COL_PRODUCT_product_QTY = 6;
        public static final int COL_PRODUCT_product_customer_number = 7;
        public static final int COL_PRODUCT_product_onhand = 8;
        public static final int COL_PRODUCT_product_UnitPrice = 9;
        public static final int COL_PRODUCT_product_Total = 10;
        public static final int COL_PRODUCT_product_VisitDate = 11;
        public static final int COL_PRODUCT_product_Subinventory = 12;
    }
}
