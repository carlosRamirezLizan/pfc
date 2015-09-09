package com.carlos.ramirez.android.service.pfc.location;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Improve Your City
 * Created by Kamil Zabdyr on 06/05/2014.
 * kamilzabdyr@gmail.com
 * © Rad(+) 2014
 */
public class Response {

    @SerializedName("results")
    public ArrayList<Location> locations = new ArrayList<Location>();
    public String status;

    public Response() {
    }

    public List<Location> getLocations(){
        return locations;
    }

    public Location getStreetLocation() {
        if (locations == null || locations.size() == 0) return null;
        for (Location location : locations) {
            if (location.isStreetAddress()) return location;
        }
        return null;
    }

    public boolean isOK() {
        return status.equals("OK");
    }
}
