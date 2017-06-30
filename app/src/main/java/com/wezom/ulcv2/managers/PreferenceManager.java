package com.wezom.ulcv2.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.mvp.model.UrlsModel;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.Sex;
import com.wezom.ulcv2.net.models.Status;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Created by zorin.a on 22.01.2017.
 */
public class PreferenceManager {

    public static final String PREFERENCES_NAME_KEY = "ulc_prefs";
    public static final String TOKEN_KEY = "auth_token";
    private SharedPreferences mSharedPreferences;

    @Inject
    public PreferenceManager(@ApplicationContext Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME_KEY, 0);
    }


    public static void setTempContentDevUrl(Context context, String url) {
        context.getSharedPreferences("api", 0).edit().putString("temp_content_url", url).apply();
    }

    public static String getTempContentDevUrl(Context context) {
        return context.getSharedPreferences("api", 0).getString("temp_content_url", "dev.ulc.network/user_content/");
    }

    public static void setWsProfileUrl(Context context, String url) {
        context.getSharedPreferences("api", 0).edit().putString("wsProfile", url).apply();
    }

    public static String getWsProfileUrl(Context context) {
        return context.getSharedPreferences("api", 0).getString("wsProfile", "ws://dev.ulc.network/ws/profile");
    }

    public static void setWsSessionUrl(Context context, String url) {
        context.getSharedPreferences("api", 0).edit().putString("wsSession", url).apply();
    }

    public static String getWsSessionUrl(Context context) {
        return context.getSharedPreferences("api", 0).getString("wsSession", "ws://dev.ulc.network/ws/session/");
    }

    public static int getUserId(Context context) {
        return context.getSharedPreferences("Main", 0).getInt("id", 0);
    }

    public String getAuthToken() {
        return mSharedPreferences.getString(TOKEN_KEY, "");
    }

    public void setAuthToken(String authToken) {
        mSharedPreferences.edit().putString(TOKEN_KEY, authToken).apply();
    }

    public void clearToken() {
        setAuthToken("");
    }

    public boolean getPushEnabled() {
        return mSharedPreferences.getBoolean("push_enabled", true);
    }

    public void setPushEnabled(boolean enabled) {
        mSharedPreferences.edit().putBoolean("push_enabled", enabled).apply();
    }

    public String getUserAvatar() {
        return mSharedPreferences.getString("user_avatar", "");
    }

    public void setUserAvatar(String avatar) {
        mSharedPreferences.edit().putString("user_avatar", avatar).apply();
    }

    public void setPrivateProfile(int status) {
        mSharedPreferences.edit().putInt("account_status", status).apply();
    }

    public Profile getProfileData() {
        if (!mSharedPreferences.contains("name")) {
            return null;
        }
        Profile profile = new Profile();
        profile.setName(mSharedPreferences.getString("name", ""));
        profile.setLevel(mSharedPreferences.getInt("level", 0));
        profile.setSex(Sex.getSexById(mSharedPreferences.getInt("sex", 0)));
        profile.setExp(mSharedPreferences.getLong("exp", 0));
        profile.setExpMax(mSharedPreferences.getLong("exp_max", 1));
        profile.setGamesTotal(mSharedPreferences.getInt("played_games", 0));
        profile.setLikes(mSharedPreferences.getInt("likes", 0));
        profile.setGamesWin(mSharedPreferences.getInt("wins", 0));
        profile.setGamesLoss(mSharedPreferences.getInt("loses", 0));
        profile.setFollowers(mSharedPreferences.getInt("followers", 0));
        profile.setFollowing(mSharedPreferences.getInt("following", 0));
        profile.setAbout(mSharedPreferences.getString("about", ""));
        profile.setAvatar(mSharedPreferences.getString("avatar_url", ""));
        profile.setBackground(mSharedPreferences.getString("background_url", ""));
        profile.setLogin(mSharedPreferences.getString("login", ""));
        profile.setStatus(Status.ONLINE);
        profile.setAccountStatusId(mSharedPreferences.getInt("account_status", 1));
        profile.setTalks(mSharedPreferences.getInt("talks", 0));
        return profile;
    }

    public void setProfileData(Profile profile) {
        mSharedPreferences.edit().putString("name", profile.getName())
                .putInt("level", profile.getLevel())
                .putInt("sex", profile.getSex().getId())
                .putLong("exp", profile.getExp())
                .putLong("exp_max", profile.getExpMax())
                .putInt("played_games", profile.getGamesTotal())
                .putInt("wins", profile.getGamesWin())
                .putInt("loses", profile.getGamesLoss())
                .putInt("followers", profile.getFollowers())
                .putInt("following", profile.getFollowing())
                .putString("about", profile.getAbout())
                .putString("login", profile.getLogin())
                .putString("avatar_url", profile.getAvatar())
                .putString("background_url", profile.getBackground())
                .putInt("account_status", profile.getAccountStatusId())
                .putInt("talks", profile.getTalks())
                .apply();
    }

    public int getUserId() {
        return mSharedPreferences.getInt("id", 0);
    }

    public void setUserId(int id) {
        mSharedPreferences.edit().putInt("id", id).apply();
    }

    public FilterPrefs getFilterPrefs() {
        boolean games = mSharedPreferences.getBoolean("filter_games", true);
        boolean follows = mSharedPreferences.getBoolean("filter_follows", true);
        boolean levelUps = mSharedPreferences.getBoolean("filter_talks", true);
        return new FilterPrefs(games, follows, levelUps);
    }

    public void setFilterPrefs(FilterPrefs prefs) {
        mSharedPreferences.edit()
                .putBoolean("filter_games", prefs.isGames())
                .putBoolean("filter_follows", prefs.isFollows())
                .putBoolean("filter_talks", prefs.isTalkss())
                .apply();

    }

    public void clearData() {
        setAuthToken("");
        mSharedPreferences.edit().clear().apply();
    }

    public int getMaxUserLevel() {
        return mSharedPreferences.getInt("max_user_level", 0);
    }

    public void setMaxUserLevel(int maxLevel) {
        mSharedPreferences.edit().putInt("max_user_level", maxLevel).apply();
    }

    public String getSelectedLanguage() {
        return mSharedPreferences.getString("selected_language", "");
    }

    public void setSelectedLanguage(String language) {
        mSharedPreferences.edit().putString("selected_language", language).apply();
    }


    //TODO: for testing purpose
    public void setIsDevServ(boolean isDev) {
        mSharedPreferences.edit().putBoolean("id_dev", isDev).apply();
    }

    public boolean isDevServ() {
        return mSharedPreferences.getBoolean("id_dev", false);
    }

    public static UrlsModel getBaseUrl(Context context) {
        String restUrl = context.getSharedPreferences(PREFERENCES_NAME_KEY, 0).getString("base_url", "https://ulc.tv");
        String socketUrl = context.getSharedPreferences(PREFERENCES_NAME_KEY, 0).getString("base_socket_url", "wss://ulc.tv");
        Log.d("Log_ get URLS ", restUrl +"\n"+socketUrl);
        return new UrlsModel(restUrl, socketUrl);
    }

    public static void setBaseUrl(Context context, String restUrl, String socketUrl) {
        Log.d("Log_ set URLS ", restUrl +"\n"+socketUrl);
        context.getSharedPreferences(PREFERENCES_NAME_KEY, 0).edit().putString("base_url", restUrl).apply();
        context.getSharedPreferences(PREFERENCES_NAME_KEY, 0).edit().putString("base_socket_url", socketUrl).apply();
    }



    @Data
    @AllArgsConstructor
    @Accessors(prefix = "m")
    public static class FilterPrefs {
        private boolean mGames;
        private boolean mFollows;
        private boolean mTalkss;
    }
}
