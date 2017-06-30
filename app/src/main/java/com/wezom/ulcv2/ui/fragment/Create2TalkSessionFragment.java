package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.interfaces.OnItemSelectListener;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.presenter.Create2TalkSessionPresenter;
import com.wezom.ulcv2.mvp.view.Create2TalkSessionView;
import com.wezom.ulcv2.ui.adapter.CategoriesExtendedAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListQuadDividerDecorator;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created: Zorin A.
 * Date: 16.07.2016.
 */
public class Create2TalkSessionFragment extends BaseFragment
        implements Create2TalkSessionView, OnItemSelectListener {


    private static final String EXTRA_NAME = "fragment_create_2talk";
    //region views
    @BindView(R.id.fragment_create_2talk_categories_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_create_2talk_categories)
    RecyclerView mCategoriesRecyclerView;
    @BindView(R.id.fragment_create_2talk_input)
    EditText mTitleInputEditText;
    //endregion

    //region var
    @InjectPresenter
    Create2TalkSessionPresenter mPresenter;
    @Inject
    CategoriesExtendedAdapter mCategoriesAdapter;

    private ItemListQuadDividerDecorator mItemDecorator;
    private List<Category> mCategoriesList;
    private int mSelectedCategoryIndex = -1;
    //endregion


    public static Create2TalkSessionFragment getNewInstance(String name) {
        Create2TalkSessionFragment fragment = new Create2TalkSessionFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_create_2talk_session;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setSupportActionBar(mToolbar);
        initToolbar();
        setupRecyclerView();
        mPresenter.getData();

    }

    @Override
    public void onStop() {
        if (mSelectedCategoryIndex > -1) {
            mCategoriesList.get(mSelectedCategoryIndex).setChecked(false);
        }
        super.onStop();
    }

    private void setupRecyclerView() {
        mCategoriesAdapter.setListener(this);
        if (mItemDecorator == null) {
            mItemDecorator = new ItemListQuadDividerDecorator(getActivity(), R.drawable.bg_line_divider_dialogs);
        }
        mCategoriesRecyclerView.addItemDecoration(mItemDecorator);
        mCategoriesRecyclerView.setHasFixedSize(true);
        mCategoriesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4, LinearLayoutManager.VERTICAL, false));
        mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.accept_item).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_item:
                if (isAllDataSet()) {
                    mPresenter.start2TalkSession(mSelectedCategoryIndex+1, mTitleInputEditText.getText().toString());
                } else {
                    notifyNotAllDataSet();
                }
                break;
        }
        return true;
    }

    @Override
    public void setData(List<Category> categories) {
        mCategoriesList = categories;
        mCategoriesAdapter.setData(mCategoriesList);
    }

    @Override
    public void onSelected(int position) {
        if (mSelectedCategoryIndex == position) {
            return;
        }
        mSelectedCategoryIndex = position;
        for (Category category : mCategoriesList) {
            category.setChecked(false);
        }
        mCategoriesList.get(position).setChecked(true);
        mCategoriesAdapter.notifyDataSetChanged();
    }

    private void notifyNotAllDataSet() {
//        if (TextUtils.isEmpty(mTitleInputEditText.getText())) {
//            mPresenter.notifyUserDataNotSet(R.string.session_name_not_set);
//            return;
//        }
        if (mSelectedCategoryIndex == -1) {
            mPresenter.notifyUserDataNotSet(R.string.talk_category_not_set);
        }
    }

    private void initToolbar() {
        mToolbar.setTitle(R.string.make_2talk_session);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mPresenter.onBackPressed());
    }

    public boolean isAllDataSet() {
        return (mSelectedCategoryIndex > -1);
    }
}
