package com.carlos.ramirez.android.service.pfc.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.activity.ClientConnections;

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
            }
        }
    }
}
