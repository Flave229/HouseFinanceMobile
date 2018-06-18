package hoppingvikings.housefinancemobile;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileIOHandler
{
    private static FileIOHandler instance;

    private FileIOHandler()
    {
    }

    public static FileIOHandler Instance()
    {
        if(instance == null)
            instance = new FileIOHandler();

        return instance;
    }

    public void WriteToFile(String fileName, String data)
    {
        try {
            String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HouseFinance/";
            File directory = new File(directoryPath);

            if(!directory.exists())
                directory.mkdirs();

            File recentsPath = new File(directory, fileName);

            if(!recentsPath.exists())
                recentsPath.createNewFile();

            FileOutputStream fos = new FileOutputStream(recentsPath, false);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            writer.write(data);
            writer.close();

            fos.flush();
            fos.close();

        } catch (Exception e)
        {
            Log.e("Write Error: ", e.getMessage());
        }
    }

    public String ReadFileAsString(String filename)
    {
        String fileString = "";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HouseFinance/";
        File dir = new File(path);

        if(dir.exists())
        {
            File file = new File(dir, filename);

            if(file.exists())
            {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                    String line = reader.readLine();
                    while (line != null)
                    {
                        fileString = line;
                        line = reader.readLine();
                    }
                    reader.close();
                    fis.close();

                    return fileString;
                } catch (Exception e)
                {
                    Log.e("Read Error:", e.getMessage());
                    return null;
                }
            }
        }

        return fileString;
    }

    public ArrayList<JSONObject> ReadFile(String fileName)
    {
        ArrayList<JSONObject> recentItems = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HouseFinance/";
        File dir = new File(path);

        if(dir.exists())
        {
            File recents = new File(dir, fileName);

            if(recents.exists())
            {
                try {
                    FileInputStream fis = new FileInputStream(recents);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                    String line = reader.readLine();
                    while (line != null)
                    {
                        JSONObject recentItemJson = new JSONObject(line);
                        recentItems.add(recentItemJson);
                        line = reader.readLine();
                    }
                    reader.close();
                    fis.close();

                    return recentItems;
                } catch (Exception e)
                {
                    Log.e("Read Error:", e.getMessage());
                    return null;
                }
            }
        }

        return null;
    }
}
