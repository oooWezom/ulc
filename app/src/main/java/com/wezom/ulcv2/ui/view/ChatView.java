package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.wezom.ulcv2.R;

import butterknife.BindView;
import butterknife.OnClick;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 15.07.2016
 * Time: 10:38
 */
@Accessors(prefix = "m")
public class ChatView extends BaseView {

    @BindView(R.id.view_chat_message_edit_text)
    EditText mInput;

    private Callback mCallback;

    public ChatView(Context context) {
        super(context);
        initViews();
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    @Override
    protected int getLayout() {
        return R.layout.view_chat;
    }

    private void initViews() {
        if(isInEditMode()){
            return;
        }
        setBackgroundResource(R.color.color_white);
        mInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mCallback != null) {
                    mCallback.onSwitchChat(hasFocus);
                }
            }
        });
    }

    @OnClick(R.id.view_chat_message_edit_text)
    void click(){
        if (mCallback != null) {
            mCallback.onSwitchChat(true);
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onSwitchChat(boolean isOpened);
    }
}