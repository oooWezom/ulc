package com.wezom.ulcv2.ui.adapter;

import android.view.ViewGroup;


import com.wezom.ulcv2.net.models.BlacklistRecord;
import com.wezom.ulcv2.ui.view.BlacklistView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 11.02.2016.
 */
public class BlacklistAdapter extends BaseAdapter<BlacklistRecord, BlacklistView,
        BaseViewHolder<BlacklistView>> {

    private EventBus mBus;

    @Inject
    public BlacklistAdapter(EventBus bus) {
        mBus = bus;
    }

    @Override
    public BaseViewHolder<BlacklistView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new BlacklistView(parent.getContext(), mBus));
    }
}
