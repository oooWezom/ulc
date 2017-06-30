package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.injection.qualifier.ActivityContext;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.net.models.Event;
import com.wezom.ulcv2.ui.view.EventItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 20.01.2016.
 */
public class EventsAdapter extends BaseAdapter<Event, EventItemView, BaseViewHolder<EventItemView>> {

    private Context mContext;
    private EventBus mBus;
    private ArrayList<Category> mCategories;

    @Inject
    public EventsAdapter(@ActivityContext Context context, EventBus bus, ArrayList<Category> categories) {
        mContext = context;
        mBus = bus;
        mCategories = categories;
    }

    @Override
    public BaseViewHolder<EventItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new EventItemView(mContext, mBus, mCategories));
    }
}
