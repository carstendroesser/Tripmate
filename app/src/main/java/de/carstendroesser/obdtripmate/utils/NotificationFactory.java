package de.carstendroesser.obdtripmate.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.activities.MainActivity;

/**
 * Created by carstendrosser on 20.06.17.
 */

public class NotificationFactory {

    // CONSTANTS

    public static final int NOTIFICATION_ID = 2707;

    /**
     * Creates a notification for a service.
     *
     * @param pContext we need that
     * @param pText    the text this notification shall have
     * @return the created notification
     */
    public static Notification getServiceNotification(Context pContext, String pText) {
        Intent intent = new Intent(pContext, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(pContext);
        mNotificationBuilder
                .setSmallIcon(R.drawable.engine)
                .setLargeIcon(BitmapFactory.decodeResource(pContext.getResources(), R.drawable.engine))
                .setContentTitle(pContext.getString(R.string.app_name))
                .setContentText(pText)
                .setTicker(pText)
                .setOnlyAlertOnce(true)
                .setContentIntent(PendingIntent.getActivity(pContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        return mNotificationBuilder.build();
    }

}
