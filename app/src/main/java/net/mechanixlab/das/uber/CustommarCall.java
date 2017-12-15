package net.mechanixlab.das.uber;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.messaging.RemoteMessage;

import net.mechanixlab.das.uber.Common.Common;
import net.mechanixlab.das.uber.Model.FCMResponse;
import net.mechanixlab.das.uber.Model.Sender;
import net.mechanixlab.das.uber.Model.Token;
import net.mechanixlab.das.uber.Remote.IFCMService;
import net.mechanixlab.das.uber.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.icu.lang.UCharacter.getDirection;

public class CustommarCall extends AppCompatActivity {

    TextView txtTime, txtAdrees, txtDistance;

    Button btnCancel,btnAccept;

    MediaPlayer mediaPlayer;

    IGoogleAPI mService;
    IFCMService mFcmService;

    String customarID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommar_call);

        mFcmService = Common.getFCMService();


        //intit view

        txtAdrees = findViewById(R.id.txtAdress);
        txtDistance = findViewById(R.id.txtDistance);
        txtTime = findViewById(R.id.txtTime);

        btnAccept = findViewById(R.id.btnAccpts);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(customarID))
                {
                    cancelBooking(customarID);
                }

            }
        });


        mService = Common.getIGoogleAPI();

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null) {
            double lat = getIntent().getDoubleExtra("lat", -1.0);
            double lan = getIntent().getDoubleExtra("lng", -1.0);
            customarID= getIntent().getStringExtra("customer");


            //Just copy and paste

            getDirection(lat, lan);

        }

    }

    private void cancelBooking(String customarID) {
        Token token = new Token(customarID);
        Notification notification = new Notification("Notice","Drivers has cancel your request ");

        Sender sender = new Sender(token.getToken(),notification);
        mFcmService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1)
                        {
                            Toast.makeText(CustommarCall.this," Cancelled" ,+Toast.LENGTH_LONG).show();
                       finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });


    }

    private void getDirection(double lat, double lan) {

        //currentPossiton = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        String requestAPI = null;

        try {

            requestAPI = "https://maps.googleapis.com/maps/api/directions/json"
                    + "mode=driving&" + "transit_routing_preference=less_driving" +
                    "origin=" + Common.mLastLocation.getLatitude() + " ," + Common.mLastLocation.getLongitude() + "&" +
                    "destination=" + lat + " " + lan + "&" + "key=" + getResources().getString(R.string.google_direction_api);


            mService.getPath(requestAPI)
                    .enqueue(new Callback<String>() {
                                 @Override
                                 public void onResponse(Call<String> call, Response<String> response) {


                                     try {

                                         JSONObject jsonObject = new JSONObject(response.body().toString());

                                         JSONArray routes = jsonObject.getJSONArray("routes");


                                         //after get routes th e fisrt commit


                                         JSONObject object = routes.getJSONObject(0);
                                         JSONArray legs = object.getJSONArray("legs");
                                         JSONObject legsobject = legs.getJSONObject(0);

                                         JSONObject distace= legsobject.getJSONObject("distance");
                                         txtDistance.setText(distace.getString("text"));

                                         JSONObject time= legsobject.getJSONObject("duration");
                                         txtTime.setText(time.getString("text"));

                                         String adress= legsobject.getString("end_address");
                                         txtTime.setText(adress);




                                     } catch (JSONException e) {

                                         e.printStackTrace();
                                     }

                                 }

                                 @Override
                                 public void onFailure(Call<String> call, Throwable t) {

                                     Toast.makeText(CustommarCall.this," "+t.getMessage(),Toast.LENGTH_SHORT).show();


                                 }


                             }
                    );
        } finally {

        }
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        mediaPlayer.start();

    }
}