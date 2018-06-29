package hoppingvikings.housefinancemobile.WebService;

import java.io.Serializable;

public class SessionPersister implements Serializable
{
    private String _sessionID = "";

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
