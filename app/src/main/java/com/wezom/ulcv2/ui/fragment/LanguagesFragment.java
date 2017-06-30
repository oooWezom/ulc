package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.interfaces.OnCheckChangeListener;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.presenter.LanguagesFragmentPresenter;
import com.wezom.ulcv2.mvp.view.LanguagesFragmentView;
import com.wezom.ulcv2.ui.adapter.LanguagesAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created: Zorin A.
 * Date: 09.06.2016.
 */
public class LanguagesFragment extends BaseFragment
        implements LanguagesFragmentView, OnCheckChangeListener, MenuItem.OnMenuItemClickListener {
    private static final String LANG_KEY = "key_languages";
    private static final String EXTRA_NAME = "fragment_languages";

    //region injects
    @InjectPresenter
    LanguagesFragmentPresenter mPresenter;
    @Inject
    LanguagesAdapter mLanguagesAdapter;
    //endregion

    //region var
    List<Language> mLanguages;
    //region views
    @BindView(R.id.fragment_languages_toolbar)
    Toolbar mToolbar;
    //endregion
    @BindView(R.id.fragment_languages_recycler)
    RecyclerView mRecyclerView;
    private int mUserAddedItemsQuantity;
    private ItemListDividerDecorator mDecorator;
    //endregion

    public static LanguagesFragment getNewInstance(String name, ArrayList<Language> languages) {
        LanguagesFragment fragment = new LanguagesFragment();

        if (languages != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_NAME, name);
            bundle.putSerializable(LANG_KEY, languages);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    //region overrides
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_languages;
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

        if (getArguments() != null) {
            if (getArguments().getSerializable(LANG_KEY) != null) {
                mLanguages = new ArrayList<>();

                for (Language language : ((List<Language>) getArguments().getSerializable(LANG_KEY))) {
                    Language newLanguage = new Language();
                    newLanguage.setServerId(language.getServerId());
                    newLanguage.setCheckStatus(language.isCheckStatus());
                    newLanguage.setName(language.getName());
                    mLanguages.add(newLanguage);
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLanguagesAdapter.setOnCheckChangeListener(this);
        setSupportActionBar(mToolbar);
        prepareToolbar();
        prepareRecyclerView();

        if (mLanguages == null) {
           mPresenter.loadData();
        } else {
            setLanguageData(mLanguages);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.accept_item)
                .setVisible(true)
                .setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_item:
                   mPresenter.onUserSelectedLanguages(mLanguages);
                break;
        }
        return false;
    }

    @Override
    public void onItemChecked(boolean check, int position) {
        mLanguages.get(position).setCheckStatus(check);
        mLanguagesAdapter.notifyItemChanged(position);
        itemsCount(check);
    }

    @Override
    public void setLanguageData(List<Language> languages) {
        mLanguages = languages;
        if (languages.size() == 0) {
            mLanguages.add(new Language(0, getString(R.string.english), false));
        }
        mLanguagesAdapter.setData(mLanguages);
    }
    //endregion

    private void prepareToolbar() {
        mToolbar.setTitle(R.string.language);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v ->mPresenter.onBackPressed());
    }

    private void prepareRecyclerView() {
        mRecyclerView.setAdapter(mLanguagesAdapter);
        if (mDecorator == null) {
            mDecorator = new ItemListDividerDecorator(getActivity(), R.drawable.bg_line_divider_dialogs);
            mRecyclerView.addItemDecoration(mDecorator);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
    }

    private void itemsCount(boolean check) {
        if (check) {
            mUserAddedItemsQuantity++;
        } else {
            mUserAddedItemsQuantity--;
        }
    }
}
