package de.carstendroesser.obdtripmate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by carstendrosser on 24.06.17.
 */

public class BitmapResizer {

    /**
     * Resizes a given resource drawable to a given width and height.
     *
     * @param pContext  we need that
     * @param pResource the resource drawable
     * @param pWidth    the wanted width
     * @param pHeight   the wanted height
     * @return a bitmap of the given resource drawable in the specified dimensions
     */
    public static Bitmap resizeBitmap(Context pContext, int pResource, int pWidth, int pHeight) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(pContext.getResources(), pResource);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, pWidth, pHeight, false);
        return resizedBitmap;
    }

}
