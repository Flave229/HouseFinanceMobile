package hoppingvikings.housefinancemobile.Services.SaltVault.ToDo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class ToDoEndpoint extends HTTPHandler
{
    private final String TODO_ENDPOINT = "http://house.flave.co.uk/api/v2/ToDo";

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.TODO;
            Endpoint = TODO_ENDPOINT;
            OwnerV2 = ToDoEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPost(final JSONObject postData)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.TODO;
            Endpoint = TODO_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = ToDoEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(final JSONObject patchData)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.TODO;
            Endpoint = TODO_ENDPOINT;
            RequestBody = String.valueOf(patchData);
            OwnerV2 = ToDoEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructDelete(final JSONObject deleteData)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.TODO;
            Endpoint = TODO_ENDPOINT;
            RequestBody = String.valueOf(deleteData);
            OwnerV2 = ToDoEndpoint.this;
        }};
    }

    @Override
    public void HandleResponse(CommunicationResponse result)
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

            if (result.RequestTypeData == RequestType.GET)
            {
                HandleToDoListResponse(result);
            }
            else
                result.Callback.OnSuccess(result.RequestTypeData, null);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Failed to parse the response from the server");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Failed to handle the response from the server");
        }
    }

    private void HandleToDoListResponse(CommunicationResponse result)
    {
        try
        {
            JSONArray todoArray = result.Response.getJSONArray("toDoTasks");

            ArrayList<TodoListObject> toDos = new ArrayList<>();
            for(int k = 0; k < todoArray.length(); k++)
            {
                JSONObject toDoJson = todoArray.getJSONObject(k);

                TodoListObject todo = new TodoListObject(toDoJson);
                toDos.add(todo);
            }

            TodoRepository.Instance().Set(toDos);

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
    }
}