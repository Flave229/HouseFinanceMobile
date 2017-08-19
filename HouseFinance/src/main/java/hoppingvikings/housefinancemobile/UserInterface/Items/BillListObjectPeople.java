package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Josh on 02/10/2016.
 */

public class BillListObjectPeople {
    public int billID = 0;
    public String URL = "";
    public boolean Paid = false;

    public BillListObjectPeople(JSONObject peopleObject, int id)
    {
        billID = id;
        try {
            URL = peopleObject.getString("imageLink");
            Paid = peopleObject.getBoolean("paid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
