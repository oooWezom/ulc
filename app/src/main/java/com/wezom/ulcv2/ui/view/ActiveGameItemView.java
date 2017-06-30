package com.wezom.ulcv2.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.QuantityMapper;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.net.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wezom.ulcv2.common.Constants.Games.HELICOPTER;
import static com.wezom.ulcv2.common.Constants.Games.MEMORIZE_HIDES;
import static com.wezom.ulcv2.common.Constants.Games.RANDOM;
import static com.wezom.ulcv2.common.Constants.Games.ROCK_SPOCK;
import static com.wezom.ulcv2.common.Constants.Games.SPIN_THE_DISKS;
import static com.wezom.ulcv2.common.Constants.Games.UFO;

/**
 * Created by zorin.a on 01.07.2016.
 */
public class ActiveGameItemView extends RelativeLayout implements ViewModel<Session> {

    @BindView(R.id.view_active_game_left_avatar_image)
    ImageView mLeftAvatarImage;
    @BindView(R.id.view_active_game_right_avatar_image)
    ImageView mRightAvatarImage;
    @BindView(R.id.view_active_game_game_name_text)
    TextView mGameNameText;
    @BindView(R.id.view_active_game_left_name_text)
    TextView mLeftNameText;
    @BindView(R.id.view_active_game_right_name_text)
    TextView mRightNameText;
    @BindView(R.id.view_active_game_watchers)
    TextView mViewersCountText;
    @BindView(R.id.view_active_game_left_webcam_image)
    ImageView mLeftWebcamImage;
    @BindView(R.id.view_active_game_right_webcam_image)
    ImageView mRightWebcamImage;
    @BindView(R.id.view_active_game_left_level_text)
    TextView mLeftLevelTextView;
    @BindView(R.id.view_active_game_right_level_text)
    TextView mRightLevelTextView;
    @BindView(R.id.view_active_game_icon)
    ImageView mGameIconImageView;


    public ActiveGameItemView(Context context) {
        super(context);
        init(context);
    }

    public ActiveGameItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActiveGameItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public ActiveGameItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void setData(Session data) {
        User leftPlayer = data.getPlayers().get(0);
        User rightPlayer = data.getPlayers().get(1);

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + leftPlayer.getAvatar()).into(mLeftAvatarImage);
        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + leftPlayer.getAvatar()).into(mLeftWebcamImage);
        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + rightPlayer.getAvatar()).into(mRightAvatarImage);
        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + rightPlayer.getAvatar()).into(mRightWebcamImage);

        mLeftNameText.setText(leftPlayer.getName());
        mRightNameText.setText(rightPlayer.getName());
        mLeftLevelTextView.setText(getContext().getString(R.string.lvl_format, leftPlayer.getLevel()));
        mRightLevelTextView.setText(getContext().getString(R.string.lvl_format, rightPlayer.getLevel()));
        mViewersCountText.setText(new QuantityMapper().convertFrom(data.getViewers()));
        if (Constants.Games.getResNameById(data.getGameId()) != null) {
            mGameNameText.setText(Constants.Games.getResNameById(data.getGameId()));
        } else {
            mGameNameText.setText(R.string.unknown_game);
        }

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
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_active_game_item, this);
        ButterKnife.bind(this);
    }

    private void setImage(int image) {
        mGameIconImageView.setImageResource(image);
    }

    private boolean checkGameId(Constants.Games game, int gameID) {
        return gameID == game.getId();
    }
}
