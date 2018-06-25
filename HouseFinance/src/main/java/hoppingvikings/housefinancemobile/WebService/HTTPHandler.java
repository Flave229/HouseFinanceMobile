package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

abstract public class HTTPHandler
{
    private final Map<String, String> _requestProperties = new HashMap<>();

    private final boolean CheckInternetConnection(Context context)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    private final String ConstructURLPropertyString(Map<String, String> urlProperties)
    {
        if (urlProperties == null)
            return "";

        String parameterString = "?";
        for (String propertyKey : urlProperties.keySet())
        {
            parameterString += propertyKey + "=" + urlProperties.get(propertyKey) + "&";
        }

        // Remove last ampersand
        parameterString = parameterString.substring(0, parameterString.length() - 1);
        return parameterString;
    }

    protected abstract CommunicationRequest ConstructGet(String urlAdditions);

    public final void SetRequestProperty(String key, String value)
    {
        _requestProperties.put(key, value);
    }

    public final void Get(Context context, final CommunicationCallback callback)
    {
        Get(context, callback, null);
    }

    // TODO: Don't pass the clientID and sessionID here. Should have an option to pass custom authentication headers to the HTTPHandler
    public final void Get(Context context, final CommunicationCallback callback, Map<String, String> urlProperties)
    {
        if(CheckInternetConnection(context))
        {
            String urlPropertyString = ConstructURLPropertyString(urlProperties);
            CommunicationRequest request = ConstructGet(urlPropertyString);
            request.Callback = callback;
            new WebService(_requestProperties).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public abstract void HandleResponse(CommunicationResponse result);
}
