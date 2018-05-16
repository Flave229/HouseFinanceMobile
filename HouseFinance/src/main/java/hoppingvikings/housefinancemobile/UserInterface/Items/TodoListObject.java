package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import hoppingvikings.housefinancemobile.Person;

/**
 * Created by Josh on 02/10/2017.
 */

public class TodoListObject {
    public int id = 0;
    public String title = "";
    public String dueDate = "";
    public boolean completed = false;
    public ArrayList<Person> peopleForTask = new ArrayList<>();
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public TodoListObject(JSONObject jsonObject)
    {
        try {
            id = jsonObject.getInt("id");
            title = jsonObject.getString("title");
            dueDate = _dateFormat.format(_dateFormat.parse(jsonObject.getString("due")));
            completed = jsonObject.getBoolean("complete");
            JSONArray addedFor = jsonObject.getJSONArray("peopleForTask");
            for (int i = 0; i < addedFor.length(); i++)
                peopleForTask.add(new Person(addedFor.getJSONObject(i)));
        } catch (JSONException je)
        {

        } catch (ParseException pe)
        {

        }
    }
}
