package hu.nik.uniobuda.tdk;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by Hidvégi Péter on 2015.11.16..
 */
public class WifiDirectFragmentShowDetailsEvent {

    public WifiDirectFragmentShowDetailsEvent(WifiP2pDevice name) {
        this.device = name;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    WifiP2pDevice device;

}
