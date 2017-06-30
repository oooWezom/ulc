package com.wezom.ulcv2.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.facebook.login.LoginManager;
import com.vk.sdk.VKSdk;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.common.CameraPreview;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.AvatarCropEvent;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.BackgroundCropEvent;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.ChangeBackgroundRequestEvent;
import com.wezom.ulcv2.events.ChangeEvents;
import com.wezom.ulcv2.events.ConfirmChangePassEvent;
import com.wezom.ulcv2.events.ConnectionRecoveredEvent;
import com.wezom.ulcv2.events.CountDownEvent;
import com.wezom.ulcv2.events.CreateTalkClickEvent;
import com.wezom.ulcv2.events.CreateTalkEvent;
import com.wezom.ulcv2.events.DialogSelectedEvent;
import com.wezom.ulcv2.events.ErrorMessageEvent;
import com.wezom.ulcv2.events.FailureConnectionEvent;
import com.wezom.ulcv2.events.FindTalkPartnerEvent;
import com.wezom.ulcv2.events.FollowClickEvent;
import com.wezom.ulcv2.events.FollowersShowEvent;
import com.wezom.ulcv2.events.FollowsRequestEvent;
import com.wezom.ulcv2.events.GameCreatedEvent;
import com.wezom.ulcv2.events.GameFoundEvent;
import com.wezom.ulcv2.events.HideDrawerEvent;
import com.wezom.ulcv2.events.InitDrawerEvent;
import com.wezom.ulcv2.events.InviteToTalkRequestEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.events.LanguageSelectedEvent;
import com.wezom.ulcv2.events.LanguagesFetchEvent;
import com.wezom.ulcv2.events.LoginEvent;
import com.wezom.ulcv2.events.LogoutRequestEvent;
import com.wezom.ulcv2.events.LostConnectionEvent;
import com.wezom.ulcv2.events.NewFollowerEvent;
import com.wezom.ulcv2.events.NewMessageEvent;
import com.wezom.ulcv2.events.NewNotificationEvent;
import com.wezom.ulcv2.events.On2PlayClickEvent;
import com.wezom.ulcv2.events.On2TalkClickEvent;
import com.wezom.ulcv2.events.OnDrawerOpenEvent;
import com.wezom.ulcv2.events.OnNetworkStateEvent;
import com.wezom.ulcv2.events.OnShowDrawerEvent;
import com.wezom.ulcv2.events.OnShowMessageDialogEvent;
import com.wezom.ulcv2.events.ProfileClickedEvent;
import com.wezom.ulcv2.events.RectonnectChannelEvent;
import com.wezom.ulcv2.events.SearchGameEvent;
import com.wezom.ulcv2.events.SessionInfoEvent;
import com.wezom.ulcv2.events.ShowGlobalLoadingEvent;
import com.wezom.ulcv2.events.ShowTalkEvent;
import com.wezom.ulcv2.events.SocialLoginRequestEvent;
import com.wezom.ulcv2.events.UpdateDateRequestEvent;
import com.wezom.ulcv2.events.VideoPreviewEvent;
import com.wezom.ulcv2.events.WatchGameEvent;
import com.wezom.ulcv2.interfaces.OnNotificationClickListener;
import com.wezom.ulcv2.interfaces.ToolbarActions;
import com.wezom.ulcv2.managers.NotificationManager;
import com.wezom.ulcv2.mvp.model.Avatar;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.model.Conversation;
import com.wezom.ulcv2.mvp.model.Follows;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.model.NewsFeed;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.mvp.presenter.HomeActivityPresenter;
import com.wezom.ulcv2.mvp.view.HomeActivityView;
import com.wezom.ulcv2.navigation.SupportFragmentNavigator;
import com.wezom.ulcv2.net.models.Event;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;
import com.wezom.ulcv2.net.models.responses.websocket.NotificationResponse;
import com.wezom.ulcv2.ui.adapter.NotificationAdapter;
import com.wezom.ulcv2.ui.dialog.CountDownDialog;
import com.wezom.ulcv2.ui.dialog.InformationDialog;
import com.wezom.ulcv2.ui.dialog.InformationDialogBuilder;
import com.wezom.ulcv2.ui.dialog.InviteDialog;
import com.wezom.ulcv2.ui.dialog.InviteDialogBuilder;
import com.wezom.ulcv2.ui.fragment.AvatarFragment;
import com.wezom.ulcv2.ui.fragment.BlackListFragment;
import com.wezom.ulcv2.ui.fragment.ChangeLoginFragment;
import com.wezom.ulcv2.ui.fragment.ChangePasswordFragment;
import com.wezom.ulcv2.ui.fragment.ChatFragment;
import com.wezom.ulcv2.ui.fragment.ConfirmRestorePassFragmentBuilder;
import com.wezom.ulcv2.ui.fragment.ConversationFragment;
import com.wezom.ulcv2.ui.fragment.Create2TalkSessionFragment;
import com.wezom.ulcv2.ui.fragment.DialogFragment;
import com.wezom.ulcv2.ui.fragment.DialogsFragment;
import com.wezom.ulcv2.ui.fragment.FeedFilterFragment;
import com.wezom.ulcv2.ui.fragment.FollowsFragment;
import com.wezom.ulcv2.ui.fragment.GamesFragment;
import com.wezom.ulcv2.ui.fragment.LanguagesFragment;
import com.wezom.ulcv2.ui.fragment.LoginFragment;
import com.wezom.ulcv2.ui.fragment.NavigationDrawerFragment;
import com.wezom.ulcv2.ui.fragment.NewsfeedFragment;
import com.wezom.ulcv2.ui.fragment.PasswordRecoveryFragment;
import com.wezom.ulcv2.ui.fragment.ProfileFragment;
import com.wezom.ulcv2.ui.fragment.SearchFragment;
import com.wezom.ulcv2.ui.fragment.SessionInfoFragment;
import com.wezom.ulcv2.ui.fragment.SettingsFragment;
import com.wezom.ulcv2.ui.fragment.SignupFragment;
import com.wezom.ulcv2.ui.fragment.TermsOfUseFragment;
import com.wezom.ulcv2.ui.fragment.ToPlayFragment;
import com.wezom.ulcv2.ui.fragment.ToTalkFragment;
import com.wezom.ulcv2.ui.fragment.WatchSessionsFragment;
import com.wezom.ulcv2.ui.view.NotificationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.commands.Command;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wezom.ulcv2.common.Constants.DialogResult.CANCEL;
import static com.wezom.ulcv2.navigation.AnimationType.FADE_ANIM;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.CANCEL_BUTTON_MODE;

