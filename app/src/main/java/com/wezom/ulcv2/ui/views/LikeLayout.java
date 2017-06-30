package com.wezom.ulcv2.ui.views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.QuantityMapper;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.interfaces.OnLikeClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;

/**
 * Created: Zorin A.
 * Date: 04.10.2016.
 */

public class LikeLayout extends RelativeLayout {

    //region var
    public static final int LEFT_POSITION = 0;
    public static final int RIGHT_POSITION = 1;

    int mHeartMode;

    int MIN_DURATION = 2000;
    int MAX_DURATION = 2200;

    int MIN_TRANSLATION_Y = -90;
    int MAX_TRANSLATION_Y = 90;

    int MIN_TRANSLATION_X = -90;
    int MAX_TRANSLATION_X = 90;

    final int MAX_BUBBLE_TRANSLATION_Y = 90;
    final int MAX_BUBBLE_TRANSLATION_X = 90;

    final int MIN_BUBBLE_TRANSLATION_X = -MAX_BUBBLE_TRANSLATION_X;
    final int MIN_BUBBLE_TRANSLATION_Y = -MAX_BUBBLE_TRANSLATION_Y;

    final int MIN_HEART_SIZE = 40;
    final int MAX_HEART_SIZE = 50;

    final int MIN_BUBBLE_SIZE = 13;
    final int MAX_BUBBLE_SIZE = 15;

