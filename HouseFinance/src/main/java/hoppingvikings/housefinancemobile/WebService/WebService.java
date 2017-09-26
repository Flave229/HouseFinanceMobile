package hoppingvikings.housefinancemobile.WebService;

import android.os.AsyncTask;
import android.telecom.Call;

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
    }

    @Override
    protected void onPostExecute(CommunicationResponse result)
    {
        String type;
        switch (_request.ItemTypeData)
        {
            case BILL:
                type = "Bills";
                if (result.RequestTypeData == RequestType.POST)
                    type = "";
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

    private CommunicationResponse DownloadUrl() throws IOException
    {
        String subEndpoint;

        switch (_request.ItemTypeData)
        {
            case BILL:
                subEndpoint = "Bills";
                if (_request.RequestTypeData == RequestType.POST)
                    subEndpoint += "/Add";
                break;
            case BILL_DETAILED:
                subEndpoint = "Bills";
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
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(45000);
            conn.setRequestProperty("Authorization", _authToken);

            conn.setRequestMethod(_request.RequestTypeData.toString());

            switch (_request.RequestTypeData)
            {
                case GET:
                    conn.setDoInput(true);
                    break;
                case POST:
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    OutputStream os = conn.getOutputStream();
                    os.write(_request.RequestBody.getBytes("UTF-8"));
                    os.close();
                    break;
            }

            final InputStream input = conn.getInputStream();

            CommunicationResponse response = new CommunicationResponse()
            {{
                Response = new JSONObject(ReadInputStream(input));
                Callback = _request.Callback;
                RequestTypeData = _request.RequestTypeData;
            }};

            return response;
        }
        catch (JSONException e)
        { }
        finally
        {
            conn.disconnect();
        }
        return null;
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