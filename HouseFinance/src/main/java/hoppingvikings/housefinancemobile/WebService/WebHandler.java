package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;

/**
 * Created by Josh on 24/09/2016.
 */

public class WebHandler
{
    private DownloadCallback _billListOwner;
    private DownloadDetailsCallback _billDetailsOwner;
    private DownloadPeopleCallback _peopleDownloadOwner;
    private DownloadCallback _shoppingListOwner;
    private DeleteItemCallback _itemDeleteOwner;
    private UploadCallback _uploadOwner;
    private String _authToken = "";
    private boolean _debugging;

    public void SetAuthToken(Context context)
    {
        _authToken = GlobalObjects.SetAuthToken(context);
    }

    public void contactWebsiteBills(Context context, DownloadCallback owner)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
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
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
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
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
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
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
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
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
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
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
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
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
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
                ShoppingListObject item;
                try {
                    JSONObject itemsObject = result.getJSONObject("items");
                    JSONArray shoppingList = itemsObject.getJSONArray("shoppingList");

                    for(int k = 0; k < shoppingList.length(); k++)
                    {
                        JSONObject shoppingItem = shoppingList.getJSONObject(k);
                        JSONArray peopleArray = shoppingItem.getJSONArray("addedForImages");
                        item = new ShoppingListObject(shoppingItem, peopleArray);
                        items.add(item);
                    }
                    GlobalObjects.SetShoppingItems(items);

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

                    _peopleDownloadOwner.UsersDownloadSuccess(parsedUsers);

                } catch (JSONException je)
                {
                    je.printStackTrace();
                    GlobalObjects.downloading = false;
                    _peopleDownloadOwner.UsersDownloadFailed("Failed to parse Shopping list");
                } catch (Exception e)
                {

                }
                GlobalObjects.downloading = false;

                break;

            case "BillDetails":
                BillObjectDetailed detailedBill = null;

                try {
                    JSONObject detailedJson = result.getJSONObject("bill");
                    JSONArray paymentsArray = detailedJson.getJSONArray("payments");
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

        private JSONObject downloadUrl(String webUrl, String billId) throws IOException
        {
            JSONObject jsonObject;
            URL url = new URL(webUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try {
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(45000);
                conn.setRequestProperty("Authorization", _authToken);

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
                    JSONObject billIdJson = new JSONObject();
                    billIdJson.put("BillId", billId);
                    os.write(billIdJson.toString().getBytes("UTF-8"));
                    os.close();
                }

                int response = conn.getResponseCode();
                // TODO: Bother Josh about the below comment
                 //Toast.makeText(context, "The response is: " + String.valueOf(response), Toast.LENGTH_LONG).show();

                InputStream input = conn.getInputStream();
                jsonObject = new JSONObject(readInputStream(input));
                return jsonObject;
            } catch (JSONException e) {
                conn.disconnect();
            }
            finally {
                conn.disconnect();
            }
            return null;
        }

        public String readInputStream(InputStream input)
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
                connection.setRequestProperty("Authorization", _authToken);
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
                String returnMessage = "";
                while ((line = serverAnswer.readLine()) != null)
                {
                    returnMessage += line;
                }

                serverAnswer.close();

                returnJson = new JSONObject(returnMessage);

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
                connection.setRequestProperty("Authorization", _authToken);
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
                connection.setRequestProperty("Authorization", _authToken);
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
