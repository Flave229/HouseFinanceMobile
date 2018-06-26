package hoppingvikings.housefinancemobile.Endpoints.SaltVault;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.Repositories.BillRepository;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class BillEndpoint extends HTTPHandler
{
    private final String BILL_ENDPOINT = "http://house.flave.co.uk/api/v2/Bills";

    @Override
    protected CommunicationRequest ConstructGet(final String urlAdditions)
    {
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
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.BILL;
            Endpoint = BILL_ENDPOINT;
            RequestBody = String.valueOf(postData);
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
                String errorMessage = result.Response.getJSONObject("error").getString("message");
                Log.e("Error", errorMessage);
                result.Callback.OnFail(result.RequestTypeData, errorMessage);
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

    public static void HandleNormalResponse(CommunicationResponse result)
    {
        result.Callback.OnSuccess(result.RequestTypeData, null);
    }

    public static void HandleBillListResponse(CommunicationResponse result) throws JSONException
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

    public static void HandleDetailedBillResponse(CommunicationResponse result) throws JSONException
    {
        BillObjectDetailed detailedBill;

        JSONArray billJsonArray = result.Response.getJSONArray("bills");
        JSONObject detailedJson = billJsonArray.getJSONObject(0);
        JSONArray paymentsArray = detailedJson.getJSONArray("payments");
        detailedBill = new BillObjectDetailed(detailedJson, paymentsArray);

        result.Callback.OnSuccess(result.RequestTypeData, detailedBill);
    }
}