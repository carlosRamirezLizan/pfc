/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.carlos.ramirez.android.service.pfc.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.activity.ClientConnections;
import com.carlos.ramirez.android.service.pfc.activity.NewConnection;
import com.carlos.ramirez.android.service.pfc.adapter.PublishOptionAdapter;
import com.carlos.ramirez.android.service.pfc.event.LogginChanged;
import com.carlos.ramirez.android.service.pfc.fragment.ConnectionDetails;
import com.carlos.ramirez.android.service.pfc.fragment.PublishFragment;
import com.carlos.ramirez.android.service.pfc.location.LocationService;
import com.carlos.ramirez.android.service.pfc.model.Connection;
import com.carlos.ramirez.android.service.pfc.model.Connections;
import com.carlos.ramirez.android.service.pfc.model.PublishOptions;
import com.carlos.ramirez.android.service.pfc.model.SubscribeOptions;
import com.carlos.ramirez.android.service.pfc.util.ActivityConstants;
import com.carlos.ramirez.android.service.pfc.util.Notify;
import com.carlos.ramirez.android.service.pfc.util.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.LogManager;

import de.greenrobot.event.EventBus;

/**
 * Deals with actions performed in the ClientConnections activity
 *
 */
public class Listener implements View.OnClickListener, MenuItem.OnMenuItemClickListener, View.OnLongClickListener {

  private String clientHandle = null;
  private final int SLEEP_TIME = 60000; //one minute
  private ConnectionDetails connectionDetails = null;
  private ClientConnections clientConnections = null;
  /** {@link Context} used to load and format strings **/
  private Context context = null;

  /** Whether Paho is logging is enabled**/
  public static boolean logging = false;


  /**
   * Constructs a listener object for use with {@link ConnectionDetails} activity and
   * associated fragments.
   * @param connectionDetails The instance of ConnectionDetails
   * @param clientHandle The handle to the client that the actions are to be performed on
   */
  public Listener(ConnectionDetails connectionDetails, String clientHandle)
  {
    this.connectionDetails = connectionDetails;
    this.clientHandle = clientHandle;
    context = connectionDetails;

  }

  /**
   * Constructs a listener object for use with {@link ClientConnections} activity.
   * @param clientConnections The instance of {@link ClientConnections}
   */
  public Listener(ClientConnections clientConnections) {
    this.clientConnections = clientConnections;
    context = clientConnections;
  }

  /**
   * Perform the needed action required based on the button that
   * the user has clicked.
   * 
   * @param item The menu item that was clicked
   * @return If there is anymore processing to be done
   * 
   */
  @Override
  public void onClick(View item) {

    int id = item.getId();

    switch (id)
    {
      case R.id.publish:
        publish(null);
        break;
      case R.id.subscribe:
        subscribe();
        break;
      case R.id.newConnection:
        createAndConnect();
        break;
      case R.id.startLogging:
        enablePahoLogging();
        break;
      case R.id.endLogging:
        disablePahoLogging();
        break;
      default:
        break;
    }
  }

  @Override
  public boolean onLongClick(View view) {
    int id = view.getId();

    switch (id) {
      case R.id.publish:
        showPublishOptionsDialog();
        break;
      case R.id.subscribe:
        showSubscribeOptionsDialog();
      default:
        break;
    }
    return false;
  }

  @Override
  public boolean onMenuItemClick(MenuItem menuItem) {
    int id = menuItem.getItemId();

    switch (id)
    {
      case R.id.connectMenuOption:
        reconnect();
        break;
      case R.id.disconnect:
        killAllThreads();
        disconnect();
        break;
      default:
        break;
    }

    return false;
  }

