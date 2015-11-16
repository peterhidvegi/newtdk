package hu.nik.uniobuda.tdk;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Method;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by Peter on 2015.11.07..
 */
public class MiddleScreenFragment extends Fragment {

    EventBus bus = EventBus.getDefault();

    Activity mActivity;
    IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
    IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

    }

    ListView listView;
    ArrayAdapter<MyBluetoothDevice> myAdapter;

    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.middle_screen_fragment,container,false);

        listView = (ListView) v.findViewById(R.id.fragment_listview);
        myAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)mActivity).getmDeviceConnector().getmAdapter().cancelDiscovery();
                MyBluetoothDevice myBluetoothDevice = (MyBluetoothDevice) (parent.getItemAtPosition(position));
                ((MainActivity)mActivity).getmDeviceConnector().setmBluetoothDevice(myBluetoothDevice.getBluetoothDevice());
                ((MainActivity)mActivity).getmDeviceConnector().createBound();
                ((MainActivity)mActivity).getmDeviceConnector().connect();
            }
        } );

        return v;
    }


    private final BroadcastReceiver pairingRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                try {
                    byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, "1234");
                    Method m = ((MainActivity)mActivity).getmDeviceConnector().getmBluetoothDevice().getClass().getMethod("setPin", byte[].class);
                    m.invoke(((MainActivity)mActivity).getmDeviceConnector().getmBluetoothDevice(), pin);
                    ((MainActivity)mActivity).getmDeviceConnector().getmBluetoothDevice().getClass().getMethod("setPairingConfirmation", boolean.class).invoke(((MainActivity)mActivity).getmDeviceConnector().getmBluetoothDevice(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                myAdapter.add(new MyBluetoothDevice(device));

            }
        }
    };

    public void onEvent(FindDeviceEvent event){
       unregister();
       myAdapter.clear();
     ((MainActivity)mActivity).getmDeviceConnector().getmAdapter().startDiscovery();
      mActivity.registerReceiver(mReceiver, filter2);
      mActivity.registerReceiver(pairingRequest, filter);
    }

    public void onEvent(ListPairedEvent listPairedEvent) {
        unregister();
        myAdapter.clear();
        Set<BluetoothDevice> pairedDevices =  ((MainActivity)mActivity).getmDeviceConnector().getmAdapter().getBondedDevices();
        for(BluetoothDevice bt : pairedDevices)
            myAdapter.add(new MyBluetoothDevice(bt));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        unregister();

    }

    private void unregister()
    {
        try {
            mActivity.unregisterReceiver(pairingRequest);
            mActivity.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
    }
}
