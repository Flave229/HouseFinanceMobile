package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by iView on 15/08/2017.
 */

public class BillObjectDetailedPayments {
    public String BillID = "";
    public String PaymentID = "";
    public String personName = "";
    public String Date = "";
    public double AmountPaid = 0.0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillObjectDetailedPayments(JSONObject paymentObject, String billID)
    {
        try {
            BillID = billID;
            PaymentID = paymentObject.getString("id");
            personName = paymentObject.getString("personName");
            Date = paymentObject.getString("datePaid");
            AmountPaid = paymentObject.getDouble("amountPaid");
        } catch (Exception e)
        {

        }
    }
}
