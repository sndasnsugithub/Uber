package net.mechanixlab.das.uber.Remote;

import java.util.PriorityQueue;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by User on 12/8/2017.
 */

public class ReterofitClient {

    private  static Retrofit retrofit = null;

    public static Retrofit getClient(String Baseurl)
    {
        if (retrofit==null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Baseurl)
                    .addConverterFactory(ScalarsConverterFactory.create())
            .build();
        }

        return retrofit;
    }
}
