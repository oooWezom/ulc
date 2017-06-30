package com.wezom.ulcv2.managers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.ui.activity.HomeActivity;

import javax.inject.Inject;

import static com.wezom.ulcv2.ui.activity.HomeActivity.AVATAR_KEY;
import static com.wezom.ulcv2.ui.activity.HomeActivity.DIALOG_ID_KEY;
import static com.wezom.ulcv2.ui.activity.HomeActivity.NAME_KEY;
import static com.wezom.ulcv2.ui.activity.HomeActivity.REQUEST_CODE_OPEN_DIALOG;
import static com.wezom.ulcv2.ui.activity.HomeActivity.USER_ID_KEY;

/**
 * Created by kartavtsev.s on 01.02.2016.
 */
public class NotificationManager {

    private static final int MESSAGE_NOTIFICATION_ID = 0;
    private static final int FOLLOWER_NOTIFICATION_ID = 1;

    public static final String ACTION_MESSAGE = "message";
    public static final String ACTION_FOLLOW = "follow";

    private Context mContext;
    private android.app.NotificationManager mNotificationManager;

    @Inject
    public NotificationManager(@ApplicationContext Context context) {
        mContext = context;
        mNotificationManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void newMessageNotification(String message, int dialogId, String name, String avatar) {
        Intent resultIntent = new Intent(mContext, HomeActivity.class);
        resultIntent.putExtra(DIALOG_ID_KEY, dialogId);
        resultIntent.putExtra(AVATAR_KEY, avatar);
        resultIntent.putExtra(NAME_KEY, name);
        resultIntent.setAction(ACTION_MESSAGE);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE_OPEN_DIALOG,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(mContext.getString(R.string.new_message_notification_title))
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, notification);
    }

    public void newFollowerNotification(String userName, int userId) {
        Intent resultIntent = new Intent(mContext, HomeActivity.class);
        resultIntent.putExtra(USER_ID_KEY, userId);
        resultIntent.setAction(ACTION_FOLLOW);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                REQUEST_CODE_OPEN_DIALOG, resultIntent, 0);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_follows_white)
                .setContentTitle(mContext.getString(R.string.new_follower_notification_title))
                .setContentText(mContext.getString(R.string.new_follower_notification_message, userName))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        mNotificationManager.notify(FOLLOWER_NOTIFICATION_ID, notification);
    }
}
