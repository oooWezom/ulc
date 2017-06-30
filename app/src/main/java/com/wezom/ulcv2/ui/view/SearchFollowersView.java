package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.wezom.ulcv2.R;

import butterknife.ButterKnife;

/**
 * Created by Sivolotskiy.v on 25.05.2016.
 */
public class SearchFollowersView extends RelativeLayout {

    public SearchFollowersView(Context context) {
        super(context);
        init(context);
    }

    public SearchFollowersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_search, this);
        ButterKnife.bind(this);
    }
}
