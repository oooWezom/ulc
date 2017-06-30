package com.wezom.ulcv2.ui.adapter;

import android.view.ViewGroup;

import com.wezom.ulcv2.interfaces.OnItemSelectListener;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.ui.view.CategoryExtendedItemView;
import com.wezom.ulcv2.ui.view.CategoryItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import javax.inject.Inject;

/**
 * Created by zorin.a 12.07.2016
 */
public class CategoriesExtendedAdapter extends BaseAdapter<Category, CategoryExtendedItemView, BaseViewHolder<CategoryExtendedItemView>> {
    OnItemSelectListener mListener;

    @Inject
    public CategoriesExtendedAdapter() {
    }

    @Override
    public BaseViewHolder<CategoryExtendedItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new CategoryExtendedItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CategoryExtendedItemView> holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.getView().setListener(mListener);
        holder.getView().setPosition(position);
    }

    public void setListener(OnItemSelectListener listener) {
        mListener = listener;
    }
}
