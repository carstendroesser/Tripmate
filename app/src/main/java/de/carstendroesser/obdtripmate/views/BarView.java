package de.carstendroesser.obdtripmate.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by carstendrosser on 17.06.17.
 */

public class BarView extends View {

    // MEMBERS

    private int mBackgroundColor = Color.WHITE;
    private int mForegroundColor = Color.RED;
    private int mProgress = 0;
    private int mMaxProgress = 100;
    private Paint mPaint;
    private Orientation mOrientation = Orientation.VERTICAL;
    private static final int MAXDURATION = 1000;
    private ObjectAnimator mAnimator;

    // ENUMS

    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    // CONSTRUCTORS

    public BarView(Context pContext) {
        // chained constructor
        this(pContext, null);
    }

    public BarView(Context pContext, @Nullable AttributeSet pAttrs) {
        // chained constructor
        this(pContext, pAttrs, 0);
    }

    public BarView(Context pContext, @Nullable AttributeSet pAttrs, int pDefStyleAttr) {
        super(pContext, pAttrs, pDefStyleAttr);

        // setup the paint
        mPaint = new Paint();
        mPaint.setColor(mForegroundColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    // PUBLIC API

    /**
     * Sets the progress animated.
     *
     * @param pProgress the new progress
     */
    public void setProgressAnimated(int pProgress) {
        // stop the animator if it is currently running
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        // calculate percentage to know the animationduration later
        float percentage = (float) Math.abs(mProgress - pProgress) / (float) mMaxProgress;

        // animate!
        mAnimator = ObjectAnimator.ofInt(this, "Progress", mProgress, pProgress);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration((long) (percentage * MAXDURATION));
        mAnimator.start();

        invalidate();
    }

    /**
     * Sets the maximum of progress this bar can show.
     *
     * @param pMaxProgress the maximum
     */
    public void setMaxProgress(int pMaxProgress) {
        mMaxProgress = pMaxProgress;
        invalidate();
    }

    /**
     * Sets the orientation, either horizontal or vertical.
     *
     * @param pOrientation either horizonta or vertical
     */
    public void setOrientation(Orientation pOrientation) {
        mOrientation = pOrientation;
    }

    // PROTECTED API

    /**
     * Sets the progress non-animated. Used by the animator.
     *
     * @param pProgress the progress to show
     */
    protected void setProgress(int pProgress) {
        mProgress = pProgress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas pCanvas) {
        super.onDraw(pCanvas);

        mPaint.setColor(mBackgroundColor);

        // draw the bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pCanvas.drawRoundRect(0, 0, pCanvas.getWidth(), pCanvas.getHeight(), 5, 5, mPaint);
        } else {
            pCanvas.drawRect(0, 0, pCanvas.getWidth(), pCanvas.getHeight(), mPaint);
        }

        // draw the progress
        mPaint.setColor(mForegroundColor);

        if (mOrientation == Orientation.VERTICAL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pCanvas.drawRoundRect(0, pCanvas.getHeight() * (mMaxProgress - mProgress) / mMaxProgress, pCanvas.getWidth(), pCanvas.getHeight(), 5, 5, mPaint);
            } else {
                pCanvas.drawRect(0, pCanvas.getHeight() * (mMaxProgress - mProgress) / mMaxProgress, pCanvas.getWidth(), pCanvas.getHeight(), mPaint);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pCanvas.drawRoundRect(0, 0, pCanvas.getWidth() * mProgress / mMaxProgress, pCanvas.getHeight(), 5, 5, mPaint);
            } else {
                pCanvas.drawRect(0, 0, pCanvas.getWidth() * mProgress / mMaxProgress, pCanvas.getHeight(), mPaint);
            }
        }
    }

}
