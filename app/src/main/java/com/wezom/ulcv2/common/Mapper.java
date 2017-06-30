package com.wezom.ulcv2.common;

/**
 * Created: Zorin A.
 * Date: 03.06.2016.
 */
public interface Mapper<From, To> {
    To convertFrom(From from);
    From convertTo(To to);
}
