package com.carlos.ramirez.android.service.pfc.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.activity.ClientConnections;
import com.carlos.ramirez.android.service.pfc.model.Application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by carlos on 9/9/15.
 */
public class Utils {

    public static void setUpToolBar(Activity activity){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        ColorDrawable actionBarColor = new ColorDrawable(activity.getResources().getColor(R.color.light_purple));
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(activity.getResources().getDimension(R.dimen.toolbar_elevation));
        }
        toolbar.setBackgroundDrawable(actionBarColor);
                ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();

        if (actionBar != null) {
            if(!(activity instanceof ClientConnections)) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                actionBar.setIcon(R.drawable.icon_toolbar);
            }
        }
    }

    public static Application loadServerPreferencesFromJson(Context c)
    {
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            String raw = file_get_contents(c, R.raw.app);
            return gson.fromJson(raw, Application.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String file_get_contents(Context c, int resource)
    {
        InputStream in = c.getResources().openRawResource(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder s = new StringBuilder();
        String l;
        try {
            while ((l = reader.readLine()) != null) {
                s.append(l);
            }
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }
}
