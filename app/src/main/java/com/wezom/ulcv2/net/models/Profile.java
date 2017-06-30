package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.mvp.model.Game;

import java.util.ArrayList;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
@Data
@Accessors(prefix = "m")
public class Profile {

    @SerializedName("id")
    private int mId;
    @SerializedName("account_status_id")
    private int mAccountStatusId;
    @SerializedName("disabled")
    private boolean mDisabled;
    @SerializedName("login")
    private String mLogin;
    @SerializedName("name")
    private String mName;
    @SerializedName("level")
    private int mLevel;
    @SerializedName("exp")
    private long mExp;
    @SerializedName("exp_max")
    private long mExpMax;
    @SerializedName("likes")
    private int mLikes;
    @SerializedName("games_total")
    private int mGamesTotal;
    @SerializedName("games_win")
    private int mGamesWin;
    @SerializedName("games_loss")
    private int mGamesLoss;
    @SerializedName("talks")
    private int mTalks;
    @SerializedName("status")
    private Status mStatus;
    @SerializedName("link")
    private int mLink;
    @SerializedName("block")
    private int mBlock;
    @SerializedName("following")
    private int mFollowing;
    @SerializedName("followers")
    private int mFollowers;
    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("background")
    private String mBackground;
    @SerializedName("sex")
    private Sex mSex;
    @SerializedName("about")
    private String mAbout;
    @SerializedName("warning_ban")
    private boolean mWarningBan;
    @SerializedName("block_messages")
    private boolean mBlockMessages;
    @SerializedName("languages")
    private ArrayList<Integer> mLanguages;
    @SerializedName("warnings")
    private ArrayList<Warning> mWarnings;
    @SerializedName("game_session")
    private Session mGameSession;
    @SerializedName("talk")
    private Talk mTalk;
    @SerializedName("game")
    private Session mGame;
}
