package mx.itesm.segi.perfectproject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by ianne on 7/03/2018.
 */

public class PointDragShadowBuilder extends View.DragShadowBuilder {


    private View view;
    private float x;
    private float y;

    public PointDragShadowBuilder(View view, float x, float y) {
        super(view);
        this.view = view;
        this.x = x;
        this.y = y;
    }
    @Override
    public void onDrawShadow(Canvas canvas) {
        //super.onDrawShadow(canvas);

    /*Modify canvas if you want to show some custom view that you want
      to animate, that you can check by putting a condition passed over
      constructor. Here I'm taking the same view*/

        //canvas.drawBitmap(getBitmapFromView(view), 0, 0, null);
    }
    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {
/*Modify x,y of shadowSize to change the shadow view
   according to your requirements. Here I'm taking the same view width and height*/
        shadowSize.set(view.getWidth(), view.getHeight());
/*Modify x,y of touchPoint to change the touch for the view
 as per your needs. You may pass your x,y position of finger
 to achieve your results. Here I'm taking the lower end point of view*/
        touchPoint.set((int)x, (int)y);
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}