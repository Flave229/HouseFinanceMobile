package hoppingvikings.housefinancemobile.Services.SaltVault.ToDo;

import dagger.Module;
import dagger.Provides;
import hoppingvikings.housefinancemobile.Services.SaltVault.Shopping.ShoppingEndpoint;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

@Module
public class ToDoModule
{
    @Provides
    @ToDoScope
    public ToDoEndpoint ToDoEndpoint(SessionPersister session)
    {
        return new ToDoEndpoint(session);
    }
}