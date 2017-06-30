package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.SearchGamesResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
@NoArgsConstructor
public class SearchGameEvent {

    private SearchGamesResponse mSearchResponse;
}
