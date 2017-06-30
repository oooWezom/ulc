package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.injection.qualifier.ActivityContext;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.ui.view.SearchItemView;
import com.wezom.ulcv2.ui.view.TalkUserSearchItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by zorin.a on 17.06.2016.
 */

public class TalkUserSearchAdapter extends BaseAdapter<Profile, TalkUserSearchItemView, BaseViewHolder<TalkUserSearchItemView>> {

    private Context mContext;
    private EventBus mBus;

    @Inject
    public TalkUserSearchAdapter(@ActivityContext Context context, EventBus bus) {
        mContext = context;
        mBus = bus;
    }

    @Override
    public BaseViewHolder<TalkUserSearchItemView> onCreateViewHolder(ViewGroup parent, int viewType) {

        return new BaseViewHolder<>(new TalkUserSearchItemView(mContext, mBus));
    }
}
