package com.wezom.ulcv2.common;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.mvp.model.Game;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;

import static com.wezom.ulcv2.common.Constants.Games.HELICOPTER;
import static com.wezom.ulcv2.common.Constants.Games.MEMORIZE_HIDES;
import static com.wezom.ulcv2.common.Constants.Games.RANDOM;
import static com.wezom.ulcv2.common.Constants.Games.ROCK_SPOCK;
import static com.wezom.ulcv2.common.Constants.Games.SPIN_THE_DISKS;
import static com.wezom.ulcv2.common.Constants.Games.UFO;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
public class Constants {

    public final static int LOADING_MODE_INITIAL = 0;
    public final static int LOADING_MODE_REFRESH = 1;
    public final static int LOADING_MODE_ENDLESS = 2;
    public final static int CONNECTION_MODE_CONNECT = 0;
    public final static int CONNECTION_MODE_DISCONNECT = 1;
    public final static int CONNECTION_MODE_RECONNECT = 2;
    public static final int GENDER_FEMALE = 2;
    public static final int GENDER_MALE = 1;
    public final static int API_PLATFORM_ID = 2; // android
    public final static int API_VERSION = 1;
    public final static int GAME_COUNTDOWN_NUMBER = 3;
    public final static int[] SUPPORTED_GAMES = {
            Games.SPIN_THE_DISKS.getId(),
            Games.ROCK_SPOCK.getId()};
    public static final int LOAD_ITEMS_LIMIT = 20;

    public static final int VERSION_API = 1;

    //Languages
    public static final int ENGLISH = 0;
    public static final int RUSSIAN = 1;

    //Player states
    public static final int OFFLINE = 1;
    public static final int ONLINE = 2;
    public static final int SEAERCHING = 3;
    public static final int WATCHING = 4;
    public static final int PLAYING = 5;
    public static final int TALKING = 6;

    public static final int DISCONNECTED = 4;
    public static final int NEW_SESSION = 4;

    //reports
    public static final int REPORT_NUDITY = 1;
    public static final int REPORT_VIOLENCE = 2;
    public static final int REPORT_HARASSMENT = 3;


    //url constants
    public static String SOCKET_BASE_URL = "wss://ulc.tv";
    public static String API_URL = "https://ulc.tv";
    public static String ICONS_BASE_URL = API_URL + "/images/mobile/";
    public static String CONTENT_DEV_URL = API_URL + "/user_content/";
    public static String WEBSOCKET_PROFILE_URL = "/ws/profile";
    public static String WEBSOCKET_SESSION_URL = "/ws/session/";
    public static String WEBSOCKET_TALK_URL = "/ws/talk/";
    public static String ACTIVE_SESSIONS_PREVIEW_URL = API_URL + "/preview/";
    public static String GAMES_IMAGES_URL = API_URL + "/images/games/common/"; //in Unity
    public static String VOD_BASE_URL = API_URL + "/vod/index/"; //link to m3u file


    public static List<Game> getGamesList(Context context) {
        List<Game> games = new ArrayList<>();
        Game random = new Game(getFromRes(context, RANDOM.getResId()), RANDOM.getId(), RANDOM.getId(), null, RANDOM.getResId(), false, null);
        Game helicopter = new Game(getFromRes(context, HELICOPTER.getResId()), HELICOPTER.getId(), HELICOPTER.getId(), null, HELICOPTER.getResId(), false, null);
        Game rockSpock = new Game(getFromRes(context, ROCK_SPOCK.getResId()), ROCK_SPOCK.getId(), ROCK_SPOCK.getId(), null, ROCK_SPOCK.getResId(), false, null);
        Game spinTheDisks = new Game(getFromRes(context, SPIN_THE_DISKS.getResId()), SPIN_THE_DISKS.getId(), SPIN_THE_DISKS.getId(), null, SPIN_THE_DISKS.getResId(), false, null);
        Game memorizeHides = new Game(getFromRes(context, MEMORIZE_HIDES.getResId()), MEMORIZE_HIDES.getId(), MEMORIZE_HIDES.getId(), null, MEMORIZE_HIDES.getResId(), false, null);
        Game ufo = new Game(getFromRes(context, UFO.getResId()), UFO.getId(), UFO.getId(), null, UFO.getResId(), false, null);

        games.add(random);
        games.add(helicopter);
        games.add(rockSpock);
        games.add(spinTheDisks);
        games.add(memorizeHides);
        games.add(ufo);
        return games;
    }

    private static String getFromRes(Context context, int res) {
        return context.getResources().getString(res);
    }

    public enum LayoutMode {
        NOT_LOGGED_IN, NOT_LOGGED_IN_WITH_TOOLBAR, LOGGED_IN, LOGGED_IN_WITHOUT_TOOLBAR
    }

    @Getter
    @Accessors(prefix = "m")
    public enum Games {
        RANDOM(0, R.string.random_game),
        HELICOPTER(5, R.string.helicopter),
        ROCK_SPOCK(10, R.string.rock_spock),
        SPIN_THE_DISKS(7, R.string.spin_the_disks),
        MEMORIZE_HIDES(8, R.string.memorize_hides),
        UFO(9, R.string.ufo);

        private int mId;
        private int mResId;

        Games(int id, int gameNameResId) {
            mId = id;
            mResId = gameNameResId;
        }

        @Nullable
        public static Integer getResNameById(int id) {
            Integer finalName = null;
            for (Games game : Games.values()) {
                if (game.getId() == id) {
                    finalName = game.getResId();
                    break;
                }
            }
            return finalName;
        }
    }

    public enum DialogResult {
        OK,
        CANCEL,
        DELAY,
        TIME_IS_OVER
    }

    public enum InviteDialogType {
        INFO_MODE(100, R.string.app_name),
        WAITING_MODE(101, R.string.app_name),
        INVITES_MODE(102, R.string.app_name),
        DELAY_MODE(102, R.string.app_name);

        public int dialogId;
        public int titleRes;

        InviteDialogType(int dialogId, int titleRes) {
            this.dialogId = dialogId;
            this.titleRes = titleRes;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOADING_MODE_INITIAL, LOADING_MODE_REFRESH, LOADING_MODE_ENDLESS})
    public @interface LoadingMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONNECTION_MODE_CONNECT, CONNECTION_MODE_DISCONNECT, CONNECTION_MODE_RECONNECT})
    public @interface ConnectionMode {
    }
}
