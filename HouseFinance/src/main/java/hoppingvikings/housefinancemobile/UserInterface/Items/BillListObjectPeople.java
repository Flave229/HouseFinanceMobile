package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONException;
import org.json.JSONObject;

public class BillListObjectPeople {
    public int billID = 0;
    public int personID = 0;
    public String URL = "";
    public boolean Paid = false;

    public BillListObjectPeople(JSONObject peopleObject, int id)
    {
        billID = id;
        try {
            personID = peopleObject.getInt("id");
            URL = peopleObject.getString("imageLink");
            Paid = peopleObject.getBoolean("paid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
