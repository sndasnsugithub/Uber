package net.mechanixlab.das.uber;


import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.mechanixlab.das.uber.Common.Common;
import net.mechanixlab.das.uber.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends FragmentActivity implements OnMapReadyCallback
        ,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 1000;
    private static final int REQUEST_LOCATION = 2;

    // private static final int ACCESS_FINE_LOCATION=100;


    //
    // public GoogleApiClient.Builder mGoogleApiClient;
    private LocationRequest mlocationRequest;
    private GoogleApiClient mGoogleApiClient;
    //private  Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient,this);




    public static int UPDATE_INTERVAL = 5000;
    public static int FATEST_INTERVAL = 3000;
    public static int DISPLACEMENT = 10;


    DatabaseReference drivers;
    GeoFire geoFire;

    Marker mCurrent;
    MaterialAnimatedSwitch location_swtich;
    SupportMapFragment mapFragment;


    //running image

    private List<LatLng> polyLineList;
    private Marker curretMarker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosstion,currentPossiton;
    private int index, next;
    private Button btnGo;
    private PlaceAutocompleteFragment places;
    private String destinationline;
    private PolygonOptions polyglineOptions, backpolygonOptions;
    private Polyline backpolyline, grayPolyline;
    private IGoogleAPI iGoogleAPIservices;

    Runnable drawPathRunbale = new Runnable() {
        @Override
        public void run() {
            if (index<polyLineList.size()-1)
            {
                index++;
                next=index+1;

            }
            if (index<polyLineList.size()-1)

            {
                startPosition =polyLineList.get(index);
                endPosstion = polyLineList.get(next);

            }

            final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v=valueAnimator.getAnimatedFraction();
                    lng = v*endPosstion.longitude+(1-v)*startPosition.longitude;
                    lat = v*endPosstion.latitude+(1-v)*startPosition.latitude;
                    LatLng newPos = new LatLng(lat,lng);
                    curretMarker.setPosition(newPos);
                    curretMarker.setAnchor(0.5f,0.5f);
                    curretMarker.setRotation(getBearing(startPosition,newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(newPos)
                            .zoom(15.5f)
                            .build())

                    );
                }
            });


            valueAnimator.start();
            handler.postDelayed(this,3000);

        }
    };

    private float getBearing(LatLng startPosition, LatLng newPos) {
        double lat = Math.abs(startPosition.latitude-endPosstion.latitude);
        double lng = Math.abs(startPosition.longitude-endPosstion.longitude);
        if (startPosition.latitude<endPosstion.latitude && startPosition.longitude<endPosstion.longitude)
            return  (float) (Math.toDegrees(Math.atan(lng/lat)));


        else if (startPosition.latitude >= endPosstion.latitude && startPosition.longitude<endPosstion.longitude)
            return  (float) (90-Math.toDegrees(Math.atan(lng/lat)+90));



        else if (startPosition.latitude >= endPosstion.latitude && startPosition.longitude>=endPosstion.longitude)
            return (float) (Math.toDegrees(Math.atan(lng/lat)+180));

        else if (startPosition.latitude < endPosstion.latitude && startPosition.longitude>=endPosstion.longitude)
            return (float) (90-Math.toDegrees(Math.atan(lng/lat)+270));

        return -1;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //intial view




        location_swtich = findViewById(R.id.locationswtich);
        location_swtich.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {

                if (isOnline) {
                    startLocationUpdates();
                    disPlayLocation();

                    Snackbar.make(mapFragment.getView(), "Yor are Online ", Snackbar.LENGTH_SHORT).show();
                } else {

                    stopLocationUpdate();
                    mCurrent.remove();
                    Snackbar.make(mapFragment.getView(), "Yor are Offline ", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        drivers = FirebaseDatabase.getInstance().getReference("Drivers");
        geoFire = new GeoFire(drivers);
        setUpLocation();
    }




    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);

    }


    private void getDirection() {
        //m

        currentPossiton = new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude());
        String requestAPI = null;

        try {

            requestAPI = "https://maps.googleapis.com/maps/api/directions/json"
                    + "mode=driving&" + "transit_routing_preference=less_driving" +
                    "origin=" + currentPossiton.latitude + " ," + currentPossiton.longitude + "&" +
                    "destination=" + destinationline + "&" + "key=" + getResources().getString(R.string.google_direction_api);


            iGoogleAPIservices.getPath(requestAPI)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {




                            try {

                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
                                }


                                //adjust boundary

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList)
                                    builder.include(latLng);
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);

                                mMap.animateCamera(mCameraUpdate);
                                polyglineOptions = new PolygonOptions();
                                polyglineOptions.fillColor(Color.GRAY);
                                polyglineOptions.strokeWidth(5);
                                //polyglineOptions.startCap(new SquareCap());
                                // polyglineOptions.endCup(new SquareCap());
                                polyglineOptions.strokeJointType(JointType.ROUND);
                                polyglineOptions.addAll(polyLineList);
                                //   grayPolyline=mMap.addPolyline(polyglineOptions);


                                backpolygonOptions = new PolygonOptions();
                                backpolygonOptions.fillColor(Color.GRAY);
                                backpolygonOptions.strokeWidth(5);
                                //  backpolygonOptions.startCap(new SquareCap());
                                //  backpolygonOptions.endCup(new SquareCap());
                                backpolygonOptions.strokeJointType(JointType.ROUND);
                                backpolygonOptions.addAll(polyLineList);
                                //  grayPolyline=mMap.addPolyline(backpolygonOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1))
                                        .title("Pick Up location")
                                );


                                ValueAnimator polylineAmimator = ValueAnimator.ofInt(0, 100);
                                polylineAmimator.setDuration(2000);
                                polylineAmimator.setInterpolator(new LinearInterpolator());
                                polylineAmimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        List<LatLng> points = grayPolyline.getPoints();
                                        int presentvalue = (int) ValueAnimator.getFrameDelay();
                                        int size = points.size();
                                        int newPoints = (int) (size * (presentvalue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        backpolyline.setPoints(p);
                                        curretMarker = mMap.addMarker(new MarkerOptions().position(currentPossiton)
                                                .flat(true)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.imageicon)));

                                        handler = new Handler();
                                        index = -1;
                                        next = -1;
                                        handler.postDelayed(drawPathRunbale, 3000);


                                    }
                                });

                                polylineAmimator.start();


                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }


                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(WelcomeActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        } catch (Exception e) {

            e.printStackTrace();
        }


    }



    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationReuest();

                        if (location_swtich.isChecked()) {
                            disPlayLocation();
                        }
                    }
                }
        }

    }

    private void setUpLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION

            },MY_PERMISSION_REQUEST_CODE);

        }
        else
        {
            if (checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationReuest();

                if (location_swtich.isChecked())
                {
                    disPlayLocation();
                }
            }
        }

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build()
//
//                .addApi(Places.GEO_DATA_API)
//                .addConnectionCallbacks(this);


                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }


    private void createLocationReuest() {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FATEST_INTERVAL);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(DISPLACEMENT);


    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void stopLocationUpdate() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;

        }
        else {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    private void disPlayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;

        }


         Common.mLastLocation =  LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //    mLastLocation = LocationServices.getFusedLocationProviderClient(mGoogleApiClient,this);


        if (Common.mLastLocation != null) {
            if (location_swtich.isChecked()) {
                final double latitude = Common.mLastLocation.getLatitude();
                final double longitute =Common.mLastLocation.getLongitude();


                //update to firebas34

                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitute)
                        , new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                                //add Maker
                                if (mCurrent != null) {
                                    mCurrent.remove();
                                    mCurrent = mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.imageicon))
                                            .position(new LatLng(latitude, longitute))
                                            .title("You"));


                                    //move camera to this posstion means location icon

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitute), 15.0f));

                                    //Drew animation rotate marker
                                    rotateMaker(mCurrent, -360, mMap);
                                }

                            }
                        });

            }
        }
        else
        {
            Log.d("ERROR","Cannot get Your connetion");
        }


    }

    private void rotateMaker(final Marker mCurrent, final float i, GoogleMap mMap) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = mCurrent.getRotation();
        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis()-start;
                float t = interpolator.getInterpolation((float)elapsed/duration);
                float rot = t*i+(1-t)*startRotation;
                mCurrent.setRotation(-rot > 180?20/2:rot);

                if (t<1.0)
                {
                    handler.postDelayed(this,16);
                }

            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    @Override
    public void onLocationChanged(Location location) {

       Common.mLastLocation = location;
        disPlayLocation();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        disPlayLocation();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
            mGoogleApiClient.disconnect();
        }
    }
}
