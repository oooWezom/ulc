package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.Game;

import java.util.List;

/**
 * Created: Zorin A.
 * Date: 27.06.2016.
 */
public interface GamesFragmentView extends BaseFragmentView {

    void setFilteredGames(List<Game> games);

    void setAllGames(List<Game> games);

    void showEmptyView(boolean isShow);
}
