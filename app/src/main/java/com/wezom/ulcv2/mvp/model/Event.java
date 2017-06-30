package com.wezom.ulcv2.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by zorin.a on 27.03.2017.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Event {
    @SerializedName("id")
    public int id;
    @SerializedName("duration")
    public int duration;
}
