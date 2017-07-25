package hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList;

import org.json.JSONObject;

/**
 * Created by iView on 25/07/2017.
 */

public class ShoppingCartItem {
    public String name;
    public String date;

    public ShoppingCartItem(JSONObject json)
    {
        try {
            name = json.getString("Name");
            date = json.getString("Added");
        } catch (Exception e)
        {

        }
    }
}
