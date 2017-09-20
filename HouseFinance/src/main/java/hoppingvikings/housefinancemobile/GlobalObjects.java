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

import hoppingvikings.housefinancemobile.MemoryRepositories.BillMemoryRepository;
import hoppingvikings.housefinancemobile.MemoryRepositories.ShoppingMemoryRepository;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class GlobalObjects
{
    public static WebHandler WebHandler;
    public static BillMemoryRepository BillRepository = new BillMemoryRepository();
    public static ShoppingMemoryRepository ShoppingRepository = new ShoppingMemoryRepository();

    // TODO: JOSH, I assume this needs to stick around for a future feature. Please replace this comment detailing its purpose
    public static BackgroundService backgroundService;
    public static boolean bound = false;

    public static boolean downloading = false;

    public static final String SHOPPING_RECENTITEMS_FILENAME = "hf_shopping_recent.txt";

    public static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";

    public static final String ITEM_TYPE_SHOPPING = "item_shopping";
    public static final String ITEM_TYPE_BILL = "item_bill";
    public static final String ITEM_TYPE_BILLPAYMENT = "item_billpayment";

    public static void ShowNotif(String text, String subtext, int notificationId)
    {
        AppServiceBinder._service.ShowNotification(text, subtext, notificationId);
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
                        JSONObject recentItemJson = new JSONObject(line);
                        recentItems.add(recentItemJson);
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
