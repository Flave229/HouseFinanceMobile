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

    public void DeleteItem(Context context, final CommunicationCallback callback, final JSONObject itemJson, final ItemType itemType)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _itemDeleteOwner = callback;
        _downloading = true;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = itemType;
                RequestTypeData = RequestType.DELETE;
                RequestBody = String.valueOf(itemJson);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_authToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
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

    public void EditItem(Context context, final JSONObject editedItem, final CommunicationCallback callback, final ItemType itemType)
    {
        _debugging = 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        _uploadOwner = callback;
        String editedItemString = editedItem.toString();

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = itemType;
                RequestTypeData = RequestType.PATCH;
                RequestBody = String.valueOf(editedItem);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_authToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
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
}