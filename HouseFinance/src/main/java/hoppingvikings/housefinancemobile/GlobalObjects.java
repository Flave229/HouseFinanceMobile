package hoppingvikings.housefinancemobile;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListPeople;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

/**
 * Created by Josh on 25/09/2016.
 */

public class GlobalObjects{

    static ArrayList<BillListObject> _bills = new ArrayList<>();
    static ArrayList<BillListObjectPeople> _billsPeople = new ArrayList<>();

    static ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();
    static ArrayList<ShoppingListPeople> _shoppingPeople = new ArrayList<>();

    public static WebHandler webHandler;
    public static BackgroundService backgroundService;
    public static boolean _bound = false;

    public static boolean downloading = false;

    public static final String SHOPPING_RECENTITEMS_FILENAME = "hf_shopping_recent.txt";
    public static final String WEB_API_URL = "http://house.flave.co.uk/api/";
    public static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";

    public static final int USERGUID_DAVE = 1;
    public static final int USERGUID_JOSH = 3;

    public static final String ITEM_TYPE_SHOPPING = "item_shopping";
    public static final String ITEM_TYPE_BILL = "item_bill";
    public static final String ITEM_TYPE_BILLPAYMENT = "item_billpayment";

    public static void SetBills(ArrayList<BillListObject> bills)
    {
        _bills.clear();
        _bills.addAll(bills);
    }

    public static ArrayList<BillListObject> GetBills()
    {
        return _bills;
    }

    public static BillListObject GetBillFromID(String id)
    {
        for (BillListObject bill: _bills) {
            if(id.equals(bill.ID))
            {
                return bill;
            }
        }
        return null;
    }

    public static void SetBillPeopleList(ArrayList<BillListObjectPeople> people)
    {
        _billsPeople.clear();
        _billsPeople.addAll(people);
    }

    public static BillListObjectPeople GetPersonFromID(String id)
    {
        for (BillListObjectPeople person:_billsPeople) {
            if(id.equals(person.ID))
            {
                return person;
            }
        }

        return null;
    }

    public static void SetShoppingItems(ArrayList<ShoppingListObject> items)
    {
        _shoppingItems.clear();
        _shoppingItems.addAll(items);
    }

    public static ArrayList<ShoppingListObject> GetShoppingItems()
    {
        return _shoppingItems;
    }


    public static ShoppingListObject GetShoppingItemFromID(String id)
    {
        for(ShoppingListObject item : _shoppingItems)
        {
            if(id.equals(item.ID))
            {
                return item;
            }
        }
        return null;
    }

    public static void SetShoppingPeopleList(ArrayList<ShoppingListPeople> people)
    {
        _shoppingPeople.clear();
        _shoppingPeople.addAll(people);
    }

    public static void ShowNotif(Context context, String text, String subtext, int notifid)
    {
        AppServiceBinder._service.ShowNotification(text, subtext, notifid);
    }

    public static void WriteToFile(Context context, String data)
    {
        try {
            // Get the directory path
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HouseFinance/";
            File dir = new File(path);

            // Make it if it doesn't already exist
            if(!dir.exists())
                dir.mkdirs();

            File recentspath = new File(dir, SHOPPING_RECENTITEMS_FILENAME);

            // Create our file if it doesn't exist
            if(!recentspath.exists())
                recentspath.createNewFile();

            FileOutputStream fos = new FileOutputStream(recentspath, true);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            writer.append(data);
            writer.append("\n");
            writer.close();

            fos.flush();
            fos.close();

        } catch (Exception e)
        {
            Log.e("Write Error: ", e.getMessage());
        }
    }

    public static ArrayList<JSONObject> ReadFile(String filename)
    {
        ArrayList<JSONObject> recentitems = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HouseFinance/";
        File dir = new File(path);

        if(dir.exists())
        {
            File recents = new File(dir, filename);

            if(recents.exists())
            {
                try {
                    FileInputStream fis = new FileInputStream(recents);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                    String line = reader.readLine();
                    while (line != null)
                    {
                        JSONObject recentitemjson = new JSONObject(line);
                        recentitems.add(recentitemjson);
                        line = reader.readLine();
                    }
                    reader.close();
                    fis.close();

                    return recentitems;
                } catch (Exception e)
                {
                    Log.e("Read Error:", e.getMessage());
                    return null;
                }
            }
        }

        return null;
    }
}
