package hoppingvikings.housefinancemobile.Services.SaltVault.Bills;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdInviteEndpoint;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

@Module
public class BillModule
{
    @Provides
    @BillScope
    public BillEndpoint HouseholdEndpoint(SessionPersister session)
    {
        return new BillEndpoint(session);
    }

    @Provides
    @BillScope
    public PaymentsEndpoint PaymentsEndpoint(SessionPersister session)
    {
        return new PaymentsEndpoint(session);
    }
}