package com.wezom.ulcv2.events;

import com.wezom.ulcv2.mvp.model.Language;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 27.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class LanguagesSelectedWithDelay {
    List<Language> mLanguageList;
}
