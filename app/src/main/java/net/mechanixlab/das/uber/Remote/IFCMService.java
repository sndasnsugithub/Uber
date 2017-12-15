package net.mechanixlab.das.uber.Remote;

import net.mechanixlab.das.uber.Model.FCMResponse;
import net.mechanixlab.das.uber.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by User on 12/15/2017.
 */

public interface IFCMService {

    @Headers({
            "Content_Type:application/json",
            "Authorization:key=AAAAjPBb0A0:APA91bEmzlaqlzitan0yfllN0hjIIGNPqVj4ugPsNcQb0JeZqjTVxSPIRPKxR8y2Kdi5TnLUnUpBqi-U4sk71Fwv6yHI_5HlTwFHZ8dgBAXY99WI8mfh2N3lX0xSK_xiswHNhvUn895f"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
