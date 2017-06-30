package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.SendTalkMessageEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.mvp.view.ChatFragmentView;
import com.wezom.ulcv2.net.models.Profile;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created: Zorin A.
 * Date: 23.09.2016.
 */
@InjectViewState
public class ChatPresenter extends BasePresenter<ChatFragmentView> {
    @Inject
    EventBus mBus;
    @Inject
    @ApplicationContext
    Context mContext;

    @Inject
    PreferenceManager mPreferenceManager;

    public ChatPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void sendMessage(String message) {

        if (Utils.isUserLoggedIn(mContext)) {
            mBus.post(new SendTalkMessageEvent(message));
            Profile data = mPreferenceManager.getProfileData();
            NewMessage newMessage = new NewMessage();

            NewMessage.User user = new NewMessage.User();
            user.setId(data.getId());
            user.setName(data.getName());
            user.setLevel(data.getLevel());
            user.setSex(data.getSex().getId());

            newMessage.setUser(user);
            newMessage.setText(message);

            getViewState().onMessageSent(newMessage);
        } else {
            getViewState().showCloseSessionDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void clearChat() {
        getViewState().onChatClear();
    }
}
