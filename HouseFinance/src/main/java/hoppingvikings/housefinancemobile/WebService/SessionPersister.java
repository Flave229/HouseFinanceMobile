package hoppingvikings.housefinancemobile.WebService;

import java.io.Serializable;

import javax.inject.Inject;

public class SessionPersister implements Serializable
{
    private String _sessionID = "";

    @Inject
    public SessionPersister()
    {}

    public void SetSessionID(String sessionID)
    {
        _sessionID = sessionID;
    }

    public String GetSessionID()
    {
        return _sessionID;
    }

    public boolean HasSessionID()
    {
        return _sessionID.equals("") == false;
    }
}
