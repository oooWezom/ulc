package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.MessageEchoEvent;
import com.wezom.ulcv2.events.MessagesUpdatedEvent;
import com.wezom.ulcv2.events.NewMessageEvent;
import com.wezom.ulcv2.events.SetTitleEvent;
import com.wezom.ulcv2.mvp.model.Conversation;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.mvp.presenter.ConversationPresenter;
import com.wezom.ulcv2.mvp.view.ConversationView;
import com.wezom.ulcv2.net.models.responses.websocket.MessageEchoResponse;
import com.wezom.ulcv2.ui.adapter.MessagesAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Sivolotskiy.v on 16.06.2016.
 */
public class ConversationFragment extends ListLceFragment<ArrayList<Message>> implements ConversationView {

    private final static String KEY_PARTNER_ID = "key_partner_id";
    private final static String KEY_PARTNER_NAME = "key_partner_name";
    private final static String KEY_PARTNER_AVATAR = "key_partner_avatar";
    private static final String EXTRA_NAME = "fragment_conversation";

    @InjectPresenter
    ConversationPresenter mDialogFragmentPresenter;
    @Inject
    MessagesAdapter mMessagesAdapter;
    @Inject
    EventBus mBus;

    @BindView(R.id.fragment_dialog_send_button)
    Button mSendButton;
    @BindView(R.id.toolbar_image_view)
    ImageView mAvatarImageView;
    @BindView(R.id.fragment_dialog_message_text)
    EditText mMessageText;
    @BindView(R.id.fragment_conversation_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbat_conversation_title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.contentView)
    RecyclerView contentView;

    private int mPartnerId;
    private String mPartnerName;
    private String mPartnerAvatar;

    public static ConversationFragment getNewInstance(String name, Conversation conversation) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putInt(KEY_PARTNER_ID, conversation.getDialogId());
        bundle.putString(KEY_PARTNER_NAME, conversation.getName());
        bundle.putString(KEY_PARTNER_AVATAR, conversation.getAvatar());
        ConversationFragment dialogFragment = new ConversationFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public static ConversationFragment newInstance(int partnerId) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PARTNER_ID, partnerId);
        ConversationFragment dialogFragment = new ConversationFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_conversation;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
        mPartnerId = getArguments().getInt(KEY_PARTNER_ID);
        mPartnerName = getArguments().getString(KEY_PARTNER_NAME);
        mPartnerAvatar = getArguments().getString(KEY_PARTNER_AVATAR);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBus.isRegistered(this)) {
            mBus.unregister(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBus.isRegistered(this)) {
            mBus.register(this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setTitle(R.string.messages);
        mToolbar.setNavigationOnClickListener(v -> mDialogFragmentPresenter.onBackPressed());
        mSendButton.setVisibility(GONE);

        if (mPartnerAvatar != null) {
            Picasso.with(getContext())
                    .load(Constants.CONTENT_DEV_URL + mPartnerAvatar)
                    .placeholder(R.drawable.ic_default_user_avatar)
                    .into(mAvatarImageView);
        } else {
            mDialogFragmentPresenter.fetchPartnerAvatar(mPartnerId);
        }

        mTitleTextView.setText(mPartnerName);
        contentView.setItemAnimator(new SlideInUpAnimator());
        contentView.setAdapter(mMessagesAdapter);
        contentView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSendButton.setOnClickListener(v -> {
            mDialogFragmentPresenter.sendMessage(mMessageText.getText().toString(),
                    mPartnerId);
            mMessageText.setText("");
        });
        loadData(false);
        mDialogFragmentPresenter.fetchMessages(mPartnerId);
        if (mPartnerName == null) {
            mDialogFragmentPresenter.loadConversationName(mPartnerId);
        } else {
            setDialogName(mPartnerName);
        }
    }

    @Override
    public void setData(ArrayList<Message> data) {
        mMessagesAdapter.setData(data);
        scrollToEnd();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        mDialogFragmentPresenter.loadMessages(mPartnerId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelfMessageReceive(MessageEchoEvent event) {
        MessageEchoResponse.Message message = event.getEchoResponse().getMessage();
        mMessagesAdapter.addData(new Message(message.getId(), null,
                message.getText(), message.getPostedTimestamp(), message.getPostedDate(), false,
                1, true));
        scrollToEnd();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFetchEnd(MessagesUpdatedEvent event) {
        loadData(false);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void onNewMessage(NewMessageEvent event) {
        if (event.getMessage().getMessage().getSender().getId() == mPartnerId)
            mBus.cancelEventDelivery(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
    public void onNewMessageF(NewMessageEvent event) {
        com.wezom.ulcv2.net.models.Message message = event.getMessage().getMessage();
        if (message.getSender().getId() == mPartnerId) {
            mDialogFragmentPresenter.readMessage(mPartnerId, message.getId());
            mMessagesAdapter.addData(new Message(message.getId(), null,
                    message.getMessage(), message.getPostedTimestamp(), message.getPostedDate(),
                    false, 0, true));
            scrollToEnd();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEchoEvents(MessageEchoEvent event) {
        mDialogFragmentPresenter.loadMessages(event.getEchoResponse().getRecipientId());
    }

    @Override
    public void setNewMessages(ArrayList<Message> messages) {
        mMessagesAdapter.setData(messages);
        mMessagesAdapter.notifyItemInserted(0);
    }

    @Override
    public void setDialogName(String partnerName) {
        mBus.postSticky(new SetTitleEvent(partnerName));
    }

    @Override
    public void setPartnerAvatar(String avatar) {
        mPartnerAvatar = avatar;
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + mPartnerAvatar)
                .placeholder(R.drawable.ic_default_user_avatar)
                .into(mAvatarImageView);
    }

   /* @Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.LOGGED_IN)
                .build();
        features.add(feature);
        return features;
    }*/

    @OnTextChanged(R.id.fragment_dialog_message_text)
    void onTextChanged(CharSequence text) {
        mSendButton.setVisibility(text.toString().trim().isEmpty() ? GONE : VISIBLE);
    }

    @OnClick(R.id.toolbar_image_view)
    void onClickAvatar() {
        mDialogFragmentPresenter.onAvatarPressed(mPartnerId);
    }

    private void scrollToEnd() {
        contentView.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
    }
}
