package hoppingvikings.housefinancemobile.Endpoints.SaltVault.House;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

@Module
public class HouseholdModule
{
    @Provides
    @HouseholdScope
    public HouseholdEndpoint HouseholdEndpoint(SessionPersister session)
    {
        return new HouseholdEndpoint(session);
    }

    @Provides
    @HouseholdScope
    public HouseholdInviteEndpoint HouseholdInviteEndpoint(SessionPersister session)
    {
        return new HouseholdInviteEndpoint(session);
    }
}