package hoppingvikings.housefinancemobile.Services.SaltVault.User;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import javax.inject.Inject;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

public class LogInEndpoint extends HTTPHandler
{
    private final String LOG_IN_ENDPOINT = "http://house.flave.co.uk/api/v2/LogIn";
    private final SessionPersister _session;

    @Inject
    public LogInEndpoint(SessionPersister session)
    {
        _session = session;
    }

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected CommunicationRequest ConstructPost(final JSONObject postData)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.LOG_IN;
            Endpoint = LOG_IN_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = LogInEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(JSONObject patchData) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected CommunicationRequest ConstructDelete(JSONObject deleteData) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void HandleResponse(CommunicationResponse result)
    {
        try
        {
            if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
            {
                ApiErrorCodes errorCode = ApiErrorCodes.get(result.Response.getJSONObject("error").getInt("errorCode"));
                if(errorCode == ApiErrorCodes.SESSION_EXPIRED || errorCode == ApiErrorCodes.SESSION_INVALID) {
                    result.Callback.OnFail(result.RequestTypeData, String.valueOf(errorCode.getValue()));
                }
                else
                {
                    String errorMessage = result.Response.getJSONObject("error").getString("message");
                    result.Callback.OnFail(result.RequestTypeData, errorMessage);
                }
                return;
            }

            HandleLogInResponse(result);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Failed to parse the response from the server");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Failed to handle the response from the server");
        }
    }

    private void HandleLogInResponse(CommunicationResponse result)
    {
        try
        {
            if(result.Response.has("user"))
            {
                FileIOHandler.Instance().WriteToFile("CurrentUser", result.Response.getJSONObject("user").toString());
            }

            if (result.Response.has("sessionId"))
            {
                String sessionID = result.Response.getString("sessionId");
                _session.SetSessionID(sessionID);
                result.Callback.OnSuccess(result.RequestTypeData, sessionID);
            }
            else
            {
                result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
            }
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
        }
    }
}
