package Model;

import android.graphics.Bitmap;
import android.util.Log;

public class Utils {
    public static Bitmap bitmapToThumbnail(Bitmap bitmap){
        double ratio = 150.0 / Math.max(bitmap.getHeight(), bitmap.getWidth());


        Log.e("New Values", "x: " + (int) (bitmap.getWidth() * ratio) + ", y: " + (int) (bitmap.getHeight() * ratio));

        return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * ratio), (int) (bitmap.getHeight() * ratio), false);
    }
}
