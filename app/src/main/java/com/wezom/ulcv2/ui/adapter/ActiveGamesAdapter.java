package com.wezom.ulcv2.ui.adapter;

import android.view.ViewGroup;

import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.ui.view.ActiveGameItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 16.03.2016.
 */
public class ActiveGamesAdapter extends BaseAdapter<Session, ActiveGameItemView, BaseViewHolder<ActiveGameItemView>> {

    @Inject
    public ActiveGamesAdapter() {
    }

    @Override
    public BaseViewHolder<ActiveGameItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new ActiveGameItemView(parent.getContext()));
    }
}