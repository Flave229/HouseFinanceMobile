package hoppingvikings.housefinancemobile.Endpoints.SaltVault;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class UserEndpoint extends HTTPHandler
{
    private final String TODO_ENDPOINT = "http://house.flave.co.uk/api/v2/Users";

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.TODO;
            Endpoint = TODO_ENDPOINT;
            OwnerV2 = UserEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPost(JSONObject postData) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
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
                String errorMessage = result.Response.getJSONObject("error").getString("message");
                Log.e("Error", errorMessage);
                result.Callback.OnFail(result.RequestTypeData, errorMessage);
                return;
            }

            if (result.RequestTypeData == RequestType.GET)
            {
                HandlePersonListResponse(result);
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

    private void HandlePersonListResponse(CommunicationResponse result)
    {
        JSONArray returnedObject;
        ArrayList<JSONObject> userObjects = new ArrayList<>();
        ArrayList<Person> parsedUsers = new ArrayList<>();

        try
        {
            returnedObject = result.Response.getJSONArray("people");

            for(int i = 0; i < returnedObject.length(); i++)
            {
                userObjects.add(returnedObject.getJSONObject(i));
            }

            for (int j = 0; j < userObjects.size(); j++)
            {
                parsedUsers.add(new Person(userObjects.get(j)));
            }

            result.Callback.OnSuccess(result.RequestTypeData, parsedUsers);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain People");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain People");
        }
    }
}
