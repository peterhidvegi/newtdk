package hu.nik.uniobuda.tdk;

import android.net.wifi.p2p.WifiP2pConfig;

/**
 * Created by Hidv�gi P�ter on 2015.11.17..
 */
public class WifiDirectFragmentConnectEvent {

    public WifiDirectFragmentConnectEvent(WifiP2pConfig config) {
        this.config = config;
    }

    public WifiP2pConfig getConfig() {
        return config;
    }

    WifiP2pConfig config;

}
