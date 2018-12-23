package com.example.nitya.starein;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
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
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,PermissionsListener {

    FirebaseAuth auth;
    DatabaseReference reference;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,"pk.eyJ1Ijoibml0eWFhcm9yYTcyIiwiYSI6ImNqcHpqeDdjYjAwenU0OG82MDBuZWxtdGIifQ.mMPtHap9shSb-WWzZFyPew");
        setContentView(R.layout.activity_main);

        mapView=findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(25.3254275, 83.0088737))
                .zoom(14)
                .tilt(20)
                .build();

        auth=FirebaseAuth.getInstance();

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

        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);

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

        Toast.makeText(this,"Explanation needed",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted){
            enableLocationComponent();
        }
        else{
            Toast.makeText(this,"Permission Needed",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap=mapboxMap;
        enableLocationComponent();

    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent() {

        if (PermissionsManager.areLocationPermissionsGranted(this)){


            LocationComponentOptions options=
                    LocationComponentOptions.builder(this)
                    .trackingGesturesManagement(true)
                    .accuracyColor(Color.green(1))
                    .build();

            LocationComponent locationComponent=mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(this,options);

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            Location location= locationComponent.getLastKnownLocation();
            Log.i("location",location.getLatitude()+"    "+location.getLongitude());

            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

            FirebaseUser firebaseUser=auth.getCurrentUser();
            String userId=firebaseUser.getUid();

            reference=FirebaseDatabase.getInstance().getReference("Users").child(userId);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    HashMap user=(HashMap) dataSnapshot.getValue();
                    Log.i("userid",user.get("userId").toString());
                    Log.i("username",user.get("username").toString());

                    reference.child("latLng").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Log.i("userblaa",dataSnapshot.toString());

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


        }
        else {

            permissionsManager=new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


}
