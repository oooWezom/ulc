package com.wezom.ulcv2.injection.module;

import android.content.Context;

import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.database.DatabaseManager;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.injection.scope.ApplicationScope;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.ProfileChannelHandler;
import com.wezom.ulcv2.net.ProfileChannelHandlerInTalk;
import com.wezom.ulcv2.net.SessionChannelHandler;
import com.wezom.ulcv2.net.TalkChannelHandler;
import com.wezom.ulcv2.net.services.ULCService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@Module
public class ApiModule {

    private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;

    @Provides
    @ApplicationScope
    public Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(Constants.API_URL)
                .build();
    }

    @Provides
    @ApplicationScope
    public ULCService provideUlcService(Retrofit retrofit) {
        return retrofit.create(ULCService.class);
    }

    @Provides
    @ApplicationScope
    public ApiManager provideApiManager(ULCService service,
                                        PreferenceManager preferenceManager) {
        return new ApiManager(service, preferenceManager);
    }

    @Provides
    @ApplicationScope
    public ProfileChannelHandler provideProfileChannelHandler(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
        return new ProfileChannelHandler(bus, context, preferenceManager);
    }
    @Provides
    @ApplicationScope
    public ProfileChannelHandlerInTalk provideProfileChannelHandlerInTalk(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
        return new ProfileChannelHandlerInTalk(bus, context, preferenceManager);
    }

    @Provides
    @ApplicationScope
    public TalkChannelHandler provideTalkChannelHandler(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
        return new TalkChannelHandler(bus, context, preferenceManager);
    }

    @Provides
    @ApplicationScope
    public SessionChannelHandler provideSessionChannelHandler(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
        return new SessionChannelHandler(bus, context, preferenceManager);
    }


    @Provides
    @ApplicationScope
    public DatabaseManager provideDatabaseManager(@ApplicationContext Context context, ApiManager apiManager) {
        return new DatabaseManager(apiManager);
    }

    @Provides
    @ApplicationScope
    OkHttpClient provideOkHttpClient(Context context) {
        return createOkHttpClient(context);
    }

    private static OkHttpClient createOkHttpClient(Context context) {
        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        return client;
    }
}
