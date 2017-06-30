package com.wezom.ulcv2.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.injection.component.ActivityComponent;
import com.wezom.ulcv2.injection.component.ApplicationComponent;
import com.wezom.ulcv2.injection.module.ActivityModule;
import com.wezom.ulcv2.interfaces.ToolbarActions;
import com.wezom.ulcv2.mvp.view.BaseActivityView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.experimental.Accessors;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.wezom.ulcv2.common.Constants.CONTENT_DEV_URL;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */

@Getter
@Accessors(prefix = "m")
public abstract class BaseActivity extends MvpAppCompatActivity
        implements BaseActivityView, ToolbarActions {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected ActivityComponent mActivityComponent;
    @Inject
    EventBus bus;
    @Inject
    Picasso mPicasso;
    @Nullable
    @BindView(R.id.global_loading_view)
    RelativeLayout mLoadingView;
    private Toolbar toolbar;

    public abstract void injectDependencies();

    protected abstract Activity getActivity();

    @Override
    public void showDrawerToggleButton() {
    }

    @Override
    public void showDrawerToggleButton(Toolbar toolbar) {
    }

    @Override
    public Toolbar getToolBar() {
        return toolbar;
    }

    @Override
    public void setSupportToolBar(Toolbar toolbar) {
        this.toolbar = toolbar;
        if (toolbar != null) {
            toolbar.setSubtitle("");
        }
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivityComponent = getApplicationComponent().providesActivityComponent(new ActivityModule(this));
        injectDependencies();
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (bus.isRegistered(this)) {
                bus.unregister(this);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!bus.isRegistered(this)) {
                bus.register(this);
            }
        } catch (Exception e) {
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void initViews() {
        ButterKnife.bind(this);
    }

    public void setScreenOrientation(int orientation) {
        switch (orientation) {
            case ScreenOrientation.PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case ScreenOrientation.LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case ScreenOrientation.UNSPECIFIED:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
        }
    }

    ApplicationComponent getApplicationComponent() {
        return ULCApplication.mApplicationComponent;
    }

    public abstract int getLayoutRes();

    public void showLoading(boolean show) {
        mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    protected void loadImage(ImageView imageView, String url, @DrawableRes int errorDrawableRes) {
        final String fullUrl = !TextUtils.isEmpty(url) ? CONTENT_DEV_URL + url : null;

        mPicasso
                .load(!TextUtils.isEmpty(fullUrl) ? fullUrl : "http://")
                .placeholder(errorDrawableRes)
                .error(errorDrawableRes)
                .into(imageView);
    }

    protected void loadImage(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.contains("http")) {
            url = CONTENT_DEV_URL + url;
        }

        mPicasso
                .load(url)
                .into(imageView);
    }

    @Override
    public void showMessageDialog(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.show();
    }

    @Override
    public void showMessageDialog(int title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.show();
    }

    @Override
    public void showMessageDialog(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void showMessageDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.show();
    }


    public void showUpdateDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setMessage(getString(R.string.update_appliaction_message))
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(dialog1 -> {

            Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                //// TODO: 23.02.2017 Replace later
                //final String appPackageName = getPackageName();
                final String appPackageName = "org.telegram.messenger";

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            });
        });
        dialog.show();
    }

    @Override
    public void showToast(int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId, int duration) {
        Toast.makeText(getActivity(), resId, duration).show();
    }

}
