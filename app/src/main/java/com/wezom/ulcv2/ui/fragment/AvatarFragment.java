package com.wezom.ulcv2.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.isseiaoki.simplecropview.CropImageView;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.model.Avatar;
import com.wezom.ulcv2.mvp.presenter.AvatarFragmentPresenter;
import com.wezom.ulcv2.mvp.view.AvatarFragmentView;

import butterknife.BindView;

/**
 * Created by kartavtsev.s on 29.01.2016.
 */
public class AvatarFragment extends BaseFragment
        implements AvatarFragmentView {

    public final static String KEY_AVATAR_URI = "key_avatar_uri";
    public final static String KEY_CROP_MODE = "key_crop_mode";

    public final static int CROP_MODE_AVATAR = 0;
    public final static int CROP_MODE_BACKGROUND = 1;

    public final static int AVATAR_CROP_SIZE = 200;

    @InjectPresenter
    AvatarFragmentPresenter mFragmentPresenter;

    @BindView(R.id.fragment_avatar_crop_view_rectangle)
    CropImageView mCropView;
    @BindView(R.id.fragment_avatar_toolbar)
    Toolbar mToolbar;


    private Uri mAvatarUri;
    private int mCropMode;

    public static AvatarFragment newInstance(Avatar avatar) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_AVATAR_URI, avatar.getUri());
        bundle.putInt(KEY_CROP_MODE, avatar.getCropMode());
        AvatarFragment avatarFragment = new AvatarFragment();
        avatarFragment.setArguments(bundle);
        return avatarFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_avatar;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentPresenter.setDrawerVisible(false);

        mCropView.setGuideColor(android.R.color.transparent);
        mCropView.setFrameColor(android.R.color.transparent);
        mCropView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);


        mAvatarUri = getArguments().getParcelable(KEY_AVATAR_URI);
        mCropMode = getArguments().getInt(KEY_CROP_MODE);
        String title = "";
        switch (mCropMode) {
            case CROP_MODE_AVATAR:
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
                title = getString(R.string.crop_avatar);
                break;
            case CROP_MODE_BACKGROUND:
                mCropView.setCropMode(CropImageView.CropMode.CUSTOM);
                mCropView.setCustomRatio(318, 133);
                title = getString(R.string.crop_background);
                break;
        }

        setSupportActionBar(mToolbar);
        prepareToolbar(title);

        Picasso.with(getContext()).load(mAvatarUri).resize(1000, 1000).centerInside().onlyScaleDown().into(mCropView);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.accept_item).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_item:
                onCropClick();
                break;
        }
        return true;
    }

    public void onCropClick() {
        Bitmap finalBitmap = mCropView.getCroppedBitmap();
        if (mCropMode == CROP_MODE_AVATAR) {
            finalBitmap = Bitmap.createScaledBitmap(finalBitmap, AVATAR_CROP_SIZE, AVATAR_CROP_SIZE, true);
        }
        mFragmentPresenter.onCrop(finalBitmap, mCropMode == CROP_MODE_AVATAR);
    }

    private void prepareToolbar(String title) {
        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mFragmentPresenter.onBackPressed());
    }

/*
    @Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.NOT_LOGGED_IN)
                .build();
        features.add(feature);
        return features;
    }*/

    @Override
    public void onStop() {
        mFragmentPresenter.setDrawerVisible(true);
        super.onStop();
    }
}
