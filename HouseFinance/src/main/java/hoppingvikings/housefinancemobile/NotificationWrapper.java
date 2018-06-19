package hoppingvikings.housefinancemobile;

import java.io.Serializable;

public class NotificationWrapper implements Serializable
{
    public void ShowNotification(NotificationType type, String text, String subtext, int notificationId)
    {
        if (AppServiceBinder.CheckIsBound())
        {
            AppServiceBinder.GetService().ShowNotification(type, text, subtext, notificationId);
        }
    }
}