package git.arithbandara.pavathma.dashbord.location;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class DataAll {
    private String where;
    private String key;

    public DataAll(String where, String key) {
        this.where = where;
        this.key = key;
    }

    public void writeToJson(DataAll data, String filePath, Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileOutputStream fos = context.openFileOutput(filePath, Context.MODE_PRIVATE)) {
            String jsonData = gson.toJson(data);
            fos.write(jsonData.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "where='" + where + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
