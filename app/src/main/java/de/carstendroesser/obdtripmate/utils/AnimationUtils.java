package de.carstendroesser.obdtripmate.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by carstendrosser on 04.07.17.
 */

public class AnimationUtils {

    /**
     * Animates the height of a given view.
     *
     * @param pView         the view to animate it's height
     * @param pWantedHeight the height to animate to
     */
    public static void animateHeight(final View pView, int pWantedHeight) {
        ValueAnimator anim = ValueAnimator.ofInt(pView.getMeasuredHeight(), pWantedHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator pValueAnimator) {
                int value = (Integer) pValueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = pView.getLayoutParams();
                layoutParams.height = value;
                pView.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

}
