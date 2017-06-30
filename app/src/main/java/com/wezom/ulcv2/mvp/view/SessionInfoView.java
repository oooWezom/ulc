package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.net.models.Event;

/**
 * Created by sivolotskiy.v on 14.07.2016.
 */
public interface SessionInfoView extends BaseFragmentView {
    void setDetailedEvent(Event event);
}
