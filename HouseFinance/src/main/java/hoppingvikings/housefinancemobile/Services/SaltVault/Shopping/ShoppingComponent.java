package hoppingvikings.housefinancemobile.Services.SaltVault.Shopping;

import dagger.Component;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.BillEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.PaymentsEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.SessionPersisterComponent;

@ShoppingScope
@Component(modules = ShoppingModule.class, dependencies = SessionPersisterComponent.class )
public interface ShoppingComponent
{
    ShoppingEndpoint GetShoppingEndpoint();
}