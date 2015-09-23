package com.carlos.ramirez.pfc.test.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.activity.About;
import com.carlos.ramirez.android.service.pfc.activity.NewConnection;
import com.robotium.solo.Solo;

/**
 * Created by carlos on 21/9/15.
 */
public class NewConnectionActivityTest extends ActivityInstrumentationTestCase2<NewConnection> {

    private Solo solo;

    public NewConnectionActivityTest(){
        super(NewConnection.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        solo = new Solo(getInstrumentation(), getActivity());
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testNewConnectionTextView() throws Exception {
        solo.assertCurrentActivity("wrong activity", NewConnection.class);

        int expectedValue = 0; // 0=VISIBLE, 4=INVISIBLE, 8=GONE

        String expectedText = getActivity().getResources().getString(R.string.clientID);
        String actualText = ((TextView) solo.getView(R.id.clientIDTextView)).getText().toString();
        assertEquals("CLientTextView is Invisible", expectedValue, solo.getView(R.id.clientIDTextView).getVisibility());
        assertTrue(actualText.equals(expectedText));

        String expectedText2 = getActivity().getResources().getString(R.string.server);
        String actualText2 = ((TextView) solo.getView(R.id.serverTextView)).getText().toString();
        assertEquals("serverTextView is Invisible", expectedValue, solo.getView(R.id.serverTextView).getVisibility());
        assertTrue(actualText2.equals(expectedText2));

        String expectedText3 = getActivity().getResources().getString(R.string.port);
        String actualText3 = ((TextView) solo.getView(R.id.portTextView)).getText().toString();
        assertEquals("portTextView is Invisible", expectedValue, solo.getView(R.id.portTextView).getVisibility());
        assertTrue(actualText3.equals(expectedText3));

        String expectedText4 = getActivity().getResources().getString(R.string.cleanSession);
        String actualText4 = ((TextView) solo.getView(R.id.cleanSessionTextView)).getText().toString();
        assertEquals("cleanSessionTextView is Invisible", expectedValue, solo.getView(R.id.cleanSessionTextView).getVisibility());
        assertTrue(actualText4.equals(expectedText4));
    }
}

