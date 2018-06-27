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
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.HouseholdEndpoint;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.LogInEndpoint;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.PaymentsEndpoint;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.ShoppingEndpoint;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.ToDoEndpoint;
import hoppingvikings.housefinancemobile.Endpoints.SaltVault.UserEndpoint;
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
    private static WebHandler _instance;
    private SessionPersister _session;
    private String _clientID = "";

    private BillEndpoint _billEndpoint;
    private PaymentsEndpoint _paymentsEndpoint;
    private ShoppingEndpoint _shoppingEndpoint;
    private ToDoEndpoint _toDoEndpoint;
    private HouseholdEndpoint _householdEndpoint;
    private UserEndpoint _userEndpoint;
    private LogInEndpoint _logInEndpoint;

    private static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";
    private static final String API_HOUSEHOLD_INVITE = "Household/InviteLink";

    private WebHandler()
    {
        _billEndpoint = new BillEndpoint();
        _paymentsEndpoint = new PaymentsEndpoint();
        _shoppingEndpoint = new ShoppingEndpoint();
        _toDoEndpoint = new ToDoEndpoint();
        _householdEndpoint = new HouseholdEndpoint();
        _userEndpoint = new UserEndpoint();
        _session = new SessionPersister();
        _logInEndpoint = new LogInEndpoint(_session);
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
        _session.SetSessionID(sessionID);
    }

    public String GetSessionID()
    {
        return _session.GetSessionID();
    }

    public void GetSessionID(Context context, final CommunicationCallback callback, final JSONObject idToken)
    {
        _logInEndpoint.Post(context, callback, idToken);
    }

    public void GetBills(Context context, final CommunicationCallback callback)
    {
        _billEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
        _billEndpoint.Get(context, callback);
    }

    public void GetShoppingItems(Context context, final CommunicationCallback callback)
    {
        _shoppingEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
        _shoppingEndpoint.Get(context, callback);
    }

    public void GetToDoItems(Context context, final CommunicationCallback callback)
    {
        _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
        _toDoEndpoint.Get(context, callback);
    }

    public void RequestBillDetails(Context context, final CommunicationCallback callback, final int billId)
    {
        _billEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("id", Integer.toString(billId));
        _billEndpoint.Get(context, callback, urlParameters);
    }

    public void RequestUsers(Context context, final CommunicationCallback callback)
    {
        _userEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
        _userEndpoint.Get(context, callback);
    }

    public void UploadNewItem(Context context, final JSONObject newItem, final CommunicationCallback callback, final ItemType itemType)
    {
        if (itemType == ItemType.BILL)
        {
            _billEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _billEndpoint.Post(context, callback, newItem);
            return;
        }
        if (itemType == ItemType.PAYMENT)
        {
            _paymentsEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _paymentsEndpoint.Post(context, callback, newItem);
            return;
        }
        if (itemType == ItemType.SHOPPING)
        {
            _shoppingEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _shoppingEndpoint.Post(context, callback, newItem);
            return;
        }
        if (itemType == ItemType.TODO)
        {
            _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _toDoEndpoint.Post(context, callback, newItem);
            return;
        }
        if (itemType == ItemType.HOUSEHOLD)
        {
            _householdEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _householdEndpoint.Post(context, callback, newItem);
        }
    }

    public void EditItem(Context context, final JSONObject editedItem, final CommunicationCallback callback, final ItemType itemType)
    {
        if (itemType == ItemType.BILL)
        {
            _billEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _billEndpoint.Patch(context, callback, editedItem);
            return;
        }
        if (itemType == ItemType.PAYMENT)
        {
            _paymentsEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _paymentsEndpoint.Patch(context, callback, editedItem);
            return;
        }
        if (itemType == ItemType.SHOPPING)
        {
            _shoppingEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _shoppingEndpoint.Patch(context, callback, editedItem);
            return;
        }
        if (itemType == ItemType.TODO)
        {
            _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _toDoEndpoint.Patch(context, callback, editedItem);
            return;
        }
    }

    public void DeleteItem(Context context, final CommunicationCallback callback, final JSONObject itemJson, final ItemType itemType)
    {
        if (itemType == ItemType.BILL)
        {
            _billEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _billEndpoint.Delete(context, callback, itemJson);
            return;
        }
        if (itemType == ItemType.PAYMENT)
        {
            _paymentsEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _paymentsEndpoint.Delete(context, callback, itemJson);
            return;
        }
        if (itemType == ItemType.SHOPPING)
        {
            _shoppingEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _shoppingEndpoint.Delete(context, callback, itemJson);
            return;
        }
        if (itemType == ItemType.TODO)
        {
            _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _toDoEndpoint.Delete(context, callback, itemJson);
            return;
        }
        if (itemType == ItemType.HOUSEHOLD)
        {
            _householdEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _householdEndpoint.Delete(context, callback, itemJson);
        }
    }

    public void GetHousehold(Context context, final CommunicationCallback callback)
    {
        _householdEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
        _householdEndpoint.Get(context, callback);
    }

    public void GetHouseholdInviteCode(Context context, final CommunicationCallback callback)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = ItemType.HOUSEHOLD_INVITE;
                Endpoint = WEB_APIV2_URL + API_HOUSEHOLD_INVITE;
                RequestTypeData = RequestType.GET;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _session.GetSessionID());
            new WebService(authenticationProperty).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        }
        else
        {
            callback.OnFail(RequestType.GET, "No internet connection");
        }
    }

    public void JoinHousehold(Context context, final JSONObject jsonObject, final CommunicationCallback callback)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            CommunicationRequest request = new CommunicationRequest()
            {{
                ItemTypeData = ItemType.HOUSEHOLD_INVITE;
                Endpoint = WEB_APIV2_URL + API_HOUSEHOLD_INVITE;
                RequestBody = jsonObject.toString();
                RequestTypeData = RequestType.POST;
                Owner = WebHandler.this;
                Callback = callback;
            }};
            Map<String, String> authenticationProperty = new HashMap<>();
            authenticationProperty.put("Authorization", _session.GetSessionID());
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
                if(result.Response.getJSONObject("error").getInt("errorCode") == ApiErrorCodes.USER_NOT_IN_HOUSEHOLD.getValue())
                {
                    result.Callback.OnFail(result.RequestTypeData, ApiErrorCodes.USER_NOT_IN_HOUSEHOLD.name());
                    return;
                }

                String errorMessage = result.Response.getJSONObject("error").getString("message");
                Log.e("Error", errorMessage);
                result.Callback.OnFail(result.RequestTypeData, errorMessage);
                return;
            }

            switch (type)
            {
            case SHOPPING:
                try {
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

                case HOUSEHOLD_INVITE:
                    try {
                        if(result.Response.has("inviteLink"))
                        {
                            result.Callback.OnSuccess(result.RequestTypeData, result.Response.getString("inviteLink"));
                            return;
                        }

                        result.Callback.OnFail(result.RequestTypeData, "Failed to obtain invite link");
                    } catch (JSONException je)
                    {
                        result.Callback.OnFail(result.RequestTypeData, "Failed to obtain invite link");
                    } catch (Exception e)
                    {
                        result.Callback.OnFail(result.RequestTypeData, "Failed to obtain invite link");
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