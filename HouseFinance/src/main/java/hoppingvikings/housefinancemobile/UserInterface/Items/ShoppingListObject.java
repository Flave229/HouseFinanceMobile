package hoppingvikings.housefinancemobile.UserInterface.Items;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import hoppingvikings.housefinancemobile.Person;

/**
 * Created by Josh on 06/11/2016.
 */

public class ShoppingListObject {
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public int Id = 0;
    public String ItemName = "";
    public String AddedDate = "";
    public Person AddedBy;
    public boolean Purchased;
    public ArrayList<Person> AddedFor = new ArrayList<>();

    public boolean ItemExpanded = false;

    public ShoppingListObject(JSONObject shoppingItem)
    {
        try
        {
            Id = shoppingItem.getInt("id");
            ItemName = shoppingItem.getString("name");
            AddedDate = _dateFormat.format(_dateFormat.parse(shoppingItem.getString("dateAdded")));
            AddedBy = new Person(shoppingItem.getJSONObject("addedBy"));
            Purchased = shoppingItem.getBoolean("purchased");

            JSONArray addedFor = shoppingItem.getJSONArray("addedFor");

            for (int i = 0; i < addedFor.length(); i++)
                AddedFor.add(new Person(addedFor.getJSONObject(i)));
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
