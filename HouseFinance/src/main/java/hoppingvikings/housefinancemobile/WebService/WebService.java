package hoppingvikings.housefinancemobile.WebService;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebService extends AsyncTask<CommunicationRequest, Void, CommunicationResponse>
{
    private static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";
    private String _authToken = "";
    private CommunicationRequest _request;

    public WebService(String authToken)
    {
        _authToken = authToken;
    }

    @Override
    protected CommunicationResponse doInBackground(CommunicationRequest... requests)
    {
        try
        {
            for (CommunicationRequest request : requests)
            {
                _request = request;
                return DownloadUrl();
            }
            return null;
        }
        catch(IOException e)
        {
            return null;
        }
        catch (JSONException e)
        {
            return null;
        }
    }

    @Override
    protected void onPostExecute(CommunicationResponse result)
    {
        String type;

        if (_request.RequestTypeData != RequestType.GET)
        {
            _request.Owner.websiteResult(result, "");
            return;
        }

        switch (_request.ItemTypeData)
        {
            case BILL:
                type = "Bills";
                break;
            case SHOPPING:
                type = "Shopping";
                break;
            case BILL_DETAILED:
                type = "BillDetails";
                break;
            case PERSON:
                type = "People";
                break;
            default:
                type = "";
        }

        _request.Owner.websiteResult(result, type);
    }

    private CommunicationResponse DownloadUrl() throws IOException, JSONException
    {
        String subEndpoint;

        switch (_request.ItemTypeData)
        {
            case BILL:
                subEndpoint = "Bills";
                break;
            case BILL_DETAILED:
                subEndpoint = "Bills?id=" + new JSONObject(_request.RequestBody).getInt("BillId");
                break;
            case SHOPPING:
                subEndpoint = "Shopping";
                break;
            case PAYMENT:
                subEndpoint = "Bills/Payments";
                break;
            case PERSON:
                subEndpoint = "Users";
                break;
            default:
                subEndpoint = "";
        }

        URL url = new URL(WEB_APIV2_URL + subEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        CommunicationResponse response = new CommunicationResponse()
        {{
            Callback = _request.Callback;
            RequestTypeData = _request.RequestTypeData;
        }};

        try
        {
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(45000);
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", _authToken);

            connection.setRequestMethod(_request.RequestTypeData.toString());

            if (_request.RequestTypeData != RequestType.GET)
            {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                out.write(_request.RequestBody.getBytes("UTF-8"));
                out.close();
            }

            InputStream input = connection.getInputStream();
            response.Response = new JSONObject(ReadInputStream(input));
        }
        catch (IOException e)
        {
            Log.e("IO Error", "Error reading the input stream from '" + WEB_APIV2_URL + subEndpoint + "': " + e.getMessage());
        }
        catch (JSONException e)
        { }
        finally
        {
            connection.disconnect();
        }
        return response;
    }

    public String ReadInputStream(InputStream input)
    {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try{
            reader = new BufferedReader(new InputStreamReader(input));
            String line = "";
            while((line = reader.readLine()) != null)
            {
                response.append(line);
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if(reader != null)
            {
                try{
                    reader.close();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
        return response.toString();
    }
}