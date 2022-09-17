package de.carstendroesser.obdtripmate.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by carstendrosser on 17.06.17.
 */

public class DensityConverter {

    /**
     * Converts dp to px.
     *
     * @param pContext
     * @param pDensityPoints
     * @return the given dp in px
     */
    public static int convertDpToPixels(Context pContext, int pDensityPoints) {
        Resources resources = pContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pDensityPoints, resources.getDisplayMetrics());
    }

}
