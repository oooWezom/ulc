package com.wezom.ulcv2.jobs;


import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.wezom.ulcv2.injection.component.ApplicationComponent;

/**
 * Created by kartavtsev.s on 08.02.2016.
 */
public abstract class BaseJob extends Job {

    public static final int UI_PRIORITY = 10;
    public static final int BACKGROUND_PRIORITY = 1;

    public BaseJob(Params params) {
        super(params);
    }

    public void inject(ApplicationComponent component) {

    }
}
