package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;

import org.json.JSONObject;

import hoppingvikings.housefinancemobile.Services.SaltVault.Shopping.ShoppingEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.ToDoEndpoint;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.ItemType;

public class WebHandler
{
    private static WebHandler _instance;
    private SessionPersister _session;

    private ShoppingEndpoint _shoppingEndpoint;
    private ToDoEndpoint _toDoEndpoint;

    private WebHandler()
    {
        _shoppingEndpoint = new ShoppingEndpoint();
        _toDoEndpoint = new ToDoEndpoint();
        _session = HouseFinanceClass.GetSessionPersisterComponent().GetSessionPersister();
    }

    public static WebHandler Instance()
    {
        if (_instance != null)
            return _instance;

        _instance = new WebHandler();
        return _instance;
    }

    public void SetSessionID(String sessionID)
    {
        _session.SetSessionID(sessionID);
    }

    public String GetSessionID()
    {
        return _session.GetSessionID();
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

    public void UploadNewItem(Context context, final JSONObject newItem, final CommunicationCallback callback, final ItemType itemType)
    {
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
    }

    public void EditItem(Context context, final JSONObject editedItem, final CommunicationCallback callback, final ItemType itemType)
    {
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
    }

    public void SetSessionPersister(SessionPersister session)
    {
        _session = session;
    }
}