package settings;

/**
 * Created by devdeeds.com on 1/10/17.
 */

public class Constants {

    public static int LOCATION_INTERVAL=2 * 60 * 1000; //2m
    public static int FASTEST_LOCATION_INTERVAL=1 * 60 * 1000; //2m
    public static int MaxWaitTime=4 * 60 * 1000; //4 m



    public static int getLocationInterval() {
        return LOCATION_INTERVAL;
    }

    public static void setLocationInterval(int locationInterval) {
        LOCATION_INTERVAL = locationInterval;
    }

    public static int getFastestLocationInterval() {
        return FASTEST_LOCATION_INTERVAL;
    }

    public static void setFastestLocationInterval(int fastestLocationInterval) {
        FASTEST_LOCATION_INTERVAL = fastestLocationInterval;
    }
}
