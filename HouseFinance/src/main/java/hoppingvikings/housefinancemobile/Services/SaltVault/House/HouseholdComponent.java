package hoppingvikings.housefinancemobile.Services.SaltVault.House;

import dagger.Component;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.SessionPersisterComponent;

@HouseholdScope
@Component(modules = HouseholdModule.class, dependencies = SessionPersisterComponent.class )
public interface HouseholdComponent
{
    HouseholdEndpoint GetHouseholdEndpoint();
    HouseholdInviteEndpoint GetHouseholdInviteEndpoint();
}