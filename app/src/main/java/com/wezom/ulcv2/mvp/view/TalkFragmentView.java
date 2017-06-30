package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.net.models.Profile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 23.07.2016
 * Time: 21:40
 */

public interface TalkFragmentView extends SessionFragmentView<List<Profile>> {

    void setParticipantsData(UserData firstParticipantData, UserData secondParticipantData);

    void setTalkMode(int mode, boolean initInterface);

    void showTalkClosedMessage();

    void startWaitingForParticipant(long expires);

    void finishWaitingForParticipant(String message);

    void showDonateMessage(String message);
}
