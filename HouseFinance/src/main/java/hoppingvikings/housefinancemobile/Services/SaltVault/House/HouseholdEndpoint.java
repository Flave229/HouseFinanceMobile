package hoppingvikings.housefinancemobile.Services.SaltVault.House;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

public class HouseholdEndpoint extends HTTPHandler
{
    private final String HOUSEHOLD_ENDPOINT = "http://house.flave.co.uk/api/v2/Household";
    private final SessionPersister _session;

    @Inject
    public HouseholdEndpoint(SessionPersister session)
    {
        _session = session;
    }

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.HOUSEHOLD;
            Endpoint = HOUSEHOLD_ENDPOINT;
            OwnerV2 = HouseholdEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPost(final JSONObject postData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.HOUSEHOLD;
            Endpoint = HOUSEHOLD_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = HouseholdEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(final JSONObject patchData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.HOUSEHOLD;
            Endpoint = HOUSEHOLD_ENDPOINT;
            RequestBody = String.valueOf(patchData);
            OwnerV2 = HouseholdEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructDelete(final JSONObject deleteData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.HOUSEHOLD;
            Endpoint = HOUSEHOLD_ENDPOINT;
            RequestBody = String.valueOf(deleteData);
            OwnerV2 = HouseholdEndpoint.this;
        }};
    }

    @Override
    public void HandleResponse(CommunicationResponse result)
    {
        try
        {
            if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
            {
                int errorCode = result.Response.getJSONObject("error").getInt("errorCode");
                if(errorCode == ApiErrorCodes.USER_NOT_IN_HOUSEHOLD.getValue())
                {
                    result.Callback.OnFail(result.RequestTypeData, ApiErrorCodes.USER_NOT_IN_HOUSEHOLD.name());
                    return;
                }

                String errorMessage = result.Response.getJSONObject("error").getString("message");
                Log.e("Error", errorMessage);
                result.Callback.OnFail(result.RequestTypeData, errorMessage);
                return;
            }

            if (result.RequestTypeData == RequestType.GET)
            {
                HandleHouseholdResponse(result);
            }
            else
                result.Callback.OnSuccess(result.RequestTypeData, null);
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

    private void HandleHouseholdResponse(CommunicationResponse result)
    {
        try
        {
            if (result.Response.has("house"))
            {
                JSONObject house = result.Response.getJSONObject("house");

                FileIOHandler.Instance().WriteToFile("CurrentHousehold", house.toString());
                result.Callback.OnSuccess(result.RequestTypeData, house.getString("id"));
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
        catch (Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
        }
    }
}
