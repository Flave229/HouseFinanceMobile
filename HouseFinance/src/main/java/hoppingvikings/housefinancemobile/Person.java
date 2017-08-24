package hoppingvikings.housefinancemobile;

import org.json.JSONObject;

/**
 * Created by iView on 23/08/2017.
 */

public class Person {

    public int ID = 0;
    public String FirstName = "";
    public String Surname = "";
    public String ImageUrl = "";

    public boolean selected = false;

    public Person(JSONObject userObj)
    {
        try {
            ID = userObj.getInt("id");
            FirstName = userObj.getString("firstName");
            Surname = userObj.getString("lastName");
            ImageUrl = userObj.getString("image");
        } catch (Exception e)
        {

        }

    }
}
