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

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.listener.Listener;
import com.carlos.ramirez.android.service.pfc.model.Connection;
import com.carlos.ramirez.android.service.pfc.model.Connections;
import com.carlos.ramirez.android.service.pfc.util.Utils;
import com.github.clans.fab.FloatingActionButton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * The connection details activity operates the fragments that make up the
 * connection details screen.
 * <p>
 * The fragments which this FragmentActivity uses are
 * <ul>
 * HistoryFragment}
 * PublishFragment
 * SubscribeFragment
 * </ul>
 * 
 */
public class ConnectionDetails extends AppCompatActivity {

  /**
   * {@link SectionsPagerAdapter} that is used to get pages to display
   */
  SectionsPagerAdapter sectionsPagerAdapter;
  /**
   * {@link ViewPager} object allows pages to be flipped left and right
   */
  ViewPager viewPager;

  /** The currently selected tab **/
  private int selected = 0;

  /**
   * The handle to the Connection which holds the data for the client
   * selected
   **/
  private String clientHandle = null;

  /** This instance of <code>ConnectionDetails</code> **/
  private final ConnectionDetails connectionDetails = this;

  /**
   * The instance of Connection that the <code>clientHandle</code>
   * represents
   **/
  private Connection connection = null;

  /**
   * The {@link ChangeListener} this object is using for the connection
   * updates
   **/
  private ChangeListener changeListener = null;

  /**
   * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
   */

  private FloatingActionButton publish;
  private FloatingActionButton subscribe;
  private FloatingActionButton disconnect;
  private FloatingActionButton connectMenuOption;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    clientHandle = getIntent().getStringExtra("handle");

    setContentView(R.layout.activity_connection_details);
    // Create the adapter that will return a fragment for each of the pages
    Utils.setUpToolBar(this);

    sectionsPagerAdapter = new SectionsPagerAdapter(
        getSupportFragmentManager());


    // add the sectionsPagerAdapter
    viewPager = (ViewPager) findViewById(R.id.pager);
    viewPager.setAdapter(sectionsPagerAdapter);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        selected = tab.getPosition();
        // invalidate the options menu so it can be updated
        invalidateOptionsMenu();
        // history fragment is at position zero so get this then refresh its
        // view
        ((HistoryFragment) sectionsPagerAdapter.getItem(0)).refresh();
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });

    connection = Connections.getInstance(this).getConnection(clientHandle);
    changeListener = new ChangeListener();
    connection.registerChangeListener(changeListener);

    disconnect = (FloatingActionButton) findViewById(R.id.disconnect);
    connectMenuOption = (FloatingActionButton) findViewById(R.id.connectMenuOption);
    publish = (FloatingActionButton) findViewById(R.id.publish);
    subscribe = (FloatingActionButton) findViewById(R.id.subscribe);

    View.OnClickListener listener = new Listener(this, clientHandle);
    disconnect.setOnClickListener(listener);
    connectMenuOption.setOnClickListener(listener);
    publish.setOnClickListener(listener);
    subscribe.setOnClickListener(listener);
  }

  @Override
  protected void onDestroy() {
    connection.removeChangeListener(null);
    super.onDestroy();
  }

  /**
   * Provides the Activity with the pages to display for each tab
   * 
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    // Stores the instances of the pages
    private ArrayList<Fragment> fragments = null;

    /**
     * Only Constructor, requires a the activity's fragment managers
     * 
     * @param fragmentManager
     */
    public SectionsPagerAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
      fragments = new ArrayList<Fragment>();
      // create the history view, passes the client handle as an argument
      // through a bundle
      Fragment fragment = new HistoryFragment();
      Bundle args = new Bundle();
      args.putString("handle", getIntent().getStringExtra("handle"));
      fragment.setArguments(args);
      // add all the fragments for the display to the fragments list
      fragments.add(fragment);
      fragments.add(new SubscribeFragment());
      fragments.add(new PublishFragment());

    }

    /**
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    /**
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
      return fragments.size();
    }

    /**
     * 
     * @see FragmentPagerAdapter#getPageTitle(int)
     */
    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0 :
          return getString(R.string.history).toUpperCase();
        case 1 :
          return getString(R.string.subscribe).toUpperCase();
        case 2 :
          return getString(R.string.publish).toUpperCase();
      }
      // return null if there is no title matching the position
      return null;
    }

  }

  /**
   * <code>ChangeListener</code> updates the UI when the {@link Connection}
   * object it is associated with updates
   * 
   */
  private class ChangeListener implements PropertyChangeListener {

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      // connection object has change refresh the UI

      connectionDetails.runOnUiThread(new Runnable() {

        @Override
        public void run() {
          connectionDetails.invalidateOptionsMenu();
          ((HistoryFragment) connectionDetails.sectionsPagerAdapter
              .getItem(0)).refresh();

        }
      });

    }
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
