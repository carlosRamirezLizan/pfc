package com.carlos.ramirez.pfc.test.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.activity.About;
import com.robotium.solo.Solo;

/**
 * Created by carlos on 21/9/15.
 */
public class AboutActivityTest extends ActivityInstrumentationTestCase2<About> {

    private Solo solo;

    public AboutActivityTest(){
        super(About.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        solo = new Solo(getInstrumentation(), getActivity());
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testPrivacityTextView() throws Exception {
        solo.assertCurrentActivity("wrong activity", About.class);

        int expectedValue = 0; // 0=VISIBLE, 4=INVISIBLE, 8=GONE

        String expectedText = getActivity().getResources().getString(R.string.about);
        String actualText = ((TextView) solo.getView(R.id.privacityTextView)).getText().toString();

        assertEquals("AboutTextView is Invisible", expectedValue, solo.getView(R.id.privacityTextView).getVisibility());
        assertTrue(actualText.equals(expectedText));
    }
}

