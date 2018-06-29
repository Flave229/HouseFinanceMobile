package hoppingvikings.housefinancemobile;

import java.io.Serializable;

import javax.inject.Inject;

public class NotificationWrapper implements Serializable
{
    @Inject
    public NotificationWrapper()
    {}

    public void ShowNotification(NotificationType type, String text, String subtext, int notificationId)
    {
        if (AppServiceBinder.CheckIsBound())
        {
            AppServiceBinder.GetService().ShowNotification(type, text, subtext, notificationId);
        }
    }
}