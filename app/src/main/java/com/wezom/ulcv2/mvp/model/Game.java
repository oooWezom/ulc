package com.wezom.ulcv2.mvp.model;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.User;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 29.06.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class Game {
    private String mTitle;
    @SerializedName("id")
    private int mGameId;
    @SerializedName("game")
    private int mGameType;
    @SerializedName("videoUrl")
    private String mVideoUrl;
    private int mResId;//res title
    private boolean mIsChecked;
    @SerializedName("players")
    private ArrayList<User> mUsers;
}
