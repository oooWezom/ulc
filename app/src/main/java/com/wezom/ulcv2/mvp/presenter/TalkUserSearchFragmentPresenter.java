package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.FindTalkPartnerClickEvent;
import com.wezom.ulcv2.events.OnCancelUserSearchClickEvent;
import com.wezom.ulcv2.events.OnCloseSearchPanelEvent;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.view.TalkUserSearchFragmentView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.SearchRequest;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import static com.wezom.ulcv2.common.Constants.LOADING_MODE_ENDLESS;
import static com.wezom.ulcv2.common.Constants.LOADING_MODE_INITIAL;
import static com.wezom.ulcv2.common.Constants.LOADING_MODE_REFRESH;
import static com.wezom.ulcv2.common.Constants.LOAD_ITEMS_LIMIT;

/**
 * Created: Zorin A.
 * Date: 19.09.2016.
 */

@InjectViewState
public class TalkUserSearchFragmentPresenter extends BasePresenter<TalkUserSearchFragmentView> {
    @Inject
    ApiManager mApiManager;
    @Inject
    EventBus mBus;
    @Inject
    PreferenceManager mPreferenceManager;

    private int mOffset = 0;

    public TalkUserSearchFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void searchUser(SearchRequest.Request request, int loadMode) {
        if (loadMode == LOADING_MODE_REFRESH || loadMode == LOADING_MODE_INITIAL) {
            mOffset = 0;
        }
        getViewState().showLoading(false);

        SearchRequest searchRequest = new SearchRequest(request, mOffset, LOAD_ITEMS_LIMIT);
        mApiManager.search(searchRequest)
                .subscribe(response -> {
                    if (!(loadMode == LOADING_MODE_ENDLESS)) {
                        getViewState().setData(response.getResponse());
                    } else {
                        getViewState().addData(response.getResponse());
                    }
                    getViewState().showContent();

                }, throwable -> {
                    getViewState().showError(throwable, false);
                }, () -> mOffset += LOAD_ITEMS_LIMIT);
    }

    public void searchRandomUser() {
        mBus.post(new FindTalkPartnerClickEvent());
    }

    public void cancelUserSearch() {
        mBus.post(new OnCancelUserSearchClickEvent());
    }

    public void closeSearchPanel() {
        mBus.post(new OnCloseSearchPanelEvent());
    }


    public void setSearchState(boolean isActive) {
        getViewState().setSearchState(isActive);
    }
}
