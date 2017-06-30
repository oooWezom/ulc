package com.wezom.ulcv2.common;

import java.text.DecimalFormat;

/**
 * Created: Zorin A.
 * Date: 03.06.2016.
 */
public class QuantityMapper implements Mapper<Integer, String> {

    @Override
    public String convertFrom(Integer integer) {

        String output = "0";
        float input = integer;
        if (input > 0 && input < 999) {
            output = integer + "";
        }
        if (input >= 1000 && input <= 999999) {
            input /= 1000;
            input = Math.round(input);
            if (input == 1000) {
                input /= 1000;
                output = new DecimalFormat("#.#").format(input) + "M";
            } else {
                output = new DecimalFormat("#.#").format(input) + "K";
            }

        }
        if (input >= 1000000 && input <= 999999999) {
            input /= 1000000;
            if (input == 1000) {
                input /= 1000;
                output = new DecimalFormat("#.#").format(input) + "B";
            } else {
                output = new DecimalFormat("#.#").format(input) + "M";
            }

        }
        if (input >= 1000000000) {
            input /= 1000000000;
            output = new DecimalFormat("#.#").format(input) + "B";
        }
        return output;
    }

    public String convertFrom(Long l) {

        String output = "0";
        float input = l;
        if (input > 0 && input < 999) {
            output = l + "";
        }
        if (input >= 1000 && input <= 999999) {
            input /= 1000;
            input = Math.round(input);
            if (input == 1000) {
                input /= 1000;
                output = new DecimalFormat("#.#").format(input) + "M";
            } else {
                output = new DecimalFormat("#.#").format(input) + "K";
            }

        }
        if (input >= 1000000 && input <= 999999999) {
            input /= 1000000;
            if (input == 1000) {
                input /= 1000;
                output = new DecimalFormat("#.#").format(input) + "B";
            } else {
                output = new DecimalFormat("#.#").format(input) + "M";
            }

        }
        if (input >= 1000000000) {
            input /= 1000000000;
            output = new DecimalFormat("#.#").format(input) + "B";
        }
        return output;
    }

    @Override
    public Integer convertTo(String s) {
        return 0;
    }


}
