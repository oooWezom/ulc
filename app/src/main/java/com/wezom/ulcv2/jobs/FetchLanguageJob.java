package com.wezom.ulcv2.jobs;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.events.LanguagesFetchEvent;
import com.wezom.ulcv2.events.OnMessageEvent;
import com.wezom.ulcv2.injection.component.ApplicationComponent;
import com.wezom.ulcv2.net.ApiManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 04.04.2016.
 */
public class FetchLanguageJob extends BaseJob {

    @Inject
    transient ApiManager mApiManager;
    @Inject
    transient EventBus mBus;

    public FetchLanguageJob() {
        super(new Params(UI_PRIORITY).groupBy("fetch-languages").requireNetwork());
    }

    @Override
    public void inject(ApplicationComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mApiManager.getLanguages()
                .subscribe(languageResponse -> {
                    mBus.post(new LanguagesFetchEvent(languageResponse));
                }, throwable -> {
                    mBus.post(new OnMessageEvent(R.string.error, R.string.network_error)

                    );
                });
    }

    @Override
    protected void onCancel(int cancelReason) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(10, 1000);
    }


}
