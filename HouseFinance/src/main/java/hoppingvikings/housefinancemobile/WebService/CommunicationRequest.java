package hoppingvikings.housefinancemobile.WebService;

import java.lang.reflect.Method;

import hoppingvikings.housefinancemobile.ItemType;

public class CommunicationRequest
{
    public String Endpoint;
    public ItemType ItemTypeData;
    public RequestType RequestTypeData;
    public String RequestBody;
    public WebHandler Owner;
    public HTTPHandler OwnerV2;
    public CommunicationCallback Callback;
}