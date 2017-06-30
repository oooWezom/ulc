package com.wezom.ulcv2.interfaces;

import android.support.v7.widget.Toolbar;

/**
 * Created: Zorin A.
 * Date: 06.06.2016.
 */
public interface ToolbarActions {

    void showDrawerToggleButton();

    void showDrawerToggleButton(Toolbar toolbar);

    Toolbar getToolBar();

    void setSupportToolBar(Toolbar toolbar);
}
