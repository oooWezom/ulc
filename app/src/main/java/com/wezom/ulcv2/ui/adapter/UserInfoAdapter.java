package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.ui.view.UserItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by sivolotskiy.v on 14.07.2016.
 */
public class UserInfoAdapter extends BaseAdapter<User, UserItemView, BaseViewHolder<UserItemView>> {

    private Context mContext;
    private EventBus mEventBus;

    @Inject
    public UserInfoAdapter(Context context, EventBus bus) {
        mContext = context;
        mEventBus = bus;
    }

    @Override
    public BaseViewHolder<UserItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new UserItemView(mContext, mEventBus));
    }
}
