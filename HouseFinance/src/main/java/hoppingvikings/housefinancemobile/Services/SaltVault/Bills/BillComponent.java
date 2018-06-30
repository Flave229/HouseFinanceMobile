package hoppingvikings.housefinancemobile.Services.SaltVault.Bills;

import dagger.Component;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.SessionPersisterComponent;

@BillScope
@Component(modules = BillModule.class, dependencies = SessionPersisterComponent.class )
public interface BillComponent
{
    BillEndpoint GetBillEndpoint();
    PaymentsEndpoint GetPaymentsEndpoint();
}