package hoppingvikings.housefinancemobile.UserInterface.Items;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by iView on 06/07/2017.
 */

public class BillObjectDetailed {
    public String id = "";
    public String name = "";
    public String dateDue = "";
    public double amountDue = 0.0;
    public double amountTotal = 0.0;
    public double amountPaid = 0.0;
    public ArrayList<BillObjectDetailedPayments> paymentDetails = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillObjectDetailed(JSONObject details, JSONArray payments)
    {
        try {
            id = details.getString("id");
            name = details.getString("name");
            dateDue = dateFormat.format(dateFormat.parse(details.getString("fullDateDue")));
            amountPaid = details.getDouble("amountPaid");
            amountTotal = details.getDouble("totalAmount");
           // amountDue = details.getDouble("amountDue");
            amountDue = (amountTotal - amountPaid);
            if(payments.length() > 0)
            {
                for(int i = 0; i < payments.length(); i++)
                {
                    paymentDetails.add(new BillObjectDetailedPayments(payments.getJSONObject(i), id));
                }
            }
        } catch (JSONException e)
        {

        }
        catch (ParseException e)
        {
            Log.i("Info: ", "Failed to parse date " + e.getMessage());
        }
    }
}
