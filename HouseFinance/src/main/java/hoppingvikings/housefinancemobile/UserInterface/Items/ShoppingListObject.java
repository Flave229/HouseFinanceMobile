package hoppingvikings.housefinancemobile.UserInterface.Items;

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
    public String addedBy = "";
    public boolean done = false;
    public String addedForImage1 = "";
    public String addedForImage2 = "";
    public String addedForImage3 = "";

    public boolean itemExpanded = false;

    public ShoppingListObject(JSONObject object, JSONArray addedForImages)
    {
        try
        {
            ID = object.getString("id");
            itemName = object.getString("name");
            addedDate = object.getString("addedOn");
            addedBy = object.getString("addedByImage");
            done = object.getBoolean("purchased");

            switch (addedForImages.length())
            {
                case 1:
                    addedForImage1 = addedForImages.getString(0);
                    addedForImage2 = "";
                    addedForImage3 = "";
                    break;

                case 2:
                    addedForImage1 = addedForImages.getString(0);
                    addedForImage2 = addedForImages.getString(1);
                    addedForImage3 = "";
                    break;

                case 3:
                    addedForImage1 = addedForImages.getString(0);
                    addedForImage2 = addedForImages.getString(1);
                    addedForImage3 = addedForImages.getString(2);
                    break;
            }

        }catch (JSONException e)
        {

        }
    }
}