package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 21.01.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class LanguageResponse extends Response {

    @SerializedName("response")
    private ArrayList<Language> mResponse;

    @Data
    @Accessors(prefix = "m")
    public class Language {

        @SerializedName("id")
        private int mId;
        @SerializedName("display_name")
        private String mDisplayName;
    }
}
