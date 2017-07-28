package hoppingvikings.housefinancemobile.UserInterface.Lists.BillList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iView on 06/07/2017.
 */

public class BillObjectDetailed {
    public String id = "";
    public String name = "";
    public String dateDue = "";
    public double amountDue = 0.0;
    public double amountPaid = 0.0;
    public ArrayList<JSONObject> paymentDetails = new ArrayList<>();

    public BillObjectDetailed(JSONObject details, JSONArray payments)
    {
        try {
            id = details.getString("id");
            name = details.getString("name");
            dateDue = details.getString("dateDue");
            amountDue = details.getDouble("amountDue");
            amountPaid = details.getDouble("amountPaid");
            if(payments.length() > 0)
            {
                for(int i = 0; i < payments.length(); i++)
                {
                    paymentDetails.add(payments.getJSONObject(i));
                }
            }
        } catch (JSONException e)
        {

        }
    }
}
