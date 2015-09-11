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
package com.carlos.ramirez.android.service.pfc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.listener.Listener;
import com.carlos.ramirez.android.service.pfc.model.Connection;
import com.carlos.ramirez.android.service.pfc.model.Connections;


/**
 * Fragment for the publish message pane.
 *
 */
public class PublishFragment extends Fragment {

  /**
   * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view  = LayoutInflater.from(getActivity()).inflate(R.layout.activity_publish, null);
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    Button lastWillButton = (Button) view.findViewById(R.id.publish);
    ConnectionDetails.publish = lastWillButton;
    lastWillButton.setText(getString(R.string.publish));
    toolbar.setVisibility(View.GONE);
    return view;

  }

}
