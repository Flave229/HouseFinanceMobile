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

/**
 * Created by Josh on 17/09/2016.
 */
public class BillListObject {
    public int ID = 0;
    public String billName = "";
    public String billDate = "";
    public String billAmount = "";
    public String billAmountPaid = "";
    public String billTotalAmount = "";
    public ArrayList<BillListObjectPeople> people = null;
    public boolean paid = false;
    public boolean overdue = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillListObject(JSONObject jsonObject, JSONArray peopleObjects)
    {
        try {
            ID = jsonObject.getInt("id");
            billName = jsonObject.getString("name");
            billDate = dateFormat.format(dateFormat.parse(jsonObject.getString("fullDateDue")));

            billAmountPaid = jsonObject.getString("amountPaid");
            billTotalAmount = jsonObject.getString("totalAmount");
            billAmount = String.valueOf((Double.valueOf(billTotalAmount) - Double.valueOf(billAmountPaid)));

            if(Double.valueOf(billAmount) <= 0)
            {
                paid = true;
            }

            if(new Date().after(dateFormat.parse(billDate)))
            {
                overdue = true;
            }

            people = new ArrayList<>();
            for(int i = 0; i < peopleObjects.length(); i++) {
                JSONObject person = peopleObjects.getJSONObject(i);
                people.add(new BillListObjectPeople(person, ID));
            }
        } catch (JSONException e) {

        }
        catch (ParseException e)
        {
            Log.i("Info: ", "Failed to parse date " + e.getMessage());
        }
    }
}
