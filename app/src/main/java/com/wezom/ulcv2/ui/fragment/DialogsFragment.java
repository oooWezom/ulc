package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.events.NewMessageEvent;
import com.wezom.ulcv2.events.game.DialogsUpdatedEvent;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.mvp.presenter.DialogsFragmentPresenter;
import com.wezom.ulcv2.mvp.view.DialogsFragmentView;
import com.wezom.ulcv2.ui.adapter.DialogAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;
import com.wezom.ulcv2.ui.listeners.UlcActionButtonClickListener;
import com.wezom.ulcv2.ui.view.UlcActionButtonView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * Created by kartavtsev.s on 22.01.2016.
 */
public class DialogsFragment extends ListLceFragment<ArrayList<Dialog>>
        implements DialogsFragmentView, SwipeRefreshLayout.OnRefreshListener, UlcActionButtonClickListener {

    private static final String EXTRA_NAME = "fragment_dialogs";
    @InjectPresenter
    DialogsFragmentPresenter mDialogsFragmentPresenter;

    @Inject
    EventBus mBus;
    @BindView(R.id.fragment_dialogs_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.fragment_dialogs_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_dialogs_action_view)
    UlcActionButtonView mActionButtonView;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    DialogAdapter mDialogAdapter;

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

    public static DialogsFragment getNewInstance(String name) {
        DialogsFragment fragment = new DialogsFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_dialogs;
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
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
    public void setData(ArrayList<Dialog> data) {
        super.setData(data);
        mDialogAdapter.setData(data);
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        contentView.setRefreshing(false);
    }



  /*  @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
    public void onNewMessageF(NewMessageEvent event) {
        mDialogsFragmentPresenter.loadDialogs(false);
    }*/

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDialogAdapter = new DialogAdapter(getContext(), mDialogsFragmentPresenter.getMBus(),
                mDialogsFragmentPresenter.getMPreferenceManager().getUserAvatar());
        mRecycler.setAdapter(mDialogAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.addItemDecoration(new ItemListDividerDecorator(getContext(), R.drawable.bg_line_divider_dialogs));
        contentView.setOnRefreshListener(this);
        mActionButtonView.setTintEnabled(true);
        mActionButtonView.setActionListener(this);
        setEmptyViewText(R.string.you_dont_have_dialogs);
        loadData(false);
        mDialogsFragmentPresenter.fetchNewDialogs();

        setSupportActionBar(mToolbar);
        showDrawerToggleButton(mToolbar);
        mToolbar.setTitle(getString(R.string.title_fragment_dialogs));
        mToolbar.setSubtitle(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        mDialogsFragmentPresenter.loadDialogs(loadMode == Constants.LOADING_MODE_REFRESH);
    }

    @Override
    public void onRefresh() {
        mDialogsFragmentPresenter.fetchNewDialogs();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceive(NewMessageEvent event) {
        mDialogsFragmentPresenter.updateDialogs();
    }

    @Subscribe
    public void onDialogsUpdate(DialogsUpdatedEvent event) {
        mDialogsFragmentPresenter.updateDialogs();
        showContent();
    }

    private void applyDialogFilter(String filter) {
        mDialogAdapter.filter(filter);
    }

    @Override
    public void on2PlayClick(View view) {
        mDialogsFragmentPresenter.onClick2Play();
    }

    @Override
    public void on2TalkClick(View view) {
        mDialogsFragmentPresenter.onClick2Talk();
    }
}
