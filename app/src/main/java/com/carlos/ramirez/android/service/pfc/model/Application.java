package com.carlos.ramirez.android.service.pfc.model;


/**
 * IYCP
 * Created by Carlos Ramírez on 08/01/15.
 * © Rad(+) 2015
 */
public class Application {

    private String port;
    private String server_url;

    private Application() {
    }

    public Application(String server_url) {
        this.server_url = server_url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServer_url() {
        return server_url;
    }

}
