package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Josh on 06/11/2016.
 */

public class ShoppingListPeople {
    public String ID = "";
    public String URL = "";

    public ShoppingListPeople(JSONObject object, String id)
    {
        ID = id;
        try
        {
            URL = object.getString("ImageLink");
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
