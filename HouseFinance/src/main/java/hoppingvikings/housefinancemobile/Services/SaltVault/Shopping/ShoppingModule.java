package hoppingvikings.housefinancemobile.Services.SaltVault.Shopping;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.BillEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.PaymentsEndpoint;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

@Module
public class ShoppingModule
{
    @Provides
    @ShoppingScope
    public ShoppingEndpoint ShoppingEndpoint(SessionPersister session)
    {
        return new ShoppingEndpoint(session);
    }
}