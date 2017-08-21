package hoppingvikings.housefinancemobile;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;

import hoppingvikings.housefinancemobile.WebService.WebHandler;

/**
 * Created by iView on 14/07/2017.
 */

public class HouseFinanceClass extends Application implements AppServiceBinder.OnBindInterface {

    @Override
    public void OnBind() {
      // AppServiceBinder._service.ShowNotification("Started", NotificationManager.IMPORTANCE_DEFAULT);
        if(GlobalObjects.webHandler == null)
            GlobalObjects.webHandler = new WebHandler();

        GlobalObjects.webHandler.SetAuthToken(getApplicationContext());
    }

    @Override
    public void OnUnbind() {
        AppServiceBinder.DisconnectBackgroundService(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppServiceBinder.owner = this;

        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent service = new Intent(getApplicationContext(), BackgroundService.class);
                startService(service);
                AppServiceBinder.ConnectToBackgroundService(getApplicationContext());
            }
        }, "HouseFinanceServiceThread");
        serviceThread.setPriority(Thread.NORM_PRIORITY);
        serviceThread.start();
    }
}