  private void showPublishOptionsDialog(){

    Utils.showPublishOptionsDialog((Activity) context, new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(Utils.publishOptionsDialog.isShowing()){
          Utils.publishOptionsDialog.dismiss();
        }
        List<PublishOptions> publishOptions = Utils.getPublishOptionsList();
        initializePublishThread(publishOptions.get(position).getId());
      }
    }).show();
  }

  private void showSubscribeOptionsDialog(){

    Utils.showSubscribeOptionsDialog((Activity) context, new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (Utils.subscribeOptionsDialog.isShowing()) {
          Utils.subscribeOptionsDialog.dismiss();
        }
        List<SubscribeOptions> subscribeOptions = Utils.getSubscribeOptionsList();
        if (position == 0) {
          TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
          String mPhoneNumber = tMgr.getLine1Number();
          if (!TextUtils.isEmpty(mPhoneNumber)) {
            subscribeToTopic(mPhoneNumber);
          } else {
            Notify.toast(context, "Introduce tu número de movil en el topic para recibir mensajes destinados a ti", Toast.LENGTH_LONG);
          }
        } else {
          subscribeToTopic(subscribeOptions.get(position).getTitle());
        }
      }
    }).show();
  }

  private void killAllThreads(){
    if(ConnectionDetails.thread!=null) {
      ConnectionDetails.thread.kill();
    }
  }

  private void initializePublishThread (int publishOption){
    switch (publishOption){
      case 1:
        if (ConnectionDetails.thread == null) {
          ConnectionDetails.thread = new LocationThread();
          ConnectionDetails.thread.start();
        }
        else {
          ConnectionDetails.thread.setPaused(false);
        }
        break;
      case 2:
        if (ConnectionDetails.batteryThread == null) {
          ConnectionDetails.batteryThread = new BatteryThread();
          ConnectionDetails.batteryThread.start();
        }
        else {
          ConnectionDetails.batteryThread.setPaused(false);
        }
        break;
      case 3:
        publishDeviceId();
        break;
      case 4:
        publishModelInfo();
        break;
      case 5:
        if (ConnectionDetails.internetStationCellThread == null) {
          ConnectionDetails.internetStationCellThread = new InternetStationCellThread();
          ConnectionDetails.internetStationCellThread.start();
        }
        else {
          ConnectionDetails.internetStationCellThread.setPaused(false);
        }
        break;
      default:
        break;
    }
  }

  private void subscribeToTopic(String topic){
    subscribe(topic);
  }

  /**
   * Reconnect the selected client
   */
  private void reconnect() {

    Connections.getInstance(context).getConnection(clientHandle).changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

    Connection c = Connections.getInstance(context).getConnection(clientHandle);
    try {
      c.getClient().connect(c.getConnectionOptions(), null, new ActionListener(context, ActionListener.Action.CONNECT, clientHandle, null));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    } catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    }

  }

  /**
   * Disconnect the client
   */
  private void disconnect() {

    Connection c = Connections.getInstance(context).getConnection(clientHandle);

    //if the client is not connected, process the disconnect
    if (!c.isConnected()) {
      return;
    }

    try {
      c.getClient().disconnect(null, new ActionListener(context, ActionListener.Action.DISCONNECT, clientHandle, null));
      c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTING);
    } catch (Exception e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to disconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to disconnect");
    }
  }

  private void subscribe(String topic){
    try {
      int qos = 2;
      String[] topics = new String[1];
      topics[0] = topic;
      Connections.getInstance(context).getConnection(clientHandle).getClient()
              .subscribe(topic, qos, null, new ActionListener(context, ActionListener.Action.SUBSCRIBE, clientHandle, topics));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
  }

  /**
   * Subscribe to a topic that the user has specified
   */
  private void subscribe()
  {
    String topic = ((EditText) connectionDetails.findViewById(R.id.topic)).getText().toString();
    ((EditText) connectionDetails.findViewById(R.id.topic)).getText().clear();

    RadioGroup radio = (RadioGroup) connectionDetails.findViewById(R.id.qosSubRadio);
    int checked = radio.getCheckedRadioButtonId();
    int qos = ActivityConstants.defaultQos;

    switch (checked) {
      case R.id.qos0 :
        qos = 0;
        break;
      case R.id.qos1 :
        qos = 1;
        break;
      case R.id.qos2 :
        qos = 2;
        break;
    }

    try {
      String[] topics = new String[1];
      topics[0] = topic;
      Connections.getInstance(context).getConnection(clientHandle).getClient()
          .subscribe(topic, qos, null, new ActionListener(context, ActionListener.Action.SUBSCRIBE, clientHandle, topics));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
  }

  /**
   * Publish the message the user has specified
   */
  private void publish(String message) //TODO make another publish method that publishes from threads, so no NullPointerException
  {
    EditText editText = (EditText) connectionDetails.findViewById(R.id.lastWillTopic);
    String topic = "";
    if(editText!=null){
       topic = ((EditText) connectionDetails.findViewById(R.id.lastWillTopic))
              .getText().toString();
    }
    if(TextUtils.isEmpty(topic)){
      topic = "localizacion";
    }

    if(editText!=null){
      editText.getText().clear();
    }

    String messageToPublish;
    if(!TextUtils.isEmpty(message)){
      messageToPublish = message;
    } else {
      messageToPublish = ((EditText) connectionDetails.findViewById(R.id.lastWill)).getText()
              .toString();
      ((EditText) connectionDetails.findViewById(R.id.lastWill)).getText().clear();
    }

    RadioGroup radio = (RadioGroup) connectionDetails.findViewById(R.id.qosRadio);
    int checked = 0;
    int qos = 0;
    if(radio!=null) {
      checked = radio.getCheckedRadioButtonId();
      qos = ActivityConstants.defaultQos;
    }

    switch (checked) {
      case R.id.qos0 :
        qos = 0;
        break;
      case R.id.qos1 :
        qos = 1;
        break;
      case R.id.qos2 :
        qos = 2;
        break;
    }
    SwitchCompat switchCompat = (SwitchCompat) connectionDetails.findViewById(R.id.retained);
      boolean retained = false;
      if(switchCompat!=null){
         retained = switchCompat
                .isChecked();
      }

    String[] args = new String[2];
    args[0] = message;
    args[1] = topic+";qos:"+qos+";retained:"+retained;

    try {
      Connections.getInstance(context).getConnection(clientHandle).getClient()
          .publish(topic, messageToPublish.getBytes(), qos, retained, null, new ActionListener(context, ActionListener.Action.PUBLISH, clientHandle, args));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + clientHandle, e);
    }

  }

  /**
   * Create a new client and connect
   */
  private void createAndConnect()
  {
    Intent createConnection;

    //start a new activity to gather information for a new connection
    createConnection = new Intent();
    createConnection.setClass(
            clientConnections.getApplicationContext(),
            NewConnection.class);

    clientConnections.startActivityForResult(createConnection,
            ActivityConstants.connect);
  }

  /**
   * Enables logging in the Paho MQTT client
   */
  private void enablePahoLogging() {

    try {
      InputStream logPropStream = context.getResources().openRawResource(R.raw.jsr47android);
      LogManager.getLogManager().readConfiguration(logPropStream);
      logging = true;

      HashMap<String, Connection> connections = (HashMap<String,Connection>)Connections.getInstance(context).getConnections();
      if(!connections.isEmpty()){
    	  Entry<String, Connection> entry = connections.entrySet().iterator().next();
    	  Connection connection = (Connection)entry.getValue();
    	  connection.getClient().setTraceEnabled(true);

      }else{
    	  Log.i("SampleListener","No connection to enable log in service");
      }
    }
    catch (IOException e) {
      Log.e("MqttAndroidClient",
          "Error reading logging parameters", e);
    }
    EventBus.getDefault().post(new LogginChanged());

  }

  /**
   * Disables logging in the Paho MQTT client
   */
  private void disablePahoLogging() {
    LogManager.getLogManager().reset();
    logging = false;

    HashMap<String, Connection> connections = (HashMap<String,Connection>)Connections.getInstance(context).getConnections();
    if(!connections.isEmpty()){
  	  Entry<String, Connection> entry = connections.entrySet().iterator().next();
  	  Connection connection = (Connection)entry.getValue();
  	  connection.getClient().setTraceEnabled(false);
  	  //change menu state.
  	  clientConnections.invalidateOptionsMenu();
    }else{
  	  Log.i("SampleListener","No connection to disable log in service");
    }
    EventBus.getDefault().post(new LogginChanged());
  }

  //Thread to publish location
  public class LocationThread extends Thread {
    private boolean mAlive = true;
    private boolean mPaused = false;

    //Init thread
    public LocationThread() {
    }

    @Override
    public void run() {
      try {

        while (mAlive) {
          if (mPaused) {
            yield();
          }
          else {
            Address address = LocationService.getUserLocation(context);
            if(address!=null) {
              publish(address.getAddressLine(0));
            }
            Thread.sleep(SLEEP_TIME);
          }
        }
      }
      catch (InterruptedException e) {
        Log.e("Thread", "hilo interrumpido");
      }
    }

    public void setPaused(boolean paused) {
      mPaused = paused;
    }

    public void kill() {
      mAlive = false;
      mPaused = false;
    }
  }

  //Thread to publish location
  public class BatteryThread extends Thread {
    private boolean mAlive = true;
    private boolean mPaused = false;

    //Init thread
    public BatteryThread() {
      context.registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void run() {
      try {

        while (mAlive) {
          if (mPaused) {
            yield();
          }
          else {
            Thread.sleep(SLEEP_TIME);
          }
        }
      }
      catch (InterruptedException e) {
        Log.e("Thread", "hilo interrumpido");
      }
    }

    public void setPaused(boolean paused) {
      mPaused = paused;
    }

    public void kill() {
      mAlive = false;
      mPaused = false;
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        if(level !=0) {
          publish(String.valueOf(level));
        }
      }
    };
  }

  private void publishDeviceId(){
    String android_id = Settings.Secure.getString(context.getContentResolver(),
            Settings.Secure.ANDROID_ID);
    if(!TextUtils.isEmpty(android_id)) {
      publish(android_id);
    }
  }

  private void publishModelInfo(){
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    String message;
    if (model.startsWith(manufacturer)) {
      message = capitalize(model);
    } else if (manufacturer.equalsIgnoreCase("HTC")) {
      // make sure "HTC" is fully capitalized.
      message = "HTC " + model;
    } else {
      message = capitalize(manufacturer) + " " + model;
    }
    if(TextUtils.isEmpty(message)) {
      publish(message);
    }
  }

    private String capitalize(String str) {
      if (TextUtils.isEmpty(str)) {
        return str;
      }
      char[] arr = str.toCharArray();
      boolean capitalizeNext = true;
      String phrase = "";
      for (char c : arr) {
        if (capitalizeNext && Character.isLetter(c)) {
          phrase += Character.toUpperCase(c);
          capitalizeNext = false;
          continue;
        } else if (Character.isWhitespace(c)) {
          capitalizeNext = true;
        }
        phrase += c;
      }
      return phrase;
    }

  //Thread to publish location
  public class InternetStationCellThread extends Thread {
    private boolean mAlive = true;
    private boolean mPaused = false;

    //Init thread
    public InternetStationCellThread() {
    }

    @Override
    public void run() {
      try {

        while (mAlive) {
          if (mPaused) {
            yield();
          }
          else {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            String message = "";
            //For 3G check
            if( manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting()) {

              Cursor cursor = context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), null, null, null, null);
              do {
                message = cursor.getString(0);
              } while (cursor.moveToNext());
              cursor.close();

            } else if(manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting()) {
              WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
              WifiInfo info = wifiManager.getConnectionInfo();
              message = info.getBSSID();
            }

            if(!TextUtils.isEmpty(message)) {
              publish(message);
            }
            Thread.sleep(SLEEP_TIME);
          }
        }
      }
      catch (InterruptedException e) {
        Log.e("Thread", "hilo interrumpido");
      }
    }

    public void setPaused(boolean paused) {
      mPaused = paused;
    }

    public void kill() {
      mAlive = false;
      mPaused = false;
    }
  }
}
