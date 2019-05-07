package com.example.nitya.starein;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,PermissionsListener {

    FirebaseAuth auth;
    DatabaseReference reference;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    Location originLocation;
    LocationEngine locationEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoibml0eWFhcm9yYTcyIiwiYSI6ImNqcHpqeDdjYjAwenU0OG82MDBuZWxtdGIifQ.mMPtHap9shSb-WWzZFyPew");
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        /*CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(25.3254275, 83.0088737))
                .zoom(14)
                .tilt(20)
                .build();*/

        auth = FirebaseAuth.getInstance();

        mapView.getMapAsync(this);
            /*@Override
            public void onMapReady(MapboxMap mapboxMap) {

                mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(25.3254275,83.0088737))
                    .title("My Location")
                    .snippet("bro"));


                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 200);*/


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

        Toast.makeText(this, "Explanation needed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {
            initializeLocationEngine();
            enableLocationComponent();
        } else {
            Toast.makeText(this, "Permission Needed", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        locationEnable();

    }

    void locationEnable() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            enableLocationComponent();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent() {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {


            Location lastLocation = locationEngine.getLastLocation();

            FirebaseUser firebaseUser = auth.getCurrentUser();
            String userId = firebaseUser.getUid();

            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    HashMap user = (HashMap) dataSnapshot.getValue();
                    Log.i("userid", user.get("userId").toString());
                    Log.i("username", user.get("username").toString());

                    reference.child("latLng").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Log.i("userblaa", dataSnapshot.toString());
                            if(lastLocation!=null){
                                originLocation=lastLocation;
                            }
                            Log.i("Location",String.valueOf(originLocation.getLatitude())+originLocation.getLongitude());


                            LatLng latLng = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());

                            IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                            Bitmap iconDrawable = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
                            iconDrawable=Bitmap.createScaledBitmap(iconDrawable, 70, 70, true);
                            com.mapbox.mapboxsdk.annotations.Icon icon=iconFactory.fromBitmap(iconDrawable);

                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()))
                                    .setIcon(icon)
                                    );

                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(originLocation.getLatitude(), originLocation.getLongitude()), 12));

                            reference.child("latLng").setValue(latLng);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //Log.i("userlatlng",user.getLatLng().getLatitude()+" "+user.getLatLng().getLongitude());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference otherUsers = FirebaseDatabase.getInstance().getReference("Users");

            otherUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap users = (HashMap) dataSnapshot.getValue();

                    Set<String> keys = users.keySet();

                    for (String key : keys) {

                        if (!key.equals(userId)) {

                            otherUsers.child(key).child("latLng").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    HashMap hm = (HashMap) dataSnapshot.getValue();

                                    String lat = String.valueOf( hm.get("latitude"));
                                    String lng = String.valueOf( hm.get("longitude"));
                                    if(distance(Double.valueOf(lat),Double.valueOf(lng),originLocation.getLatitude(),originLocation.getLongitude())<=5) {


                                        mapboxMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                                                .title(key));
                                    }



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {

                    // Show a toast with the title of the selected marker
                    //Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("me", userId);
                    intent.putExtra("other", marker.getTitle());
                    startActivity(intent);


                    return true;

                }

            });

        } else {

            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.BALANCED_POWER_ACCURACY);
        locationEngine.addLocationEngineListener(new LocationEngineListener() {

            @Override
            public void onConnected() {
                locationEngine.requestLocationUpdates();
            }

            @Override
            public void onLocationChanged(Location location) {
                originLocation = location;
                Log.i("Location",String.valueOf(location.getLatitude())+location.getLongitude());
                //if (!mCameraPositionSet) {
                  //  setCameraPosition(location);
                  //  mCameraPositionSet = true;
          //  }

        }
    });

        locationEngine.activate();
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
