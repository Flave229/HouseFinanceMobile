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

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.Repositories.BillRepository;
import hoppingvikings.housefinancemobile.Repositories.ShoppingRepository;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;

public class WebHandler
{
    private static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";

    private static WebHandler _instance;
    private boolean _downloading;
    private CommunicationCallback _itemDeleteOwner;
    private CommunicationCallback _uploadOwner;
    private String _authToken = "";
    private boolean _debugging;

    private WebHandler()
    {}

    public static WebHandler Instance()
    {
        if (_instance != null)
        {
            return _instance;
        }

        _instance = new WebHandler();
        return _instance;
    }

    public void SetAuthToken(Context context)
    {
        InputStream inputStream = context.getResources().openRawResource(R.raw.api_authtoken);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try
        {
            String line = reader.readLine();
            if(line != null)
            {
                _authToken = line;
            }
        }
        catch (Exception e)
        {
            Log.i("Read Error", "Failed to read file. " + e.getMessage());
        }
    }

    public void contactWebsiteBills(Context context, final CommunicationCallback callback)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                RequestTypeData = RequestType.GET;
                ItemTypeData = ItemType.BILL;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_authToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            _downloading = false;
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void RequestBillDetails(Context context, final CommunicationCallback callback, final int billId)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            try
            {
                final JSONObject billDetailsRequest = new JSONObject()
                {{
                    put("BillId", billId);
                }};

                CommunicationRequest request = new CommunicationRequest()
                {{
                    ItemTypeData = ItemType.BILL_DETAILED;
                    RequestTypeData = RequestType.POST;
                    RequestBody = String.valueOf(billDetailsRequest);
                    Owner = WebHandler.this;
                    Callback = callback;
                }};
                new WebService(_authToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
            }
            catch (JSONException e)
            {
                callback.OnFail(RequestType.GET, "Failed to create JSON for Bill Details");
            }

        }
        else
        {
            _downloading = false;
            callback.OnFail(RequestType.GET, "No Internet Connection");
        }
    }

    public void RequestUsers(Context context, final CommunicationCallback callback)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = ItemType.PERSON;
                RequestTypeData = RequestType.GET;
                Owner = WebHandler.this;
                Callback = callback;
            }};

            new WebService(_authToken).execute(request);
        }
        else
        {
            _downloading = false;
            callback.OnFail(RequestType.GET, "No Internet Connection");
        }
    }

    public void DeleteItem(Context context, CommunicationCallback owner, JSONObject itemjson, ItemType itemType)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _itemDeleteOwner = owner;
        _downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            switch (itemType)
            {
                case BILL:
                    new DeleteItem().execute(WEB_APIV2_URL + "Bills/Delete", itemjson.toString());
                    break;
                case SHOPPING:
                    new DeleteItem().execute(WEB_APIV2_URL + "Shopping/", itemjson.toString());
                    break;
                case PAYMENT:
                    new DeleteItem().execute(WEB_APIV2_URL + "Bills/Payments", itemjson.toString());
                    break;
                default:
                    _downloading = false;
                    _itemDeleteOwner.OnFail(RequestType.DELETE, "Incorrect item type");
                    break;
            }
        }
        else
        {
            _downloading = false;
            _itemDeleteOwner.OnFail(RequestType.DELETE, "No Internet Connection");
        }
    }

    public void UploadNewItem(Context context, final JSONObject newItem, final CommunicationCallback callback, final ItemType itemType)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _uploadOwner = callback;
        _downloading = true;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo!= null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = itemType;
                RequestTypeData = RequestType.POST;
                RequestBody = String.valueOf(newItem);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_authToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            _downloading = false;
            _uploadOwner.OnFail(RequestType.POST, "No internet connection");
        }
    }

    public void EditItem(Context context, JSONObject editedItem, CommunicationCallback owner, ItemType itemType)
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
                case BILL:
                    new EditItem().execute(WEB_APIV2_URL + "Bills/Update", editedItemString);
                    break;

                case SHOPPING:
                    new EditItem().execute(WEB_APIV2_URL + "Shopping/", editedItemString);
                    break;

                case PAYMENT:
                    new EditItem().execute(WEB_APIV2_URL + "Bills/Payments", editedItemString);
                    break;

                default:
                    _uploadOwner.OnFail(RequestType.PATCH, "Incorrect item type");
                    break;
            }
        }
        else
        {
            _downloading = false;
            _uploadOwner.OnFail(RequestType.PATCH, "No Internet Connection");
        }
    }

    public void contactWebsiteShoppingItems(Context context, final CommunicationCallback callback)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = ItemType.SHOPPING;
                RequestTypeData = RequestType.GET;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_authToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            _downloading = false;
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void websiteResult(CommunicationResponse result, String type)
    {
        switch (type)
        {
            case "Bills":
                try {
                    JSONArray billJsonArray = result.Response.getJSONArray("bills");

                    ArrayList<BillListObject> bills = new ArrayList<>();
                    for(int k = 0; k < billJsonArray.length(); k++)
                    {
                        JSONObject billJson = billJsonArray.getJSONObject(k);
                        JSONArray peopleArray = billJson.getJSONArray("people");

                        BillListObject bill = new BillListObject(billJson, peopleArray);
                        bills.add(bill);
                    }

                    BillRepository.Instance().Set(bills);
                    _downloading = false;

                    result.Callback.OnSuccess(RequestType.GET, null);

                } catch (JSONException je) {
                    je.printStackTrace();
                    _downloading = false;
                    result.Callback.OnFail(RequestType.GET, "Failed to parse Bill list");
                } catch(Exception e) {
                    _downloading = false;
                    result.Callback.OnFail(RequestType.GET, "Unknown Error in Bill list download");
                }
                break;

            case "Shopping":
                ArrayList<ShoppingListObject> items = new ArrayList<>();
                ShoppingListObject item;
                try {
                    JSONObject itemsObject = result.Response.getJSONObject("items");
                    JSONArray shoppingList = itemsObject.getJSONArray("shoppingList");

                    for(int k = 0; k < shoppingList.length(); k++)
                    {
                        JSONObject shoppingItem = shoppingList.getJSONObject(k);
                        item = new ShoppingListObject(shoppingItem);
                        items.add(item);
                    }
                    ShoppingRepository.Instance().Set(items);

                    _downloading = false;
                    result.Callback.OnSuccess(RequestType.GET, null);

                } catch (JSONException je) {
                    je.printStackTrace();
                    _downloading = false;
                    result.Callback.OnFail(RequestType.GET, "Failed to parse Shopping list");
                } catch(Exception e) {
                    _downloading = false;
                    result.Callback.OnFail(RequestType.GET, "Unknown Error in Shopping List download");
                }
                break;

            case "People":
                JSONArray returnedObject;
                ArrayList<JSONObject> userObjects = new ArrayList<>();
                ArrayList<Person> parsedUsers = new ArrayList<>();

                try {
                    returnedObject = result.Response.getJSONArray("people");

                    for(int i = 0; i < returnedObject.length(); i++)
                    {
                        userObjects.add(returnedObject.getJSONObject(i));
                    }

                    for (int j = 0; j < userObjects.size(); j++)
                    {
                        parsedUsers.add(new Person(userObjects.get(j)));
                    }

                    result.Callback.OnSuccess(RequestType.GET, parsedUsers);

                } catch (JSONException je)
                {
                    je.printStackTrace();
                    _downloading = false;
                    result.Callback.OnFail(RequestType.GET, "Failed to parse Shopping list");
                } catch (Exception e)
                {

                }
                _downloading = false;

                break;

            case "BillDetails":
                BillObjectDetailed detailedBill = null;

                try {
                    JSONObject detailedJson = result.Response.getJSONObject("bill");
                    JSONArray paymentsArray = detailedJson.getJSONArray("payments");
                    detailedBill = new BillObjectDetailed(detailedJson, paymentsArray);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    _downloading = false;
                    result.Callback.OnFail(RequestType.POST, "Failed to parse Detailed Bill");
                    return;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    _downloading = false;
                    result.Callback.OnFail(RequestType.POST, "Unknown error in Detailed Bill Download");
                    return;
                }

                _downloading = false;
                result.Callback.OnSuccess(RequestType.POST, detailedBill);
                break;
            default:
                if(result.Response.has("hasError"))
                {
                    try
                    {
                        if(result.Response.getBoolean("hasError"))
                        {
                            String errorMessage = result.Response.getJSONObject("error").getString("message");
                            Log.e("Error", errorMessage);
                            result.Callback.OnFail(result.RequestTypeData, errorMessage);
                        }

                        result.Callback.OnSuccess(result.RequestTypeData, result.Response);
                    }
                    catch (JSONException e)
                    {
                        String errorMessage = "An error occurred while parsing the server response: " + e.getMessage();
                        Log.e("Error", errorMessage);
                        result.Callback.OnFail(result.RequestTypeData, errorMessage);
                    }
                }
                _downloading = false;
                break;
        }
    }

    public boolean IsDownloading()
    {
        return _downloading;
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
                _itemDeleteOwner.OnSuccess(RequestType.DELETE, null);
            }
            else
            {
                _itemDeleteOwner.OnFail(RequestType.DELETE, "Failed to Delete Item. Please try again");
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
                _uploadOwner.OnSuccess(RequestType.PATCH, null);
            }
            else
            {
                _uploadOwner.OnFail(RequestType.PATCH, "Failed to edit item. Please try again");
            }
        }
    }
}
