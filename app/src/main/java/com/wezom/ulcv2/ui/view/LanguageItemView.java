package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.interfaces.OnCheckChangeListener;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.view.ViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created: Zorin A.
 * Date: 09.06.2016.
 */

public class LanguageItemView extends RelativeLayout implements ViewModel<Language> {

    //region views
    @BindView(R.id.view_language_text)
    TextView mTitle;
    @BindView(R.id.view_language_checkbox)
    CheckBox mCheckBox;
    //endregion

    private boolean mCheckStatus;
    private OnCheckChangeListener mOnCheckChangeListener;
    private int mItemPosition;

    public LanguageItemView(Context context) {
        super(context);
        init(context);
    }


    private void sendCheckStatus(boolean checked) {
        if (mOnCheckChangeListener != null) {
            mOnCheckChangeListener.onItemChecked(checked, mItemPosition);
        }
    }

    private void switchCheckStatus() {
        mCheckStatus = !mCheckStatus;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_language, this);
        ButterKnife.bind(this);
    }

    private void changeCheckStatus(boolean status) {
        mCheckBox.setChecked(status);
    }

    @Override
    public void setData(Language data) {
        mCheckStatus = data.isCheckStatus();
        mTitle.setText(data.getName());
        mCheckBox.setChecked(mCheckStatus);
    }

    @OnClick({R.id.view_language_root, R.id.view_language_checkbox})
    public void onClick(View view) {
        switchCheckStatus();
        changeCheckStatus(mCheckStatus);
        sendCheckStatus(mCheckStatus);
    }

    public void setOnCheckChangeListener(OnCheckChangeListener listener) {
        mOnCheckChangeListener = listener;
    }

    public void setItemPosition(int position) {
        mItemPosition = position;
    }
}

