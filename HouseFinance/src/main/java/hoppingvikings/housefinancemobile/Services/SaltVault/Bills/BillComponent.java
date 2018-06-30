package hoppingvikings.housefinancemobile.Services.SaltVault.Bills;

import dagger.Component;
import hoppingvikings.housefinancemobile.UserInterface.Activities.Main.SessionPersisterComponent;

@BillScope
@Component(modules = BillModule.class, dependencies = SessionPersisterComponent.class )
public interface BillComponent
{
    BillEndpoint GetBillEndpoint();
    PaymentsEndpoint GetPaymentsEndpoint();
}