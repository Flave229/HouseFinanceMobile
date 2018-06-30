package hoppingvikings.housefinancemobile.WebService;

import android.content.Context;

import org.json.JSONObject;

import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.ToDoEndpoint;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.ItemType;

public class WebHandler
{
    private static WebHandler _instance;
    private SessionPersister _session;

    private ToDoEndpoint _toDoEndpoint;

    private WebHandler()
    {
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

    public void GetToDoItems(Context context, final CommunicationCallback callback)
    {
        _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
        _toDoEndpoint.Get(context, callback);
    }

    public void UploadNewItem(Context context, final JSONObject newItem, final CommunicationCallback callback, final ItemType itemType)
    {
        if (itemType == ItemType.TODO)
        {
            _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _toDoEndpoint.Post(context, callback, newItem);
            return;
        }
    }

    public void EditItem(Context context, final JSONObject editedItem, final CommunicationCallback callback, final ItemType itemType)
    {
        if (itemType == ItemType.TODO)
        {
            _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _toDoEndpoint.Patch(context, callback, editedItem);
            return;
        }
    }

    public void DeleteItem(Context context, final CommunicationCallback callback, final JSONObject itemJson, final ItemType itemType)
    {
        if (itemType == ItemType.TODO)
        {
            _toDoEndpoint.SetRequestProperty("Authorization", _session.GetSessionID());
            _toDoEndpoint.Delete(context, callback, itemJson);
            return;
        }
    }
}