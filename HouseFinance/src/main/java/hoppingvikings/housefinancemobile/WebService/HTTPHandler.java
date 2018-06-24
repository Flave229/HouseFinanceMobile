package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

abstract public class HTTPHandler
{
    private final boolean CheckInternetConnection(Context context)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    protected abstract CommunicationRequest ConstructGet();

    // TODO: Don't pass the clientID and sessionID here. Should have an option to pass custom authentication headers to the HTTPHandler
    public final void Get(Context context, final CommunicationCallback callback, String clientID, String sessionID)
    {
        if(CheckInternetConnection(context))
        {
            CommunicationRequest request = ConstructGet();
            request.Callback = callback;
            new WebService(clientID, sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public abstract void HandleGetResponse(CommunicationResponse result);
}
