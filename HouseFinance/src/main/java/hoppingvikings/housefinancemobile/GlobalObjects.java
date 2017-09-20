package hoppingvikings.housefinancemobile;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class GlobalObjects
{
    static ArrayList<BillListObject> _bills = new ArrayList<>();
    static ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();

    public static WebHandler webHandler;

    // TODO: JOSH, I assume this needs to stick around for a future feature. Please replace this comment detailing its purpose
    public static BackgroundService backgroundService;
    public static boolean bound = false;

    public static boolean downloading = false;

    public static final String SHOPPING_RECENTITEMS_FILENAME = "hf_shopping_recent.txt";

    public static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";

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

    public static BillListObject GetBillFromID(int id)
    {
        for (BillListObject bill: _bills) {
            if(id == bill.id)
            {
                return bill;
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


    public static ShoppingListObject GetShoppingItemFromID(int id)
    {
        for(ShoppingListObject item : _shoppingItems)
        {
            if(id == item.Id)
            {
                return item;
            }
        }
        return null;
    }

    public static void ShowNotif(String text, String subtext, int notificationId)
    {
        AppServiceBinder._service.ShowNotification(text, subtext, notificationId);
    }

    public static String SetAuthToken(Context context)
    {
        InputStream inputStream = context.getResources().openRawResource(R.raw.api_authtoken);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line = reader.readLine();
            if(line != null)
            {
                return line;
            }
        } catch (Exception e)
        {
            Log.i("Read Error", "Failed to read file. " + e.getMessage());
        }
        return "";
    }

    public static void WriteToFile(String data)
    {
        try {
            String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HouseFinance/";
            File directory = new File(directoryPath);

            if(!directory.exists())
                directory.mkdirs();

            File recentsPath = new File(directory, SHOPPING_RECENTITEMS_FILENAME);

            if(!recentsPath.exists())
                recentsPath.createNewFile();

            FileOutputStream fos = new FileOutputStream(recentsPath, true);
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
        ArrayList<JSONObject> recentItems = new ArrayList<>();
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
                        recentItems.add(recentitemjson);
                        line = reader.readLine();
                    }
                    reader.close();
                    fis.close();

                    return recentItems;
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
