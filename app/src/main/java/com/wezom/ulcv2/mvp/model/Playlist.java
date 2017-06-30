package com.wezom.ulcv2.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by zorin.a on 27.03.2017.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Playlist {
    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("state")
    @Expose
    public long state;
    @SerializedName("events")
    @Expose
    public List<Event> events;
}
