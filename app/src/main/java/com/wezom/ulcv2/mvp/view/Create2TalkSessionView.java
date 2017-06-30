package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.Category;

import java.util.List;

/**
 * Created: Zorin A.
 * Date: 16.07.2016.
 */
public interface Create2TalkSessionView extends BaseFragmentView {
    void setData(List<Category> categories);
}
