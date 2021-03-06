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
package com.carlos.ramirez.android.service.pfc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.util.ActivityConstants;
import com.carlos.ramirez.android.service.pfc.util.Utils;


/**
 * Activity for setting the last will message for the client
 *
 */
public class LastWill extends AppCompatActivity {

  /**
   * Reference to the current instance of <code>LastWill</code> for use with anonymous listener
   */
  private LastWill last = this;

  /**
   * @see Activity#onCreate(Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_publish);
    Utils.setUpToolBar(this);

    findViewById(R.id.publish).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent result = new Intent();

        String message = ((EditText) findViewById(R.id.lastWill)).getText().toString();
        String topic = ((EditText) findViewById(R.id.lastWillTopic)).getText().toString();

        RadioGroup radio = (RadioGroup) findViewById(R.id.qosRadio);
        int checked = radio.getCheckedRadioButtonId();
        int qos = ActivityConstants.defaultQos;

        //determine which qos value has been selected
        switch (checked)
        {
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

        boolean retained = ((SwitchCompat) findViewById(R.id.retained)).isChecked();

        //package the data collected into the intent
        result.putExtra(ActivityConstants.message, message);
        result.putExtra(ActivityConstants.topic, topic);
        result.putExtra(ActivityConstants.qos, qos);
        result.putExtra(ActivityConstants.retained, retained);

        //set the result and finish activity
        last.setResult(RESULT_OK, result);
        last.finish();
      }
    });

  }



  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home :
      case android.R.id.title:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
