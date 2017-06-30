package com.wezom.ulcv2.features;

import android.os.Bundle;

import com.wezom.ulcv2.common.Constants;

/**
 * Created by kartavtsev.s on 15.03.2016.
 */
public class ToolbarFeature {

    private Bundle mBundle;

    private ToolbarFeature() {
        mBundle = new Bundle();
    }

    public static Builder newBuilder() {
        return new ToolbarFeature().new Builder();
    }

    public int getTitleRes() {
        return mBundle.getInt("title_res");
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public Constants.LayoutMode getLayoutMode() {
        int layoutMode = mBundle.getInt("mode");
        switch (layoutMode) {
            case 0:
                return Constants.LayoutMode.NOT_LOGGED_IN;
            case 1:
                return Constants.LayoutMode.NOT_LOGGED_IN_WITH_TOOLBAR;
            case 2:
                return Constants.LayoutMode.LOGGED_IN;
            case 3:
                return Constants.LayoutMode.LOGGED_IN_WITHOUT_TOOLBAR;
            default:
                return Constants.LayoutMode.NOT_LOGGED_IN;
        }
    }

    public class Builder {

        private Builder() {
        }

        public Builder setTitleRes(int titleRes) {
            ToolbarFeature.this.mBundle.putInt("title_res", titleRes);
            return this;
        }

        public Builder setLayoutMode(Constants.LayoutMode layoutMode) {
            ToolbarFeature.this.mBundle.putInt("mode", layoutMode.ordinal());
            return this;
        }

        public ToolbarFeature buildWithBundle(Bundle bundle) {
            ToolbarFeature.this.mBundle = bundle;
            return ToolbarFeature.this;
        }

      /*  public Feature build() {
            return new Feature("Toolbar", ToolbarFeature.this.mBundle);
        }*/
    }
}
