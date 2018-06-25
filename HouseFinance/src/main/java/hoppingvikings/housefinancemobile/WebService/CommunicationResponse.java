package hoppingvikings.housefinancemobile.WebService;

import org.json.JSONObject;

import java.lang.reflect.Method;

import hoppingvikings.housefinancemobile.ItemType;

public class CommunicationResponse
{
    public CommunicationCallback Callback;
    public ItemType ItemTypeData;
    public JSONObject Response;
    public RequestType RequestTypeData;
}