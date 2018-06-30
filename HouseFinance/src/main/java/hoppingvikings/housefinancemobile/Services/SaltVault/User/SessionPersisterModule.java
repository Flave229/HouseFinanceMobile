package hoppingvikings.housefinancemobile.Services.SaltVault.User;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

@Module
public class SessionPersisterModule
{
    @Provides
    @SessionPersisterScope
    public SessionPersister SessionPersister()
    {
        return new SessionPersister();
    }
}