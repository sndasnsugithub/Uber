package net.mechanixlab.das.uber.Common;

import android.location.Location;

import net.mechanixlab.das.uber.Remote.FCMClient;
import net.mechanixlab.das.uber.Remote.IFCMService;
import net.mechanixlab.das.uber.Remote.IGoogleAPI;
import net.mechanixlab.das.uber.Remote.ReterofitClient;

import retrofit2.Retrofit;

/**
 * Created by User on 12/8/2017.
 */

public class Common {

    public static String curretToken=" ";


    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInformation";
    public static final String user_rider_tbl = "RidersInformation";
    public static final String picup_request_tbl = "PickUpRequest";
    public static final String token_tbl = "Tokens";

    public static Location mLastLocation=null;


    public static final String baseURL = "https://map.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";

    public static IGoogleAPI getIGoogleAPI()
    {
        return ReterofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }


    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

}
