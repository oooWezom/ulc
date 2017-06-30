package com.wezom.ulcv2.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.interfaces.ChatConnector;
import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.mvp.presenter.ChatPresenter;
import com.wezom.ulcv2.mvp.view.ChatFragmentView;
import com.wezom.ulcv2.mvp.view.TalkActivityView;
import com.wezom.ulcv2.ui.adapter.ChatAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created: Zorin A.
 * Date: 23.09.2016.
 */

public class ChatFragment extends BaseFragment implements ChatFragmentView, ChatConnector {
    public static final String TAG = ChatFragment.class.getSimpleName();
    private static final String EXTRA_NAME = "fragment_chat";
    @InjectPresenter
    ChatPresenter mPresenter;

    @BindView(R.id.chat_fragment_fake_video_frame)
    ViewGroup mFakeVideoFrame; //for padding
    @BindView(R.id.chat_fragment_chat_frame)
    RelativeLayout mChatFrameRelativeLayout;
    @BindView(R.id.fragment_chat_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_chat_message_edit_text)
    EditText mInputEditText;
    @BindView(R.id.fragment_chat_spectators_count_label)
    TextView mSpectatorsCountTextView;
    @BindView(R.id.fragment_chat_root)
    ViewGroup mRootView;

    @Inject
    ChatAdapter mChatAdapter;

    @BindDimen(R.dimen.chat_divider_padding)
    int mDividerPadding;

    private ItemListDividerDecorator mItemDecorator;
    private boolean isKbWasHidden = true;
    private boolean isKbWasShown;
    private int mFullHeight;
    private int mWidth;

    public static ChatFragment getNewInstance(String name) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_chat;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        final View rootView = getActivity().findViewById(R.id.talk_activity_root);
        listenLayoutOnKeyBoardAppear(rootView);
    }

    private void listenLayoutOnKeyBoardAppear(View rootView) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mFullHeight = rootView.getRootView().getHeight();
            mWidth = rootView.getRootView().getWidth();
            //rect will be populated with the coordinates of your view that area still visible.
            Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);
            int kBHeight = mFullHeight - (rect.bottom - rect.top);

            if (kBHeight > mFullHeight / 4) { // if more than 25%   pixels, its probably a keyboard...
                handleInputVisible(true);
            } else {
                handleInputVisible(false);
            }
        });
    }

    private void handleInputVisible(boolean needToShowKb) {
        if (needToShowKb && isKbWasHidden) {
            RelativeLayout.LayoutParams shrinkedFrameParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mFullHeight / 3); //chat padding from top is 30% of screen, when kb expanded

            mFakeVideoFrame.setLayoutParams(shrinkedFrameParams);
            mChatFrameRelativeLayout.requestLayout();

            isKbWasHidden = !isKbWasHidden;
            isKbWasShown = true;
        }

        if (!needToShowKb && isKbWasShown) {
            Log.v("RESIZE", " keyboard not visible");

            RelativeLayout.LayoutParams fullVisibleVideoFrameParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mWidth / 0.75f)); //4:3 aspect ratio
            mFakeVideoFrame.setLayoutParams(fullVisibleVideoFrameParams);
            mChatFrameRelativeLayout.requestLayout();

            isKbWasShown = !isKbWasShown;
            isKbWasHidden = true;

        }
    }

    @Override
    public void onMessageSent(NewMessage newMessage) {
        mChatAdapter.addData(newMessage);
        mInputEditText.setText(null);
    }

    @Override
    public void onChatClear() {
        mChatAdapter.clearData();
        mChatAdapter.clearColors();
    }

    @OnClick(R.id.fragment_chat_send_text_view)
    public void sendMessageClick() {
        String message = mInputEditText.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            mPresenter.sendMessage(message);
        }
    }

    private void setupRecyclerView() {
        if (mItemDecorator == null) {
            mItemDecorator = new ItemListDividerDecorator(getActivity(), R.drawable.bg_line_divider_dialogs);
            mItemDecorator.setPaddingLeft(mDividerPadding);
            mItemDecorator.setPaddingRight(mDividerPadding);
        }
        mRecyclerView.addItemDecoration(mItemDecorator);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mChatAdapter);
    }

    @Override
    public void sendNewMessage(NewMessage newMessage) {
//        newMessage.setText(String.format(": %s", newMessage.getText()));
        mChatAdapter.addData(newMessage);
        mRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    @Override
    public void clearChat() {
        mPresenter.clearChat();
    }

    @Override
    public void setSpectators(int spectators) {
        mSpectatorsCountTextView.setText(String.valueOf(spectators));
    }

    @Override
    public void showCloseSessionDialog(int title, int message) {
        ((TalkActivityView) getActivity()).showSignInDialog(title, message);
    }
}
