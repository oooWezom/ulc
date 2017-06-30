package com.wezom.ulcv2.ui.adapter.decorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zorin.a on 22.06.2016.
 */
public class ItemListQuadDividerDecorator extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mPaddingLeft;

    public ItemListQuadDividerDecorator(Context context, int drawable) {
        mDivider = context.getResources().getDrawable(drawable);
    }

    public void setPaddingLeft(int paddingLeft) {
        mPaddingLeft = paddingLeft;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + mPaddingLeft;
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount() - 1;

        for (int i = 0; i < childCount; i++) {
            if (i % 4 == 0) {

                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
