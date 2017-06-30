package com.wezom.ulcv2.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.net.models.BlacklistRecord;
import com.wezom.ulcv2.net.models.Event;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.requests.ActiveTalksRequest;
import com.wezom.ulcv2.net.models.requests.AddToBlackListRequest;
import com.wezom.ulcv2.net.models.requests.BlacklistRequest;
import com.wezom.ulcv2.net.models.requests.ChangePasswordRequest;
import com.wezom.ulcv2.net.models.requests.ConfirmPasswordRequest;
import com.wezom.ulcv2.net.models.requests.ConfirmRegisterRequest;
import com.wezom.ulcv2.net.models.requests.DialogsRequest;
import com.wezom.ulcv2.net.models.requests.EventsRequest;
import com.wezom.ulcv2.net.models.requests.FollowsRequest;
import com.wezom.ulcv2.net.models.requests.LoginRequest;
import com.wezom.ulcv2.net.models.requests.MarkWarningAsReadRequest;
import com.wezom.ulcv2.net.models.requests.MessagesRequest;
import com.wezom.ulcv2.net.models.requests.MessagesSinceRequest;
import com.wezom.ulcv2.net.models.requests.PasswordRestoreRequest;
import com.wezom.ulcv2.net.models.requests.ProfileRequest;
import com.wezom.ulcv2.net.models.requests.ProfileUpdateRequest;
import com.wezom.ulcv2.net.models.requests.RegisterRequest;
import com.wezom.ulcv2.net.models.requests.RemoveFromBlackListRequest;
import com.wezom.ulcv2.net.models.requests.ReportUserRequest;
import com.wezom.ulcv2.net.models.requests.SearchRequest;
import com.wezom.ulcv2.net.models.responses.ActiveGamesResponse;
import com.wezom.ulcv2.net.models.responses.ActiveSessionResponse;
import com.wezom.ulcv2.net.models.responses.ActiveTalksResponse;
import com.wezom.ulcv2.net.models.responses.BlacklistResponse;
import com.wezom.ulcv2.net.models.responses.CategoryResponse;
import com.wezom.ulcv2.net.models.responses.ConfirmResponse;
import com.wezom.ulcv2.net.models.responses.CountersResponse;
import com.wezom.ulcv2.net.models.responses.DialogsResponse;
import com.wezom.ulcv2.net.models.responses.EventByIdResptnse;
import com.wezom.ulcv2.net.models.responses.EventsResponse;
import com.wezom.ulcv2.net.models.responses.FollowsResponse;
import com.wezom.ulcv2.net.models.responses.LanguageResponse;
import com.wezom.ulcv2.net.models.responses.LevelResponse;
import com.wezom.ulcv2.net.models.responses.LoginResponse;
import com.wezom.ulcv2.net.models.responses.MessagesResponse;
import com.wezom.ulcv2.net.models.responses.ProfileResponse;
import com.wezom.ulcv2.net.models.responses.ProfileSearchResponse;
import com.wezom.ulcv2.net.models.responses.RegisterResponse;
import com.wezom.ulcv2.net.models.responses.Response;
import com.wezom.ulcv2.net.models.responses.VersionResponse;
import com.wezom.ulcv2.net.services.ULCService;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
public class ApiManager {

    private ULCService mUlcService;
    private PreferenceManager mPreferenceManager;
    private Gson mGson;

    public ApiManager(ULCService service, PreferenceManager preferenceManager) {
        mUlcService = service;
        mPreferenceManager = preferenceManager;
        mGson = new GsonBuilder().create();
    }

    public Observable<RegisterResponse> register(RegisterRequest registerRequest) {
        return mUlcService.registrer(registerRequest).compose(applyTransformers());
    }

    public Observable<ConfirmResponse> confirmRegister(String key) {
        return mUlcService.confirmRegister(new ConfirmRegisterRequest(key))
                .doOnNext(confirm -> mPreferenceManager.setUserId(confirm.getId()))
                .compose(applyTransformers());
    }

    public Observable<LoginResponse> login(LoginRequest loginRequest) {
        return mUlcService.login(loginRequest)
                .doOnNext(loginResponse -> {
                    mPreferenceManager.setUserId(loginResponse.getId());
                }).compose(applyTransformers());
    }

