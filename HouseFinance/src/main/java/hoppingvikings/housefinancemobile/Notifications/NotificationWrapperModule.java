package hoppingvikings.housefinancemobile.Notifications;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.NotificationWrapper;

@Module
public class NotificationWrapperModule
{
    @Provides
    @NotificationWrapperScope
    public NotificationWrapper NotificationWrapper()
    {
        return new NotificationWrapper();
    }
}