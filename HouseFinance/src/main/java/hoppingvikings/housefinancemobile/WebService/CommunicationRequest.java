package hoppingvikings.housefinancemobile.WebService;

import hoppingvikings.housefinancemobile.ItemType;

public class CommunicationRequest
{
    public ItemType ItemTypeData;
    public RequestType RequestTypeData;
    public String RequestBody;
    public WebHandler Owner;
    public CommunicationCallback Callback;
}