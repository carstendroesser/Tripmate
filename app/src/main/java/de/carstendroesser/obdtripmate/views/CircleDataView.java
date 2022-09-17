package de.carstendroesser.obdtripmate.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import de.carstendroesser.obdtripmate.R;

/**
 * Created by carstendrosser on 08.06.17.
 */

public class CircleDataView extends View {

    // CONSTANTS

    private static final int STROKEWIDTH_OUTER_CIRCLE = 8;
    private static final int STROKEWIDTH_INNER_CIRCLE = 20;
    private static final int SPACE = 2;
    private static final int MAXDURATION = 3000;

    // MEMBERS

    private Paint mTextPaint;
    private Paint mCirclePaint;
    private int mValue = 0;
    private int mMaxValue = 7000;
    private ObjectAnimator mAnimator;
    private String mText = "";

    // CONSTRUCTORS

    public CircleDataView(Context pContext) {
        // chained constructor
        this(pContext, null);
    }

    public CircleDataView(Context pContext, @Nullable AttributeSet pAttrs) {
        // chained constructor
        this(pContext, pAttrs, 0);
    }

    public CircleDataView(Context pContext, @Nullable AttributeSet pAttrs, int pDefStyleAttr) {
        super(pContext, pAttrs, pDefStyleAttr);

        // setup the paint for the circles
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        // setup paint for the text
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setColor(getContext().getResources().getColor(R.color.white));

        setText("-");
        setValue(0);
        setMaxValue(240);
    }

    // PUBLIC API

    /**
     * Sets the text which shall be shown.
     *
     * @param pText the text to show
     */
    public void setText(String pText) {
        mText = pText.toUpperCase();
        invalidate();
    }

    /**
     * Sets the maximum value the circle can be.
     *
     * @param pMaxValue the new maximum
     */
    public void setMaxValue(int pMaxValue) {
        mMaxValue = pMaxValue;
        invalidate();
    }

    /**
     * Animates to the given new value.
     *
     * @param pValue the new value
     */
    public void setValueAnimated(int pValue) {

        // cancel the currently running animator
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        // calculate the percentage of value and max
        float percentage = (float) Math.abs(mValue - pValue) / (float) mMaxValue;

        // animate it!
        mAnimator = ObjectAnimator.ofInt(this, "Value", mValue, pValue);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration((long) (percentage * MAXDURATION));
        mAnimator.start();

        invalidate();
    }

    // PROTECTED API

    @Override
    protected void onDraw(Canvas pCanvas) {
        super.onDraw(pCanvas);

        // draw the outer circle
        mCirclePaint.setColor(getContext().getResources().getColor(R.color.white));
        mCirclePaint.setStrokeWidth(STROKEWIDTH_OUTER_CIRCLE);
        pCanvas.drawArc(new RectF(STROKEWIDTH_OUTER_CIRCLE / 2, STROKEWIDTH_OUTER_CIRCLE / 2, pCanvas.getWidth() - STROKEWIDTH_OUTER_CIRCLE / 2, pCanvas.getHeight() - STROKEWIDTH_OUTER_CIRCLE / 2), 135, 270, false, mCirclePaint);
        float ratio = (float) mValue / (float) mMaxValue;
        int rpmAngle = (int) (ratio * 270);

        // draw the inner circle
        mCirclePaint.setColor(getContext().getResources().getColor(R.color.red));
        mCirclePaint.setStrokeWidth(STROKEWIDTH_INNER_CIRCLE);
        int padding = STROKEWIDTH_OUTER_CIRCLE + SPACE + STROKEWIDTH_INNER_CIRCLE / 2;
        pCanvas.drawArc(new RectF(padding, padding, pCanvas.getWidth() - padding, pCanvas.getHeight() - padding), 135, rpmAngle, false, mCirclePaint);

        // draw the text
        mTextPaint.setTextSize(60);

        int xPos = (pCanvas.getWidth() / 2);
        int yPos = (int) ((pCanvas.getHeight() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));

        pCanvas.drawText("" + mValue, xPos, yPos, mTextPaint);

        Rect rect = new Rect();
        mTextPaint.setTextSize(40);
        mTextPaint.getTextBounds(mText, 0, mText.length(), rect);

        pCanvas.drawText(mText, xPos, pCanvas.getHeight() - rect.height(), mTextPaint);
    }

    @Override
    protected void onMeasure(int pWidthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(pWidthMeasureSpec, pWidthMeasureSpec);
    }

    /**
     * Sets the value that shall be shown as the circle.
     *
     * @param pValue the new value
     */
    protected void setValue(int pValue) {
        mValue = pValue;
        invalidate();
    }

}
