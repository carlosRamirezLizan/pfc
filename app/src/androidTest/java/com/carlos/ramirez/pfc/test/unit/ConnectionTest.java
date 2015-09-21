package com.carlos.ramirez.pfc.test.unit;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.carlos.ramirez.android.service.pfc.model.Connection;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by raddev01 on 06/10/2014.
 */
public class ConnectionTest extends InstrumentationTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }


    @SmallTest
    public void test01Deleterequests(){
        Connection connection = Connection.createConnection("carlos", "188.226.128.236", 1884,
                this.getInstrumentation().getTargetContext().getApplicationContext(), false);
        assertNotNull(connection);
        assertEquals("carlos", connection.getId());
        assertEquals("188.226.128.236", connection.getHostName());
        assertEquals(1884, connection.getPort());
        assertEquals(0, connection.isSSL());
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