@RuntimePermissions
public class HomeActivity extends BaseActivity implements
        HomeActivityView, ToolbarActions, FragmentManager.OnBackStackChangedListener, OnNotificationClickListener {

    public final static int REQUEST_CODE_OPEN_DIALOG = 100;
    public final static String ACTION_SHOW_LOGIN = "show_login_screen";
    public static final int NOTIFICATION_ITEM_LIFE_TIME = 5000;
    public final static String DIALOG_ID_KEY = "dialog_id";
    public final static String NAME_KEY = "name";
    public final static String AVATAR_KEY = "avatar";
    public final static String USER_ID_KEY = "user_id";
    private final static String TAG = HomeActivity.class.getName();
    private final static int REQUEST_IMAGE_PICK_AVATAR = 1;
    private final static int REQUEST_IMAGE_CAPTURE_AVATAR = 2;
    private final static int REQUEST_IMAGE_PICK_BACKGROUND = 3;
    private static final int MAXIMUM_NOTIFICATIONS_QUANTITY = 3;
    private static Handler mNotificationHandler;
    @InjectPresenter
    HomeActivityPresenter mPresenter;
    @Inject
    NavigatorHolder navigatorHolder;
    @Inject
    Router router;
    @Inject
    EventBus mBus;

    //region views
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_main_block)
    CoordinatorLayout mMainBlock;
    @BindView(R.id.activity_main_reveal_transition)
    View mRevealView;
    @BindView(R.id.home_activity_navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_home_video_placeholder)
    FrameLayout mVideoPlaceHolderLayout;
    @BindView(R.id.activity_main_notification_recycler)
    RecyclerView mNotificationRecycler;
    //endregion
    @BindView(R.id.fragment_login_tint_layer)
    View mTintLayer;

    private int mActionbarSize = 0;
    private ActionBarDrawerToggle mDrawerToggle;
    private File mPhotoFile;
    private Uri mPhotoUri;
    private boolean mIsVideoPermissionAllowed = true;
    private InformationDialog mGameSearchDialog;
    private NotificationAdapter mNotificationAdapter;
    private boolean isPreviewStarted;
    private boolean isPreviewAllowed = true; //disable preview when logged in


    private Navigator navigator = new SupportFragmentNavigator(getSupportFragmentManager(), R.id.placeholder, FADE_ANIM) {
        @Override
        public void applyCommand(Command command) {
            super.applyCommand(command);
        }

        @Override
        protected Fragment createFragment(String screenKey, Object data) {
            switch (screenKey) {
                case Screens.LOGIN_SCREEN:
                    return LoginFragment.getNewInstance(Screens.LOGIN_SCREEN);
                case Screens.NEWS_FEED_SCREEN:
                    return NewsfeedFragment.getNewInstance(Screens.NEWS_FEED_SCREEN, (NewsFeed) data);
                case Screens.SETTINGS_SCREEN:
                    return SettingsFragment.getNewInstance(Screens.SETTINGS_SCREEN);
                case Screens.PASSWORD_RECOVERY_SCREEN:
                    return PasswordRecoveryFragment.getNewInstance(Screens.PASSWORD_RECOVERY_SCREEN);
                case Screens.SEARCH_SCREEN:
                    return SearchFragment.getNewInstance(Screens.SEARCH_SCREEN);
                case Screens.FEED_FILTER_SCREEN:
                    return FeedFilterFragment.getNewInstance(Screens.FEED_FILTER_SCREEN);
                case Screens.SIGNUP_SCREEN:
                    return SignupFragment.getNewInstance(Screens.SIGNUP_SCREEN);
                case Screens.PROFILE_SCREEN:
                    return ProfileFragment.getNewInstance(Screens.PROFILE_SCREEN, (int) data);
                case Screens.SESSION_INFO_SCREEN:
                    return SessionInfoFragment.getNewInstance(Screens.SESSION_INFO_SCREEN, (Event) data);
                case Screens.FOLLOWS_SCREEN:
                    return FollowsFragment.getNewInstance(Screens.FOLLOWS_SCREEN, (Follows) data);
                case Screens.DIALOGS_SCREEN:
                    return DialogsFragment.getNewInstance(Screens.DIALOGS_SCREEN);
                case Screens.DIALOG_SCREEN:
                    return DialogFragment.newInstance(Screens.DIALOG_SCREEN, (int) data);
                case Screens.CONVERSATION_SCREEN:
                    return ConversationFragment.getNewInstance(Screens.CONVERSATION_SCREEN, (Conversation) data);
                case Screens.TO_TALK_SCREEN:
                    return ToTalkFragment.getNewInstance(Screens.TO_TALK_SCREEN);
                case Screens.TO_PLAY_SCREEN:
                    return ToPlayFragment.getNewInstance(Screens.TO_PLAY_SCREEN);
                case Screens.GAMES_SCREEN:
                    return GamesFragment.getNewInstance(Screens.GAMES_SCREEN);
                case Screens.CREATE_2TALK_SCREEN:
                    return Create2TalkSessionFragment.getNewInstance(Screens.CREATE_2TALK_SCREEN);
                case Screens.CHANGE_PASS_SCREEN:
                    return ChangePasswordFragment.getNewInstance(Screens.CHANGE_PASS_SCREEN);
                case Screens.CHANGE_LOGIN_SCREEN:
                    return ChangeLoginFragment.getNewInstance(Screens.CHANGE_LOGIN_SCREEN);
                case Screens.CHAT_SCREEN:
                    return ChatFragment.getNewInstance(Screens.CHAT_SCREEN);
                case Screens.LANGUAGES_SCREEN:
                    return LanguagesFragment.getNewInstance(Screens.LANGUAGES_SCREEN, (ArrayList<Language>) data);
                case Screens.BLACK_LIST_SCREEN:
                    return BlackListFragment.getNewInstance(Screens.BLACK_LIST_SCREEN);
                case Screens.SESSIONS_WATCH_FRAGMENT:
                    return WatchSessionsFragment.getNewInstance();
                case Screens.TERMS_OF_USE_FRAGMENT:
                    return TermsOfUseFragment.getNewInstance();
                case Screens.AVATAR_SCREEN:
                    return AvatarFragment.newInstance(((Avatar) data));
                case Screens.CONFIRM_RESTORE_PASS_FRAGMENT:
                    return new ConfirmRestorePassFragmentBuilder(((String) data)).build();
                default:
                    throw new RuntimeException("Unknown screen key!");
            }
        }

        @Override
        protected void showSystemMessage(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void exit() {
            finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.settings_item).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            showBackButton();
        } else {
            hideBackButton();
        }
    }

    @Override
    public void setLayoutMode(Constants.LayoutMode mode) {
        ViewGroup.LayoutParams layoutParams = mToolbar.getLayoutParams();
        switch (mode) {
            case NOT_LOGGED_IN:
                layoutParams.height = 0;
                mToolbar.setAlpha(0);
                break;
            case NOT_LOGGED_IN_WITH_TOOLBAR:
                setSupportActionBar(mToolbar);
                layoutParams.height = mActionbarSize;
                mToolbar.setAlpha(1);
                break;
            case LOGGED_IN:
                setSupportActionBar(mToolbar);
                layoutParams.height = mActionbarSize;
                mToolbar.setAlpha(1);
                break;
            case LOGGED_IN_WITHOUT_TOOLBAR:
                layoutParams.height = 0;
                mToolbar.setAlpha(0);
                break;
        }
        mToolbar.setVisibility(View.VISIBLE);
        mToolbar.requestLayout();
    }

    @Override
    public void showLoadingEvent(boolean show) {
        mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showFbLogin(ArrayList<String> scope) {
        LoginManager.getInstance().logInWithReadPermissions(this, scope);
    }

    @Override
    public void showVkLogin(String email) {
        VKSdk.login(this, "email");
    }

    @Override
    public void dispatchPickPictureIntent(boolean isAvatar) {
        HomeActivityPermissionsDispatcher.allowPickPictureWithCheck(this, isAvatar);

    }

    public void dispatchTakePictureIntent() {
        HomeActivityPermissionsDispatcher.allowTakePictureIntentWithCheck(this);
    }

    @Override
    public void animateRevealOut() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        mRevealView.setVisibility(View.VISIBLE);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRevealView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alphaAnimation.setFillAfter(true);
        mRevealView.startAnimation(alphaAnimation);
    }

    @Override
    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void dispatchStartGameIntent(int sessionId, int gameType, String videoUrl, int watchers, boolean isParticipant, UserData firstUser, UserData secondUser) {
        mPresenter.onPlayIntent(sessionId, gameType, videoUrl, watchers, isParticipant, firstUser, secondUser);
    }

    @Override
    public void showGameSearch(boolean show) {
        if (show) {
            mGameSearchDialog = new InformationDialogBuilder(CANCEL_BUTTON_MODE)
                    .dialogTitle(getString(R.string.searching_for_opponent))
                    .viewRes(R.layout.layout_dialog_loading_view).build();
            mGameSearchDialog.setOnDialogResult((dialogResult, result) -> {
                if (dialogResult == CANCEL) {
                    mPresenter.onCancelSearch();
                }
            });

            mGameSearchDialog.show(getSupportFragmentManager(), null);

        } else {
            if (mGameSearchDialog != null) {
                mGameSearchDialog.dismiss();
                mGameSearchDialog = null;
            }
        }
    }

    @Override
    public void setGameSearchCountdown(int count) {
        if (mGameSearchDialog != null) {
            mGameSearchDialog.dismiss();
            mGameSearchDialog = null;
        }

        boolean isShownFirstTime = CountDownDialog.show(getSupportFragmentManager());
        if (isShownFirstTime) {
            CountDownDialog.getInstance().setOnDialogResult((dialogResult, result) -> {
                if (dialogResult == CANCEL) {
                    // TODO cancel game
                }
            });
        }

        bus.post(new CountDownEvent(count));
    }

    @Override
    public void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void showDrawer(boolean show) {
        if (show) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void showAcceptDialog(InviteToTalkRequest inviteToTalkRequest) {
        int expireTime = inviteToTalkRequest.getTimeToExpire();
        int categoryId = inviteToTalkRequest.getCategory();
        int userId = inviteToTalkRequest.getUser().getId();
        String userName = inviteToTalkRequest.getUser().getName() + " ";
        String avatar = inviteToTalkRequest.getUser().getAvatar();

        String titlePart1 = getString(R.string.invite_to_talk_part_1);
        String titlePart2 = " " + getString(R.string.invite_to_talk_part_2);
        Category category = Category.getCategoryById(categoryId);

        InviteDialog mInviteDialog = new InviteDialogBuilder(Constants.InviteDialogType.DELAY_MODE)
                .dialogMessage(titlePart1)
                .dialogMessage2(titlePart2)
                .userName(userName)
                .dialogUserUrl(avatar)
                .dialogCategoryUrl(Utils.getCorrectCategoryIconSizeURL(this, false, false) + category.getIcon())
                .dialogTime(expireTime)
                .build();
        mInviteDialog.setOnDialogResult((dialogResult, result) -> {
            int responseResult = -1;
            int delay = -1;
            if (dialogResult == Constants.DialogResult.OK) {
                responseResult = InviteToTalkResponseEvent.ACCEPT;//accept
            }
            if (dialogResult == Constants.DialogResult.CANCEL) {
                responseResult = InviteToTalkResponseEvent.DENY;// deny
            }
            if (dialogResult == Constants.DialogResult.DELAY) {
                responseResult = InviteToTalkResponseEvent.DO_NOT_DISTURB; //do not disturb
                delay = (int) result[0];
            }
            if (dialogResult == Constants.DialogResult.TIME_IS_OVER) {

            }
            mPresenter.sendInviteResponse(userId, responseResult, delay);
        });
        mInviteDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void startVideoPreview() {
        Log.v("HA", "startVideoPreview()");
        HomeActivityPermissionsDispatcher.checkCameraPermissionWithCheck(this);
        if (mIsVideoPermissionAllowed && !isPreviewStarted && isPreviewAllowed) {
            try {
                Camera var = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                CameraPreview cameraPreview = new CameraPreview(this, var);
                mVideoPlaceHolderLayout.addView(cameraPreview);
                isPreviewStarted = true;
            } catch (RuntimeException e) {
                Log.v("HA", "Error open camera");
            }
        }
    }

    @Override
    public void stopVideoPreview() {
        Log.v("HA", "stopVideoPreview()");
        mVideoPlaceHolderLayout.removeAllViews();
        isPreviewStarted = false;
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void setNewNotification(NotificationResponse notification) {
        if (mNotificationAdapter.getData().size() < MAXIMUM_NOTIFICATIONS_QUANTITY) {
            mNotificationAdapter.addData(notification);
        } else {
            mNotificationAdapter.removeData(0);
            mNotificationAdapter.addData(notification);
        }

        mNotificationHandler = new Handler();
        mNotificationHandler.postDelayed(() -> mNotificationAdapter.clearData(), NOTIFICATION_ITEM_LIFE_TIME);
    }

    @Override
    public void hideKeyboard() {
        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void hideTintLayer(boolean hide) {
        mTintLayer.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @Override
    public void allowVideoPreview(boolean isAllowed) {
        isPreviewAllowed = isAllowed;
    }

    @Override
    public void confirmRegister(String token) {
        mPresenter.onRegisterConfirm(new ConfirmChangePassEvent(token));
    }

    @Override
    public void showReconnectDialog() {
        AlertDialog mReconnectDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setMessage(getString(R.string.you_have_incomplete_session)
                )
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    mPresenter.performReconnect();
                    dialogInterface.dismiss();
                })
                .setCancelable(false)
                .create();

        rx.Observable.timer(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    mReconnectDialog.show();
                });
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void allowPickPicture(boolean isAvatar) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (isAvatar) {
            startActivityForResult(intent, REQUEST_IMAGE_PICK_AVATAR);
        } else {
            startActivityForResult(intent, REQUEST_IMAGE_PICK_BACKGROUND);
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void allowTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            mPhotoFile = null;
            try {
                mPhotoFile = Utils.createImageFile(this);
                mPhotoUri = Uri.fromFile(mPhotoFile);
            } catch (IOException ex) {
                Log.v(TAG, " cant create file");
            }
            // Continue only if the File was successfully created
            if (mPhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_AVATAR);
            }
        }
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    public void showRationaleForCamera(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Need Camera!!!!!")
                .setPositiveButton("Allow", (dialog, button) -> request.proceed())
                .setNegativeButton("Deny", (dialog, button) -> request.cancel())
                .show();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    public void checkCameraPermission() {
        mIsVideoPermissionAllowed = true;
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    public void showDeniedForCamera() {
        mPresenter.showPermissionDeniedMessage();
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void showDeniedForTakePicture() {
        mPresenter.showPermissionDeniedMessage();
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showDeniedForPickPicture() {
        mPresenter.showPermissionDeniedMessage();
    }

    @Override
    public void onNotificationClick(int itemPosition, int sessionType) {
        if (sessionType == NotificationView.TALK_TYPE) {
            Talk talk = mNotificationAdapter.getData(itemPosition).getTalk();
            mPresenter.createTalk(talk);
        }
        if (sessionType == NotificationView.GAME_TYPE) {
            Game game = mNotificationAdapter.getData(itemPosition).getGame();
            mPresenter.onPlayIntent(
                    game.getGameId(),
                    game.getGameType(),
                    game.getVideoUrl(),
                    game.getUsers().get(0),    // first user
                    game.getUsers().get(1));    //second user
        }
        mNotificationAdapter.removeData(itemPosition);
    }

    @Override
    public void onCloseNotification(int itemPosition) {
        mNotificationAdapter.removeData(itemPosition);
    }

    @Override
    public void injectDependencies() {
        getActivityComponent().inject(this);
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    public void showDrawerToggleButton() {
        showDrawerToggleButton(mToolbar);
    }

    public void showDrawerToggleButton(Toolbar toolbar) {
        if (toolbar != null) {
            mToolbar = toolbar;
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public Toolbar getToolBar() {
        return mToolbar;
    }

    @Override
    public void setSupportToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.initApplicationLocale();
        Log.v("HA", "onCreate(Bundle....)");
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mPresenter.switchToInitialScreen(getIntent().getExtras());

        mPresenter.getLanguages();

        setStatusbarClolor();

        Intent intent = getIntent();
        if (intent != null) {
            mPresenter.actionIntentEvent(intent);
        }
    }


    @Override
    protected void onPause() {
        navigatorHolder.removeNavigator();
        super.onPause();
        if (mBus.isRegistered(this)) {
            mBus.unregister(this);
        }
        Log.v("HA", "onPause()");
        mPresenter.disconnectSocket();
        mPresenter.onVideoPreview(new VideoPreviewEvent(VideoPreviewEvent.STOP));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("HA", "onResume()");
        if (!mBus.isRegistered(this)) {
            mBus.register(this);
        }
        navigatorHolder.setNavigator(navigator);
        if (mIsVideoPermissionAllowed) {
            mPresenter.onVideoPreview(new VideoPreviewEvent(VideoPreviewEvent.START));
        }
        mPresenter.checkConnection();
        mPresenter.connectSocket();
        mPresenter.checkIskNeedReconnectSession();

    }

    @Override
    public void initViews() {
        super.initViews();
        Log.v("HA", "initViews()");
        setSupportActionBar(mToolbar);
        initNavDrawer();
        TypedValue size = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, size, true);
        mActionbarSize = TypedValue.complexToDimensionPixelSize(size.data, getResources().getDisplayMetrics());
        mNotificationAdapter = new NotificationAdapter(this, bus);
        mNotificationAdapter.setListener(this);
        mNotificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        mNotificationRecycler.setAdapter(mNotificationAdapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_home;
    }

    @Override
    public void showMessageDialog(int messageResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(getString(messageResId));
        builder.show();
    }

    @Override
    protected void onDestroy() {
        Log.v("HA", "onDestroy()");
        mPresenter.disconnectSocket();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("HA", "onStop()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE_AVATAR:
                    mPresenter.changeAvatar(mPhotoUri);
                    break;
                case REQUEST_IMAGE_PICK_AVATAR:
                    mPresenter.changeAvatar(data.getData());
                    break;
                case REQUEST_IMAGE_PICK_BACKGROUND:
                    mPresenter.changeBackground(data.getData());
                    break;
            }
        }
//        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Log.v("HA", "onBackPressed()");
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            hideDrawer();
        } else {
            mPresenter.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v("HA", "onNewIntent(Intent intent)");
        String action = intent.getAction();
        if (action.equals(NotificationManager.ACTION_MESSAGE)) {
            int dialogId = intent.getIntExtra(DIALOG_ID_KEY, 0);
            String name = intent.getStringExtra(NAME_KEY);
            String avatar = intent.getStringExtra(AVATAR_KEY);
            mPresenter.openNotificationDialog(dialogId, name, avatar);

        }
        mPresenter.actionIntentEvent(intent);

        if (action.equals(NotificationManager.ACTION_FOLLOW)) {
            int userId = intent.getIntExtra(USER_ID_KEY, 0);
            mPresenter.onNewFollowerNotificationClick(userId);
        }
        if (action.equals(HomeActivity.ACTION_SHOW_LOGIN)) {
            mPresenter.switchToInitialScreen(null);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void restart() {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = getIntent();
            finishAndRemoveTask();
            startActivity(intent);
        }*/
    }

    private void initNavDrawer() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_activity_navigation_view,
                new NavigationDrawerFragment(), TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showBackButton() {
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void hideBackButton() {
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setStatusbarClolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }
    }


    //region subscribes

    @Subscribe
    public void onHideDrawer(HideDrawerEvent event) {
        mPresenter.onHideDrawer(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReconnectSocket(RectonnectChannelEvent event) {
        mPresenter.onReconnectSocket(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateTalkClickEvent(CreateTalkClickEvent event) {
        mPresenter.onCreateTalkClickEvent(event);
    }

    @Subscribe
    public void onProfileClickedEvent(ProfileClickedEvent event) {
        mPresenter.onProfileClickedEvent(event);
    }

    @Subscribe
    public void onFollowsRequest(FollowsRequestEvent event) {
        mPresenter.onFollowsRequest(event);
    }


    @Subscribe
    public void onBackEvents(BackEvents event) {
        mPresenter.onBackEvents(event);
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        mPresenter.onLogin(event);
    }

    @Subscribe
    public void onAvatarCrop(AvatarCropEvent event) {
        mPresenter.onAvatarCrop(event);
    }

    @Subscribe
    public void onBackgroundCrop(BackgroundCropEvent event) {
        mPresenter.onBackgroundCrop(event);
    }

    @Subscribe
    public void onAvatarChange(ChangeAvatarRequestEvent event) {
        mPresenter.onAvatarChange(event);
    }

    @Subscribe
    public void onBackgroundChange(ChangeBackgroundRequestEvent event) {
        mPresenter.onBackgroundChange(event);
    }

    @Subscribe
    public void onLogoutRequest(LogoutRequestEvent event) {
        mPresenter.onLogoutRequest(event);
    }

    @Subscribe
    public void onVideoPreview(VideoPreviewEvent event) {
        mPresenter.onVideoPreview(event);
    }

    @Subscribe
    public void onDrawerOpen(OnDrawerOpenEvent event) {
        mPresenter.onDrawerOpen(event);
    }

    @Subscribe
    public void onChangedLogin(ChangeEvents.ChangedLoginEvent event) {
        mPresenter.onChangedLogin(event);
    }

    @Subscribe
    public void onLanguagesFetch(LanguagesFetchEvent event) {
        mPresenter.onLanguagesFetch(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUserSelectedLanguages(LanguageSelectedEvent event) {
        mPresenter.onUserSelectedLanguages(event);
    }

    @Subscribe
    public void onSocialLogin(SocialLoginRequestEvent event) {
        mPresenter.onSocialLogin(event);
    }

    @Subscribe
    public void onShowGlobalLoading(ShowGlobalLoadingEvent event) {
        mPresenter.onShowGlobalLoading(event);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 2)
    public void onNewMessage(NewMessageEvent event) {
        mPresenter.onNewMessage(event);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 0)
    public void onNewMessageNotify(NewMessageEvent event) {
        mPresenter.onNewMessageNotify(event);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onNewFollowerNotify(NewFollowerEvent event) {
        mPresenter.onNewFollowerNotify(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileConnectionLost(LostConnectionEvent event) { //socket connection lost
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileConnectionRecovered(ConnectionRecoveredEvent event) { //deprecated

    }

    @Subscribe
    public void onFollowClick(FollowClickEvent event) {
        mPresenter.onFollowClick(event);
    }

    @Subscribe
    public void onDialogSelected(DialogSelectedEvent event) {
        mPresenter.onDialogSelected(event);
    }

    @Subscribe
    public void onRefreshRequestEvent(UpdateDateRequestEvent event) {
        mPresenter.onRefreshRequestEvent(event);
    }

    @Subscribe
    public void on2PlayClick(On2PlayClickEvent event) {
        mPresenter.on2PlayClick(event);

    }


    @Subscribe
    public void onSessionInfoEvent(SessionInfoEvent event) {
        mPresenter.onSessionInfoEvent(event);

    }

    @Subscribe
    public void isShowDrawer(OnShowDrawerEvent event) {
        mPresenter.isShowDrawer(event);
    }

    @Subscribe
    public void on2TalkClick(On2TalkClickEvent event) {
        mPresenter.on2TalkClick(event);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameSearchResult(SearchGameEvent event) {
        mPresenter.onGameSearchResult(event);
    }

    @Subscribe
    public void onWatchGameRequest(WatchGameEvent event) {
        mPresenter.onWatchGameRequest(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameCreated(GameCreatedEvent event) {
        mPresenter.onGameCreated(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameFound(GameFoundEvent event) {
        mPresenter.onGameFound(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewNotification(NewNotificationEvent event) {
        mPresenter.onNewNotification(event);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onSendInviteToTalkResult(InviteToTalkSentResultEvent event) { //we receive result is our request sent successfully
        mPresenter.onSendInviteToTalkResult(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInviteToTalkResponse(InviteToTalkResponseEvent event) {
        mPresenter.onInviteToTalkResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveInviteToTalk(InviteToTalkRequestEvent event) {
        mPresenter.onReceiveInviteToTalk(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateTalk(CreateTalkEvent event) {
        mPresenter.onCreateTalk(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateTalk(ShowTalkEvent event) {
        if (event.getTalk() != null) {
            mPresenter.onCreateTalk(event);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onCreateGame(CreatePlayEvent event) {
//        presenter.onCreateGame(event);
//    }

    @Subscribe
    public void onFindTalkPartnerEvent(FindTalkPartnerEvent event) {
        mPresenter.onFindTalkPartnerEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowsShow(FollowersShowEvent event) {
        mPresenter.onFollowsShow(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkState(OnNetworkStateEvent event) {
        mPresenter.onNetworkState(event);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageDialog(OnShowMessageDialogEvent event) {
        mPresenter.onShowMessageDialog(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFailureConnection(FailureConnectionEvent event) {
        mPresenter.onFailureConnection(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(ErrorMessageEvent event) {
        mPresenter.onError(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitDrawer(InitDrawerEvent event) {
        initNavDrawer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirmChangePass(ConfirmChangePassEvent event) {
        mPresenter.onRegisterConfirm(event);
    }
    //endregion
}
