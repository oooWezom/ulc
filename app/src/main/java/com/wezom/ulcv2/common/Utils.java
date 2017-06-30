package com.wezom.ulcv2.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.managers.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by kartavtsev.s on 21.01.2016.
 */
public class Utils {

    public static final int BASE_EXP_AMOUNT = 100;
    public static final int BASE_WINS = 10;
    public static final int INC_WINS = 2;

    public static int measureContentWidth(Context context, ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        View itemView = null;
        int maxWidth = 0;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);

            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(context);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

    public static long calculateExpBoundary(int level) {
        return ((level - 1) * INC_WINS + BASE_WINS) * BASE_EXP_AMOUNT;
    }

    public static void defaultValidateAction(Context context, List<ValidationError> errors) {
        View mostTopView = errors.get(0).getView();
        for (ValidationError error : errors) {
            if (error.getView().getTop() < mostTopView.getTop()) {
                mostTopView = error.getView();
            }
        }

        mostTopView.requestFocus();
        for (ValidationError error : errors) {
            if (error.getView() instanceof TextView) {
                if (error.getView().getParent() != null && error.getView().getParent() instanceof TextInputLayout) {
                    ((TextInputLayout) error.getView().getParent())
                            .setError(error.getCollatedErrorMessage(context));
                } else {
                    ((TextView) error.getView()).setError(error.getCollatedErrorMessage(context));
                }
            }
        }
        Toast.makeText(context, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,   //prefix
                ".jpg",          //suffix
                storageDir       //directory
        );
        return image;
    }

    public static int randInt(int min, int max) {

        Random rand = new Random(System.currentTimeMillis());

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP);

        return "data:image/png;base64," + imageEncoded;
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        try {

            InputMethodManager inputManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    public static void showKeyboard(View view) {
        InputMethodManager keyboard = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(view, 0);
    }

    public static boolean isGameSupported(int gameId) {
        boolean isSupported = false;
        for (int game : Constants.SUPPORTED_GAMES) {
            if (game == gameId) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    public static String getCorrectCategoryIconSizeURL(Context context, boolean isSmall, boolean isWhite) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int screenDensity = metrics.densityDpi;

        String iconSize;
        String iconColor;

        switch (screenDensity) { //size selection
            case DisplayMetrics.DENSITY_HIGH:
                iconSize = isSmall ? "hdpi-s" : "hdpi";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                iconSize = isSmall ? "xhdpi-s" : "xhdpi";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                iconSize = isSmall ? "xxhdpi-s" : "xxhdpi";
                break;
            default:
                iconSize = isSmall ? "xhdpi-s" : "xhdpi";
                break;
        }

        if (isWhite) { //color selection
            iconColor = "talk_categories_white";
        } else {
            iconColor = "talk_categories_color";
        }

        return Constants.ICONS_BASE_URL + iconColor + "/" + iconSize + "/";
    }

    public static boolean checkAndroidIsKitKatOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
    public static boolean checkandroidLollipOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isUserLoggedIn(Context context) {
        String token = context.getSharedPreferences(PreferenceManager.PREFERENCES_NAME_KEY, 0).getString(PreferenceManager.TOKEN_KEY, "");
        return !TextUtils.isEmpty(token);

    }

    public int pixelsToDp(int pixels) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, displaymetrics);
        return dp;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
