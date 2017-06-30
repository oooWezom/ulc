package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.wezom.ulcv2.R;

import butterknife.BindView;
import butterknife.OnClick;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 27.07.2016
 * Time: 12:27
 */

@Data
@Accessors(prefix = "m")
public class SearchParticipantView extends BaseView {

    @BindView(R.id.fragment_talk_search_edit_text)
    EditText mSearchEditText;
    @BindView(R.id.fragment_talk_search_recycler_view)
    RecyclerView mSearchRecyclerView;
    @BindView(R.id.fragment_talk_random_user_layout)
    ViewGroup mRandomUserLayout;
    @BindView(R.id.fragment_talk_search_clear_image_view)
    View mClearImageView;

    public SearchParticipantView(Context context) {
        super(context);
    }

    public SearchParticipantView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_search_participant;
    }
}
