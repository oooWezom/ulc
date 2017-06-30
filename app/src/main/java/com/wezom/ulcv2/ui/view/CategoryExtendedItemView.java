package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.interfaces.OnItemSelectListener;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.ViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created: Zorin A.
 * Date: 12.07.2016.
 */
public class CategoryExtendedItemView extends FrameLayout implements ViewModel<Category> {
    OnItemSelectListener mListener;

    @BindView(R.id.view_category_item_image)
    ImageView mCategoryImageView;
    @BindView(R.id.view_category_item_choose_circle)
    ImageView mCircleImageView;
    @BindView(R.id.view_category_item_title)
    TextView mTitleTextView;

    private int mPosition;

    public CategoryExtendedItemView(Context context) {
        super(context);
        init(context);
    }

    public CategoryExtendedItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_category_extended_item, this);
        ButterKnife.bind(this);
    }

    @Override
    public void setData(Category data) {
        Picasso.with(getContext()).load(Utils.getCorrectCategoryIconSizeURL(getContext(), false, false) + data.getIcon())
                .resizeDimen(R.dimen.category_item_icon_size_image_big, R.dimen.category_item_icon_size_image_big)
                .into(mCategoryImageView);
        mTitleTextView.setText(data.getName());
        mCircleImageView.setVisibility(data.isChecked() ? VISIBLE : GONE);
    }

    @OnClick(R.id.view_category_item_ripple_layout)
    void onClick() {
        if (mListener != null) {
            mListener.onSelected(mPosition);
        }
    }

    public void setListener(OnItemSelectListener listener) {
        mListener = listener;
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}
