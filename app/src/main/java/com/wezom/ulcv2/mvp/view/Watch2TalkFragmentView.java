package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.net.models.Talk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: Zorin A.
 * Date: 23.02.2017.
 */

public interface Watch2TalkFragmentView extends  ListLceView<ArrayList<Talk>> {
    void setCategories(List<Category> categories);

    void showCategoryProgress(boolean isShow);

    void showCategoryError(boolean isShow);
}
