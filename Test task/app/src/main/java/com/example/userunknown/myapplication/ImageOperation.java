package com.example.userunknown.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

import java.io.File;

/**
 * Created by Kondrat on 05.03.2017.
 */

public abstract class ImageOperation {

    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight ){
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }




    public static Bitmap decodeSampledBitmapFromPath( String fullPath, int reqWidth, int reqHeight ){

        if( new File( fullPath ).exists() == false ){
            return null;
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( fullPath, options );

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize( options, reqWidth, reqHeight );

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile( fullPath, options );
    }
    public static Bitmap decodeSampledBitmapFromPath( Context context, String fullPath ){
        Point size = getScreenSize();
        return  decodeSampledBitmapFromPath( fullPath, size.x, size.y );
    }



    public static Bitmap decodeSampledBitmapFromResource( Resources res, int resId, int reqWidth, int reqHeight ){

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource( res, resId, options );

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize( options, reqWidth, reqHeight );

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource( res, resId, options );
    }
    public static Bitmap decodeSampledBitmapFromResource( Context context, int resId ){
        Point size = getScreenSize();
        return  decodeSampledBitmapFromResource( context.getResources(), resId, size.x, size.y );
    }




    public static Point getScreenSize(){
        return new Point(   Resources.getSystem().getDisplayMetrics().widthPixels,
                            Resources.getSystem().getDisplayMetrics().heightPixels );
    }
}
































