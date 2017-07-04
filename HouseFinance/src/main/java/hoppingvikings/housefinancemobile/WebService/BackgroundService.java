package hoppingvikings.housefinancemobile.WebService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListPeople;

/**
 * Created by Josh on 24/09/2016.
 */

public class BackgroundService {

    DownloadCallback _billListOwner;
    DownloadCallback _shoppingListOwner;
    UploadCallback _uploadOwner;



    public void contactWebsiteBills(Context context, DownloadCallback owner)
    {
        _billListOwner = owner;
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String authToken = "D2DB7539-634F-47C4-818D-59AD03C592E3";

        if(networkInfo != null && networkInfo.isConnected())
        {
            //Toast.makeText(getBaseContext(), "Obtaining list of bills", Toast.LENGTH_LONG).show();
            new DownloadJsonString().execute("https://saltavenue.azurewebsites.net/api/"+ authToken + "/RequestBillList", "Bills");
        }
        else
        {
            GlobalObjects.downloading = false;
            _billListOwner.OnFailedDownload("No internet connection");
        }
    }

    public void UploadNewBill(Context context, JSONObject newBill, UploadCallback owner)
    {
        _uploadOwner = owner;
        String newBillString = newBill.toString();
        GlobalObjects.downloading = true;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String authToken = "D2DB7539-634F-47C4-818D-59AD03C592E3";

        if(networkInfo!= null && networkInfo.isConnected())
        {
            new UploadBillJson().execute(newBillString, "https://saltavenue.azurewebsites.net/api/"+ authToken + "/AddBillItem");
        }
        else
        {
            GlobalObjects.downloading = false;
            _uploadOwner.OnFailedUpload("No internet connection");
        }
    }

    public void UploadNewShoppingItem(Context context, JSONObject newItem, UploadCallback owner)
    {
        _uploadOwner = owner;
        String newItemString = newItem.toString();
        GlobalObjects.downloading = true;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String authToken = "D2DB7539-634F-47C4-818D-59AD03C592E3";

        if(networkInfo != null && networkInfo.isConnected())
        {
            new UploadShoppingItemJson().execute(newItemString, "https://saltavenue.azurewebsites.net/api/"+ authToken + "/AddShoppingItem");
        }
        else
        {
            GlobalObjects.downloading = false;
            _uploadOwner.OnFailedUpload("No Internet Connection");
        }
    }

