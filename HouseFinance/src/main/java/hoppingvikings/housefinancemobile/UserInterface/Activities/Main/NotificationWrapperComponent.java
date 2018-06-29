package hoppingvikings.housefinancemobile.UserInterface.Activities.Main;

import dagger.Component;
import hoppingvikings.housefinancemobile.NotificationWrapper;

@NotificationWrapperScope
@Component(modules = NotificationWrapperModule.class)
public interface NotificationWrapperComponent
{
    NotificationWrapper GetNotificationWrapper();
}
