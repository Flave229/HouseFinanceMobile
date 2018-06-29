package hoppingvikings.housefinancemobile.UserInterface.Activities.Main;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.House.HouseholdScope;
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