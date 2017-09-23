package hoppingvikings.housefinancemobile.WebService;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebService extends AsyncTask<CommunicationRequest, Void, JSONObject>
{
    private static final String WEB_APIV2_URL = "http://house.flave.co.uk/api/v2/";
    private String _authToken = "";
    private CommunicationRequest _request;

    public WebService(String authToken)
    {
        _authToken = authToken;
    }

    @Override
    protected JSONObject doInBackground(CommunicationRequest... requests)
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
    protected void onPostExecute(JSONObject result)
    {
        String type;
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

    private JSONObject DownloadUrl() throws IOException
    {
        JSONObject jsonObject;

        String subEndpoint;

        switch (_request.ItemTypeData)
        {
            case BILL:
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

            InputStream input = conn.getInputStream();
            jsonObject = new JSONObject(ReadInputStream(input));
            return jsonObject;
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