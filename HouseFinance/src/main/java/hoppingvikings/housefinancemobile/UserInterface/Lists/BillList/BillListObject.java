package hoppingvikings.housefinancemobile.UserInterface.Lists.BillList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Josh on 17/09/2016.
 */
public class BillListObject {
    public String ID = "";
    public String cardName = "";
    public String cardDesc = "";
    public String cardAmount = "";
    public String totalAmount = "";
    public int cardImage = 0;
    public ArrayList<BillListObjectPeople> people = null;
    public boolean paid = false;
    public boolean overdue = false;
    public JSONObject originalJson = null;
    public JSONArray imagesJsonArr = null;
    public ArrayList<JSONObject> imagesJsonObjs;

    public BillListObject(JSONObject jsonObject, ArrayList<JSONObject> peopleObjects)
    {
        // Base Initialiser
        try {
            ID = jsonObject.getString("Id");
            cardName = jsonObject.getString("Name");
            cardDesc = jsonObject.getString("DateDue");
            cardAmount = jsonObject.getString("AmountDue");
            totalAmount = jsonObject.getString("TotalAmount");
            cardImage = android.R.drawable.ic_menu_camera;
            paid = jsonObject.getBoolean("Paid");
            overdue = jsonObject.getBoolean("Overdue");
            originalJson = jsonObject;

            people = new ArrayList<>();
            for (JSONObject person: peopleObjects) {
                people.add(new BillListObjectPeople(person, ID));
            }

            //people = new BillListObjectPeople(peopleObject, ID);

        } catch (JSONException e) {

        }
    }
}
