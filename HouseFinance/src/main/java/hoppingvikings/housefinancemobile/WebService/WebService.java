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
import java.util.Map;

import hoppingvikings.housefinancemobile.ItemType;

public class WebService extends AsyncTask<CommunicationRequest, Void, CommunicationResponse>
{
    private final Map<String, String> _requestProperties;
    private CommunicationRequest _request;

    public WebService(Map<String, String> requestProperties)
    {
        _requestProperties = requestProperties;
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
    }

    @Override
    protected void onPostExecute(CommunicationResponse result)
    {
        if (_request.OwnerV2 != null)
            _request.OwnerV2.HandleResponse(result);
    }

    private CommunicationResponse DownloadUrl() throws IOException
    {
        URL url = new URL(_request.Endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        CommunicationResponse response = new CommunicationResponse()
        {{
            Callback = _request.Callback;
            RequestTypeData = _request.RequestTypeData;
            ItemTypeData = _request.ItemTypeData;
        }};

        try
        {
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(45000);
            connection.setDoInput(true);

            for (String requestPropertyKey : _requestProperties.keySet())
            {
                connection.setRequestProperty(requestPropertyKey, _requestProperties.get(requestPropertyKey));
            }

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