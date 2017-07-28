package hoppingvikings.housefinancemobile.UserInterface.Lists.BillList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Josh on 02/10/2016.
 */

public class BillListObjectPeople {
    public String ID = "";
    public String URL = "";
    public boolean Paid = false;

    public BillListObjectPeople(JSONObject peopleObject, String id)
    {
        ID = id;
        try {
            URL = peopleObject.getString("imageLink");
            Paid = peopleObject.getBoolean("paid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
