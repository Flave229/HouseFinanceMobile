package hoppingvikings.housefinancemobile;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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

    public void ShowNotification(String text, String subtext, int notifid)
    {
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent i = new Intent(this, MainActivity.class);

        Notification not = new NotificationCompat.Builder(this)
                .setContentTitle("House Finance")
                .setContentText(text)
                .setSubText(subtext)
                .setSmallIcon(R.drawable.ic_app_notif)
                .setContentIntent(PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        man.notify(notifid, not);
    }
}
