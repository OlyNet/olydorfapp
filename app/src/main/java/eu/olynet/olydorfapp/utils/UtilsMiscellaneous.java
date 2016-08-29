/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

/**
 * Utility class
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class UtilsMiscellaneous {
    /**
     * Returns the size in pixels of an attribute dimension
     *
     * @param context the context to get the resource from
     * @param attr    is the attribute dimension we want to know the size from
     * @return the size in pixels of an attribute dimension
     */
    public static int getThemeAttributeDimensionSize(Context context, int attr) {
        TypedArray a = null;
        try {
            a = context.getTheme().obtainStyledAttributes(new int[]{attr});
            return a.getDimensionPixelSize(0, 0);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    /**
     * Get a Bitmap that is optimally scaled to prevent memory waste. This was actually a problem
     * causing OutOfMemoryErrors to be thrown.<p/>
     * Note that the scaling factor will always be a multiple of 2.
     *
     * @param image       the to be converted to a Bitmap as a byte array. May be <b>null</b> or
     *                    of zero length (this will cause a <b>null</b> return value).
     * @param widthPixels the desired width in pixels of the Bitmap. This is usually the width of
     *                    the screen.
     * @return the Bitmap or <b>null</b> if some error occurred along the way.
     */
    @Nullable
    public static Bitmap getOptimallyScaledBitmap(@Nullable byte[] image, int widthPixels) {
        if (image != null && image.length > 0) {
            /* get the raw data of the image */
            BitmapFactory.Options rawOptions = new BitmapFactory.Options();
            rawOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(image, 0, image.length, rawOptions);

            if (rawOptions.outWidth == -1) {
                return null;
            } else {
                /* find a scale divisible by two that optimizes the size of the Bitmap */
                BitmapFactory.Options finalOptions = new BitmapFactory.Options();
                finalOptions.inSampleSize = 1;
                while (rawOptions.outWidth / finalOptions.inSampleSize >= 2 * widthPixels) {
                    finalOptions.inSampleSize *= 2;
                }

                /* generate and return the final Bitmap */
                return BitmapFactory.decodeByteArray(image, 0, image.length, finalOptions);
            }
        } else {
            return null;
        }
    }

}
