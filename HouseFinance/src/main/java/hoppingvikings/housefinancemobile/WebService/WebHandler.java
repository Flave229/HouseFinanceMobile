package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListPeople;

/**
 * Created by Josh on 24/09/2016.
 */

public class WebHandler {

    DownloadCallback _billListOwner;
    DownloadDetailsCallback _billDetailsOwner;
    DownloadPeopleCallback _peopleDownloadOwner;
    DownloadCallback _shoppingListOwner;
    DeleteItemCallback _itemDeleteOwner;
    UploadCallback _uploadOwner;
    String authToken = "";
    private boolean debugging = false;


    public void SetAuthToken(Context context)
    {
        authToken = GlobalObjects.SetAuthToken(context);
    }

    public void contactWebsiteBills(Context context, DownloadCallback owner)
    {
        debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _billListOwner = owner;
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            //Toast.makeText(getBaseContext(), "Obtaining list of bills", Toast.LENGTH_LONG).show();
            new DownloadJsonString().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GlobalObjects.WEB_APIV2_URL + "Bills/", "Bills");
        }
        else
        {
            GlobalObjects.downloading = false;
            _billListOwner.OnFailedDownload("No internet connection");
        }
    }

    public void RequestBillDetails(Context context, DownloadDetailsCallback owner, int billID)
    {
        debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _billDetailsOwner = owner;
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            new DownloadJsonString().execute(GlobalObjects.WEB_APIV2_URL + "Bills/", "BillDetails", String.valueOf(billID));
        }
        else
        {
            GlobalObjects.downloading = false;
            _billDetailsOwner.OnDownloadFailed("No Internet Connection");
        }
    }

    public void RequestUsers(Context context, DownloadPeopleCallback owner)
    {
        debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _peopleDownloadOwner = owner;
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            new DownloadJsonString().execute(GlobalObjects.WEB_APIV2_URL + "Users/", "People");
        }
        else
        {
            GlobalObjects.downloading = false;
            _peopleDownloadOwner.UsersDownloadFailed("No Internet Connection");
        }
    }

    public void DeleteItem(Context context, DeleteItemCallback owner, JSONObject itemjson, String itemtype)
    {
        debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _itemDeleteOwner = owner;
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            switch (itemtype)
            {
                case GlobalObjects.ITEM_TYPE_BILL:
                    new DeleteItem().execute(GlobalObjects.WEB_APIV2_URL + "Bills/Delete", itemjson.toString());
                    break;

                case GlobalObjects.ITEM_TYPE_SHOPPING:
                    new DeleteItem().execute(GlobalObjects.WEB_APIV2_URL + "Shopping/", itemjson.toString());
                    break;

                case GlobalObjects.ITEM_TYPE_BILLPAYMENT:
                    new DeleteItem().execute(GlobalObjects.WEB_APIV2_URL + "Bills/Payments", itemjson.toString());
                    break;

                default:
                    GlobalObjects.downloading = false;
                    _itemDeleteOwner.OnFailedDelete("Incorrect item type");
                    break;
            }

        }
        else
        {
            GlobalObjects.downloading = false;
            _itemDeleteOwner.OnFailedDelete("No Internet Connection");
        }
    }

    public void UploadNewItem(Context context, JSONObject newItem, UploadCallback owner, String itemtype)
    {
        debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _uploadOwner = owner;
        String newItemString = newItem.toString();
        GlobalObjects.downloading = true;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo!= null && networkInfo.isConnected())
        {
            switch (itemtype)
            {
                case GlobalObjects.ITEM_TYPE_BILL:
                    new UploadItem().execute(newItemString, GlobalObjects.WEB_APIV2_URL + "Bills/Add");
                    break;

                case GlobalObjects.ITEM_TYPE_SHOPPING:
                    new UploadItem().execute(newItemString, GlobalObjects.WEB_APIV2_URL + "Shopping/");
                    break;

                case GlobalObjects.ITEM_TYPE_BILLPAYMENT:
                    new UploadItem().execute(newItemString, GlobalObjects.WEB_APIV2_URL + "Bills/Payments");
                    break;

                default:
                    GlobalObjects.downloading = false;
                    _uploadOwner.OnFailedUpload("Incorrect item type");
                    break;
            }
        }
        else
        {
            GlobalObjects.downloading = false;
            _uploadOwner.OnFailedUpload("No internet connection");
        }
    }

    public void EditItem(Context context, JSONObject editedItem, UploadCallback owner, String itemType)
    {
        debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _uploadOwner = owner;
        String editedItemString = editedItem.toString();

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            switch (itemType)
            {
                case GlobalObjects.ITEM_TYPE_BILL:
                    new EditItem().execute(GlobalObjects.WEB_APIV2_URL + "Bills/Update", editedItemString);
                    break;

                case GlobalObjects.ITEM_TYPE_SHOPPING:
                    new EditItem().execute(GlobalObjects.WEB_APIV2_URL + "Shopping/", editedItemString);
                    break;

                case GlobalObjects.ITEM_TYPE_BILLPAYMENT:
                    new EditItem().execute(GlobalObjects.WEB_APIV2_URL + "Bills/Payments", editedItemString);
                    break;

                default:
                    _uploadOwner.OnFailedUpload("Incorrect item type");
                    break;
            }
        }
        else
        {
            GlobalObjects.downloading = false;
            _uploadOwner.OnFailedUpload("No Internet Connection");
        }
    }

    public void contactWebsiteShoppingItems(Context context, DownloadCallback owner)
    {
        debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _shoppingListOwner = owner;
        GlobalObjects.downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            //Toast.makeText(getBaseContext(), "Obtaining list of bills", Toast.LENGTH_LONG).show();
            new DownloadJsonString().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GlobalObjects.WEB_APIV2_URL + "Shopping/", "Shopping");
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
                try {
                    JSONArray billJsonArray = result.getJSONArray("bills");

                    ArrayList<BillListObject> bills = new ArrayList<>();
                    for(int k = 0; k < billJsonArray.length(); k++)
                    {
                        JSONObject billJson = billJsonArray.getJSONObject(k);
                        JSONArray peopleArray = billJson.getJSONArray("people");

                        BillListObject bill = new BillListObject(billJson, peopleArray);
                        bills.add(bill);
                    }

                    GlobalObjects.SetBills(bills);
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
                    JSONObject itemsobject = result.getJSONObject("items");
                    JSONArray array = itemsobject.getJSONArray("shoppingList");

                    ArrayList<JSONObject> allObjects = new ArrayList<>();
                    ArrayList<JSONObject> allPeopleObjects = new ArrayList<>();

                    for(int i = 0; i < array.length(); i++)
                    {
                        allObjects.add(array.getJSONObject(i));
                    }

                    for(int k = 0; k < allObjects.size(); k++)
                    {
                        JSONArray peopleArray = allObjects.get(k).getJSONArray("addedForImages");
                        item = new ShoppingListObject(allObjects.get(k), peopleArray);
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

            case "People":
                Person newPerson = null;

                JSONArray returnedObject;
                ArrayList<JSONObject> userObjects = new ArrayList<>();
                ArrayList<Person> parsedUsers = new ArrayList<>();

                try {
                    returnedObject = result.getJSONArray("people");

                    for(int i = 0; i < returnedObject.length(); i++)
                    {
                        userObjects.add(returnedObject.getJSONObject(i));
                    }

                    for (int j = 0; j < userObjects.size(); j++)
                    {
                        parsedUsers.add(new Person(userObjects.get(j)));
                    }

                    GlobalObjects.SetCurrentUsers(parsedUsers);

                } catch (JSONException je)
                {
                    je.printStackTrace();
                    GlobalObjects.downloading = false;
                    _peopleDownloadOwner.UsersDownloadFailed("Failed to parse Shopping list");
                } catch (Exception e)
                {

                }
                GlobalObjects.downloading = false;
                _peopleDownloadOwner.UsersDownloadSuccess();

                break;

            case "BillDetails":
                BillObjectDetailed detailedBill = null;
                JSONArray paymentsArray;
                JSONObject detailedJson;

                try {
                    detailedJson = result.getJSONObject("bill");
                    paymentsArray = detailedJson.getJSONArray("payments");
                    detailedBill = new BillObjectDetailed(detailedJson, paymentsArray);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    GlobalObjects.downloading = false;
                    _billDetailsOwner.OnDownloadFailed("Failed to parse Detailed Bill");
                    return;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    GlobalObjects.downloading = false;
                    _billDetailsOwner.OnDownloadFailed("Unknown error in Detailed Bill Download");
                    return;
                }

                GlobalObjects.downloading = false;
                _billDetailsOwner.OnDownloadSuccessful(detailedBill);
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
                if(type.equals("Bills") || type.equals("Shopping") || type.equals("People"))
                {
                    return downloadUrl(urls[0], null);
                }
                else
                {
                    return downloadUrl(urls[0], urls[2]);
                }

            } catch(IOException e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result)
        {
            websiteResult(result, type);
        }

        private JSONObject downloadUrl(String weburl, String billid) throws IOException
        {
            // Changed to 1MB buffer length. Previous was way too small
            JSONObject jsonObject;
            URL url = new URL(weburl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Might need this at some point
                try {
                    conn.setReadTimeout(30000);
                    conn.setConnectTimeout(45000);
                    conn.setRequestProperty("Authorization", authToken);

                    if(type.equals("Bills") || type.equals("Shopping") || type.equals("People"))
                    {
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                    }
                    else
                    {
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        OutputStream os = conn.getOutputStream();
                        JSONObject billidjson = new JSONObject();
                        billidjson.put("BillId", billid);
                        os.write(billidjson.toString().getBytes("UTF-8"));
                        os.close();
                    }

                    int response = conn.getResponseCode();
                    //Toast.makeText(getBaseContext(), "The response is: " + String.valueOf(response), Toast.LENGTH_LONG).show();

                    InputStream input = conn.getInputStream();

                    jsonObject = new JSONObject(readIt(input));

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

    private class UploadItem extends AsyncTask<String, Void, Boolean>
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
                _uploadOwner.OnFailedUpload("Failed to upload new Item. Please try again");
            }
        }

        private boolean UploadJsonObject(String newItemString, String weburl) throws IOException
        {
            JSONObject returnJson;
            URL url = new URL(weburl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", authToken);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                OutputStream out = connection.getOutputStream();
                out.write(newItemString.getBytes("UTF-8"));
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

                if(returnJson.has("hasError"))
                {
                    if(returnJson.getBoolean("hasError"))
                    {
                        Log.e("Error", returnJson.getJSONObject("error").getString("message"));
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

    private class DeleteItem extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                return SendItemDeleteRequest(params[0], params[1]);
            } catch (Exception e)
            {
                return false;
            }
        }

        private boolean SendItemDeleteRequest(String weburl, String itemjsonstring) throws IOException
        {
            JSONObject returnJson;
            URL url = new URL(weburl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Authorization", authToken);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                OutputStream out = connection.getOutputStream();
                out.write(itemjsonstring.getBytes("UTF-8"));
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

                if(returnJson.has("hasError"))
                {
                    if(returnJson.getBoolean("hasError"))
                    {
                        Log.e("Error", returnJson.getJSONObject("error").getString("message"));
                        return false;
                    }
                }
            } catch (Exception e)
            {
                Log.e("Error", "Problem Sending payment: " + e.getMessage());
                return false;
            }
            finally {
                connection.disconnect();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
               _itemDeleteOwner.OnSuccessfulDelete();
            }
            else
            {
                _itemDeleteOwner.OnFailedDelete("Failed to Delete Item. Please try again");
            }
        }
    }

    private class EditItem extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                return SendEditRequest(params[0], params[1]);
            } catch (Exception e)
            {
                return false;
            }
        }

        private boolean SendEditRequest(String weburl, String editeditemjson) throws IOException
        {
            JSONObject returnJson;
            URL url = new URL(weburl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Authorization", authToken);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                OutputStream out = connection.getOutputStream();
                out.write(editeditemjson.getBytes("UTF-8"));
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

                if(returnJson.has("hasError"))
                {
                    if(returnJson.getBoolean("hasError"))
                    {
                        Log.e("Error", returnJson.getJSONObject("error").getString("message"));
                        return false;
                    }
                }

            } catch (Exception e)
            {
                Log.e("Error", "Problem editing item: " + e.getMessage());
                return false;
            }
            finally {
                connection.disconnect();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                _uploadOwner.OnSuccessfulUpload();
            }
            else
            {
                _uploadOwner.OnFailedUpload("Failed to edit item. Please try again");
            }
        }
    }
}
