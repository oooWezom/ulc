package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.Language;

import java.util.List;

/**
 * Created: Zorin A.
 * Date: 24.05.2016.
 */

public interface LanguagesFragmentView extends BaseFragmentView {
    void setLanguageData(List<Language> languages);
}
