package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.injection.qualifier.ActivityContext;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.ui.view.DialogItemView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 22.01.2016.
 */
public class DialogAdapter extends BaseAdapter<Dialog, DialogItemView, BaseViewHolder<DialogItemView>> {

    private Context mContext;
    private EventBus mBus;
    private String mUserAvatar;
    private String mFilter = "";
    private List<Dialog> mFilteredData;

    @Inject
    public DialogAdapter(@ActivityContext Context context, EventBus bus, String userAvatar) {
        mContext = context;
        mBus = bus;
        mUserAvatar = userAvatar;
    }

    @Override
    public BaseViewHolder<DialogItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new DialogItemView(mContext, mBus, mUserAvatar));
    }

    public void filter(String filter) {
        mFilter = filter;
        applyFilter();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mFilter.isEmpty()) {
            return super.getItemCount();
        } else {
            return mFilteredData.size();
        }
    }

    @Override
    public Dialog getData(int position) {
        if (mFilter.isEmpty()) {
            return super.getData(position);
        } else {
            return mFilteredData.get(position);
        }
    }

    private void applyFilter() {
        mFilteredData = new ArrayList<>();
        for (Dialog dialog : mContent) {
            if (dialog.getPartner().getName().toLowerCase().contains(mFilter.toLowerCase())) {
                mFilteredData.add(dialog);
            }
        }
    }
}
