package hoppingvikings.housefinancemobile.Services.SaltVault.Bills;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

public class BillEndpoint extends HTTPHandler
{
    private final String BILL_ENDPOINT = "http://house.flave.co.uk/api/v2/Bills";
    private final SessionPersister _session;

    @Inject
    public BillEndpoint(SessionPersister session)
    {
        _session = session;
    }

    @Override
    protected CommunicationRequest ConstructGet(final String urlAdditions)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = (urlAdditions == "") ? ItemType.BILL : ItemType.BILL_DETAILED;
            Endpoint = BILL_ENDPOINT + urlAdditions;
            OwnerV2 = BillEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPost(final JSONObject postData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.BILL;
            Endpoint = BILL_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = BillEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(final JSONObject patchData) throws UnsupportedOperationException
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.BILL;
            Endpoint = BILL_ENDPOINT;
            RequestBody = String.valueOf(patchData);
            OwnerV2 = BillEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructDelete(final JSONObject deleteData)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.BILL;
            Endpoint = BILL_ENDPOINT;
            RequestBody = String.valueOf(deleteData);
            OwnerV2 = BillEndpoint.this;
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
                String errorMessage = result.Response.getJSONObject("error").getString("message");
                Log.e("Error", errorMessage);

                if(errorCode == ApiErrorCodes.SESSION_INVALID || errorCode == ApiErrorCodes.SESSION_EXPIRED)
                {
                    result.Callback.OnFail(result.RequestTypeData, String.valueOf(errorCode.getValue()));
                }
                else
                {
                    result.Callback.OnFail(result.RequestTypeData, errorMessage);
                }
                return;
            }

            if (result.RequestTypeData == RequestType.GET)
            {
                if (result.ItemTypeData == ItemType.BILL)
                    HandleBillListResponse(result);
                else if (result.ItemTypeData == ItemType.BILL_DETAILED)
                    HandleDetailedBillResponse(result);
            }
            else
                HandleNormalResponse(result);
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

    private void HandleNormalResponse(CommunicationResponse result)
    {
        result.Callback.OnSuccess(result.RequestTypeData, null);
    }

    private void HandleBillListResponse(CommunicationResponse result) throws JSONException
    {
        JSONArray billJsonArray = result.Response.getJSONArray("bills");

        ArrayList<BillListObject> bills = new ArrayList<>();
        for(int k = 0; k < billJsonArray.length(); k++)
        {
            JSONObject billJson = billJsonArray.getJSONObject(k);
            JSONArray peopleArray = billJson.getJSONArray("people");

            BillListObject bill = new BillListObject(billJson, peopleArray);
            bills.add(bill);
        }

        BillRepository.Instance().Set(bills);
        HandleNormalResponse(result);
    }

    private void HandleDetailedBillResponse(CommunicationResponse result) throws JSONException
    {
        BillObjectDetailed detailedBill;

        JSONArray billJsonArray = result.Response.getJSONArray("bills");
        JSONObject detailedJson = billJsonArray.getJSONObject(0);
        JSONArray paymentsArray = detailedJson.getJSONArray("payments");
        detailedBill = new BillObjectDetailed(detailedJson, paymentsArray);

        result.Callback.OnSuccess(result.RequestTypeData, detailedBill);
    }
}