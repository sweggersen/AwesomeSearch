package info.awesomedevelopment.awesomesearch;

import android.animation.Animator;
import android.os.Build;

public class AwesomeSearchUtils {

    public static boolean isJellyBeanOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static class SimpleAnimationListener implements Animator.AnimatorListener {
            @Override
            public void onAnimationStart(Animator animation) {

            };

            @Override
            public void onAnimationEnd(Animator animation) {

            };

            @Override
            public void onAnimationCancel(Animator animation) {

            };

            @Override
            public void onAnimationRepeat(Animator animation) {

            };
    }

    public static int getTransparentColor(int color, float alpha) {
        return (((int) (alpha * 255) & 0xFF) << 24) | (color & 0xFFFFFF);
    }
}
