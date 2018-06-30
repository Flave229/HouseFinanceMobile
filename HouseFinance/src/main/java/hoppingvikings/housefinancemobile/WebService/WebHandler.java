package hoppingvikings.housefinancemobile.WebService;

import hoppingvikings.housefinancemobile.HouseFinanceClass;

public class WebHandler
{
    private static WebHandler _instance;
    private SessionPersister _session;

    private WebHandler()
    {
        _session = HouseFinanceClass.GetSessionPersisterComponent().GetSessionPersister();
    }

    public static WebHandler Instance()
    {
        if (_instance != null)
            return _instance;

        _instance = new WebHandler();
        return _instance;
    }
}