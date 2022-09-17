package de.carstendroesser.obdtripmate.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.utils.DensityConverter;

/**
 * Created by carstendrosser on 17.06.17.
 */

public class BarDataView extends LinearLayout {

    // MEMBERS

    private TextView mTextView;
    private BarView mBarView;

    // CONSTRUCTORS

    public BarDataView(Context pContext) {
        // chained constructor
        this(pContext, null);
    }

    public BarDataView(Context pContext, @Nullable AttributeSet pAttrs) {
        // chained constructor
        this(pContext, pAttrs, 0);
    }

    public BarDataView(Context pContext, @Nullable AttributeSet pAttrs, int pDefStyleAttr) {
        super(pContext, pAttrs, pDefStyleAttr);

        // inflate this view
        inflate(getContext(), R.layout.view_dataview_bar, this);

        // get all subviews
        ImageView imageView = (ImageView) findViewById(R.id.barDataViewImageView);
        mTextView = (TextView) findViewById(R.id.barDataViewTextView);
        mBarView = (BarView) findViewById(R.id.barDataViewBarView);

        // set the drawable
        TypedArray typedArray = getContext().obtainStyledAttributes(pAttrs, R.styleable.BarDataView, 0, 0);
        imageView.setImageDrawable(typedArray.getDrawable(R.styleable.BarDataView_barDataViewIcon));

        ViewGroup.LayoutParams params = mBarView.getLayoutParams();

        // are we horizontaly/verticaly aligned?
        if (typedArray.getBoolean(R.styleable.BarDataView_barDataViewHorizontal, false)) {
            setOrientation(HORIZONTAL);
            mBarView.setOrientation(BarView.Orientation.HORIZONTAL);
            params.height = DensityConverter.convertDpToPixels(pContext, 8);

        } else {
            setOrientation(VERTICAL);
            mBarView.setOrientation(BarView.Orientation.VERTICAL);
            params.width = DensityConverter.convertDpToPixels(pContext, 8);
        }

        // update the layout
        mBarView.requestLayout();

        setText("-");
    }

    // PUBLIC API

    /**
     * Sets the progress of the bar by animating the current
     * value to the new one.
     *
     * @param pProgress the progress to show
     */
    public void setProgress(int pProgress) {
        mBarView.setProgressAnimated(pProgress);
    }

    /**
     * Sets the maximum progress this view can handle.
     *
     * @param pMaxProgress the max progress
     */
    public void setMaxProgress(int pMaxProgress) {
        mBarView.setMaxProgress(pMaxProgress);
    }

    /**
     * Sets the text that is shown.
     *
     * @param pText the text to show
     */
    public void setText(String pText) {
        mTextView.setText(pText);
    }

}
