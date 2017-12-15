package net.mechanixlab.das.uber.Common;

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


    public static final String baseURL = "https://www.map.googleapis.com.";

    public static IGoogleAPI getIGoogleAPI()
    {
        return ReterofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

}
