package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by iView on 15/08/2017.
 */

public class BillObjectDetailedPayments {
    public String ID = "";
    public String personName = "";
    public String Date = "";
    public double AmountPaid = 0.0;

    public BillObjectDetailedPayments(JSONObject paymentObject)
    {
        try {
            ID = paymentObject.getString("id");
            personName = paymentObject.getString("personName");
            Date = paymentObject.getString("datePaid");
            AmountPaid = paymentObject.getDouble("amountPaid");
        } catch (Exception e)
        {

        }
    }
}