    public void contactWebsiteShoppingItems(Context context, DownloadCallback owner)
    {
        _shoppingListOwner = owner;
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String authToken = "D2DB7539-634F-47C4-818D-59AD03C592E3";

        if(networkInfo != null && networkInfo.isConnected())
        {
            //Toast.makeText(getBaseContext(), "Obtaining list of bills", Toast.LENGTH_LONG).show();
            new DownloadJsonString().execute("https://saltavenue.azurewebsites.net/api/"+ authToken + "/RequestShoppingList", "Shopping");
        }
        else
        {
            GlobalObjects.downloading = false;
            _shoppingListOwner.OnFailedDownload("No internet connection");
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

                    for(int i = 0; i < array.length(); i++)
                    {
                        allObjects.add(array.getJSONObject(i));
                    }

                    for(int k = 0; k < allObjects.size(); k++)
                    {
                        ArrayList<JSONObject> allPeopleObjects = new ArrayList<>();
                        JSONArray peopleArray = allObjects.get(k).getJSONArray("People");
                        for(int j = 0; j < peopleArray.length(); j++)
                        {
                            allPeopleObjects.add(peopleArray.getJSONObject(j));
                        }

                        bill = new BillListObject(allObjects.get(k), allPeopleObjects);
                        _bills.add(bill);

                        //person = bill.;
                        //_people.add(person);

                    }
                    GlobalObjects.SetBills(_bills);
                    GlobalObjects.SetBillPeopleList(_people);

                    GlobalObjects.downloading = false;

                    _billListOwner.OnSuccessfulDownload();

                } catch (JSONException je) {
                    je.printStackTrace();
                    GlobalObjects.downloading = false;
                    _billListOwner.OnFailedDownload("Failed to parse Bill list");
                } catch(Exception e) {
                    GlobalObjects.downloading = false;
                    _billListOwner.OnFailedDownload("Unknown Error in Bill list download");
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
                    _shoppingListOwner.OnSuccessfulDownload();

                } catch (JSONException je) {
                    je.printStackTrace();
                    GlobalObjects.downloading = false;
                    _shoppingListOwner.OnFailedDownload("Failed to parse Shopping list");
                } catch(Exception e) {
                    GlobalObjects.downloading = false;
                    _shoppingListOwner.OnFailedDownload("Unknown Error in Shopping List download");
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
            // Changed to 1MB buffer length. Previous was way too small
            JSONObject jsonObject;
            URL url = new URL(weburl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Might need this at some point
                try {
                    conn.setReadTimeout(30000);
                    conn.setConnectTimeout(45000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    int response = conn.getResponseCode();
                    //Toast.makeText(getBaseContext(), "The response is: " + String.valueOf(response), Toast.LENGTH_LONG).show();

                    jsonObject = new JSONObject(readIt(conn.getInputStream()));

                    return jsonObject;
                } catch (JSONException e) {
                    conn.disconnect();
                }
            finally {
                    conn.disconnect();
                }
            return null;
        }

        public String readIt(InputStream input)
        {
            BufferedReader reader = null;
            StringBuilder response = new StringBuilder();

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

    private class UploadBillJson extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {

            try {
                return UploadJsonObject(params[0], params[1]);
            }
            catch (IOException e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                _uploadOwner.OnSuccessfulUpload();
            }
            else
            {
                _uploadOwner.OnFailedUpload("Failed to upload new Bill. Please try again");
            }
        }

        private boolean UploadJsonObject(String newBillJsonString, String weburl) throws IOException
        {
            JSONObject returnJson;
            URL url = new URL(weburl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(newBillJsonString);
                writer.flush();
                writer.close();

                out.close();

                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                String returnmessage = "";
                while ((line = serverAnswer.readLine()) != null)
                {
                    returnmessage += line;
                }

                serverAnswer.close();

                returnJson = new JSONObject(returnmessage);

                if(returnJson.has("HasError"))
                {
                    if(returnJson.getBoolean("HasError"))
                    {
                        Log.e("Error", "Problem Sending Item");
                        return false;
                    }
                }
            } catch (Exception e)
            {
                Log.e("Error", "Problem Sending Bill: " + e.getMessage());
                return false;
            }
            finally {
                connection.disconnect();
            }

            return true;
        }
    }

    private class UploadShoppingItemJson extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {

            try {
                return UploadJsonObject(params[0], params[1]);
            } catch (IOException e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                _uploadOwner.OnSuccessfulUpload();
            }
            else
            {
                _uploadOwner.OnFailedUpload("Failed to upload new Item. Please try again");
            }
        }

        private boolean UploadJsonObject(String newItemJsonString, String weburl) throws IOException
        {
            JSONObject returnJson;
            URL url = new URL(weburl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(newItemJsonString);
                writer.flush();
                writer.close();

                out.close();

                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                String returnmessage = "";
                while ((line = serverAnswer.readLine()) != null)
                {
                    returnmessage += line;
                }

                serverAnswer.close();

                returnJson = new JSONObject(returnmessage);

               if(returnJson.has("HasError"))
               {
                   if(returnJson.getBoolean("HasError"))
                   {
                       Log.e("Error", "Problem Sending Item");
                       return false;
                   }
               }
            } catch (Exception e)
            {
                Log.e("Error", "Problem Sending Item: " + e.getMessage());
                return false;
            }
            finally {
                connection.disconnect();
            }

            return true;
        }
    }

}
