package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.interfaces.OnItemSelectListener;
import com.wezom.ulcv2.interfaces.OnNotificationClickListener;
import com.wezom.ulcv2.net.models.responses.websocket.NotificationResponse;
import com.wezom.ulcv2.ui.view.CategoryExtendedItemView;
import com.wezom.ulcv2.ui.view.NotificationView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Sivolotskiy.v on 19.07.2016.
 */
public class NotificationAdapter extends BaseAdapter<NotificationResponse, NotificationView, BaseViewHolder<NotificationView>> {

    private Context mContext;
    private EventBus mEventBus;
    OnNotificationClickListener mListener;

    @Inject
    public NotificationAdapter(Context context, EventBus bus) {
        mContext = context;
        mEventBus = bus;
    }

    @Override
    public BaseViewHolder<NotificationView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new NotificationView(mContext, mEventBus));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<NotificationView> holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.getView().setListener(mListener);
    }

    public void setListener(OnNotificationClickListener listener) {
        mListener = listener;
    }
}
