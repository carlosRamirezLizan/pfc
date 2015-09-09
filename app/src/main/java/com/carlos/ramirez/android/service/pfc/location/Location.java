package com.carlos.ramirez.android.service.pfc.location;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Improve Your City
 * Created by Kamil Zabdyr on 06/05/2014.
 * kamilzabdyr@gmail.com
 * © Rad(+) 2014
 */
public class Location {

    public ArrayList<AddressComponent> address_components = new ArrayList<AddressComponent>();
    public String formatted_address;
    public String[] types;
    public Geometry geometry;

    public Location() {
    }

    public boolean isStreetAddress() {
        if (types == null || types.length == 0) return false;
        boolean is = false;
        for (String type : types) {
            if (type.equals("street_address") || type.equals("route")) {
                is = true;
            }
        }
        return is;
    }

    public LatLng getLocation() {
        if (geometry == null || geometry.location == null || geometry.location.lat == 0)
            return null;
        return new LatLng(geometry.location.lat, geometry.location.lng);
    }


}
