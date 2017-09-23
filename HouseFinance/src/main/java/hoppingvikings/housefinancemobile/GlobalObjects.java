package hoppingvikings.housefinancemobile;

public class GlobalObjects
{
    // TODO: If we ever hook the app to the server in a way that the server can send notifications to the app,
    // TODO We will need to have a service running in the background. This will be used to access the service within the app.
    // TODO The boolean is just for a check to see if the service has started up and is bound.
    public static BackgroundService BackgroundService;
    public static boolean Bound = false;

    public static void ShowNotif(String text, String subtext, int notificationId)
    {
        AppServiceBinder._service.ShowNotification(text, subtext, notificationId);
    }
}