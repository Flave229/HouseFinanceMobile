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
    public String ID = "";
    public String billName = "";
    public String billDate = "";
    public String billAmount = "";
    public String billAmountPaid = "";
    public String billTotalAmount = "";
    public ArrayList<BillListObjectPeople> people = null;
    public boolean paid = false;
    public boolean overdue = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillListObject(JSONObject jsonObject, ArrayList<JSONObject> peopleObjects)
    {
        // Base Initialiser
        try {
            ID = jsonObject.getString("id");
            billName = jsonObject.getString("name");
            billDate = dateFormat.format(dateFormat.parse(jsonObject.getString("fullDateDue")));

            // todo Add amountPaid field and make billAmount equal (totalAmount - amountPaid)
            //billAmountPaid = jsonObject.getString("amountPaid");
            billAmount = jsonObject.getString("amountDue");
            billTotalAmount = jsonObject.getString("totalAmount");
            //billAmount = String.valueOf((Double.valueOf(billTotalAmount) - Double.valueOf(billAmountPaid)));

            if(Double.valueOf(billAmount) == 0)
            {
                paid = true;
            }

            if(new Date().after(dateFormat.parse(billDate)))
            {
                overdue = true;
            }

            people = new ArrayList<>();
            for (JSONObject person: peopleObjects) {
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
