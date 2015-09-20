package com.carlos.ramirez.android.service.pfc.location;

import android.content.Context;
import android.location.Address;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.fragment.ConnectionDetails;
import com.carlos.ramirez.android.service.pfc.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import com.carlos.ramirez.android.service.pfc.BuildConfig;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.Locale;

/**
 * IYC
 * Created by Kamil Zabdyr on 18/09/13.
 * © Rad(+) 2013
 */
public class LocationService {

    public static Address currentAddress;

    abstract public interface LocationCompletionHandler {
        void call(boolean success, Address address);
    }

    public static AsyncTask<Void, Void, Address> locationTask;

    public static void getLocationByName(final String name, final LocationCompletionHandler completionHandler) {
        if (BuildConfig.DEBUG) Log.d("com.radmas.iyc.service", "Get location");
        if (locationTask != null && !locationTask.isCancelled()){
            locationTask.cancel(true);
        }
        locationTask = new AsyncTask<Void, Void, Address>() {
            @Override
            protected Address doInBackground(Void... params) {
                Response response = loadLocation(name);

                if (!isCancelled()) {
                    if (response != null && response.isOK()) {
                        Location street_location = response.getStreetLocation();

                        if (street_location != null) {
                            LatLng latLng = street_location.getLocation();
                            if (latLng != null) {
                                Address address = new Address(Locale.getDefault());
                                address.setAddressLine(0, street_location.formatted_address);
                                address.setLatitude(latLng.latitude);
                                address.setLongitude(latLng.longitude);
                                return address;
                            }
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Address address) {
                if (!isCancelled()) {
                    if (completionHandler != null) {
                        completionHandler.call(address != null, address);
                    }
                }
                super.onPostExecute(address);
            }
        };
        locationTask.execute();
    }

    public static void getLocation(final LatLng latLng, final LocationCompletionHandler completionHandler) {
        if (BuildConfig.DEBUG) {
            Log.d("com.radmas.iyc.service", "Get location");
        }
        if (locationTask != null && !locationTask.isCancelled()) {
            locationTask.cancel(true);
        }
        locationTask = new AsyncTask<Void, Void, Address>() {
            @Override
            protected Address doInBackground(Void... params) {
                Address address = null;
                Response response = loadLocation(latLng);

                if (!isCancelled()) {
                    if (response != null && response.isOK()) {
                        if (response.getLocations().size() > 0) {
                            Location street_location = response.getLocations().get(0);

                            address = new Address(Locale.getDefault());
                            address.setAddressLine(0, street_location.formatted_address);
                            address.setLatitude(latLng.latitude);
                            address.setLongitude(latLng.longitude);
                        }
                    }
                }

                if (address == null) {
                    address = new Address(Locale.getDefault());
                    address.setAddressLine(0, latLng.latitude + ", " + latLng.longitude);
                    address.setLatitude(latLng.latitude);
                    address.setLongitude(latLng.longitude);
                }

                return address;
            }

            @Override
            protected void onPostExecute(Address address) {
                if (!isCancelled()) {
                    if (completionHandler != null) {
                        completionHandler.call(address != null, address);
                    }
                }
                super.onPostExecute(address);
            }
        };
        locationTask.execute();

    }

    public static Response loadLocation(LatLng latLng) {
        try {
            HttpRequest get = HttpRequest.get("http://maps.google.com/maps/api/geocode/json?address=" + latLng.latitude + "," + latLng.longitude + "&sensor=true&language=es").followRedirects(true);
            get.connectTimeout(5000);
            get.readTimeout(5000);

            if (get.ok()) {
                Gson gson = new Gson();
                return gson.fromJson(get.body(), Response.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response loadLocation(String name) {
        try {
            String query = URLEncoder.encode(name, "utf-8");
            HttpRequest get = HttpRequest.get("http://maps.google.com/maps/api/geocode/json?address=" + query + "&sensor=true&language=es").followRedirects(true);
            if (get.ok()) {
                Gson gson = new Gson();
                return gson.fromJson(get.body(), Response.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Se llama cuando el usuario se desplaza a través del mapa.
     * Actualiza el mapa para mostra la nueva localización señalizada por el usuario.
     *
     * @param latLng la nueva posición señalizada por el usuario.
     */
    private static Address getCurrentLocation(LatLng latLng)
    {
        if (latLng != null && latLng.latitude != 0.0) {

            LocationService.getLocation(latLng, new LocationService.LocationCompletionHandler() {
                @Override
                public void call(boolean success, Address address) {
                    if (success) {
                        currentAddress = address;
                    }
                }
            });
        }
        return currentAddress;
    }

    public static Address getUserLocation(Context context){
        if (ConnectionDetails.location==null) {
            ConnectionDetails.tracker = new GPSTracker(context);
        }
        if (ConnectionDetails.tracker.canGetLocation()) {
            ConnectionDetails.location = ConnectionDetails.tracker.getLocation();
            if (ConnectionDetails.location!=null) {
                LatLng latLng = new LatLng(ConnectionDetails.location.getLatitude(), ConnectionDetails.location.getLongitude());
                currentAddress = getCurrentLocation(latLng);
            }
        }
        return currentAddress;
    }
}
