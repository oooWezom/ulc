package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.events.MessageEchoEvent;
import com.wezom.ulcv2.events.MessagesUpdatedEvent;
import com.wezom.ulcv2.events.NewMessageEvent;
import com.wezom.ulcv2.events.SetTitleEvent;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.mvp.presenter.DialogFragmentPresenter;
import com.wezom.ulcv2.mvp.view.DialogFragmentView;
import com.wezom.ulcv2.net.models.responses.websocket.MessageEchoResponse;
import com.wezom.ulcv2.ui.adapter.MessagesAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by kartavtsev.s on 25.01.2016.
 */
public class DialogFragment extends ListLceFragment<ArrayList<Message>>
        implements DialogFragmentView {

    private final static String KEY_PARTNER_ID = "key_partner_id";
    private final static String KEY_PARTNER_NAME = "key_partner_name";
    private static final String EXTRA_NAME = "fragment_dialog";

    @InjectPresenter
    DialogFragmentPresenter mDialogFragmentPresenter;

    @Inject
    MessagesAdapter mMessagesAdapter;

    @Inject
    EventBus mBus;

    @BindView(R.id.fragment_dialog_send_button)
    Button mSendButton;
    @BindView(R.id.fragment_dialog_message_text)
    EditText mMessageText;

    @BindView(R.id.contentView)
    RecyclerView contentView;

    private int mPartnerId;
    private String mPartnerName;

/*
    public static DialogFragment getNewInstance(String partnerName, int partnerId) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PARTNER_ID, partnerId);
        bundle.putString(KEY_PARTNER_NAME, partnerName);
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }
*/

    public static DialogFragment newInstance(String name, int partnerId) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putInt(KEY_PARTNER_ID, partnerId);
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_dialog;
    }


    @Override
    public void injectDependencies() {
        super.injectDependencies();
        getFragmentComponent().inject(this);
        mPartnerId = getArguments().getInt(KEY_PARTNER_ID);
        mPartnerName = getArguments().getString(KEY_PARTNER_NAME);
    }

    @Override
    public void setDialogName(String partnerName) {
        mBus.postSticky(new SetTitleEvent(partnerName));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    public void loadData(boolean pullToRefresh) {
        mDialogFragmentPresenter.loadMessages(mPartnerId);
    }

    @Override
    public void setData(ArrayList<Message> data) {
        mMessagesAdapter.setData(data);
        scrollToEnd();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelfMessageReceive(MessageEchoEvent event) {
        MessageEchoResponse.Message message = event.getEchoResponse().getMessage();
        mMessagesAdapter.addData(new Message(message.getId(), null,
                message.getText(), message.getPostedTimestamp(), message.getPostedDate(), false, 1, true));
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
            mMessagesAdapter.addData(new Message(message.getId(), null,
                    message.getMessage(), message.getPostedTimestamp(), message.getPostedDate(), false, 0, true));
            scrollToEnd();
        }
    }

    @Override
    public void setNewMessages(ArrayList<Message> messages) {
        mMessagesAdapter.setData(messages);
        mMessagesAdapter.notifyItemInserted(0);
        contentView.scrollToPosition(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    private void scrollToEnd() {
        contentView.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
    }
}
