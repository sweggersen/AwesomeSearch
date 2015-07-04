package info.awesomedevelopment.awesomesearch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

/**
 * Created by Sam Mathias Weggersen on 03/07/15.
 */
public class AwesomeSearchLayout extends RelativeLayout {

    private OnBackPressedListener mListener;;

    public AwesomeSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AwesomeSearchLayout(Context context) {
        super(context);
    }

    public void setSearchActivity(OnBackPressedListener listener) {
        mListener = listener;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (mListener != null
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            return mListener.onBackPressed();
        }

        return super.dispatchKeyEventPreIme(event);
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }
}
