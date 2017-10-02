package hoppingvikings.housefinancemobile.UserInterface.Items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Josh on 02/10/2017.
 */

public class TodoListObject {
    public int id = 0;
    public String name = "";
    public String date = "";
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public TodoListObject(JSONObject jsonObject, JSONArray peopleObjects)
    {
        try {
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            date = _dateFormat.format(_dateFormat.parse(jsonObject.getString("fullDate")));
        } catch (JSONException je)
        {

        } catch (ParseException pe)
        {

        }
    }
}
