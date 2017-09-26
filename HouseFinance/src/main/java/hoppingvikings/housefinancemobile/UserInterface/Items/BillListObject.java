package hoppingvikings.housefinancemobile.UserInterface.Items;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BillListObject {
    public int id = 0;
    public String name = "";
    public String date = "";
    public double remainingAmount;
    public double totalAmount;
    public RecurringType recurringType;
    public ArrayList<BillListObjectPeople> people;
    public boolean paid;
    public boolean overdue;
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillListObject(JSONObject jsonObject, JSONArray peopleObjects)
    {
        try {
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            date = _dateFormat.format(_dateFormat.parse(jsonObject.getString("fullDateDue")));

            double amountPaid = jsonObject.getDouble("amountPaid");
            totalAmount = jsonObject.getDouble("totalAmount");
            remainingAmount = totalAmount - amountPaid;
            recurringType = RecurringType.values()[jsonObject.getInt("recurringType")];

            if(remainingAmount <= 0)
            {
                paid = true;
            }
            else if(new Date().after(_dateFormat.parse(date)))
            {
                overdue = true;
            }

            people = new ArrayList<>();
            for(int i = 0; i < peopleObjects.length(); i++) {
                JSONObject person = peopleObjects.getJSONObject(i);
                people.add(new BillListObjectPeople(person, id));
            }
        } catch (JSONException e) {

        }
        catch (ParseException e)
        {
            Log.i("Info: ", "Failed to parse date " + e.getMessage());
        }
    }
}
