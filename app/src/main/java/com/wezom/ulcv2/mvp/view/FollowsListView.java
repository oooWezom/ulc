package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.net.models.Follower;

import java.util.ArrayList;

/**
 * Created by Sivolotskiy.v on 31.05.2016.
 */
public interface FollowsListView extends  ListLceView<ArrayList<Follower>> {
        void setData(ArrayList<Follower> response);
        }