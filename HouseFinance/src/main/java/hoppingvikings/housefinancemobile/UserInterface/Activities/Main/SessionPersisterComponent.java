package hoppingvikings.housefinancemobile.UserInterface.Activities.Main;

import dagger.Component;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

@SessionPersisterScope
@Component(modules = SessionPersisterModule.class)
public interface SessionPersisterComponent
{
    SessionPersister GetSessionPersister();
}
