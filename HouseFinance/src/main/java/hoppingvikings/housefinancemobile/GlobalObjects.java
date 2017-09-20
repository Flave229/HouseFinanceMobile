package hoppingvikings.housefinancemobile;

import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class GlobalObjects
{
    public static WebHandler WebHandler;

    // TODO: If we ever hook the app to the server in a way that the server can send notifications to the app,
    // TODO We will need to have a service running in the background. This will be used to access the service within the app.
    // TODO The boolean is just for a check to see if the service has started up and is bound.
    public static BackgroundService BackgroundService;
    public static boolean Bound = false;

    public static final String SHOPPING_RECENTITEMS_FILENAME = "hf_shopping_recent.txt";

    public static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";

    public static final String ITEM_TYPE_SHOPPING = "item_shopping";
    public static final String ITEM_TYPE_BILL = "item_bill";
    public static final String ITEM_TYPE_BILLPAYMENT = "item_billpayment";

    public static void ShowNotif(String text, String subtext, int notificationId)
    {
        AppServiceBinder._service.ShowNotification(text, subtext, notificationId);
    }
}