package hoppingvikings.housefinancemobile.UserInterface.Items;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import hoppingvikings.housefinancemobile.Person;

/**
 * Created by Josh on 06/11/2016.
 */

public class ShoppingListObject {
    public int ID = 0;
    public String itemName = "";
    public String addedDate = "";
    public Person addedBy;
    public boolean done = false;
    public Person addedFor1;
    public Person addedFor2;
    public Person addedFor3;

    public boolean itemExpanded = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public ShoppingListObject(JSONObject shoppingItem)
    {
        try
        {
            ID = shoppingItem.getInt("id");
            itemName = shoppingItem.getString("name");
            addedDate = dateFormat.format(dateFormat.parse(shoppingItem.getString("dateAdded")));
            addedBy = new Person(shoppingItem.getJSONObject("addedBy"));
            done = shoppingItem.getBoolean("purchased");

            JSONArray addedFor = shoppingItem.getJSONArray("addedFor");
            switch (addedFor.length())
            {
                case 1:
                    addedFor1 = new Person(addedFor.getJSONObject(0));
                    break;

                case 2:
                    addedFor1 = new Person(addedFor.getJSONObject(0));
                    addedFor2 = new Person(addedFor.getJSONObject(1));
                    break;

                case 3:
                    addedFor1 = new Person(addedFor.getJSONObject(0));
                    addedFor2 = new Person(addedFor.getJSONObject(1));
                    addedFor3 = new Person(addedFor.getJSONObject(2));
                    break;
            }
        }
        catch (JSONException e)
        {

        }
        catch (ParseException e)
        {
            Log.i("Info: ", "Failed to parse date " + e.getMessage());
        }
    }
}
