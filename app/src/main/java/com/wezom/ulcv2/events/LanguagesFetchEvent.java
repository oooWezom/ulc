package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.LanguageResponse;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 10.06.2016.
 */
@Accessors(prefix = "m")
@AllArgsConstructor
@Data
public class LanguagesFetchEvent {
    private LanguageResponse mLanguages;
}
