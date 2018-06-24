package hoppingvikings.housefinancemobile.Endpoints.SaltVault;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.Repositories.BillRepository;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class BillEndpoint extends HTTPHandler
{
    private final String BILL_ENDPOINT = "http://house.flave.co.uk/api/v2/Bills";

    @Override
    protected CommunicationRequest ConstructGet()
    {
        return new CommunicationRequest()
        {{
            RequestTypeData = RequestType.GET;
            ItemTypeData = ItemType.BILL;
            Endpoint = BILL_ENDPOINT;
            OwnerV2 = BillEndpoint.this;
        }};
    }

    @Override
    public void HandleGetResponse(CommunicationResponse result)
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
            result.Callback.OnSuccess(result.RequestTypeData, null);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain Bills list");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain Bills list");
        }
    }
}
