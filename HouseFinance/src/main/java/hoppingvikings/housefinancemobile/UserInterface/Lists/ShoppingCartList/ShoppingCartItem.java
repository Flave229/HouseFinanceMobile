package hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iView on 25/07/2017.
 */

public class ShoppingCartItem {
    public String name = "";
    public String date = "";
    public String addedBy = "";
    public ArrayList<String> people = new ArrayList<>();
    public boolean itemExpanded = false;

    public ShoppingCartItem(JSONObject json)
    {
        try {
            name = json.getString("Name");
            date = json.getString("Added");
            addedBy = json.getString("AddedBy");
            if(json.has("ItemFor"))
            {
                for(int i = 0; i < json.getJSONArray("ItemFor").length(); i++)
                {
                    people.add(json.getJSONArray("ItemFor").getString(i));
                }
            }
        } catch (Exception e)
        {

        }
    }
}
