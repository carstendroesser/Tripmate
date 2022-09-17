package de.carstendroesser.obdtripmate.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.carstendroesser.obdtripmate.R;

/**
 * Created by carstendrosser on 04.06.17.
 */

public class SimpleDataView extends LinearLayout {

    // MEMBERS

    private ImageView mImageView;
    private TextView mPrimaryTextView;
    private TextView mSecondaryTextView;
    private TextView mThirdTextView;

    // CONSTRUCTORS

    public SimpleDataView(Context pContext) {
        // chained constructor
        this(pContext, null);
    }

    public SimpleDataView(Context pContext, @Nullable AttributeSet pAttrs) {
        // chained constructor
        this(pContext, pAttrs, 0);
    }

    public SimpleDataView(Context pContext, @Nullable AttributeSet pAttrs, int pDefStyleAttr) {
        super(pContext, pAttrs, pDefStyleAttr);

        // inflate the view
        inflate(getContext(), R.layout.view_dataview, this);

        // get all subviews
        mImageView = (ImageView) findViewById(R.id.dataviewImageView);
        mPrimaryTextView = (TextView) findViewById(R.id.dataviewPrimaryTextView);
        mSecondaryTextView = (TextView) findViewById(R.id.dataviewSecondaryTextView);
        mThirdTextView = (TextView) findViewById(R.id.dataviewThirdTextView);

        // set the image drawable
        TypedArray typedArray = getContext().obtainStyledAttributes(pAttrs, R.styleable.SimpleDataView, 0, 0);
        mImageView.setImageDrawable(typedArray.getDrawable(R.styleable.SimpleDataView_dataIcon));
    }

    // PUBLIC API

    /**
     * Sets the primary text.
     *
     * @param pText the text for the primary textview
     */
    public void setPrimaryText(String pText) {
        mPrimaryTextView.setText(pText);
    }

    /**
     * Sets the secondary text.
     *
     * @param pText the text for the secondary textview
     */
    public void setSecondaryText(String pText) {
        mSecondaryTextView.setText(pText);
    }

    /**
     * Sets the third text.
     *
     * @param pText the text for the third textview
     */
    public void setThirdText(String pText) {
        mThirdTextView.setText(pText);
    }

    /**
     * Sets the drawable to show within the imageview.
     *
     * @param pDrawable some drawable
     */
    public void setImage(Drawable pDrawable) {
        mImageView.setImageDrawable(pDrawable);
    }

}
