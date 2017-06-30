package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.ui.fragment.VideoFragment.VideoMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created with IntelliJ IDEA.
 * User: zorin.a
 * Date: 27.09.2016
 * Time: 10:15
 */


public class TalkWebCamView extends BaseView implements ViewModel<User> {

    //region constants
    //view display modes
    public static final int WATCHER_SINGLE_WINDOW = 1;
    public static final int WATCHER_MULTI_WINDOW = 2;
    public static final int NO_CONTEXT_MENU = 3;
    public static final int STREAMER_MULTI_WINDOW = 4;

    private static final String TITLE = "title";
    private static final String ICON = "icon";
    private static final String ID = "id";
    //context menu items
    private static final int PREFER_ITEM = 0;
    private static final int FOLLOW_USER_ITEM = 1;
    private static final int REPORT_USER_ITEM = 2;
    private static final int REMOVE_USER_ITEM = 3;
    private static final int SWITCH_CAMERA = 4;
    //endregion

    //region var

    WebcamClickListener mListener;
    User mUserData;
    List<HashMap<String, Object>> items = null;
    @BindDimen(R.dimen.popup_window_offset)
    int mPopupWindowOffset;
    //region views
    @BindView(R.id.talk_webcam_view_avatar_image_view)
    CircularImageView mAvatarImageView;
    @BindView(R.id.talk_webcam_view_username_text_view)
    TextView mUserNameTextView;
    @BindView(R.id.talk_webcam_view_level_text_view)
    TextView mLevelTextView;

    //endregion
    private int mMenuMode;
    private VideoMode mLayoutMode;
    private boolean mIsPreferItemVisible;
    //endregion


    public TalkWebCamView(Context context, VideoMode mode) {
        super(context);
        init(context, mode);
    }

