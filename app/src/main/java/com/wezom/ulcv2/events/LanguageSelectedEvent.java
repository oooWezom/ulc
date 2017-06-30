package com.wezom.ulcv2.events;

import com.wezom.ulcv2.mvp.model.Language;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 10.06.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class LanguageSelectedEvent {
    List<Language> mLanguageList;
}
