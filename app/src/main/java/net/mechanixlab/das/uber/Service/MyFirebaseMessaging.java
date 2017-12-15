package net.mechanixlab.das.uber.Service;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import net.mechanixlab.das.uber.CustommarCall;

/**
 * Created by User on 12/15/2017.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);

        // it send the firebase message with contain lat and lng from Rider app
        //so we need convert message to lnanlan

        LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class
        );

        Intent intent = new Intent(getBaseContext(), CustommarCall.class);
        intent.putExtra("lat",customer_location.latitude);
        intent.putExtra("lng",customer_location.longitude);
        intent.putExtra("customer",remoteMessage.getNotification().getTitle());




        startActivity(intent);


    }
}
