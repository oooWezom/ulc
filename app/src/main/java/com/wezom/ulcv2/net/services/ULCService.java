package com.wezom.ulcv2.net.services;

import com.wezom.ulcv2.net.models.Event;
import com.wezom.ulcv2.net.models.requests.ChangePasswordRequest;
import com.wezom.ulcv2.net.models.requests.ConfirmPasswordRequest;
import com.wezom.ulcv2.net.models.requests.ConfirmRegisterRequest;
import com.wezom.ulcv2.net.models.requests.LoginRequest;
import com.wezom.ulcv2.net.models.requests.PasswordRestoreRequest;
import com.wezom.ulcv2.net.models.requests.ProfileUpdateRequest;
import com.wezom.ulcv2.net.models.requests.RegisterRequest;
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

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
public interface ULCService {

    @POST("/api/auth/register")
    Observable<RegisterResponse> registrer(@Body RegisterRequest registerRequest);

    @POST("/api/auth/confirm/account")
    Observable<ConfirmResponse> confirmRegister(@Body ConfirmRegisterRequest key);

    @POST("/api/auth/login")
    Observable<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("/api/events/{id}/self")
    Observable<EventsResponse> getSelfEvents(@Header("X-ULC-Token") String authToken,
                                             @Path("id") int id,
                                             @Query("offset") int offset,
                                             @Query("count") int count,
                                             @Query("filters") int filter);

    @GET("/api/events/{id}")
    Observable<EventByIdResptnse> getEventById(@Path("id") int id);

    @GET("/api/events/{id}/following")
    Observable<EventsResponse> getFollowingEvents(@Header("X-ULC-Token") String authToken,
                                                  @Path("id") int id,
                                                  @Query("offset") int offset,
                                                  @Query("count") int count);

    @GET("/api/profiles/{id}")
    Observable<ProfileResponse> getProfile(@Header("X-ULC-Token") String authToken,
                                           @Path("id") int id);

    @GET("/api/following/{id}")
    Observable<FollowsResponse> getFollowing(@Header("X-ULC-Token") String authToken,
                                             @Path("id") int id,
                                             @Query("query") String query,
                                             @Query("offset") int offset,
                                             @Query("count") int count);

    @GET("/api/followers/{id}")
    Observable<FollowsResponse> getFollowers(@Header("X-ULC-Token") String authToken,
                                             @Path("id") int id,
                                             @Query("query") String query,
                                             @Query("offset") int offset,
                                             @Query("count") int count);

    @GET("/api/followers/{id}")
    Observable<FollowsResponse> getAllFollowers(@Header("X-ULC-Token") String authToken,
                                                @Path("id") int id,
                                                @Query("offset") int offset,
                                                @Query("count") int count);

    @GET("/api/conversations")
    Observable<DialogsResponse> getDialogs(@Header("X-ULC-Token") String authToken);

    @GET("/api/conversations/{id}")
    Observable<DialogsResponse> getDialogById(@Header("X-ULC-Token") String authToken,
                                              @Query("id") int id);

    @GET("/api/database/languages")
    Observable<LanguageResponse> getLanguages();

    @GET("/api/messages")
    Observable<MessagesResponse> getMessages(@Header("X-ULC-Token") String authToken,
                                             @Query("id") int conversationId,
                                             @Query("offset") int offset,
                                             @Query("count") int count);

    @GET("/api/messages/since/{id}")
    Observable<MessagesResponse> getMessages(@Header("X-ULC-Token") String authToken,
                                             @Path("id") int sinceId,
                                             @Query("id") int conversationId,
                                             @Query("offset") int offset,
                                             @Query("count") int count);

    @GET("/api/profiles/")
    Observable<ProfileSearchResponse> search(@Header("X-ULC-Token") String authToken,
                                             @Query(value = "query") String query,
                                             @Query("offset") int offset,
                                             @Query("count") int count);

    @GET("/api/blacklist/")
    Observable<BlacklistResponse> getBlacklist(@Header("X-ULC-Token") String authToken,
                                               @Query("offset") int offset,
                                               @Query("count") int count);

    @PUT("/api/auth/password/update")
    Observable<Response> changePassword(@Header("X-ULC-Token") String authToken,
                                        @Body ChangePasswordRequest passwordBody);

    @POST("/api/blacklist/{id}")
    Observable<Response> addToBlackList(@Header("X-ULC-Token") String authToken,
                                        @Path("id") int id);

    @POST("/api/blacklist/{id}")
    Observable<Response> reportUser(@Header("X-ULC-Token") String authToken,
                                    @Path("id") int id);

    @DELETE("/api/blacklist/{id}")
    Observable<Response> removeToBlacklist(@Header("X-ULC-Token") String authToken,
                                           @Path("id") int id);

    @PUT("/api/profiles/")
    Observable<Response> updateProfile(@Header("X-ULC-Token") String authToken,
                                       @Body ProfileUpdateRequest request);

    @GET("/api/counters/")
    Observable<CountersResponse> getCounters(@Header("X-ULC-Token") String authToken);

    @GET("/api/games/active")
    Observable<ActiveGamesResponse> getActiveGames(@Header("X-ULC-Token") String authToken);

    @POST("/api/auth/restore/password")
    Observable<Response> restorePassword(@Body PasswordRestoreRequest request);

    @POST("/api/auth/confirm/restore/password")
    Observable<ConfirmResponse> confirmRestorePassword(@Body ConfirmPasswordRequest confirmPasswordRequest);

    @GET("/api/database/max-level")
    Observable<LevelResponse> getMaxLevel();

    @GET("/api/database/categories/talk")
    Observable<CategoryResponse> getCategories();

    @GET("/api/talks/")
    Observable<ActiveTalksResponse> getActiveTalks(@Query("offset") int offset,
                                                   @Query("count") int count);

    @GET("/api/talks/{category-id}")
    Observable<ActiveTalksResponse> getActiveTalksByID(@Path("category-id") int categoryId,
                                                       @Query("offset") int offset,
                                                       @Query("count") int count);

    @PUT("/api/counters/warnings")
    Observable<Response> markWarningAsRead(@Header("X-ULC-Token") String authToken, ArrayList<Long> stamps);

    @GET("/api/version")
    Observable<VersionResponse> getApiVersion();

    @GET("/api/sessions/user/{id}")
    Observable<ActiveSessionResponse> getUserActiveSession(@Header("X-ULC-Token") String authToken,
                                                           @Path("id") int userId);
}
