package com.wezom.ulcv2.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.mvp.presenter.TermsOfUsePresenter;
import com.wezom.ulcv2.mvp.view.TermsOfUseView;

import butterknife.BindView;

import static com.wezom.ulcv2.R.drawable.ic_action_back;


public class TermsOfUseFragment extends BaseFragment implements TermsOfUseView {

    @InjectPresenter
    TermsOfUsePresenter mPresenter;

    @BindView(R.id.terms_of_use_text)
    WebView webView;
    @BindView(R.id.terms_of_use_toolbar)
    Toolbar mToolbar;


    public static TermsOfUseFragment getNewInstance() {
        Bundle args = new Bundle();
        TermsOfUseFragment fragment = new TermsOfUseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_terms_of_use;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.terms_and_conditions);
        mToolbar.setNavigationIcon(ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mPresenter.onBackPressed());
        webView.loadData(getString(R.string.terms_of_use), "text/html; charset=UTF-8", null);
    }
}
