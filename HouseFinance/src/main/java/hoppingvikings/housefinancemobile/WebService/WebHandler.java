package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.Repositories.BillRepository;
import hoppingvikings.housefinancemobile.Repositories.ShoppingRepository;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Repositories.TodoRepository;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;

public class WebHandler
{
    private static WebHandler _instance;
    private String _clientID = "";
    private String _sessionID = "";

    private WebHandler()
    {}

    public static WebHandler Instance()
    {
        if (_instance != null)
            return _instance;

        _instance = new WebHandler();
        return _instance;
    }

    public void SetClientID(Context context)
    {
        _clientID = context.getString(R.string.backend_id);
    }

    public void SetSessionID(String sessionID)
    {
        _sessionID = sessionID;
    }

    public String GetSessionID()
    {
        return _sessionID;
    }

    public void GetSessionID(Context context, final CommunicationCallback callback, final JSONObject idToken)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                RequestTypeData = RequestType.POST;
                ItemTypeData = ItemType.LOG_IN;
                RequestBody = String.valueOf(idToken);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.POST, "No internet connection");
        }
    }

    public void GetBills(Context context, final CommunicationCallback callback)
    {
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
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void GetShoppingItems(Context context, final CommunicationCallback callback)
    {
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
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void GetToDoItems(Context context, final CommunicationCallback callback)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = ItemType.TODO;
                RequestTypeData = RequestType.GET;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void RequestBillDetails(Context context, final CommunicationCallback callback, final int billId)
    {
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
                    RequestTypeData = RequestType.GET;
                    RequestBody = String.valueOf(billDetailsRequest);
                    Owner = WebHandler.this;
                    Callback = callback;
                }};
                new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
            }
            catch (JSONException e)
            {
                callback.OnFail(RequestType.GET, "Failed to create JSON for Bill Details");
            }
        }
        else
        {
            callback.OnFail(RequestType.GET, "No Internet Connection");
        }
    }

    public void RequestUsers(Context context, final CommunicationCallback callback)
    {
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

            new WebService(_clientID, _sessionID).execute(request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No Internet Connection");
        }
    }

    public void DeleteItem(Context context, final CommunicationCallback callback, final JSONObject itemJson, final ItemType itemType)
    {
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
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.DELETE, "No Internet Connection");
        }
    }

    public void UploadNewItem(Context context, final JSONObject newItem, final CommunicationCallback callback, final ItemType itemType)
    {

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
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.POST, "No internet connection");
        }
    }

    public void EditItem(Context context, final JSONObject editedItem, final CommunicationCallback callback, final ItemType itemType)
    {
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
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.PATCH, "No Internet Connection");
        }
    }

    public void GetHousehold(Context context, final CommunicationCallback callback)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                RequestTypeData = RequestType.GET;
                ItemTypeData = ItemType.HOUSEHOLD;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void AddHousehold(Context context, final JSONObject requestJson, final CommunicationCallback callback)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                RequestTypeData = RequestType.POST;
                ItemTypeData = ItemType.HOUSEHOLD;
                RequestBody = String.valueOf(requestJson);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void DeleteHousehold(Context context, final JSONObject requestJson, final CommunicationCallback callback)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                RequestTypeData = RequestType.DELETE;
                ItemTypeData = ItemType.HOUSEHOLD;
                RequestBody = String.valueOf(requestJson);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            new WebService(_clientID, _sessionID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void ApiResult(CommunicationResponse result, ItemType type)
    {
        switch (type)
        {
            case BILL:
                try {
                    if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
                    {
                        /*JSONObject error = result.Response.getJSONObject("error");
                        if(error.has("errorCode"))
                        {
                            result.Callback.OnFail(result.RequestTypeData, error.getString("errorCode"));
                            return;
                        }*/
                        String errorMessage = result.Response.getJSONObject("error").getString("message");
                        Log.e("Error", errorMessage);
                        result.Callback.OnFail(result.RequestTypeData, errorMessage);
                        return;
                    }

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

                    result.Callback.OnSuccess(result.RequestTypeData, null);

                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Bills list");
                }
                catch(Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Bills list");
                }
                break;

            case SHOPPING:
                try {
                    if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
                    {
                        String errorMessage = result.Response.getJSONObject("error").getString("message");
                        Log.e("Error", errorMessage);
                        result.Callback.OnFail(result.RequestTypeData, errorMessage);
                        return;
                    }

                    JSONArray shoppingItems = result.Response.getJSONArray("shoppingList");

                    ArrayList<ShoppingListObject> items = new ArrayList<>();
                    for(int k = 0; k < shoppingItems.length(); k++)
                    {
                        JSONObject itemJson = shoppingItems.getJSONObject(k);
                        ShoppingListObject item = new ShoppingListObject(itemJson);
                        items.add(item);
                    }

                    ShoppingRepository.Instance().Set(items);

                    result.Callback.OnSuccess(result.RequestTypeData, null);

                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Shopping list");
                }
                catch(Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Shopping list");
                }
                break;

            case PERSON:
                JSONArray returnedObject;
                ArrayList<JSONObject> userObjects = new ArrayList<>();
                ArrayList<Person> parsedUsers = new ArrayList<>();

                try {
                    if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
                    {
                        String errorMessage = result.Response.getJSONObject("error").getString("message");
                        Log.e("Error", errorMessage);
                        result.Callback.OnFail(result.RequestTypeData, errorMessage);
                        return;
                    }

                    returnedObject = result.Response.getJSONArray("people");

                    for(int i = 0; i < returnedObject.length(); i++)
                    {
                        userObjects.add(returnedObject.getJSONObject(i));
                    }

                    for (int j = 0; j < userObjects.size(); j++)
                    {
                        parsedUsers.add(new Person(userObjects.get(j)));
                    }

                    result.Callback.OnSuccess(result.RequestTypeData, parsedUsers);

                } catch (JSONException je)
                {
                    je.printStackTrace();
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain People");
                }
                catch(Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain People");
                }

                break;

            case BILL_DETAILED:
                BillObjectDetailed detailedBill = null;

                try
                {
                    if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
                    {
                        String errorMessage = result.Response.getJSONObject("error").getString("message");
                        Log.e("Error", errorMessage);
                        result.Callback.OnFail(result.RequestTypeData, errorMessage);
                        return;
                    }

                    JSONArray billJsonArray = result.Response.getJSONArray("bills");
                    JSONObject detailedJson = billJsonArray.getJSONObject(0);
                    JSONArray paymentsArray = detailedJson.getJSONArray("payments");
                    detailedBill = new BillObjectDetailed(detailedJson, paymentsArray);
                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Bill details");
                }
                catch(Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Bill details");
                }

                result.Callback.OnSuccess(result.RequestTypeData, detailedBill);
                break;

            case TODO:
                try {
                    if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
                    {
                        String errorMessage = result.Response.getJSONObject("error").getString("message");
                        Log.e("Error", errorMessage);
                        result.Callback.OnFail(result.RequestTypeData, errorMessage);
                        return;
                    }

                    JSONArray todoArray = result.Response.getJSONArray("toDoTasks");

                    ArrayList<TodoListObject> todos = new ArrayList<>();
                    for(int k = 0; k < todoArray.length(); k++)
                    {
                        JSONObject toDoJson = todoArray.getJSONObject(k);

                        TodoListObject todo = new TodoListObject(toDoJson);
                        todos.add(todo);
                    }

                    TodoRepository.Instance().Set(todos);

                    result.Callback.OnSuccess(result.RequestTypeData, null);

                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Todo list");
                }
                catch(Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain Todo list");
                }
                break;

            case LOG_IN:
                try {
                    if(result.Response.has("hasError")) {
                        if(result.Response.getBoolean("hasError"))
                        {
                            String errorMessage = result.Response.getJSONObject("error").getString("message");
                            Log.e("Error", errorMessage);
                            result.Callback.OnFail(result.RequestTypeData, errorMessage);
                            return;
                        }

                        if (result.Response.has("sessionId"))
                        {
                            String sessionID = result.Response.getString("sessionId");
                            SetSessionID(sessionID);
                            result.Callback.OnSuccess(result.RequestTypeData, sessionID);
                        }
                        else {
                            result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                        }
                    }
                } catch (JSONException je)
                {
                    je.printStackTrace();
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                }
                catch(Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                }

                break;

            case HOUSEHOLD:
                try {
                    if(result.Response.has("hasError")) {
                        if(result.Response.getBoolean("hasError"))
                        {
                            String errorMessage = result.Response.getJSONObject("error").getString("message");
                            Log.e("Error", errorMessage);
                            result.Callback.OnFail(result.RequestTypeData, errorMessage);
                            return;
                        }

                        if (result.Response.has("house"))
                        {
                            JSONObject house = result.Response.getJSONObject("house");

                            //SetSessionID(sessionID);
                            FileIOHandler.Instance().WriteToFile("CurrentHousehold", house.toString());
                            result.Callback.OnSuccess(result.RequestTypeData, house.getString("id"));
                        }
                        else {
                            result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                        }
                    }
                } catch (JSONException je)
                {
                    je.printStackTrace();
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                }
                catch(Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                }

                break;

            default:
                try {
                    if(result.Response.has("hasError"))
                    {
                        if(result.Response.getBoolean("hasError"))
                        {
                            String errorMessage = result.Response.getJSONObject("error").getString("message");
                            Log.e("Error", errorMessage);
                            result.Callback.OnFail(result.RequestTypeData, errorMessage);
                            return;
                        }

                        result.Callback.OnSuccess(result.RequestTypeData, result.Response);
                    }

                }
                catch (JSONException e)
                {
                    Log.e("Error", e.getMessage());
                    result.Callback.OnFail(result.RequestTypeData, "Could not read server response");
                }
                catch (Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Failed to get a response from the server");
                }

                break;
        }
    }
}