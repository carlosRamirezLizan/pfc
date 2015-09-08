package com.carlos.ramirez.pfc.test.runner;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

/**
 * Created by raddev01 on 12/12/2014.
 */
public class MediumTestInstrumentationTestRunner extends InstrumentationTestRunner {

    @Override
    public void onCreate(Bundle arguments) {

        arguments.putString("size", "medium");

        super.onCreate(arguments);
    }
}