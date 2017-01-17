package com.xpyct.apps.anilab.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xpyct.apps.anilab.R;

/**
 * ItemDecoration implementation that applies an inset margin
 * around each child of the RecyclerView. The inset value is controlled
 * by a dimension resource.
 * <p/>
 * by Dave Smith at: https://github.com/devunwired/recyclerview-playground
 */
public class RecyclerInsetsDecoration extends RecyclerView.ItemDecoration {

    private int mInsets_vertical;
    private int mInsets_horizontal;

    public RecyclerInsetsDecoration(Context context) {
        mInsets_vertical = context.getResources().getDimensionPixelSize(R.dimen.insets_vertical);
        mInsets_horizontal = context.getResources().getDimensionPixelSize(R.dimen.insets_horizontal);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //We can supply forced insets for each item view here in the Rect
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mInsets_vertical, mInsets_horizontal, mInsets_vertical, mInsets_horizontal);
    }
}