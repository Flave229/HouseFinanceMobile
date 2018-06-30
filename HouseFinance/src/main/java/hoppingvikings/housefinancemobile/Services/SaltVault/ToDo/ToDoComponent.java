package hoppingvikings.housefinancemobile.Services.SaltVault.ToDo;

import dagger.Component;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.SessionPersisterComponent;

@ToDoScope
@Component(modules = ToDoModule.class, dependencies = SessionPersisterComponent.class )
public interface ToDoComponent
{
    ToDoEndpoint GetToDoEndpoint();
}