package hoppingvikings.housefinancemobile.UserInterface.Lists;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import hoppingvikings.housefinancemobile.R;

/**
 * Created by Josh on 10/02/2017.
 */

public class ListItemDivider extends RecyclerView.ItemDecoration {
    private Drawable _divider;

    public ListItemDivider(Context context)
    {
        _divider = context.getResources().getDrawable(R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for(int i = 0; i < childCount; i++)
        {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + _divider.getIntrinsicHeight();

            _divider.setBounds(left, top, right, bottom);
            _divider.draw(c);
        }
    }
}
