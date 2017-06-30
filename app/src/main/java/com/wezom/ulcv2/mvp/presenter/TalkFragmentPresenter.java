package com.wezom.ulcv2.mvp.presenter;

import com.wezom.ulcv2.net.ProfileChannelHandler;
import com.wezom.ulcv2.net.models.requests.SearchRequest;

import static com.wezom.ulcv2.common.Constants.LOADING_MODE_INITIAL;
import static com.wezom.ulcv2.common.Constants.LOADING_MODE_REFRESH;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 23.07.2016
 * Time: 21:45
 */

public class TalkFragmentPresenter /*extends SessionFragmentPresenter<TalkFragmentView, TalkChannelHandler, TalkActivityPresenter>*/ {

    private ProfileChannelHandler mProfileChannelHandler;
    private String mRecipientName;
    private int mOffset;

   /* @Inject
    public TalkFragmentPresenter(Context context, EventBus bus,
                                 PreferenceManager preferenceManager,
                                 TalkChannelHandler talkChannelHandler,
                                 ProfileChannelHandler profileChannelHandler,
                                 TalkActivityPresenter talkActivityPresenter,
                                 ApiManager apiManager) {
        super(context, bus, preferenceManager, talkChannelHandler, talkActivityPresenter, apiManager);
        mProfileChannelHandler = profileChannelHandler;
    }*/

//    private Talk getTalk() {
//        return getActivityPresenter().getLeftStreamerTalk();
//    }

    public void searchUser(SearchRequest.Request request, int refreshMode) {
        if (refreshMode == LOADING_MODE_REFRESH || refreshMode == LOADING_MODE_INITIAL) {
            mOffset = 0;
        }

//        getView().showLoading(false);
//
//        SearchRequest searchRequest = new SearchRequest(request, mOffset, LOAD_ITEMS_LIMIT);
//        mSubscriptions.add(
//                mApiManager
//                        .search(searchRequest)
//                        .subscribe(response -> {
//                            if (isViewAttached()) {
//                                if (!(refreshMode == LOADING_MODE_ENDLESS)) {
//                                    getView().setData(response.getResponse());
//                                } else {
//                                    getView().addData(response.getResponse());
//                                }
//                                getView().showContent();
//                            }
//                        }, throwable -> {
//                            if (isViewAttached()) {
//                                getView().showError(throwable, false);
//                            }
//                        }, () -> mOffset += LOAD_ITEMS_LIMIT));
    }

    public void searchRandomUser() {
//        getSessionChannel().findTalkPartner();
    }

    public void cancelSearch() {
//        getSessionChannel().cancelFindTalkPartner();
    }

    public void inviteToTalk(int recipientId, String recipientName) {
//        mRecipientName = recipientName;
//        mProfileChannelHandler.inviteToTalk(recipientId, getTalk().getCategory());
    }

    public void sendDonateMessage(String message) {
//        getSessionChannel().sendDonateMessage(message);
    }

    public void showSpectatorsCount() {
//        if (isViewAttached()) {
//            if (getTalk() != null) {
//                getView().setSpectatorsCount(getTalk().getSpectatorsCount());
//            }
//        }
    }

    public void switchLeftTalk() {
//        int talkId = getActivityPresenter().getLeftStreamerTalk().getId();
//        getSessionChannel().switchTalk(talkId);
    }

    public void switchRightTalk() {
//        int talkId = getActivityPresenter().getRightStreamerTalk().getId();
//        getSessionChannel().switchTalk(talkId);
    }

//    @Subscribe(priority = 1)
//    public void onCreateTalk(CreateTalkEvent event) {
////        bus.cancelEventDelivery(event);
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onTalkState(TalkStateEvent event) {
////        showParticipantsData();
////        showSpectatorsCount();
////        if (isViewAttached()) {
////            if (getActivityPresenter().getTalkStreamersCount() > 1) {
////                getView().setTalkMode(TWO_PARTICIPANT_MODE, true);
////            } else {
////                getView().setTalkMode(ONE_PARTICIPANT_MODE, true);
////            }
////        }
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onTalkStarted(NewNotificationEvent event) {
////        showParticipantsData();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onTalkAdded(TalkAddedEvent event) {
////        getView().setTalkMode(TWO_PARTICIPANT_MODE, true);
////        showParticipantsData();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onTalkRemoved(TalkRemovedEvent event) {
////        if (isViewAttached()) {
////            getView().setTalkMode(ONE_PARTICIPANT_MODE, true);
////        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onStreamerConnected(TalkStreamerConnectedEvent event) {
////        if (isViewAttached()) {
////            getView().setTalkMode(TWO_PARTICIPANT_MODE, true);
////        }
//    }
//
//    @Subscribe
//    public void onStreamerDisconnectedEvent(TalkStreamerDisconnectedEvent event) {
////        if (isViewAttached()) {
////            getView().setTalkMode(ONE_PARTICIPANT_MODE, true);
////        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSpectatorConnected(TalkSpectatorConnectedEvent event) {
//        showSpectatorsCount();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSpectatorConnected(TalkSpectatorDisconnectedEvent event) {
//        showSpectatorsCount();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLikes(TalkLikesResponseEvent event) {
////        if (isViewAttached()) {
////            TalkLikesResponse response = event.getResponse();
////            if (response.getTalkId() == getActivityPresenter().getLeftStreamerTalk().getId()) {
////                getView().runLikesAnimation(LEFT);
////            } else {
////                getView().runLikesAnimation(RIGHT);
////            }
////        }
////        showLikes();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSendInviteToTalkResult(InviteToTalkSentResultEvent event) {
////        if (isViewAttached()) {
////            InviteToTalk inviteToTalk = event.getInviteToTalk();
////            int result = inviteToTalk.getResult();
////            if (result == InviteToTalkSentResultEvent.OK) {
////                getView().startWaitingForParticipant(inviteToTalk.getExpires());
////            } else {
////                getView().finishWaitingForParticipant(null);
////                getView().setTalkMode(ONE_PARTICIPANT_MODE, true);
////            }
////        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
//    public void onReceiveResponseToTalk(InviteToTalkResponseEvent event) {
////        if (isViewAttached()) {
////            String message = null;
////            int result = event.getResponse().getResult();
////
////            switch (result) {
////                case DENY:
////                    message = context.getString(R.string.refused, mRecipientName);
////                    break;
////                case DO_NOT_DISTURB:
////                    message = context.getString(R.string.asked_no_disturb, mRecipientName, event.getResponse().getTimeDoNotDisturb());
////                    break;
////            }
////
////            getView().finishWaitingForParticipant(message);
////
////            if (result != ACCEPT) {
////                getView().setTalkMode(ONE_PARTICIPANT_MODE, true);
////            }
////        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onTalkClosed(TalkClosedEvent event) {
////        if (isViewAttached()) {
////            if (mUserId == getLeftParticipantData().getId()) {
////                getView().setTalkMode(ONE_PARTICIPANT_MODE, true);
////            } else {
////                getView().showTalkClosedMessage();
////            }
////        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDonateMessage(DonateMessageEvent event) {
////        if (isViewAttached()) {
////            String message = event.getResponse().getMessage().getText();
////            getView().setDonateMessage(message);
////        }
//    }

    public void sendLikeToLeftUser() {
//        int talkId = getActivityPresenter().getLeftStreamerTalk().getId();
//        getSessionChannel().sendLocalLike(talkId, 1);
    }

    public void sendLikeToRightUser() {
//        int talkId = getActivityPresenter().getRightStreamerTalk().getId();
//        getSessionChannel().sendLocalLike(talkId, 1);
    }
}
