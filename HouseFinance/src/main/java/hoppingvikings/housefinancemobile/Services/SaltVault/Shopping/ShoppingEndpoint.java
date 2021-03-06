package hoppingvikings.housefinancemobile.Services.SaltVault.Shopping;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

public class ShoppingEndpoint extends HTTPHandler
{
    private final SessionPersister _session;
    private final String SHOPPING_ENDPOINT = "http://house.flave.co.uk/api/v2/Shopping";

    @Inject
    public ShoppingEndpoint(SessionPersister session)
    {
        _session = session;
    }

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.SHOPPING;
            Endpoint = SHOPPING_ENDPOINT;
            OwnerV2 = ShoppingEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPost(final JSONObject postData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.SHOPPING;
            Endpoint = SHOPPING_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = ShoppingEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(final JSONObject patchData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.SHOPPING;
            Endpoint = SHOPPING_ENDPOINT;
            RequestBody = String.valueOf(patchData);
            OwnerV2 = ShoppingEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructDelete(final JSONObject deleteData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.SHOPPING;
            Endpoint = SHOPPING_ENDPOINT;
            RequestBody = String.valueOf(deleteData);
            OwnerV2 = ShoppingEndpoint.this;
        }};
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

            if (result.RequestTypeData == RequestType.GET)
            {
                HandleShoppingListResponse(result);
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

    private void HandleShoppingListResponse(CommunicationResponse result)
    {
        try
        {
            JSONArray shoppingItems = result.Response.getJSONArray("shoppingList");

            ArrayList<ShoppingListObject> items = new ArrayList<>();
            for(int k = 0; k < shoppingItems.length(); k++)
            {
                JSONObject itemJson = shoppingItems.getJSONObject(k);
                ShoppingListObject item = new ShoppingListObject(itemJson);
                items.add(item);
            }

            ShoppingRepository.Instance().Set(items);

            result.Callback.OnSuccess(result.RequestTypeData, null);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain Shopping list");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain Shopping list");
        }
    }
}
