package hoppingvikings.housefinancemobile.Endpoints.SaltVault;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;

public class PaymentsEndpoint extends HTTPHandler
{
    private final String PAYMENT_ENDPOINT = "http://house.flave.co.uk/api/v2/Bills/Payments";

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
            ItemTypeData = ItemType.PAYMENT;
            Endpoint = PAYMENT_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = PaymentsEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(final JSONObject patchData)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.PAYMENT;
            Endpoint = PAYMENT_ENDPOINT;
            RequestBody = String.valueOf(patchData);
            OwnerV2 = PaymentsEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructDelete(final JSONObject deleteData)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.PAYMENT;
            Endpoint = PAYMENT_ENDPOINT;
            RequestBody = String.valueOf(deleteData);
            OwnerV2 = PaymentsEndpoint.this;
        }};
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
}
