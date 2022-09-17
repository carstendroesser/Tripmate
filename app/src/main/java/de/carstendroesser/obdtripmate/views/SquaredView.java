package de.carstendroesser.obdtripmate.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by carstendrosser on 06.06.17.
 */

public class SquaredView extends FrameLayout {

    // CONSTRUCTORS

    public SquaredView(Context pContext) {
        // chained constructor
        this(pContext, null);
    }

    public SquaredView(Context pContext, @Nullable AttributeSet pAttrs) {
        // chained constructor
        this(pContext, pAttrs, 0);
    }

    public SquaredView(Context pContext, @Nullable AttributeSet pAttrs, int pDefStyleAttr) {
        super(pContext, pAttrs, pDefStyleAttr);
    }

    // PROTECTED API

    @Override
    protected void onMeasure(int pWidthMeasureSpec, int pHeightMeasureSpec) {
        // simply makes this view a square by changing it's width to it's height
        // ever when it gets invalidated
        super.onMeasure(pHeightMeasureSpec, pHeightMeasureSpec);
    }

}
