package hoppingvikings.housefinancemobile.WebService;

public class SessionPersister
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
}
