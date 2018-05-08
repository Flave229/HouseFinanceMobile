package hoppingvikings.housefinancemobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import hoppingvikings.housefinancemobile.UserInterface.MainActivity;

/**
 * Created by iView on 14/07/2017.
 */

public class BackgroundService extends Service {
    public class LocalBinder extends Binder
    {
        public BackgroundService getService()
        {
            return BackgroundService.this;
        }
    }

    private final IBinder _binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }

    public void ShowNotification(GlobalObjects.NotificationType type, String text, String subtext, int notifid)
    {
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, MainActivity.class);
        String channelID = getString(R.string.notification_channel_id);
        Notification not;

        switch (type)
        {
            case SHOPPING:
                CreateNotificationChannel(GlobalObjects.NotificationType.SHOPPING);

                not = new NotificationCompat.Builder(this, channelID)
                        .setContentTitle("Salt Vault Shopping")
                        .setContentText(text)
                        .setSubText(subtext)
                        .setSmallIcon(R.drawable.ic_app_notif)
                        .setContentIntent(PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .build();

                man.notify(notifid, not);
                break;

            case TODO:
                CreateNotificationChannel(GlobalObjects.NotificationType.TODO);
                not = new NotificationCompat.Builder(this, channelID)
                        .setContentTitle("Salt Vault Todo")
                        .setContentText(text)
                        .setSubText(subtext)
                        .setSmallIcon(R.drawable.ic_app_notif)
                        .setContentIntent(PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .build();

                man.notify(notifid, not);
                break;
        }
    }

    private void CreateNotificationChannel(GlobalObjects.NotificationType type)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager mgr = getSystemService(NotificationManager.class);
            switch (type)
            {
                case SHOPPING:
                    CharSequence name = getString(R.string.shopping_notification_channel_name);
                    String desc = getString(R.string.shopping_notification_channel_desc);
                    int importance = NotificationManager.IMPORTANCE_LOW;
                    NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), name, importance);
                    channel.setDescription(desc);
                    mgr.createNotificationChannel(channel);
                    break;

                case TODO:
                    CharSequence todoName = getString(R.string.todo_notification_channel_name);
                    String todoDesc = getString(R.string.todo_notification_channel_desc);
                    int todoImportance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel todoChannel = new NotificationChannel(getString(R.string.notification_channel_id), todoName, todoImportance);
                    todoChannel.setDescription(todoDesc);
                    mgr.createNotificationChannel(todoChannel);
                    break;
            }
        }
    }
}
