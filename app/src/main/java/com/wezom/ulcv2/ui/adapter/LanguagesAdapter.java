package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.injection.qualifier.ActivityContext;
import com.wezom.ulcv2.interfaces.OnCheckChangeListener;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.ui.view.LanguageItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import javax.inject.Inject;

/**
 * Created by zorin.a on 09.06.2016.
 */
public class LanguagesAdapter extends BaseAdapter<Language, LanguageItemView, BaseViewHolder<LanguageItemView>> {

    private Context mContext;
    private OnCheckChangeListener mListener;

    @Inject
    public LanguagesAdapter(@ActivityContext Context context) {
        mContext = context;
    }

    @Override
    public BaseViewHolder<LanguageItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new LanguageItemView(mContext));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<LanguageItemView> holder, int position) {
        super.onBindViewHolder(holder, position);

        LanguageItemView itemView = holder.getView();
        itemView.setItemPosition(position);
        if (mListener != null) {
            itemView.setOnCheckChangeListener(mListener);
        }
    }

    public void setOnCheckChangeListener(OnCheckChangeListener listener) {
        mListener = listener;
    }
}
