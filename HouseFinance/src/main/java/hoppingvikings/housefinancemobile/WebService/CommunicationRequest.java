package hoppingvikings.housefinancemobile.WebService;

import hoppingvikings.housefinancemobile.ItemType;

public class CommunicationRequest
{
    public String Endpoint;
    public ItemType ItemTypeData;
    public RequestType RequestTypeData;
    public String RequestBody;
    public HTTPHandler OwnerV2;
    public CommunicationCallback Callback;
}