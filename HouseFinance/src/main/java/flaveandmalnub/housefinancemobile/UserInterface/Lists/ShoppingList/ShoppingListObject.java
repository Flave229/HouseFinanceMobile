package flaveandmalnub.housefinancemobile.UserInterface.Lists.ShoppingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Josh on 06/11/2016.
 */

public class ShoppingListObject {
    public String ID = "";
    public String itemName = "";
    public String addedDate = "";
    public int addedBy = 0;
    public int addedFor1 = 0;
    public int addedFor2 = 0;
    public int addedFor3 = 0;
    public boolean done = false;
    public ShoppingListPeople people = null;

    public JSONObject originalJson = null;
    public JSONArray imagesJsonArr = null;
    public ArrayList<JSONObject> imageJsonObjs;

    public ShoppingListObject(JSONObject object)
    {
        try
        {
            ID = object.getString("Id");
            itemName = object.getString("Name");
            addedDate = object.getString("AddedOn");
            addedBy = android.R.drawable.ic_menu_camera;
            addedFor1 = android.R.drawable.ic_menu_camera;
            addedFor2 = android.R.drawable.ic_menu_camera;
            addedFor3 = android.R.drawable.ic_menu_camera;
            done = object.getBoolean("Purchased");

            //people = new ShoppingListPeople(peopleObject, ID);

        }catch (JSONException e)
        {

        }
    }
}
