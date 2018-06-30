package hoppingvikings.housefinancemobile.Services.SaltVault.User;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdInviteEndpoint;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

@Module
public class UserModule
{
    @Provides
    @UserScope
    public LogInEndpoint LogInEndpoint(SessionPersister session)
    {
        return new LogInEndpoint(session);
    }

    @Provides
    @UserScope
    public UserEndpoint UserEndpoint(SessionPersister session)
    {
        return new UserEndpoint(session);
    }
}