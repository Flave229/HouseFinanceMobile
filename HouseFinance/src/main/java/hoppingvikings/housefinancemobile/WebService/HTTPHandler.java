package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

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
    protected abstract CommunicationRequest ConstructPost(final JSONObject postData);

    public final void SetRequestProperty(String key, String value)
    {
        _requestProperties.put(key, value);
    }

    public final void Get(Context context, final CommunicationCallback callback)
    {
        Get(context, callback, null);
    }

    public final void Get(Context context, final CommunicationCallback callback, Map<String, String> urlProperties)
    {
        RequestType requestType = RequestType.GET;
        if(CheckInternetConnection(context))
        {
            String urlPropertyString = ConstructURLPropertyString(urlProperties);
            CommunicationRequest request = ConstructGet(urlPropertyString);
            request.RequestTypeData = requestType;
            request.Callback = callback;
            new WebService(_requestProperties).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(requestType, "No internet connection");
        }
    }

    public final void Post(Context context, final CommunicationCallback callback, final JSONObject postData)
    {
        RequestType requestType = RequestType.POST;
        if(CheckInternetConnection(context))
        {
            CommunicationRequest request = ConstructPost(postData);
            request.RequestTypeData = requestType;
            request.Callback = callback;
            new WebService(_requestProperties).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(requestType, "No internet connection");
        }
    }

    public abstract void HandleResponse(CommunicationResponse result);
}
