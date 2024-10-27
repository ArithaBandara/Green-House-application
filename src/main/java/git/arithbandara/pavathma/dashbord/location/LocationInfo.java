package git.arithbandara.pavathma.dashbord.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import git.arithbandara.pavathma.GoMaps;
import git.arithbandara.pavathma.R;
import git.arithbandara.pavathma.dashbord.StatusBarSet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;
import static git.arithbandara.pavathma.dashbord.IsNetOK.isConnectedToInternet;

public class LocationInfo extends AppCompatActivity {
    private ExecutorService executorService;
    private ArrayList<Double> locationData;
    private ProgressBar load;
    private EditText EName;
    private Button finish;
    private TextView lo;
    private EditText EAddress;
    private String filePath = "location_data.json";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        StatusBarSet.setStatusBarColor(this);

        executorService = Executors.newFixedThreadPool(2);
        locationData = new ArrayList<>();

        EAddress = findViewById(R.id.gaddresget);
        EName = findViewById(R.id.gtext);
        lo = findViewById(R.id.loading);

        load = findViewById(R.id.progress);
        load.setVisibility(View.INVISIBLE);
        lo.setVisibility(View.INVISIBLE);

        TextView wrn = findViewById(R.id.warn);
        finish = findViewById(R.id.finish);


        finish.setOnClickListener(l -> {

            String address = EAddress.getText().toString().trim();
            String name = EName.getText().toString().trim();

            if (!address.isEmpty() && !name.isEmpty()) {

                load.setVisibility(View.VISIBLE);
                lo.setVisibility(View.VISIBLE);

                finish.setEnabled(false);
                EAddress.setEnabled(false);
                EName.setEnabled(false);

                String encodedAddress = Uri.encode(address);
                String urlPlace = "https://maps.gomaps.pro/maps/api/geocode/json?address=" + encodedAddress + "&key=" + GoMaps.api;

                if (isConnectedToInternet(this)) {
                    fetchWeatherData(urlPlace, name, address);
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LocationInfo.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
            new Thread(() -> {
                int progressStatus = 0;
                while (!new File(filePath).exists()) {
                    progressStatus++;
                    load.setProgress(progressStatus);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                load.setVisibility(View.INVISIBLE);
            }).start();
        });

        EAddress.setOnFocusChangeListener((view, b) -> {
            if (b) {
                if (EName.getText().toString().trim().length() >= 20) {
                    EName.setBackgroundResource(R.drawable.warn);
                    wrn.setText("contain more than 20 letters");
                } else {
                    EName.setBackgroundResource(R.drawable.notwarn);
                    wrn.setText("");
                }
            }
        });

         getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });

    }

    private void writeJson(String address, String name, double lat, double lng) {
        LocationData location = new LocationData(address, name, lat, lng);
        LocationData.writeToJson(location, filePath, this);
    }

    private void fetchWeatherData(String url, String name, String address) {
        Callable<String> callable = new WeatherDataFetcher(url);

        executorService.submit(() -> {
            try {
                String result = callable.call();
                locationData = parseWeatherData(result);
                if (locationData.size() == 2) {
                    writeJson(address, name, locationData.get(0), locationData.get(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    private ArrayList<Double> parseWeatherData(String result) {
        ArrayList<Double> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            if (resultsArray.length() > 0) {
                JSONObject firstResult = resultsArray.getJSONObject(0);
                JSONObject geometry = firstResult.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");

                list.add(latitude);
                list.add(longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
