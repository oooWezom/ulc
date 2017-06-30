package com.wezom.ulcv2.ui.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.responses.websocket.ChatMessageResponse;
import com.wezom.ulcv2.ui.view.BaseView;

import butterknife.BindView;

/**
 * Created by kartavtsev.s on 01.03.2016.
 */
public class TalkMessageView extends BaseView implements ViewModel<NewMessage> {

    @BindView(R.id.view_talk_message_message_text)
    TextView mMessageTextView;

    private NewMessage mData;

    public TalkMessageView(Context context) {
        super(context);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_talk_chat_message;
    }

    @Override
    public void setData(NewMessage data) {
        mData = data;
    }

    public void setNameColor(int color) {
        SpannableString spannableString = new SpannableString(
                String.format("%s: %s", mData.getUser().getName(), mData.getText()));
        int nameLength = mData.getUser().getName().length() + 1;
        spannableString.setSpan(new ForegroundColorSpan(color), 0, nameLength, 0);
        spannableString.setSpan(new UnderlineSpan(), 0, nameLength, 0);
        mMessageTextView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }
}
