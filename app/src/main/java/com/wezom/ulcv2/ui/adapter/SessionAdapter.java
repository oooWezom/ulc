package com.wezom.ulcv2.ui.adapter;

import android.graphics.Color;
import android.view.ViewGroup;


import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.net.models.responses.websocket.ChatMessageResponse;
import com.wezom.ulcv2.ui.viewholder.BaseViewHolder;
import com.wezom.ulcv2.ui.views.MessageView;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 01.03.2016.
 */
public class SessionAdapter extends BaseAdapter<ChatMessageResponse, MessageView, BaseViewHolder<MessageView>> {

    private HashMap<Integer, Integer> mNameColorMap;

    @Inject
    public SessionAdapter() {
        mNameColorMap = new HashMap<>();
    }

    @Override
    public BaseViewHolder<MessageView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new MessageView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<MessageView> holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.getView().setNameColor(getCorrespondingColor(getData(position).getUser().getId()));
    }

    private int getCorrespondingColor(int id) {
        if (mNameColorMap.containsKey(id)) {
            return mNameColorMap.get(id);
        } else {
            float[] hsv = {Utils.randInt(0, 360), 50, 50};
            int color = Color.HSVToColor(hsv);
            mNameColorMap.put(id, color);
            return color;
        }
    }
}
