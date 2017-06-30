package com.wezom.ulcv2.ui.adapter;

import android.view.ViewGroup;

import com.wezom.ulcv2.interfaces.OnItemSelectListener;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.ui.view.CategoryItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import javax.inject.Inject;

/**
 * Created by zorin.a 12.07.2016
 */
public class CategoriesAdapter extends BaseAdapter<Category, CategoryItemView, BaseViewHolder<CategoryItemView>> {
    OnItemSelectListener mListener;
    private boolean isExtendedView;

    @Inject
    public CategoriesAdapter() {
    }

    @Override
    public BaseViewHolder<CategoryItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new CategoryItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CategoryItemView> holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.getView().setListener(mListener);
    }

    public void setListener(OnItemSelectListener listener) {
        mListener = listener;
    }

    public void setExtendedView(boolean extendedView) {
        isExtendedView = extendedView;
    }
}
