package com.wezom.ulcv2;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.injection.component.ApplicationComponent;
import com.wezom.ulcv2.injection.component.DaggerApplicationComponent;
import com.wezom.ulcv2.injection.module.ApplicationModule;
import com.wezom.ulcv2.injection.module.CiceroneModule;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.UrlsModel;

import io.fabric.sdk.android.Fabric;
import lombok.Getter;
import lombok.experimental.Accessors;
import ru.terrakok.cicerone.Cicerone;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@Getter
@Accessors(prefix = "m")
public class ULCApplication extends Application {
    public static ULCApplication INSTANCE;
    public static ApplicationComponent mApplicationComponent;


    @Override
    public void onCreate() {
        //TODO:for testing purpose
        UrlsModel urlsModel = PreferenceManager.getBaseUrl(getApplicationContext());
        Constants.API_URL = urlsModel.getRestUrl();
        Constants.SOCKET_BASE_URL = urlsModel.getSocketUrl();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .ciceroneModule(new CiceroneModule(Cicerone.create()))
                .build();
        super.onCreate();
        INSTANCE = this;


        Fabric.with(this, new Crashlytics());
        ActiveAndroid.initialize(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder() //TODO uncomment
                .setDefaultFontPath("fonts/roboto_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
