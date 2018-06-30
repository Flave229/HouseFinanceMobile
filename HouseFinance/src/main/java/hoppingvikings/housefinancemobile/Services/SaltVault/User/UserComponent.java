package hoppingvikings.housefinancemobile.Services.SaltVault.User;

import dagger.Component;

@UserScope
@Component(modules = UserModule.class, dependencies = SessionPersisterComponent.class )
public interface UserComponent
{
    LogInEndpoint GetLogInEndpoint();
    UserEndpoint GetUserEndpoint();
}