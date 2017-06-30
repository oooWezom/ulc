package com.wezom.ulcv2.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.wezom.ulcv2.R;


/**
 * Created by kartavtsev.s on 02.03.2016.
 */
public class VoteView extends View { //TODO delete

    private Paint mPaint = new Paint();
    private RectF mRect = new RectF();
    private float mProgressSize;
    private float mProgress = 40;
    private float mProgressAngle = (mProgress / 100) * 360;
    private int mProgressColor;
    private int mProgressBackgroundColor;

    public VoteView(Context context) {
        super(context);
        init();
    }

    public VoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public VoteView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        this.mProgressAngle = (progress / 100f) * 360f;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int finalSize;

        if (originalHeight > originalWidth) {
            finalSize = originalWidth;
        } else {
            finalSize = originalHeight;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalSize, MeasureSpec.EXACTLY));

        mRect.set(mProgressSize, mProgressSize,
                finalSize - mProgressSize, finalSize - mProgressSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2, height / 2, (width / 2), mPaint);

        mPaint.setColor(mProgressBackgroundColor);
        canvas.drawCircle(width / 2, height / 2, mRect.width() / 2, mPaint);

        mPaint.setColor(mProgressColor);
        canvas.drawArc(mRect, -90, -mProgressAngle, true, mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2, height / 2, (width / 2) - (mProgressSize * 2), mPaint);
    }

    private void init() {
        mProgressSize = getResources().getDimensionPixelSize(R.dimen.vote_view_progress_size);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        mProgressColor = getResources().getColor(R.color.colorPrimary);
        float[] hsv = new float[3];
        Color.colorToHSV(mProgressColor, hsv);
        hsv[1] = 0.3f; // lower the saturation
        mProgressBackgroundColor = Color.HSVToColor(hsv);
        mPaint.setAntiAlias(true);
    }
}