    public TalkWebCamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, VideoMode.LEFT);
    }

    private void init(Context context, VideoMode mode) {
        int layout = 0;
        mLayoutMode = mode;

        if (mode == mode.LEFT) {
            layout = R.layout.view_left_talk_webcam;
        }
        if (mode == mode.RIGHT) {
            layout = R.layout.view_right_talk_webcam;
        }

        LayoutInflater.from(context).inflate(layout, this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.talk_webcam_view_avatar_image_view)
    void onAvatarClick() {
        showPopupMenu();
    }

    public void setWebcamClickListener(WebcamClickListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayout() {
        return 0; //not used
    }

    @Override
    public void setData(@NonNull User userData) {
        mUserData = userData;
        loadImage(mAvatarImageView, userData.getAvatar(), R.drawable.bg_avatar_big_placeholder);
        mUserNameTextView.setText(userData.getName());
        mLevelTextView.setText(String.format(getContext().getString(R.string.level_format), userData.getLevel()));
    }

    private void showPopupMenu() {
        ListPopupWindow popupListWindow = new ListPopupWindow(getContext());
        defineMenuType();

        ListAdapter adapter = new SimpleAdapter(getContext(), items, R.layout.view_popup_item,
                new String[]{TITLE, ICON}, new int[]{R.id.view_popup_item_text, R.id.view_popup_item_icon});

        popupListWindow.setAnchorView(mAvatarImageView);
        popupListWindow.setAdapter(adapter);
        popupListWindow.setWidth(Utils.measureContentWidth(getContext(), adapter));
        popupListWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupListWindow.setVerticalOffset(mPopupWindowOffset);
        if (mLayoutMode == VideoMode.RIGHT) {
            popupListWindow.setHorizontalOffset(-mAvatarImageView.getWidth());
        }
        popupListWindow.setOnItemClickListener((parent, view, position, viewId) -> {

            int itemId = (int) items.get(position).get(ID);
            switch (itemId) {
                case PREFER_ITEM:
                    mListener.onSwitchTalk();
                    break;
                case FOLLOW_USER_ITEM:
                    mListener.onFollowStreamer();
                    break;
                case REPORT_USER_ITEM:
                    mListener.onReportStreamer();
                    break;
                case REMOVE_USER_ITEM:
                    mListener.onDetachTalk();
                    break;
                case SWITCH_CAMERA:
                    mListener.onSwitchCamera();
                    break;
            }
            popupListWindow.dismiss();
        });
        popupListWindow.show();
    }

    private void defineMenuType() {
        Log.d("Log_ defineMenuType", String.valueOf(mMenuMode));
        if (mMenuMode == NO_CONTEXT_MENU) {
            items = new ArrayList<>();
            items.add(new HashMap<String, Object>() {
                {
                    put(ID, SWITCH_CAMERA);
                    put(TITLE, "");
                    put(ICON, R.drawable.ic_switch_camera);
                }
            });
        }

        if (mMenuMode == WATCHER_MULTI_WINDOW) {
            items = prepareWatcherMultiUserMenu();
        }

        if (mMenuMode == STREAMER_MULTI_WINDOW) {
            items = prepareStreamerMenu();
        }

        if (mMenuMode == WATCHER_SINGLE_WINDOW) {
            items = prepareWatcherMenu();
        }
    }

    private List<HashMap<String, Object>> prepareWatcherMultiUserMenu() {
        List<HashMap<String, Object>> items = new ArrayList();

        if (mIsPreferItemVisible) {
            items.add(
                    new HashMap<String, Object>() {
                        {
                            put(ID, PREFER_ITEM);
                            put(TITLE, getContext().getString(R.string.prefer));
                            put(ICON, R.drawable.ic_prefer);
                        }
                    });
        }

        items.add(
                new HashMap<String, Object>() {
                    {
                        put(ID, FOLLOW_USER_ITEM);
                        put(TITLE, getContext().getString(R.string.follow_user));
                        put(ICON, R.drawable.ic_profile_follow);
                    }
                });

        items.add(
                new HashMap<String, Object>() {
                    {
                        put(ID, REPORT_USER_ITEM);
                        put(TITLE, getContext().getString(R.string.report_user));
                        put(ICON, R.drawable.ic_profile_report);
                    }
                });

        items.add(
                new HashMap<String, Object>() {
                    {
                        put(ID, REPORT_USER_ITEM);
                        put(TITLE, getContext().getString(R.string.report_user));
                        put(ICON, R.drawable.ic_profile_report);
                    }
                });

        return items;
    }

    private List<HashMap<String, Object>> prepareStreamerMenu() {
        List<HashMap<String, Object>> items = new ArrayList();

        if (mLayoutMode == VideoMode.RIGHT) {
            items.add(new HashMap<String, Object>() {
                {
                    put(ID, REMOVE_USER_ITEM);
                    put(TITLE, getContext().getString(R.string.remove_user));
                    put(ICON, R.drawable.ic_profile_block);
                }
            });


            items.add(new HashMap<String, Object>() {
                {
                    put(ID, FOLLOW_USER_ITEM);
                    put(TITLE, getContext().getString(R.string.follow_user));
                    put(ICON, R.drawable.ic_profile_follow);
                }
            });
        }

        return items;
    }

    private List<HashMap<String, Object>> prepareWatcherMenu() {
        List<HashMap<String, Object>> items = new ArrayList();
        items.add(new HashMap<String, Object>() {
            {
                put(ID, FOLLOW_USER_ITEM);
                put(TITLE, getContext().getString(R.string.follow_user));
                put(ICON, R.drawable.ic_profile_follow);
            }
        });
        items.add(new HashMap<String, Object>() {
            {
                put(ID, REPORT_USER_ITEM);
                put(TITLE, getContext().getString(R.string.report_user));
                put(ICON, R.drawable.ic_profile_report);
            }
        });

        return items;
    }


    public void setChecked(boolean isChecked) {
        int color;
        if (isChecked) {
            color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        } else {
            color = ContextCompat.getColor(getContext(), R.color.color_white);
        }
        mAvatarImageView.setBorderColor(color);
        mIsPreferItemVisible = !isChecked;
    }

    public void setMenuMode(int menuMode, boolean isPreferItemVisible) {
        mMenuMode = menuMode;
        mIsPreferItemVisible = isPreferItemVisible;
        setChecked(!isPreferItemVisible);

    }


    public interface WebcamClickListener {

        void onSwitchTalk();

        void onFollowStreamer();

        void onReportStreamer();

        void onDetachTalk();

        void onSwitchCamera();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_CONTEXT_MENU, WATCHER_MULTI_WINDOW, STREAMER_MULTI_WINDOW, WATCHER_SINGLE_WINDOW})
    public @interface WebcamViewModes {
    }
}
