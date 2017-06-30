package com.wezom.ulcv2.injection.module;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.isseiaoki.simplecropview.util.Logger;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.injection.component.ApplicationComponent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.injection.scope.ApplicationScope;
import com.wezom.ulcv2.jobs.BaseJob;
import com.wezom.ulcv2.managers.NotificationManager;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@Module(includes = {ApiModule.class, CiceroneModule.class})
public class ApplicationModule {
    private Application mULCApplication;

    public ApplicationModule(Application application) {
        mULCApplication = application;
    }

    @Provides
    @ApplicationScope
    EventBus providesBus() {
        return EventBus.builder().throwSubscriberException(true).eventInheritance(true).build();
    }


    @Provides
    @ApplicationScope
    Application providesApplication() {
        return mULCApplication;
    }

    @Provides
    @ApplicationContext
    Context providesContext(Application application) {
        return application;
    }

    @Provides
    @ApplicationScope
    JobManager providesJobManager(ApplicationComponent component, @ApplicationContext Context context) {
        return new JobManager(new Configuration.Builder(context)
                .loadFactor(1).customLogger(new CustomLogger() {
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d("JobQueue", String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e("JobQueue", String.format(text, args));
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e("JobQueue", String.format(text, args));
                    }
                })
                .injector(job -> {
                    if (job instanceof BaseJob) {
                        ((BaseJob) job).inject(component);
                    }
                }).build());
    }

    @Provides
    @ApplicationScope
    public NotificationManager provideNotificationManager(@ApplicationContext Context context) {
        return new NotificationManager(context);
    }

    @Provides
    @ApplicationScope
    Picasso providePicasso(Context context, OkHttpClient client) {
        return new Picasso.Builder(context)
                .listener((picasso, uri, e) -> {
                    Logger.e(String.format("Failed to load image: %s Error:%s", uri, e.getMessage()));
                })
                .build();
    }
}
