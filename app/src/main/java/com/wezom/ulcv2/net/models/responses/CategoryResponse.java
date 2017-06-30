package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Category;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 05.07.2016.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class CategoryResponse extends Response {

    @SerializedName("response")
    private ArrayList<Category> mResponse;
}
