package hoppingvikings.housefinancemobile.WebService;

public interface CommunicationCallback<Result>
{
    void OnSuccess(RequestType requestType, Result result);
    void OnFail(RequestType requestType, String message);
}