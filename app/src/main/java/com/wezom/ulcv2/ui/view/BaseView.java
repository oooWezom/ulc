package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

import static com.wezom.ulcv2.common.Constants.CONTENT_DEV_URL;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 15.07.2016
 * Time: 10:12
 */

public abstract class BaseView extends RelativeLayout {

    public BaseView(Context context) {
        super(context);
        bindView(getLayout());
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bindView(getLayout());
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bindView(getLayout());
    }

    protected void bindView(@LayoutRes int layoutRes) {
        if (isInEditMode()) {
            return;
        }

        if (layoutRes != 0) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(layoutRes, this);
            ButterKnife.bind(this);
        }
    }

    @LayoutRes
    protected abstract int getLayout();

    protected void loadImage(ImageView imageView, String url, @DrawableRes int errorDrawableRes) {
        final String fullUrl = !TextUtils.isEmpty(url) ? CONTENT_DEV_URL + url : null;

        Picasso.with(getContext())
                .load(!TextUtils.isEmpty(fullUrl) ? fullUrl : "http://")
                .placeholder(errorDrawableRes)
                .error(errorDrawableRes)
                .into(imageView);
    }
}