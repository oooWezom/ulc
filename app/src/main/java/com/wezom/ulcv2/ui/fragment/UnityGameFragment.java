package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wezom.ulcv2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 20.07.2016
 * Time: 18:00
 */
@Accessors(prefix = "m")
public class UnityGameFragment extends Fragment {

    private static UnityGameFragment sInstance;

    public static UnityGameFragment getInstance() {
        if (sInstance == null) {
            Class clazz = UnityGameFragment.class;
            synchronized (clazz) {
                sInstance = new UnityGameFragment();
            }
        }
        return sInstance;
    }

    @Getter
    @BindView(R.id.fragment_unity_game_layout)
    ViewGroup mGameLayout;

    @Setter
    private View mUnityView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_unity_game, container, false);
        injectViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ViewGroup) mUnityView.getParent()).removeView(mUnityView);
        mGameLayout.addView(mUnityView);
    }

    private void injectViews(View view) {
        ButterKnife.bind(this, view);
    }
}
