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

import hoppingvikings.housefinancemobile.ItemType;

public class WebService extends AsyncTask<CommunicationRequest, Void, CommunicationResponse>
{
    private String _clientID = "";
    private String _sessionID = "";
    private String _fullAuthToken = "";
    private CommunicationRequest _request;

    public WebService(String clientID, String sessionID)
    {
        _clientID = clientID;
        _sessionID = sessionID;
        _fullAuthToken = "Token " + _sessionID;
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

        if (_request.OwnerV2 != null)
        {
            _request.OwnerV2.HandleResponse(result);
            return;
        }

        if (_request.RequestTypeData != RequestType.GET)
        {
            if(_request.ItemTypeData == ItemType.LOG_IN)
            {
                _request.Owner.ApiResult(result, ItemType.LOG_IN);
                return;
            }
            _request.Owner.ApiResult(result, ItemType.NONE);
            return;
        }

        _request.Owner.ApiResult(result, _request.ItemTypeData);
    }

    private CommunicationResponse DownloadUrl() throws IOException, JSONException
    {
        URL url = new URL(_request.Endpoint);
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

            if(_request.ItemTypeData != ItemType.LOG_IN)
                connection.setRequestProperty("Authorization", _fullAuthToken);
            else
                connection.setRequestProperty("Authorization", "");

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
            Log.e("IO Error", "Error reading the input stream from '" + _request.Endpoint + "': " + e.getMessage());
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