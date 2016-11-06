package flaveandmalnub.housefinancemobile.WebService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import flaveandmalnub.housefinancemobile.GlobalObjects;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.BillList.BillListObject;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.BillList.BillListObjectPeople;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListObject;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListPeople;

/**
 * Created by Josh on 24/09/2016.
 */

public class BackgroundService extends Service {

    private final IBinder _binder = new LocalBinder();

    public class LocalBinder extends Binder
    {
        public BackgroundService getService()
        {
            return BackgroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return _binder;
    }


    public void contactWebsiteBills()
    {
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String authToken = "D2DB7539-634F-47C4-818D-59AD03C592E3";

        if(networkInfo != null && networkInfo.isConnected())
        {
            //Toast.makeText(getBaseContext(), "Obtaining list of bills", Toast.LENGTH_LONG).show();
            new DownloadJsonString().execute("http://saltavenue.azurewebsites.net/api/"+ authToken + "/RequestBillList", "Bills");
            GlobalObjects.downloading = false;
        }
        else
        {
            Toast.makeText(getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            GlobalObjects.downloading = false;
        }
    }

    public void contactWebsiteShoppingItems()
    {
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String authToken = "D2DB7539-634F-47C4-818D-59AD03C592E3";

        if(networkInfo != null && networkInfo.isConnected())
        {
            //Toast.makeText(getBaseContext(), "Obtaining list of bills", Toast.LENGTH_LONG).show();
            new DownloadJsonString().execute("http://saltavenue.azurewebsites.net/api/"+ authToken + "/RequestShoppingList", "Shopping");
            GlobalObjects.downloading = false;
        }
        else
        {
            Toast.makeText(getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            GlobalObjects.downloading = false;
        }
    }

    public void websiteResult(JSONObject result, String type)
    {
        switch (type)
        {
            case "Bills":
                ArrayList<BillListObject> _bills = new ArrayList<>();
                ArrayList<BillListObjectPeople> _people = new ArrayList<>();
                BillListObject bill;
                BillListObjectPeople person;
                try {
                    JSONArray array = result.getJSONArray("BillList");

                    ArrayList<JSONObject> allObjects = new ArrayList<>();
                    ArrayList<JSONObject> allPeopleObjects = new ArrayList<>();

                    for(int i = 0; i < array.length(); i++)
                    {
                        allObjects.add(array.getJSONObject(i));
                    }

                    for(int k = 0; k < allObjects.size(); k++)
                    {
                        JSONArray peopleArray = allObjects.get(k).getJSONArray("People");
                        for(int j = 0; j < peopleArray.length(); j++)
                        {
                            allPeopleObjects.add(peopleArray.getJSONObject(j));
                        }

                        bill = new BillListObject(allObjects.get(k), allPeopleObjects.get(k));
                        _bills.add(bill);

                        person = bill.people;
                        _people.add(person);

                    }
                    GlobalObjects.SetBills(_bills);
                    GlobalObjects.SetBillPeopleList(_people);

                    GlobalObjects.downloading = false;

                } catch (JSONException je) {
                    je.printStackTrace();
                    GlobalObjects.downloading = false;
                } catch(Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    GlobalObjects.downloading = false;
                }
                break;

            case "Shopping":
                ArrayList<ShoppingListObject> items = new ArrayList<>();
                ArrayList<ShoppingListPeople> shoppingPeople = new ArrayList<>();
                ShoppingListObject item;
                ShoppingListPeople shoppingPerson;
                try {
                    JSONArray array = result.getJSONArray("ShoppingList");

                    ArrayList<JSONObject> allObjects = new ArrayList<>();
                    ArrayList<JSONObject> allPeopleObjects = new ArrayList<>();

                    for(int i = 0; i < array.length(); i++)
                    {
                        allObjects.add(array.getJSONObject(i));
                    }

                    for(int k = 0; k < allObjects.size(); k++)
                    {
                        /*JSONArray peopleArray = allObjects.get(k).getJSONArray("AddedForImages");
                        for(int j = 0; j < peopleArray.length(); j++)
                        {
                            allPeopleObjects.add(peopleArray.getJSONObject(j));
                        }*/

                        item = new ShoppingListObject(allObjects.get(k));
                        items.add(item);

                        //shoppingPerson = item.people;
                        //shoppingPeople.add(shoppingPerson);

                    }
                    GlobalObjects.SetShoppingItems(items);
                    GlobalObjects.SetShoppingPeopleList(shoppingPeople);

                    GlobalObjects.downloading = false;

                } catch (JSONException je) {
                    je.printStackTrace();
                    GlobalObjects.downloading = false;
                } catch(Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    GlobalObjects.downloading = false;
                }
                break;
        }

        //Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
    }

    private class DownloadJsonString extends AsyncTask<String, Void, JSONObject>
    {
        String type = "";
        @Override
        protected JSONObject doInBackground(String... urls)
        {
            try
            {
                type = urls[1];
                return downloadUrl(urls[0]);
            } catch(IOException e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result)
        {
            switch (type)
            {
                case "Bills":
                    websiteResult(result, type);
                    break;

                case "Shopping":
                    websiteResult(result, type);
                    break;
            }

        }

        private JSONObject downloadUrl(String weburl) throws IOException
        {
            InputStream is = null;
            // Changed to 1MB buffer length. Previous was way too small
            boolean failed = true;
            JSONObject jsonObject;
            URL url = new URL(weburl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Might need this at some point
                try {
                    url = new URL(weburl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    conn.connect();
                    int response = conn.getResponseCode();
                    //Toast.makeText(getBaseContext(), "The response is: " + String.valueOf(response), Toast.LENGTH_LONG).show();
                    is = conn.getInputStream();

                    jsonObject = new JSONObject(readIt(is));

                    failed = false;
                    return jsonObject;
                } catch (JSONException e) {

                    conn.disconnect();
                    is.close();
                    failed = true;

                } finally {
                    if (is != null)
                        is.close();
                }
            return null;
        }

        public String readIt(InputStream input)
        {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();

            try{
                reader = new BufferedReader(new InputStreamReader(input));
                String line = "";
                while((line = reader.readLine()) != null)
                {
                    response.append(line);
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            finally {
                if(reader != null)
                {
                    try{
                        reader.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
            return response.toString();
        }
    }

}
