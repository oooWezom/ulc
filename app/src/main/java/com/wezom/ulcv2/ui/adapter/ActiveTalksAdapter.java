package com.wezom.ulcv2.ui.adapter;

import android.view.ViewGroup;

import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.ui.view.ActiveTalkItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by zorin.a 12.07.2016
 */
public class ActiveTalksAdapter extends BaseAdapter<Talk, ActiveTalkItemView, BaseViewHolder<ActiveTalkItemView>> {

    @Override
    public BaseViewHolder<ActiveTalkItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new ActiveTalkItemView(parent.getContext()));
    }
}
