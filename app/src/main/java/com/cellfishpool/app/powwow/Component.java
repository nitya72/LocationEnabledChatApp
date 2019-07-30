package com.cellfishpool.app.powwow;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Component {
    String userId;
    String username;
    LatLng latLng;

    Component(String userId,String username,LatLng latLng){
        this.latLng=latLng;
        this.userId=userId;
        this.username=username;
    }

    public String getUserId(){
        return userId;
    }

    public String getUsername(){
        return username;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
