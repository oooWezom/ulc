package com.wezom.ulcv2.ui.adapter;

import android.view.ViewGroup;

import com.wezom.ulcv2.interfaces.OnCheckListener;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.ui.view.GameItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import javax.inject.Inject;

/**
 * Created: Zorin A.
 * Date: 29.06.2016.
 */

public class GamesAdapter extends BaseAdapter<Game, GameItemView, BaseViewHolder<GameItemView>> {
    private OnCheckListener mListener;

    @Inject
    public GamesAdapter() {

    }

    @Override
    public BaseViewHolder<GameItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new GameItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<GameItemView> holder, int position) {
        super.onBindViewHolder(holder, position);
        GameItemView itemView = holder.getView();
        if (mListener != null) {
            itemView.setListener(mListener);
        }
    }

    public void setOnCheckChangeListener(OnCheckListener listener) {
        mListener = listener;
    }
}
