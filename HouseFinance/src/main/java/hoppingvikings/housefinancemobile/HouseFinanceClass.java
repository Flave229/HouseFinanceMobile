package hoppingvikings.housefinancemobile;

import android.app.Application;
import android.content.Intent;

import hoppingvikings.housefinancemobile.Endpoints.SaltVault.House.DaggerHouseholdComponent;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.House.HouseholdComponent;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.User.DaggerUserComponent;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.User.UserComponent;
import hoppingvikings.housefinancemobile.UserInterface.Activities.Main.DaggerNotificationWrapperComponent;
import hoppingvikings.housefinancemobile.UserInterface.Activities.Main.DaggerSessionPersisterComponent;
import hoppingvikings.housefinancemobile.UserInterface.Activities.Main.NotificationWrapperComponent;
import hoppingvikings.housefinancemobile.UserInterface.Activities.Main.SessionPersisterComponent;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class HouseFinanceClass extends Application implements AppServiceBinder.OnBindInterface
{
    private static NotificationWrapperComponent _notificationComponent;
    private static SessionPersisterComponent _sessionComponent;
    private static HouseholdComponent _householdComponent;
    private static UserComponent _userComponent;

    @Override
    public void OnBind()
    {
        // AppServiceBinder._service.ShowNotification("Started", NotificationManager.IMPORTANCE_DEFAULT);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        AppServiceBinder.owner = this;

        _notificationComponent = DaggerNotificationWrapperComponent.builder().build();
        _sessionComponent = DaggerSessionPersisterComponent.builder().build();
        _householdComponent = DaggerHouseholdComponent.builder()
                .sessionPersisterComponent(_sessionComponent)
                .build();
        _userComponent = DaggerUserComponent.builder()
                .sessionPersisterComponent(_sessionComponent)
                .build();

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

    public static NotificationWrapperComponent GetNotificationWrapperComponent()
    {
        return _notificationComponent;
    }

    public static SessionPersisterComponent GetSessionPersisterComponent()
    {
        return _sessionComponent;
    }

    public static HouseholdComponent GetHouseholdComponent()
    {
        return _householdComponent;
    }

    public static UserComponent GetUserComponent()
    {
        return _userComponent;
    }

    @Override
    public void OnUnbind()
    {
        AppServiceBinder.DisconnectBackgroundService(this);
    }
}
