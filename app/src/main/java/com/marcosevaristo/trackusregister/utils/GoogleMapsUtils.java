package com.marcosevaristo.trackusregister.utils;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class GoogleMapsUtils  {

    private static final String URL_SEARCH_ROUTE = "https://maps.googleapis.com/maps/api/directions/json?origin=*srcParam*&destination=*destParam*&sensor=false&units=metric&mode=driving";

    public static String getUrlSearchRoute(String srcParam, String destParam) {
        return URL_SEARCH_ROUTE.replace("*srcParam*", srcParam).replace("*destParam*", destParam);
    }

    public static PolylineOptions desenhaRota(ArrayList<LatLng> points) {
        if (CollectionUtils.isEmpty(points)) {
            return null;
        }
        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : points) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(Color.BLUE);
        polylineOpt.width(12);

        return polylineOpt;
    }

    public static String getLatLngToString(LatLng latLng) {
        return latLng != null ? latLng.latitude + "," + latLng.longitude : "";
    }
}
