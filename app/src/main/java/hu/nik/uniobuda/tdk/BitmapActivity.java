package hu.nik.uniobuda.tdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Hidvégi Péter on 2015.11.18..
 */
public class BitmapActivity extends Activity {
    boolean isImageFitToScreen;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitmap_activity);
        Intent intent = getIntent();

        path = intent.getStringExtra("bitmapPath");
        Log.e("lefutpath", path);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        Bitmap bMap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bMap);
        if(isImageFitToScreen) {
            isImageFitToScreen=false;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
        }else{
            isImageFitToScreen=true;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BitmapActivity.this.finish();
            }
        },3000);
    }
}
