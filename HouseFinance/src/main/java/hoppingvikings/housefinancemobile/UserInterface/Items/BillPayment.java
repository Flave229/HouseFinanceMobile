package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import hoppingvikings.housefinancemobile.Person;

public class BillPayment
{
    public int BillID = 0;
    public String PaymentID = "";
    public Person Person;
    public String Date = "";
    public double AmountPaid = 0.0;

    private SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillPayment(JSONObject paymentObject, int billID)
    {
        try
        {
            BillID = billID;
            PaymentID = paymentObject.getString("id");
            Person = new Person(paymentObject.getJSONObject("person"));
            Date = _dateFormat.format(_dateFormat.parse(paymentObject.getString("datePaid")));
            AmountPaid = paymentObject.getDouble("amount");
        }
        catch (Exception e)
        {

        }
    }
}