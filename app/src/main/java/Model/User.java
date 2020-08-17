package Model;

public class User {
    public static String UserID;
    public static String Username;
    public String Password;
    public static String ServerConfigID;
    public static String Currency;
    public static String AllowNegativeQty;
    public static String DeviceID;
    public static String MobileVersion;
    public static int GPSInterval;
    public static int FASTEST_LOCATION_INTERVAL;
    public static int MaxWaitTime;

    public static String GPS_EndTime;
    public static String GPS_StartTime;
    public static String GPS_Distance;

    public static boolean isAllow_Delivery_Method() {
        return Allow_Delivery_Method;
    }

    public static void setAllow_Delivery_Method(boolean allow_Delivery_Method) {
        Allow_Delivery_Method = allow_Delivery_Method;
    }

    public static boolean Allow_Delivery_Method;

    public static boolean isAllow_StockTaking() {
        return Allow_StockTaking;
    }

    public static void setAllow_StockTaking(boolean allow_StockTaking) {
        Allow_StockTaking = allow_StockTaking;
    }

    public static boolean Allow_StockTaking;


    public static String FTP_Path;
    public static String Images_Path;

    public static String getFTP_Path() {
        return FTP_Path;
    }

    public static void setFTP_Path(String FTP_Path) {
        User.FTP_Path = FTP_Path;
    }

    public static String getImages_Path() {
        return Images_Path;
    }

    public static void setImages_Path(String images_Path) {
        Images_Path = images_Path;
    }


    public static int getFastestLocationInterval() {
        return FASTEST_LOCATION_INTERVAL;
    }

    public static void setFastestLocationInterval(int fastestLocationInterval) {
        FASTEST_LOCATION_INTERVAL = fastestLocationInterval;
    }

    public static int getMaxWaitTime() {
        return MaxWaitTime;
    }

    public static void setMaxWaitTime(int maxWaitTime) {
        MaxWaitTime = maxWaitTime;
    }



    public static String getCurrency() {
        return Currency;
    }

    public static void setCurrency(String currency) {
        Currency = currency;
    }

    public static String getAllowNegativeQty() {
        return AllowNegativeQty;
    }

    public static void setAllowNegativeQty(String allowNegativeQty) {
        AllowNegativeQty = allowNegativeQty;
    }

    public static String getUserID() {
        return UserID;
    }

    public static void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getServerConfigID() {
        return ServerConfigID;
    }

    public void setServerConfigID(String serverconfigID) {
        ServerConfigID = serverconfigID;
    }

    public static String getMobileVersion() {
        return MobileVersion;
    }

    public static void setMobileVersion(String mobileVersion) {
        MobileVersion = mobileVersion;
    }

    public static int getGPSInterval() {
        return GPSInterval;
    }

    public static void setGPSInterval(int GPSInterval) {
        User.GPSInterval = GPSInterval;
    }
}
