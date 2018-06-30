package hoppingvikings.housefinancemobile.Notifications;

import dagger.Component;
import hoppingvikings.housefinancemobile.NotificationWrapper;

@NotificationWrapperScope
@Component(modules = NotificationWrapperModule.class)
public interface NotificationWrapperComponent
{
    NotificationWrapper GetNotificationWrapper();
}
