package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hoppingvikings.housefinancemobile.Endpoints.SaltVault.BillEndpoint;
import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.PaymentsEndpoint;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.ShoppingEndpoint;
import hoppingvikings.housefinancemobile.FileIOHandler;
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
    private static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";
    private static final String API_BILLS = "Bills";
    private static final String API_PAYMENTS = "Bills/Payments";
    private static final String API_SHOPPING = "Shopping";
    private static final String API_USERS = "Users";
    private static final String API_TODO = "ToDo";
    private static final String API_LOGIN = "LogIn";
    private static final String API_HOUSEHOLD = "Household";
    private static final String API_HOUSEHOLD_INVITE = "Household/InviteLink";
    private static WebHandler _instance;
    private String _clientID = "";
    private String _sessionID = "";

    private BillEndpoint _billEndpoint;
    private PaymentsEndpoint _paymentsEndpoint;
    private ShoppingEndpoint _shoppingEndpoint;

    private WebHandler()
    {
        _billEndpoint = new BillEndpoint();
        _paymentsEndpoint = new PaymentsEndpoint();
        _shoppingEndpoint = new ShoppingEndpoint();
    }

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
                Endpoint = WEB_APIV2_URL + API_LOGIN;
                RequestBody = String.valueOf(idToken);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.POST, "No internet connection");
        }
    }

    public void GetBills(Context context, final CommunicationCallback callback)
    {
        _billEndpoint.SetRequestProperty("Authorization", _sessionID);
        _billEndpoint.Get(context, callback);
    }

    public void GetShoppingItems(Context context, final CommunicationCallback callback)
    {
        _shoppingEndpoint.SetRequestProperty("Authorization", _sessionID);
        _shoppingEndpoint.Get(context, callback);
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
                Endpoint = WEB_APIV2_URL + API_TODO;
                RequestTypeData = RequestType.GET;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void RequestBillDetails(Context context, final CommunicationCallback callback, final int billId)
    {
        _billEndpoint.SetRequestProperty("Authorization", _sessionID);
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("id", Integer.toString(billId));
        _billEndpoint.Get(context, callback, urlParameters);
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
                Endpoint = WEB_APIV2_URL + API_USERS;
                RequestTypeData = RequestType.GET;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).execute(request);
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
            String apiEndpoint = "";

            switch (itemType)
            {
                case PAYMENT:
                    apiEndpoint += API_PAYMENTS;
                    break;
                case BILL:
                    apiEndpoint += API_BILLS;
                    break;
                case SHOPPING:
                    apiEndpoint += API_SHOPPING;
                    break;
                case TODO:
                    apiEndpoint += API_TODO;
                    break;
            }

            final String finalEndpointUrl = WEB_APIV2_URL + apiEndpoint;
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = itemType;
                Endpoint = finalEndpointUrl;
                RequestTypeData = RequestType.DELETE;
                RequestBody = String.valueOf(itemJson);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.DELETE, "No Internet Connection");
        }
    }

    public void UploadNewItem(Context context, final JSONObject newItem, final CommunicationCallback callback, final ItemType itemType)
    {
        if (itemType == ItemType.BILL)
        {
            _billEndpoint.SetRequestProperty("Authorization", _sessionID);
            _billEndpoint.Post(context, callback, newItem);
            return;
        }
        else if (itemType == ItemType.PAYMENT)
        {
            _paymentsEndpoint.SetRequestProperty("Authorization", _sessionID);
            _paymentsEndpoint.Post(context, callback, newItem);
            return;
        }
        else if (itemType == ItemType.SHOPPING)
        {
            _shoppingEndpoint.SetRequestProperty("Authorization", _sessionID);
            _shoppingEndpoint.Post(context, callback, newItem);
            return;
        }

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo!= null && networkInfo.isConnected())
        {
            String apiEndpoint = "";

            switch (itemType)
            {
                case TODO:
                    apiEndpoint += API_TODO;
                    break;
            }

            final String finalEndpointUrl = WEB_APIV2_URL + apiEndpoint;
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = itemType;
                Endpoint = finalEndpointUrl;
                RequestTypeData = RequestType.POST;
                RequestBody = String.valueOf(newItem);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
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
            String apiEndpoint = "";

            switch (itemType)
            {
                case PAYMENT:
                    apiEndpoint += API_PAYMENTS;
                    break;
                case BILL:
                    apiEndpoint += API_BILLS;
                    break;
                case SHOPPING:
                    apiEndpoint += API_SHOPPING;
                    break;
                case TODO:
                    apiEndpoint += API_TODO;
                    break;
            }

            final String finalEndpointUrl = WEB_APIV2_URL + apiEndpoint;
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = itemType;
                Endpoint = finalEndpointUrl;
                RequestTypeData = RequestType.PATCH;
                RequestBody = String.valueOf(editedItem);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
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
                ItemTypeData = ItemType.HOUSEHOLD;
                Endpoint = WEB_APIV2_URL + API_HOUSEHOLD;
                RequestTypeData = RequestType.GET;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
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
                ItemTypeData = ItemType.HOUSEHOLD;
                Endpoint = WEB_APIV2_URL + API_HOUSEHOLD;
                RequestTypeData = RequestType.POST;
                RequestBody = String.valueOf(requestJson);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
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
                ItemTypeData = ItemType.HOUSEHOLD;
                Endpoint = WEB_APIV2_URL + API_HOUSEHOLD;
                RequestTypeData = RequestType.DELETE;
                RequestBody = String.valueOf(requestJson);
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _sessionID);
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void ApiResult(CommunicationResponse result, ItemType type)
    {
        try
        {
            if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
            {
                String errorMessage = result.Response.getJSONObject("error").getString("message");
                Log.e("Error", errorMessage);
                result.Callback.OnFail(result.RequestTypeData, errorMessage);
                return;
            }

            switch (type)
            {
            case PERSON:
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
            case TODO:
                try
                {
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
                try
                {
                    if (result.Response.has("sessionId"))
                    {
                        String sessionID = result.Response.getString("sessionId");
                        SetSessionID(sessionID);
                        result.Callback.OnSuccess(result.RequestTypeData, sessionID);
                    }
                    else
                    {
                        result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                    }
                }
                catch (JSONException je)
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
                    if (result.Response.has("house"))
                    {
                        JSONObject house = result.Response.getJSONObject("house");

                        //SetSessionID(sessionID);
                        FileIOHandler.Instance().WriteToFile("CurrentHousehold", house.toString());
                        result.Callback.OnSuccess(result.RequestTypeData, house.getString("id"));
                    }
                    else
                    {
                        result.Callback.OnFail(result.RequestTypeData, "Could not obtain session");
                    }
                }
                catch (JSONException je)
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
                try
                {
                    result.Callback.OnSuccess(result.RequestTypeData, result.Response);
                }
                catch (Exception e)
                {
                    result.Callback.OnFail(result.RequestTypeData, "Failed to get a response from the server");
                }

                break;
            }
        }
        catch (Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Failed to get a response from the server");
        }
    }
}