    private int currentTint = 0;
    private int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW};
    //endregion

    //region views
    @BindView(R.id.heart_root)
    RelativeLayout mRootLayer;
    @BindView(R.id.heart_count)
    TextView mHeartTextView;
    @BindView(R.id.heart_icon)
    HeartBeatView mHeartIcon;
    @Getter
    @BindView(R.id.heart_view)
    ViewGroup mHeartView;
    //endregion

    QuantityMapper mMapper;
    OnLikeClickListener mLikeClickListener;
    private Animation mPulseAnim;
    private boolean mIsClickable = true;

    public LikeLayout(Context context, @HeartMode int heartMode) {
        super(context);
        init(context, heartMode);
    }

    public LikeLayout(Context context, AttributeSet attrs, @HeartMode int heartMode) {
        super(context, attrs);
        init(context, heartMode);

    }

    @NonNull
    private void init(Context context, @HeartMode int heartMode) {
        mMapper = new QuantityMapper();
        prepareAnim();

        LayoutInflater.from(context).inflate(R.layout.layout_heart, this);
        ButterKnife.bind(this);

        switchHeartMode(heartMode);
        prepareClicksListener();
        mHeartView.setAlpha(0.5f);
        mHeartIcon.setHeartBeatCallback(this::produceHeartStroke);
        changeHeartColor();
    }

    private void prepareAnim() {
        mPulseAnim = AnimationUtils.loadAnimation(getContext(), R.anim.heart_pulse);
    }

    @NonNull
    public void setMode(@HeartMode int mode) {
        mHeartMode = mode;
        switchHeartMode(mode);
    }

    public void setLikeClickListener(OnLikeClickListener listener) {
        mLikeClickListener = listener;
    }

    private void switchHeartMode(@HeartMode int mode) {
        if (mode == RIGHT_POSITION) {
            LayoutParams params = (LayoutParams) mHeartView.getLayoutParams();
            params.addRule(ALIGN_PARENT_END);
            params.addRule(ALIGN_PARENT_BOTTOM);
            mHeartView.setLayoutParams(params);
        }
        if (mode == LEFT_POSITION) {
            LayoutParams params = (LayoutParams) mHeartView.getLayoutParams();
            params.addRule(ALIGN_PARENT_START);
            params.addRule(ALIGN_PARENT_BOTTOM);
            mHeartView.setLayoutParams(params);
        }
    }

    private void prepareClicksListener() {
        ConnectableObservable<Void> connectableObservable = RxView.clicks(mHeartView).publish();

        connectableObservable
                .filter(click -> mIsClickable) //local clicks
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    if (mLikeClickListener != null) {
                        mLikeClickListener.onLocalLikeClick(1);
                    }
                });

        connectableObservable
                .filter(click -> mIsClickable) //network clicks pack with buffer
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(2, TimeUnit.SECONDS)
                .subscribe(clicksCount -> {
                    if (mLikeClickListener != null && clicksCount.size() > 0) {
                        mLikeClickListener.onNetworkLikeClick(clicksCount.size());
                    }
                });

        connectableObservable.connect();
    }

    public void performLikeAnimation(int pulseCount) {
        for (int i = 0; i < pulseCount; i++) {
            produceFlyingLike();
            produceBubbles();
        }

        mPulseAnim.setRepeatCount(pulseCount);
        mHeartView.setAlpha(1.0f);

        mHeartView.startAnimation(mPulseAnim);
        mHeartIcon.setNumberOfHeartBeats(pulseCount);
        mHeartIcon.start();
        mHeartView.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHeartView.setAlpha(0.5f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void produceFlyingLike() {
        SelfRemovableImageView heart = new SelfRemovableImageView(getContext());
        heart.setImageResource(R.drawable.ic_heart);
        int randomNum = getRandom(MIN_HEART_SIZE, MAX_HEART_SIZE);

        FrameLayout.LayoutParams iconParams = (FrameLayout.LayoutParams) mHeartIcon.getLayoutParams();
        FrameLayout.LayoutParams heartParams = new FrameLayout.LayoutParams(iconParams);
        heartParams.height = randomNum;
        heartParams.width = randomNum;
        heart.setLayoutParams(heartParams);

        Drawable drawable = heart.getDrawable();
        Drawable wrapDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapDrawable, currentTint);

        mHeartView.addView(heart, 0);

        int duration = getRandom(MIN_DURATION, MAX_DURATION);
        int translationX = getRandom(MIN_TRANSLATION_X, MAX_TRANSLATION_X);
        translationX = translationX % 2 == 0 ? translationX * -1 : translationX;
        int translationY = -getRandom(MIN_TRANSLATION_Y, MAX_TRANSLATION_Y);
        heart.selfDestructIn(duration, mHeartView);
        heart.animate()
                .translationX(translationX)
                .translationY(translationY)
                .scaleX(0)
                .scaleY(0)
                .alpha(0)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void produceHeartStroke() {
        SelfRemovableImageView heartStroke = new SelfRemovableImageView(getContext());
        heartStroke.setImageResource(R.drawable.ic_heart_stroke);

        FrameLayout.LayoutParams iconParams = (FrameLayout.LayoutParams) mHeartIcon.getLayoutParams();
        FrameLayout.LayoutParams heartStrokeParams = new FrameLayout.LayoutParams(iconParams);
        heartStroke.setLayoutParams(heartStrokeParams);
        heartStroke.setAlpha(0.4f);
        mHeartView.addView(heartStroke, 0);

        int duration = MIN_DURATION;
        heartStroke.selfDestructIn(duration, mHeartView);

        heartStroke.animate()
                .scaleXBy(0.75f)
                .scaleYBy(0.75f)
                .alpha(0)
                .setDuration(duration / 3)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void produceBubbles() {
        for (int i = 0; i < getRandom(1, 2); i++) {
            SelfRemovableImageView bubble = new SelfRemovableImageView(getContext());
            bubble.setImageResource(R.drawable.circle_bubble);

            int randomNum = getRandom(MIN_BUBBLE_SIZE, MAX_BUBBLE_SIZE);

            FrameLayout.LayoutParams iconParams = (FrameLayout.LayoutParams) mHeartIcon.getLayoutParams();
            FrameLayout.LayoutParams bubbleParams = new FrameLayout.LayoutParams(iconParams);
            bubbleParams.height = randomNum;
            bubbleParams.width = randomNum;
            bubble.setLayoutParams(bubbleParams);

            Drawable drawable = bubble.getDrawable();
            Drawable wrapDrawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrapDrawable, getRandomColor());
            mHeartView.addView(bubble, 0);

            int duration = MAX_DURATION;
            int translationX = getRandom(MIN_BUBBLE_TRANSLATION_X, MAX_BUBBLE_TRANSLATION_X);
            translationX = translationX % 2 == 0 ? translationX * -1 : translationX;
            int translationY = -getRandom(MIN_BUBBLE_TRANSLATION_Y, MAX_BUBBLE_TRANSLATION_Y);
            bubble.selfDestructIn(duration, mHeartView);
            bubble.animate()
                    .translationX(translationX)
                    .translationY(translationY)
                    .scaleX(0)
                    .scaleY(0)
                    .alpha(0)
                    .setDuration(duration)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    private void changeHeartColor() {
        final float[] red = new float[3],
                green = new float[3],
                yellow = new float[3],
                blue = new float[3],
                magenta = new float[3],
                cyan = new float[3];

        Color.colorToHSV(Color.RED, red);
        Color.colorToHSV(Color.GREEN, green);
        Color.colorToHSV(Color.YELLOW, yellow);
        Color.colorToHSV(Color.BLUE, blue);
        Color.colorToHSV(Color.MAGENTA, magenta);
        Color.colorToHSV(Color.CYAN, cyan);

        red[1] = 0.2f;
        green[1] = 0.4f;
        yellow[1] = 0.4f;
        blue[1] = 0.2f;
        magenta[1] = 0.2f;
        cyan[1] = 0.2f;

        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(
                Color.HSVToColor(red),
                Color.HSVToColor(green),
                Color.HSVToColor(green),
                Color.HSVToColor(yellow),
                Color.HSVToColor(yellow),
                Color.HSVToColor(blue),
                Color.HSVToColor(blue),
                Color.HSVToColor(magenta),
                Color.HSVToColor(magenta),
                Color.HSVToColor(cyan),
                Color.HSVToColor(cyan),
                Color.HSVToColor(red));

        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(valueAnimator -> {
            currentTint = (Integer) valueAnimator.getAnimatedValue();
            Drawable drawable = mHeartIcon.getDrawable();
            Drawable wrapDrawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrapDrawable, currentTint);
        });
        anim.setRepeatCount(ValueAnimator.INFINITE);
        int duration = Utils.checkandroidLollipOrLater() ? 15000 : 6000;
        anim.setDuration(duration);

        anim.start();
    }

    public void setLikeCount(int likeCount) {
        if (mHeartView != null) {
            mHeartTextView.setText(mMapper.convertFrom(likeCount));
        }
    }

    public void clearLikeCount() {
        mHeartTextView.setText(null);
    }

    private int getRandom(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    private int getRandomColor() {
        return colors[getRandom(1, colors.length) - 1];
    }

    public void setIsClickable(boolean clickable) {
        mIsClickable = clickable;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEFT_POSITION, RIGHT_POSITION})
    @interface HeartMode {
    }
}
