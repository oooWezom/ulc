package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.wezom.ulcv2.R;

import butterknife.BindView;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 15.07.2016
 * Time: 10:38
 */

public class ScoreView extends BaseView {

    @BindView(R.id.view_score_one_check_box)
    CheckBox mScoreOneCheckBox;
    @BindView(R.id.view_score_two_check_box)
    CheckBox mScoreTwoCheckBox;
    @BindView(R.id.view_score_three_check_box)
    CheckBox mScoreThreeCheckBox;

    public ScoreView(Context context) {
        super(context);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_score;
    }

    public void setScore(int score) {
        switch (score) {
            case 1:
                mScoreOneCheckBox.setChecked(true);
                break;
            case 2:
                mScoreOneCheckBox.setChecked(true);
                mScoreTwoCheckBox.setChecked(true);
                break;
            case 3:
                mScoreOneCheckBox.setChecked(true);
                mScoreTwoCheckBox.setChecked(true);
                mScoreThreeCheckBox.setChecked(true);
                break;
        }
    }
}
