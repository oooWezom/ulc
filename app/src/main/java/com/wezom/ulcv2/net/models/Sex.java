package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
@Getter
public enum Sex {
    @SerializedName("1")
    FEMALE(1),
    @SerializedName("2")
    MALE(2);

    private int mId;

    Sex(int id) {
        mId = id;
    }

    public static Sex getSexById(int id) {
        Sex sex = null;
        for (Sex i : values()) {
            if (i.getId() == id) {
                sex = i;
            }
        }

        return sex;
    }
}
