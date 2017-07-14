package hoppingvikings.housefinancemobile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.BIND_IMPORTANT;

/**
 * Created by iView on 14/07/2017.
 */

public class AppServiceBinder {
    public interface OnBindInterface
    {
        void OnBind();
        void OnUnbind();
    }

    public static BackgroundService _service;
    public static boolean IsBound = false;
    public static OnBindInterface owner = null;

    public static boolean ConnectToBackgroundService(Context context)
    {
        Intent intent = new Intent(context, BackgroundService.class);
        context.bindService(intent, _connection, BIND_IMPORTANT | BIND_AUTO_CREATE);

        return true;
    }

    public static boolean DisconnectBackgroundService(Context context)
    {
        context.unbindService(_connection);
        return true;
    }

    private static ServiceConnection _connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            _service = binder.getService();
            IsBound = true;

            if(owner != null)
            {
                owner.OnBind();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            IsBound = false;
            _service = null;

            if(owner != null)
            {
                owner.OnUnbind();
            }
        }
    };

}
