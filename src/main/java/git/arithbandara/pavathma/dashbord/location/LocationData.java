package git.arithbandara.pavathma.dashbord.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import git.arithbandara.pavathma.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class LocationData {

    private String address;
    private String name;
    private double lat;
    private double lng;

    public LocationData(String address, String name, double lat, double lng) {
        this.address = address;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public static void writeToJson(LocationData data, String filePath, Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileOutputStream fos = context.openFileOutput(filePath, Context.MODE_PRIVATE)) {
            String jsonData = gson.toJson(data);
            fos.write(jsonData.getBytes());
            fos.flush();

            ((Activity) context).runOnUiThread(() -> {
                Intent backTOMain = new Intent(context, MainActivity.class);
                context.startActivity(backTOMain);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromJson(String filePath, String fieldName, Context context) {

        if (!new File(String.valueOf(context.getFileStreamPath(filePath))).exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(context.getFileStreamPath(filePath))) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has(fieldName)) {
                return jsonObject.get(fieldName).getAsString();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
