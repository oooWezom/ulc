package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wezom.ulcv2.R;

/**
 * Created: Zorin A.
 * Date: 14.06.2016.
 */

public class ProfileToggleButtonView extends RelativeLayout {

    TextView mTitle;
    ImageView mIcon;
    View mRoot;

    OnClickListener mClickListener;

    private boolean mButtonState;
    private String mPositiveTitleString;
    private String mNegativeTitleString;
    private Drawable mPositiveIconDrawable;
    private Drawable mNegativeIconDrawable;

    public ProfileToggleButtonView(Context context) {
        super(context);
        init(context);
    }

    public ProfileToggleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_profile_toggle_button, this);
        mTitle = (TextView) v.findViewById(R.id.view_toggle_button_text);
        mIcon = (ImageView) v.findViewById(R.id.view_toggle_button_icon);
        mRoot = v.findViewById(R.id.view_toggle_button_root);
        mRoot.setOnClickListener(v1 -> {
            toggleClick();
        });
    }


    public void toggleClick() {
        switchState();
        refreshLayout();
        if (mClickListener != null) {
            mClickListener.onClick(this);
        }
    }

    private void refreshLayout() {
        if (!mButtonState) {
            setPositiveState();
        } else {
            setNegativeState();
        }
    }

    private void setPositiveState() {
        mTitle.setText(mPositiveTitleString);
        mIcon.setImageDrawable(mPositiveIconDrawable);
    }

    private void setNegativeState() {
        mTitle.setText(mNegativeTitleString);
        mIcon.setImageDrawable(mNegativeIconDrawable);
    }

    private void switchState() {
        mButtonState = !mButtonState;
    }

    public boolean getState() {
        return mButtonState;
    }

    public void setState(boolean state) {
        mButtonState = state;
        refreshLayout();
    }

    public void setPositiveTitle(String title) {
        mPositiveTitleString = title;
    }

    public void setNegativeTitle(String title) {
        mNegativeTitleString = title;
    }

    public void setPositiveIcon(Drawable icon) {
        mPositiveIconDrawable = icon;
    }

    public void setNegativeIcon(Drawable icon) {
        mNegativeIconDrawable = icon;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mClickListener = onClickListener;
    }


}
