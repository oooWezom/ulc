package com.wezom.ulcv2.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.ui.fragment.PlayVideoFragment;
import com.wezom.ulcv2.ui.fragment.PlayVideoFragment.VideoMode;
import com.wezom.ulcv2.ui.fragment.VideoFragment;
import com.wezom.ulcv2.ui.views.HeartParticleView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import hugo.weaving.DebugLog;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static android.R.attr.mode;
import static com.wezom.ulcv2.ui.view.PlayWebCamView.WebCamAlignEnum.LEFT;
import static com.wezom.ulcv2.ui.view.PlayWebCamView.WebCamAlignEnum.RIGHT;

@Accessors(prefix = "m")
public class PlayWebCamView extends BaseView implements ViewModel<UserData> {

    private static final int FOLLOW_USER_ITEM = 0;
    private static final int REPORT_USER_ITEM = 1;
    private static final int SWITCH_CAMERA = 2;

    private static final String TITLE = "title";
    private static final String ICON = "icon";
    private static final String ID = "id";

    @BindView(R.id.talk_webcam_view_avatar_image_view)
    CircularImageView mAvatarImageView;
    @BindView(R.id.talk_webcam_view_username_text_view)
    TextView mUserNameTextView;
    @BindView(R.id.talk_webcam_view_level_text_view)
    TextView mLevelTextView;
    @Nullable
    @BindView(R.id.view_webcam_likes_text_view)
    TextView mLikesTextView;
    @Nullable
    @BindView(R.id.view_webcam_score_view)
    ScoreView mScoreView;
    @Nullable
    @BindView(R.id.view_webcam_exp_text_view)
    TextView mExpTextView;
    @Nullable
    @BindView(R.id.view_game_webcam_control_layout)
    RelativeLayout mParentLayout;
    @Getter
    @Nullable
    @BindView(R.id.view_webcam_add_participant_image_view)
    ImageView mAddParticipantImageView;

    @BindDimen(R.dimen.popup_window_offset)
    int mPopupWindowOffset;

    @Setter
    private Callback mCallback;
    private WebCamAlignEnum mWebCamAlign;
    private NumberFormat mExpNumberFormat;
    private UserData mUserData;
    private VideoMode mLayoutMode;
    private ListPopupWindow popupListWindow;
    boolean mIsShowingMenu = false;
    private boolean mIsPlayer;

    public PlayWebCamView(Context context, VideoMode mode, boolean isPlayer) {
        super(context);
        if (isInEditMode()) {
            return;
        }
        mIsPlayer = isPlayer;
        init(context, mode);
    }

    public PlayWebCamView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mExpNumberFormat = new DecimalFormat("+#;-#");
        init(context, VideoMode.LEFT);
    }

    private void init(Context context, VideoMode mode) {
        popupListWindow = new ListPopupWindow(getContext());

        int layout = 0;
        mLayoutMode = mode;

        if (mode == VideoMode.LEFT) {
            layout = R.layout.view_left_game_webcam;
        }
        if (mode == mode.RIGHT) {
            layout = R.layout.view_right_game_webcam;
        }

        LayoutInflater.from(context).inflate(layout, this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.talk_webcam_view_avatar_image_view)
    public void onAvatarClick() {
        showPopupMenu();
    }

    @Optional
    @OnClick(R.id.view_webcam_add_participant_image_view)
    public void onAddParticipantClick() {
        if (mCallback != null) {
            mCallback.onAddParticipant();
        }
    }


    @Override
    protected int getLayout() {
        return 0;
    }

    @DebugLog
    @Override
    public void setData(UserData userData) {
        mUserData = userData;
        if (userData != null) {
            loadImage(mAvatarImageView, userData.getAvatar(), R.drawable.bg_avatar_big_placeholder);
            mUserNameTextView.setText(userData.getName());
            mLevelTextView.setText(getResources().getString(R.string.lvl_format,
                    String.valueOf(userData.getLevel())));
        }
    }

    public void setLikes(long likes) {
        mLikesTextView.setText(String.valueOf(likes));
    }

    public void setScore(int score) {
        if (mScoreView != null) {
            mScoreView.setScore(score);
        }
    }

    public void setExp(String exp) {
        mScoreView.setVisibility(GONE);
        mExpTextView.setVisibility(VISIBLE);
        mExpTextView.setText(exp);
    }


    public void hideScore() {
        mScoreView.setVisibility(GONE);
    }

    public void showPopupMenu() {
        if (mIsShowingMenu) {
            mIsShowingMenu = false;
            return;
        }
        List<HashMap<String, Object>> items = new ArrayList();

        if (mIsPlayer) {
            items.add(new HashMap<String, Object>() {
                {
                    put(ID, SWITCH_CAMERA);
                    put(TITLE, "");
                    put(ICON, R.drawable.ic_switch_camera);
                }
            });
        } else {

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
        }
        ListAdapter adapter = new SimpleAdapter(
                getContext(), items,
                R.layout.view_popup_item, new String[]{TITLE, ICON},
                new int[]{R.id.view_popup_item_text, R.id.view_popup_item_icon});


        popupListWindow.setAnchorView(mAvatarImageView);
        popupListWindow.setAdapter(adapter);
        popupListWindow.setWidth(Utils.measureContentWidth(getContext(), adapter));
        popupListWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupListWindow.setVerticalOffset(mPopupWindowOffset);
        if (mWebCamAlign == RIGHT) {
            popupListWindow.setHorizontalOffset(-mAvatarImageView.getWidth());
        }
        popupListWindow.setOnItemClickListener((parent, view, position, viewId) -> {

            int itemId = (int) items.get(position).get(ID);

            switch (itemId) {
                case FOLLOW_USER_ITEM:
                    mCallback.onAddParticipant();
                    break;
                case REPORT_USER_ITEM:
                    if (mCallback != null) {
                        mCallback.onReportParticipant(mUserData.getId(), mUserData.getAvatar());
                    }
                    break;
                case SWITCH_CAMERA:
                    if (mCallback != null) {
                        mCallback.onSwitchCamera();
                    }
                    break;
                default:
                    break;
            }

            popupListWindow.dismiss();
        });

        popupListWindow.show();
        mIsShowingMenu = true;
    }

    public enum WebCamAlignEnum {
        LEFT,
        RIGHT,
        EXECUTION
    }

    public interface Callback {

        void onReportParticipant(int userId, String avatarUrl);

        void onAddParticipant();

        void onSwitchCamera();
    }
}
