package hoppingvikings.housefinancemobile.Endpoints.SaltVault;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class HouseholdEndpoint extends HTTPHandler
{
    private final String HOUSEHOLD_ENDPOINT = "http://house.flave.co.uk/api/v2/Household";

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions)
    {
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
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.HOUSEHOLD;
            Endpoint = HOUSEHOLD_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = HouseholdEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(JSONObject patchData) throws UnsupportedOperationException
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
