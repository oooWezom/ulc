package com.wezom.ulcv2.net.models.requests;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class RegisterRequest {

    @SerializedName("login")
    private String mLogin;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("date_of_birth")
    private String mDateOfBirth;
    @SerializedName("sex")
    private int mSex;
    @SerializedName("languages")
    private HashSet<Integer> mLanguages;
}
