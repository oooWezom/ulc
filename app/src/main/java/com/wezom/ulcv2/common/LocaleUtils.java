package com.wezom.ulcv2.common;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.StringDef;
import android.util.Log;

import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created: Zorin A.
 * Date: 01.03.2017.
 */

public class LocaleUtils {
    public static final String ENGLISH = "en";
    public static final String RUSSIAN = "ru";
    @Inject
    PreferenceManager preferenceManager;
    @Inject
    @ApplicationContext
    Context context;

    @Inject
    public LocaleUtils() {
    }

    public String getLanguage() {
        return getPersistedData();
    }

    public String getCurrentLocale() {
        return context.getResources().getConfiguration().locale.toString();
    }

    public boolean setLocale(@LocaleDef String language) {
        persist(language);
        return updateResources(language);
    }

    private String getPersistedData() {
        return preferenceManager.getSelectedLanguage();
    }

    private void persist(String language) {
        preferenceManager.setSelectedLanguage(language);
    }

    private boolean updateResources(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

    public void init() {
        String persistedLocale = getPersistedData();
        if (persistedLocale.equals(ENGLISH)) {
            setLocale(ENGLISH);
        } else if (persistedLocale.equals(RUSSIAN)) {
            setLocale(RUSSIAN);
        } else {
            Log.v("LOCALE", "locale not set by user, using default");
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, RUSSIAN})
    @interface LocaleDef {
    }
}
