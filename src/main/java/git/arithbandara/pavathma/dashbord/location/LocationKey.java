package git.arithbandara.pavathma.dashbord.location;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class LocationKey {

    private final ExecutorService executorService;
    private Context context;

    public LocationKey(Context context) {
        executorService = Executors.newFixedThreadPool(2);
        this.context = context;
    }

    public ArrayList<String> fetchWeatherData(String url) {
        Callable<String> callable = new WeatherDataFetcher(url);
        AtomicReference<ArrayList<String>> data = new AtomicReference<>(new ArrayList<>());
        Future<ArrayList<String>> future = executorService.submit(() -> {
            String filePath = "allData.json";
            try {
                String result = callable.call();
                ArrayList<String> parsedData = parseWeatherData(result);
                DataAll dataAll = new DataAll(parsedData.get(0), parsedData.get(1));
                dataAll.writeToJson(dataAll, filePath, context);
                data.set(parsedData);
                return parsedData;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static class WeatherDataFetcher implements Callable<String> {
        private final String url;

        public WeatherDataFetcher(String url) {
            this.url = url;
        }

        @Override
        public String call() {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(this.url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }
    }

    private ArrayList<String> parseWeatherData(String result) {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);

            String key = jsonObject.getString("Key");

            String city = jsonObject.getString("EnglishName");

            JSONObject country = jsonObject.getJSONObject("Country");
            String countryName = country.getString("LocalizedName");

            JSONObject administrativeArea = jsonObject.getJSONObject("AdministrativeArea");
            String administrativeAreaName = administrativeArea.getString("EnglishName");
            String type = administrativeArea.getString("EnglishType");

            String set = "("+city + " " + administrativeAreaName + " " + type + "," + countryName+")";

            list.add(set);
            list.add(key);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
