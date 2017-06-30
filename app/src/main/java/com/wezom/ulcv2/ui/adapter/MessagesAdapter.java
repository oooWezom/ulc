package com.wezom.ulcv2.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wezom.ulcv2.injection.qualifier.ActivityContext;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.ui.view.MessageView;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 25.01.2016.
 */
public class MessagesAdapter extends BaseAdapter<Message, MessageView, BaseViewHolder<MessageView>> {

    private Context mContext;

    @Inject
    public MessagesAdapter(@ActivityContext Context context) {
        mContext = context;
    }

    @Override
    public BaseViewHolder<MessageView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new MessageView(mContext, viewType));
    }

    @Override
    public int getItemViewType(int position) {
        return getData(position).getIsOut();
    }

    public void addData(Message message) {
        mContent.add(message);
        notifyItemInserted(mContent.size() - 1);
    }
}