    public Observable<EventsResponse> getSelfEvents(EventsRequest request) {
        return mUlcService.getSelfEvents(mPreferenceManager.getAuthToken(), request.getId(),
                request.getOffset(), request.getCount(), request.getFilter()).compose(applyTransformers());
    }

    public Observable<EventByIdResptnse> getEventById(int id) {
        return mUlcService.getEventById( id)
                .compose(applyTransformers());
    }

    public Observable<EventsResponse> getFollowingEvents(EventsRequest request) {
        return mUlcService.getFollowingEvents(mPreferenceManager.getAuthToken(), request.getId(),
                request.getOffset(), request.getCount())
                .compose(applyTransformers())
                .map(eventsResponse -> {
                    ArrayList<Event> events = new ArrayList<>();
                    PreferenceManager.FilterPrefs prefs = mPreferenceManager.getFilterPrefs();
                    for (Event event : eventsResponse.getResponse()) {
                        switch (event.getTypeId()) {
                            case 1: // follower
                                if (prefs.isFollows()) {
                                    events.add(event);
                                }
                                break;
                            case 2: // 2play
                                if (prefs.isGames()) {
                                    events.add(event);
                                }
                                break;
                            case 4: // 2talk
                                if (prefs.isTalkss()) {
                                    events.add(event);
                                }
                                break;
                        }
                    }

                    eventsResponse.setResponse(events);
                    return eventsResponse;
                });
    }

    public Observable<BlacklistResponse> getBlacklist(BlacklistRequest request) {
        return mUlcService
                .getBlacklist(mPreferenceManager.getAuthToken(), request.getOffset(), request.getCount())
                .compose(applyTransformers())
                .map(blacklistResponse -> {
                    ArrayList<BlacklistRecord> events = new ArrayList<>();

                    for (BlacklistRecord event : blacklistResponse.getResponse()) {
                        if (event.getName() != null) {
                            if (event.getName().toLowerCase().contains(request.getQuery().toLowerCase())) {
                                events.add(event);
                            }
                        }
                    }

                    blacklistResponse.setResponse(events);
                    return blacklistResponse;
                });
    }

    public Observable<ProfileResponse> getProfile(ProfileRequest request) {
        return mUlcService.getProfile(mPreferenceManager.getAuthToken(), request.getId())
                .compose(applyTransformers())
                .doOnNext(profileResponse -> {
                    if (request.getId() == 0) {
                        cacheProfileData(profileResponse.getProfile());
                    }
                });
    }

    public Observable<FollowsResponse> getFollowers(FollowsRequest request) {
        return mUlcService.getFollowers(mPreferenceManager.getAuthToken(),
                request.getId(), mGson.toJson(request.getQuery()), request.getOffset(), request.getCount()).compose(applyTransformers());
    }

    public Observable<FollowsResponse> getFollowing(FollowsRequest request) {
        return mUlcService.getFollowing(mPreferenceManager.getAuthToken(),
                request.getId(), mGson.toJson(request.getQuery()), request.getOffset(), request.getCount()).compose(applyTransformers());
    }

    public Observable<DialogsResponse> getDialogs(DialogsRequest request) {
        return mUlcService.getDialogs(mPreferenceManager.getAuthToken()).compose(applyTransformers());
    }

    public Observable<DialogsResponse> getDialogById(DialogsRequest request) {
        return mUlcService.getDialogById(mPreferenceManager.getAuthToken(), request.getDialogId()).compose(applyTransformers());
    }

    public Observable<MessagesResponse> getMessages(MessagesRequest request) {
        return mUlcService.getMessages(mPreferenceManager.getAuthToken(), request.getConversationId(),
                request.getOffset(), request.getCount()).compose(applyTransformers());
    }

    public Observable<MessagesResponse> getMessagesSince(MessagesSinceRequest request) {
        return mUlcService.getMessages(mPreferenceManager.getAuthToken(), request.getSinceId(), request.getConversationId(),
                request.getOffset(), request.getCount()).compose(applyTransformers());
    }

