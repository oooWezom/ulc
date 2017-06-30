package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.injection.qualifier.ActivityContext;
import com.wezom.ulcv2.net.models.Follower;
import com.wezom.ulcv2.ui.view.FollowItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 19.01.2016.
 */
public class FollowsAdapter extends BaseAdapter<Follower, FollowItemView, BaseViewHolder<FollowItemView>> {

    private Context mContext;
    private EventBus mBus;

    @Inject
    public FollowsAdapter(@ActivityContext Context context,EventBus bus ) {
        mContext = context;
        mBus = bus;
    }

    @Override
    public BaseViewHolder<FollowItemView> onCreateViewHolder(ViewGroup parent, int viewType) {

        return new BaseViewHolder<>(new FollowItemView(mContext, mBus));
    }
}
