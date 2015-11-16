package hu.nik.uniobuda.tdk;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import de.greenrobot.event.EventBus;

//https://github.com/peterhidvegi/ourtdkhidvegi.git
public class MainActivity extends Activity{

    EventBus eventBus = EventBus.getDefault();

    BluetoothDeviceConnector mDeviceConnector = new BluetoothDeviceConnector();
    public BluetoothDeviceConnector getmDeviceConnector() {
        return mDeviceConnector;
    }
    //Sensor elements
    private SensorManager mSensorManager;
    //UI elements
    ImageButton btnP2PCtrl;
    ImageButton btnFindDevices;
    ImageButton btn_reset;

    Button btnUp;
    Button btnDown;
    Button btnLeft;
    Button btnRight;

    WifiP2pManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        Log.e("asd",manager.toString());
        btnUp = (Button) findViewById(R.id.up);
        btnDown = (Button) findViewById(R.id.down);
        btnLeft = (Button) findViewById(R.id.left);
        btnRight = (Button) findViewById(R.id.right);
        btnP2PCtrl = (ImageButton) findViewById(R.id.p2p_ctrl);
        btnFindDevices = (ImageButton) findViewById(R.id.btn_find_devices);
        btn_reset = (ImageButton) findViewById(R.id.btn_reset);

        btnP2PCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment wiFiDirectFragment = new WiFiDirectFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.container, wiFiDirectFragment).commit();

            }
        });

        btnFindDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new MiddleScreenFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.container, newFragment).commit();

                eventBus.post(new ListPairedEvent());
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new MiddleScreenFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.container, newFragment).commit();

                mDeviceConnector.removeBounds();
                eventBus.post(new FindDeviceEvent());
            }
        });



        btnUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    btnUp.setTextColor(getResources().getColor(R.color.button_material_light));
                    sendMessage("b");
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    btnUp.setTextColor(getResources().getColor(R.color.button_material_dark));
                }
                return false;
            }
        });

        btnDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    btnDown.setTextColor(getResources().getColor(R.color.button_material_light));
                    sendMessage("s");
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    btnDown.setTextColor(getResources().getColor(R.color.button_material_dark));
                }
                return false;
            }
        });


        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    btnLeft.setTextColor(getResources().getColor(R.color.button_material_light));
                    sendMessage("k");
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    btnLeft.setTextColor(getResources().getColor(R.color.button_material_dark));
                }

                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    btnRight.setTextColor(getResources().getColor(R.color.button_material_light));
                    sendMessage("n");
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    btnRight.setTextColor(getResources().getColor(R.color.button_material_dark));
                }

                return false;
            }
        });

        mDeviceConnector.getmAdapter().enable();

    }


    private void sendMessage(CharSequence chars) {
        if (chars.length() > 0) {
            mDeviceConnector.sendAsciiMessage(chars);
        }
    }







    @Override
    protected void onDestroy() {

        super.onDestroy();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }


    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            changedUI(se.values);

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        private void changedUI(final float[] coordinates)
        {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Right and left
                        if(coordinates[1]>3) {
                            btnRight.setTextColor(getResources().getColor(R.color.button_material_light));
                            sendMessage("n");
                        }else if(coordinates[1]<-3)
                        {
                            btnLeft.setTextColor(getResources().getColor(R.color.button_material_light));
                            sendMessage("k");
                        }else
                        {
                            btnLeft.setTextColor(getResources().getColor(R.color.button_material_dark));
                            btnRight.setTextColor(getResources().getColor(R.color.button_material_dark));
                        }

                        if(coordinates[0]>3)
                        {
                            btnDown.setTextColor(getResources().getColor(R.color.button_material_light));
                            sendMessage("p");
                        }else if(coordinates[0]<-3)
                        {
                            sendMessage("z");
                            btnUp.setTextColor(getResources().getColor(R.color.button_material_light));
                        }else
                        {
                            btnUp.setTextColor(getResources().getColor(R.color.button_material_dark));
                            btnDown.setTextColor(getResources().getColor(R.color.button_material_dark));
                        }

                        //Log.d("lefut",String.valueOf("x: "+(int)coordinates[0]+" y: "+coordinates[1]+" z:"+coordinates[2]));


                    }
                });

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

}
