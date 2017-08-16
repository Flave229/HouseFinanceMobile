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
    public String billTotalAmount = "";
    public int cardImage = 0;
    public ArrayList<BillListObjectPeople> people = null;
    public boolean paid = false;
    public boolean overdue = false;
    public JSONObject originalJson = null;
    public JSONArray imagesJsonArr = null;
    public ArrayList<JSONObject> imagesJsonObjs;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillListObject(JSONObject jsonObject, ArrayList<JSONObject> peopleObjects)
    {
        // Base Initialiser
        try {
            ID = jsonObject.getString("id");
            billName = jsonObject.getString("name");

            billDate = dateFormat.format(dateFormat.parse(jsonObject.getString("fullDateDue")));
            billAmount = jsonObject.getString("amountDue");
            billTotalAmount = jsonObject.getString("totalAmount");
            cardImage = android.R.drawable.ic_menu_camera;
            paid = jsonObject.getBoolean("paid");
            overdue = jsonObject.getBoolean("overdue");
            originalJson = jsonObject;

            people = new ArrayList<>();
            for (JSONObject person: peopleObjects) {
                people.add(new BillListObjectPeople(person, ID));
            }

            //people = new BillListObjectPeople(peopleObject, ID);

        } catch (JSONException e) {

        }
        catch (ParseException e)
        {
            Log.i("Info: ", "Failed to parse date " + e.getMessage());
        }
    }
}
