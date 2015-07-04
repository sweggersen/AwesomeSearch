package info.awesomedevelopment.awesomesearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Sam Mathias Weggersen on 03/07/15.
 */
public class AwesomeSearchDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    public AwesomeSearchDividerItemDecoration(Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.div_search_results);
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int right = parent.getWidth() - parent.getPaddingRight();
        int left = parent.getPaddingLeft();

        int count = parent.getChildCount();
        for (int i = 0; i < count-1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}