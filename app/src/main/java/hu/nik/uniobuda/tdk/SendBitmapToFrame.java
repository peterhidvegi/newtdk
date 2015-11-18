package hu.nik.uniobuda.tdk;

import android.graphics.Bitmap;

/**
 * Created by Hidvégi Péter on 2015.11.17..
 */
public class SendBitmapToFrame {

    public SendBitmapToFrame(Bitmap bitmap) {

        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    Bitmap bitmap;


}