    public Observable<LanguageResponse> getLanguages() {
        return mUlcService.getLanguages()
                .doOnNext(Language::updateLanguages)
                .compose(applyTransformers());
    }

    public Observable<ProfileSearchResponse> search(SearchRequest request) {
        return mUlcService.search(mPreferenceManager.getAuthToken(), mGson.toJson(request.getQuery()),
                request.getOffset(), request.getCount())
                .compose(applyTransformers());
    }

    public Observable<Response> changePassword(ChangePasswordRequest request) {
        return mUlcService.changePassword(mPreferenceManager.getAuthToken(), request)
                .compose(applyTransformers());
    }

    public Observable<Response> addToBlackList(AddToBlackListRequest request) {
        return mUlcService.addToBlackList(mPreferenceManager.getAuthToken(), request.getId())
                .compose(applyTransformers());
    }

    public Observable<Response> removeFromBlacklist(RemoveFromBlackListRequest request) {
        return mUlcService.removeToBlacklist(mPreferenceManager.getAuthToken(), request.getId())
                .compose(applyTransformers());
    }

    public Observable<Response> reportUser(ReportUserRequest request) {
        return mUlcService.reportUser(mPreferenceManager.getAuthToken(), request.getId())
                .compose(applyTransformers());
    }

    public Observable<Response> updateProfile(ProfileUpdateRequest request) {
        return mUlcService.updateProfile(mPreferenceManager.getAuthToken(), request)
                .compose(applyTransformers());
    }

    public Observable<CountersResponse> getCounters() {
        return mUlcService.getCounters(mPreferenceManager.getAuthToken())
                .compose(applyTransformers());
    }

    public Observable<ActiveGamesResponse> getActiveGames() {
        return mUlcService.getActiveGames(mPreferenceManager.getAuthToken())
                .compose(applyTransformers());
    }

    public Observable<ActiveTalksResponse> getActiveTalks(ActiveTalksRequest request) {
        return mUlcService.getActiveTalks(request.getOffset(), request.getCount()).compose(applyTransformers());
    }

    public Observable<ActiveTalksResponse> getActiveTalksById(ActiveTalksRequest request) {
        return mUlcService.getActiveTalksByID(request.getCategoryId(), request.getOffset(), request.getCount())
                .compose(applyTransformers());
    }

    public Observable<Response> restorePassword(PasswordRestoreRequest request) {
        return mUlcService.restorePassword(request)
                .compose(applyTransformers());
    }

    public Observable<ConfirmResponse> confirmRestorePassword(ConfirmPasswordRequest request) {
        return mUlcService.confirmRestorePassword(request)
                .doOnNext(confirm -> mPreferenceManager.setUserId(confirm.getId()))
                .compose(applyTransformers());
    }

    public Observable<LevelResponse> getMaxLevel() {
        return mUlcService.getMaxLevel()
                .compose(applyTransformers());
    }

    public Observable<CategoryResponse> getCategories() {
        return mUlcService.getCategories()
                .compose(applyTransformers());
    }

    public Observable<Response> markWarningAsRead(MarkWarningAsReadRequest request) {
        return mUlcService.markWarningAsRead(mPreferenceManager.getAuthToken(), request.getTimeStamps())
                .compose(applyTransformers());
    }

    public Observable<VersionResponse> getApiVersion() {
        return mUlcService.getApiVersion()
                .compose(applyTransformers());
    }

    public Observable<ActiveSessionResponse> getActiveSession(int userId) {
        return mUlcService.getUserActiveSession(mPreferenceManager.getAuthToken(), userId)
                .compose(applyTransformers());
    }

    private void cacheProfileData(Profile profile) {
        mPreferenceManager.setProfileData(profile);
    }

    private <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T extends Response> Observable.Transformer<T, T> applyErrorChecker() {
        return observable -> observable.first(t -> {
            if (!t.getResult().equalsIgnoreCase("ok")) {
                String message = t.getErrorMessage() == null ? "" : t.getErrorMessage();
                throw new ApiException(message, t.getError());
            }
            return true;
        });
    }

    private <T extends Response> Observable.Transformer<T, T> applyTransformers() {
        return observable -> observable
                .compose(applySchedulers())
                .compose(applyErrorChecker());
    }
}
