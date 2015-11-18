package com.example.android.wifidirect;

/**
 * Created by Hidvégi Péter on 2015.11.17..
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

import android.os.AsyncTask;
import android.os.BatteryManager;

import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;
    String info;
    Timer t = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        Intent intent = getIntent();
        info = intent.getStringExtra("info");

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    captureImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 5000, 5000);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Log.e("exc", "onPictureTaken");
                AsyncService asyncService = new AsyncService();
                asyncService.execute(data);

                refreshCamera();
            }
        };
    }

    public void captureImage() throws IOException {
        camera.takePicture(null, null, jpegCallback);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        t.cancel();
        super.onDestroy();

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        }

        catch (RuntimeException e) {
            System.err.println(e);
            return;
        }

        Camera.Parameters param;
        param = camera.getParameters();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        try{
            camera.setDisplayOrientation(90);
            param.setPreviewSize(352, 288);
            camera.setParameters(param);
        }catch (RuntimeException e)
        {
                if(param.getSupportedPreviewSizes().get(1)!=null)
                {
                    param.setPreviewSize(param.getSupportedPreviewSizes().get(1).width,param.getSupportedPreviewSizes().get(1).height);

                } else {
                    param.setPreviewSize(param.getSupportedPreviewSizes().get(0).width,param.getSupportedPreviewSizes().get(0).height);
                }
            camera.setParameters(param);
        }


        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }



    private class AsyncService extends AsyncTask<byte[], Void, Void> {


        @Override
        protected Void doInBackground(byte[]... bytes) {
            Socket socket = new Socket();
            Log.e("lefut",info.toString());
            try {
                socket.bind(null);

                socket.connect((new InetSocketAddress(info, 8988)), 5000);

                OutputStream stream = socket.getOutputStream();

                ContentResolver cr = CameraActivity.this.getContentResolver();

                InputStream is = new ByteArrayInputStream(bytes[0]);

                DeviceDetailFragment.copyFile(is, stream);
            }catch (IOException exc)
            {
                Log.e("lefut",exc.toString());
            }
            return null;
        }
    }
}