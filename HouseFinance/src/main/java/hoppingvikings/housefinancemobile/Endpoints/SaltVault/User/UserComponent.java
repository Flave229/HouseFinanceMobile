package hoppingvikings.housefinancemobile.Endpoints.SaltVault.User;

import dagger.Component;
import hoppingvikings.housefinancemobile.UserInterface.Activities.Main.SessionPersisterComponent;

@UserScope
@Component(modules = UserModule.class, dependencies = SessionPersisterComponent.class )
public interface UserComponent
{
    LogInEndpoint GetLogInEndpoint();
    UserEndpoint GetUserEndpoint();
}