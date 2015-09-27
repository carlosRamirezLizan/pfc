package com.carlos.ramirez.android.service.pfc.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.activity.ClientConnections;
import com.carlos.ramirez.android.service.pfc.adapter.PublishOptionAdapter;
import com.carlos.ramirez.android.service.pfc.adapter.SubscribeOptionAdapter;
import com.carlos.ramirez.android.service.pfc.model.Application;
import com.carlos.ramirez.android.service.pfc.model.PublishOptions;
import com.carlos.ramirez.android.service.pfc.model.SubscribeOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    public static List<PublishOptions> getPublishOptionsList(){
        List<PublishOptions> publishOptions = new ArrayList<>();
        publishOptions.add(new PublishOptions(1,"Localización", "Localización GPS del dispositivo móvil"));
        publishOptions.add(new PublishOptions(2,"Bateria", "Nivel de bateria del dispositivo"));
        publishOptions.add(new PublishOptions(3,"Device Id", "Id del dispositivo"));
        publishOptions.add(new PublishOptions(4,"Modelo", "Modelo del dispositivo"));
        publishOptions.add(new PublishOptions(5,"Torre de red/Wifi", "Nombre de la torre de la red celular móvil a la que está conectado o nombre de la Wifi"));

        return publishOptions;
    }

    public static final String LOCALIZATION_TOPIC = "Localizacion";
    public static final String BATTERY_TOPIC = "Bateria";
    public static final String DEVICE_ID_TOPIC = "Id dispositivo";
    public static final String INTERNET_STATION_TOPIC = "Internet";
    public static final String MODEL_TOPIC = "Modelo";

    public static final String BLOCK_TOPIC = "Bloqueo";
    public static final String RING_TOPIC = "Sonido";
    public static final String VIBRATE_TOPIC = "Vibrar";


    public static List<SubscribeOptions> getSubscribeOptionsList(){
        List<SubscribeOptions> subscribeOptions = new ArrayList<>();
        subscribeOptions.add(new SubscribeOptions(1,"Chat", "Recibir mensajes enviados a este dispositivo"));
        subscribeOptions.add(new SubscribeOptions(2,RING_TOPIC, "Hacer sonar el móvil cuando se haga un publish a este topic desde otro cliente"));
        subscribeOptions.add(new SubscribeOptions(3,"Alarma", "Recibir mensajes de alarma de otros clientes"));
        subscribeOptions.add(new SubscribeOptions(4, BLOCK_TOPIC, "Bloquear el dispositivo si se hace publish a este topic desde otro cliente"));
        subscribeOptions.add(new SubscribeOptions(5, VIBRATE_TOPIC, "Hacer vibrar el dispositivo"));

        return subscribeOptions;
    }

    public static AlertDialog publishOptionsDialog;

    public static AlertDialog showPublishOptionsDialog(Activity context, AdapterView.OnItemClickListener onItemClickListener) {
        List<PublishOptions> publishOptions = getPublishOptionsList();
        final LayoutInflater inflater = context.getLayoutInflater();

        PublishOptionAdapter adapter = new PublishOptionAdapter(context, publishOptions);

        @SuppressLint("InflateParams") View view2 = inflater.inflate(R.layout.dialog_options, null);
        ((TextView) view2.findViewById(R.id.title)).setText(context.getString(R.string.publish_options));
        ListView listView = (ListView) view2.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onItemClickListener);
        view2.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1000));
        publishOptionsDialog = new AlertDialog.Builder(context)
                .setView(view2)
                .create();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(publishOptionsDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        publishOptionsDialog.getWindow().setAttributes(layoutParams);
        return publishOptionsDialog;

    }

    public static AlertDialog subscribeOptionsDialog;

    public static AlertDialog showSubscribeOptionsDialog(Activity context, AdapterView.OnItemClickListener onItemClickListener) {
        List<SubscribeOptions> subscribeOptions = getSubscribeOptionsList();
        final LayoutInflater inflater = context.getLayoutInflater();

        SubscribeOptionAdapter adapter = new SubscribeOptionAdapter(context, subscribeOptions);

        @SuppressLint("InflateParams") View view2 = inflater.inflate(R.layout.dialog_options, null);
        ((TextView) view2.findViewById(R.id.title)).setText(context.getString(R.string.subscribe_options));
        ListView listView = (ListView) view2.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onItemClickListener);
        view2.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1000));
        subscribeOptionsDialog = new AlertDialog.Builder(context)
                .setView(view2)
                .create();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(subscribeOptionsDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        subscribeOptionsDialog.getWindow().setAttributes(layoutParams);
        return subscribeOptionsDialog;
    }


}
