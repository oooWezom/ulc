package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.interfaces.OnCheckListener;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.mvp.view.ViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wezom.ulcv2.common.Constants.Games.HELICOPTER;
import static com.wezom.ulcv2.common.Constants.Games.MEMORIZE_HIDES;
import static com.wezom.ulcv2.common.Constants.Games.RANDOM;
import static com.wezom.ulcv2.common.Constants.Games.ROCK_SPOCK;
import static com.wezom.ulcv2.common.Constants.Games.SPIN_THE_DISKS;
import static com.wezom.ulcv2.common.Constants.Games.UFO;

/**
 * Created: Zorin A.
 * Date: 29.06.2016.
 */
public class GameItemView extends RelativeLayout implements ViewModel<Game> {
    @BindView(R.id.view_game_item_title)
    TextView mTitleTextView;
    @BindView(R.id.view_game_item_image)
    ImageView mImageView;
    @BindView(R.id.view_game_item_checkbox)
    CheckBox mCheckBox;

    private boolean mIsChecked;
    private OnCheckListener mListener;
    private Game mData;

    public GameItemView(Context context) {
        super(context);
        init(context);
    }

    public GameItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setListener(OnCheckListener listener) {
        mListener = listener;
    }

    void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_game_item, this);
        ButterKnife.bind(this);
    }

    @Override
    public void setData(Game data) {
        mData = data;
        int gameId = data.getGameId();
        if (checkGameId(HELICOPTER, gameId)) {
            setImage(R.drawable.ic_choose_land_it);
        }
        if (checkGameId(RANDOM, gameId)) {
            setImage(R.drawable.ic_choose_random);
        }
        if (checkGameId(ROCK_SPOCK, gameId)) {
            setImage(R.drawable.ic_choose_rock_spok);
        }
        if (checkGameId(SPIN_THE_DISKS, gameId)) {
            setImage(R.drawable.ic_choose_spin);
        }
        if (checkGameId(MEMORIZE_HIDES, gameId)) {
            setImage(R.drawable.ic_choose_math2);
        }
        if (checkGameId(UFO, gameId)) {
            setImage(R.drawable.ic_choose_x_cows);
        }

        mTitleTextView.setText(data.getResId());
        mCheckBox.setChecked(mData.isChecked());
    }

    private void setImage(int image) {
        mImageView.setImageResource(image);
    }

    private boolean checkGameId(Constants.Games game, int gameID) {
        return gameID == game.getId();
    }

    @OnClick({R.id.view_game_item_ripple_layout, R.id.view_game_item_checkbox})
    void onClick() {
        switchState();
        changeCheckStatus(mIsChecked);
        if (mListener != null) {
            mData.setChecked(mIsChecked);
            mListener.onItemChecked(mData);
        }
    }

    private void changeCheckStatus(boolean isSelected) {
        mCheckBox.setChecked(isSelected);
    }

    private void switchState() {
        mIsChecked = !mIsChecked;
    }


}
