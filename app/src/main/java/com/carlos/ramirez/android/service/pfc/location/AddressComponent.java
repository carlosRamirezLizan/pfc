package com.carlos.ramirez.android.service.pfc.location;

/**
 * Improve Your City
 * Created by Kamil Zabdyr on 06/05/2014.
 * kamilzabdyr@gmail.com
 * © Rad(+) 2014
 */
public class AddressComponent {

    public String long_name;
    public String short_name;
    public String[] types;

    public AddressComponent() {
    }

    public boolean isStreetNumber() {
        if (types == null || types.length == 0) return false;
        boolean is = false;
        for (String type : types) {
            if (type.equals("street_number")) {
                is = true;
            }
        }
        return is;
    }

    public boolean isRoute() {
        if (types == null || types.length == 0) return false;
        boolean is = false;
        for (String type : types) {
            if (type.equals("route")) {
                is = true;
            }
        }
        return is;
    }

    public boolean isLocality() {
        if (types == null || types.length == 0) return false;
        boolean is = false;
        for (String type : types) {
            if (type.equals("locality")) {
                is = true;
            }
        }
        return is;
    }


}
