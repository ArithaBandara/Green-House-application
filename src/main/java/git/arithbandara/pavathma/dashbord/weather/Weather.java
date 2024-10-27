package git.arithbandara.pavathma.dashbord.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import git.arithbandara.pavathma.AccWeather;
import git.arithbandara.pavathma.R;
import git.arithbandara.pavathma.dashbord.AnimationSet;
import git.arithbandara.pavathma.dashbord.StatusBarSet;
import git.arithbandara.pavathma.dashbord.item.ListParam;
import git.arithbandara.pavathma.dashbord.location.LocationData;
import git.arithbandara.pavathma.dashbord.location.LocationKey;
import git.arithbandara.pavathma.dashbord.weather.item.today.WeatherItem;
import git.arithbandara.pavathma.dashbord.weather.item.today.WeatherItemAdapter;
import git.arithbandara.pavathma.dashbord.weather.item.tomorrow.WeatherItemAdapterTomorrow;
import git.arithbandara.pavathma.dashbord.weather.item.tomorrow.WeatherItemTomorrow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static git.arithbandara.pavathma.dashbord.IsNetOK.isConnectedToInternet;

public class Weather extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView forecastRecyclerView;

    private ExecutorService executorService;
    private ExecutorService TexecutorService;


     ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            o -> {
                int resultCode = o.getResultCode();
                if (!resultCode == RESULT_OK){
			Toast.makeText(this, "Back to Home", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);

        StatusBarSet.setStatusBarColor(this);

        recyclerView = findViewById(R.id.today);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        executorService = Executors.newFixedThreadPool(2);

        ImageView home = findViewById(R.id.homeb);
        AnimationSet.INSTANCE.startAnimation(home);

        ProgressBar progressBar=findViewById(R.id.progressBr);
        progressBar.setVisibility(View.INVISIBLE);

         home.setOnClickListener(l -> {
            Intent intent = new Intent(this, ListParam.class);
            activityResultLauncher.launch(intent);

            home.setEnabled(false);
            home.setClickable(false);

             ListParam.bar(progressBar);

         });


        forecastRecyclerView = findViewById(R.id.tommorow);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TexecutorService = Executors.newFixedThreadPool(2);

        String filePath = "location_data.json";
        String lat = LocationData.readFromJson(filePath, "lat", this);
        String lng = LocationData.readFromJson(filePath, "lng", this);

        String locationKeyURL = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=" + AccWeather.api + "&q=" + lat + "%2C" + lng;
        LocationKey locationKey = new LocationKey(this);
        ArrayList<String> list = locationKey.fetchWeatherData(locationKeyURL);

        if (!list.isEmpty()) {
            try {
                String urlToday = "https://dataservice.accuweather.com/forecasts/v1/hourly/1hour/" + list.get(1) + "?apikey=" + AccWeather.api;
                String urlForecast = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/" + list.get(1) + "?apikey=" + AccWeather.api + "&details=true";
                fetchWeatherData(urlToday, urlForecast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Thread.sleep(3000);
                try {
                    String urlToday = "https://dataservice.accuweather.com/forecasts/v1/hourly/1hour/" + list.get(1) + "?apikey=" + AccWeather.api;
                    String urlForecast = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/" + list.get(1) + "?apikey=" + AccWeather.api + "&details=true";
                    fetchWeatherData(urlToday, urlForecast);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        SwitchCompat light = findViewById(R.id.Light);
        SwitchCompat fan = findViewById(R.id.fan);
        colourSet(light, R.color.redn, R.color.redn, R.color.green, R.color.green, R.color.redn, R.color.redn);
        colourSet(fan, R.color.redn, R.color.redn, R.color.green, R.color.green, R.color.redn, R.color.redn);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Weather.this, ListParam.class);
                startActivity(intent);
            }
        });

    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    private void colourSet(SwitchCompat item, int p, int p2, int p3, int p4, int p5, int p6) {
        item.getThumbDrawable().setTint(ContextCompat.getColor(this, p));
        item.getTrackDrawable().setTint(ContextCompat.getColor(this, p2));
        item.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                item.getThumbDrawable().setTint(ContextCompat.getColor(this, p3));
                item.getTrackDrawable().setTint(ContextCompat.getColor(this, p4));
                item.setText("ON");
                item.setTextColor(R.color.green);
            } else {
                item.getThumbDrawable().setTint(ContextCompat.getColor(this, p5));
                item.getTrackDrawable().setTint(ContextCompat.getColor(this, p6));
                item.setText("OFF");
                item.setTextColor(R.color.redn);
            }
        });
    }

    private void fetchWeatherData(String url, String urlForecast) {
        Callable<String> callable = new WeatherDataFetcher(url);
        Callable<String> callable2 = new WeatherDataFetcher(urlForecast);

        Future<String> future = executorService.submit(callable);
        Future<String> future2 = TexecutorService.submit(callable2);

        runOnUiThread(() -> {
            try {
                String result = future.get();
                parseWeatherData(result);

                String result2 = future2.get();
                parseForecastWeatherData(result2);
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

    @SuppressLint("NotifyDataSetChanged")
    private void parseWeatherData(String result) {
        try {
            List<WeatherItem> itemList = new ArrayList<>();
            if (isConnectedToInternet(this)) {
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                JSONObject temperatureObject = jsonObject.getJSONObject("Temperature");
                String temperatureValue = temperatureObject.getString("Value");
                String iconPhrase = jsonObject.getString("IconPhrase");
                String iconCode = jsonObject.getString("WeatherIcon");
                boolean isdn = jsonObject.getBoolean("IsDaylight");

                String code = getIconCode(iconCode, isdn);
                int resourceId = getResources().getIdentifier(code, "drawable", getPackageName());

                itemList.add(new WeatherItem(ToCelsius(Double.parseDouble(temperatureValue)) + "°C", resourceId, iconPhrase.replace(",", "").replace(";", ",")));

            } else {
                int resourceId = getResources().getIdentifier("w03d", "drawable", getPackageName());
                itemList.add(new WeatherItem("0°C", resourceId, "0"));
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }

            WeatherItemAdapter adapter = new WeatherItemAdapter(itemList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void parseForecastWeatherData(String result) {
        try {
            List<WeatherItemTomorrow> itemList = new ArrayList<>();
            if (isConnectedToInternet(this)) {
                JSONObject responseObject = new JSONObject(result);
                JSONArray dailyForecasts = responseObject.getJSONArray("DailyForecasts");

                JSONObject tomorrowForecast = dailyForecasts.getJSONObject(1);

                JSONObject day = tomorrowForecast.getJSONObject("Day");
                String dayPhrase = day.getString("LongPhrase");
                String IconCodeD = day.getString("Icon");
                String codeDay = getIconCode(IconCodeD, true);

                int resourceIdDay = getResources().getIdentifier(codeDay, "drawable", getPackageName());
                itemList.add(new WeatherItemTomorrow(dayPhrase.replace(",", "").replace(";", ","), resourceIdDay));

            } else {
                int resourceId = getResources().getIdentifier("w03d", "drawable", getPackageName());
                itemList.add(new WeatherItemTomorrow("NO Data", resourceId));

                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }

            WeatherItemAdapterTomorrow adapter = new WeatherItemAdapterTomorrow(itemList);
            forecastRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int ToCelsius(double temperature) {
        double value = ((temperature - 32) * 5) / 9;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        double roundedValue = bd.doubleValue();
        return (int) roundedValue;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        TexecutorService.shutdown();
    }

    private String getIconCode(String code, boolean isdn) {
        String send = "";
        switch (code) {
            case "1":
            case "2":
            case "30":
                send = isdn ? "01d" : "01n";
                break;
            case "3":
            case "4":
                send = isdn ? "02d" : "02n";
                break;
            case "5":
            case "11":
                send = "50d";
                break;
            case "6":
            case "7":
                send = isdn ? "03d" : "03n";
                break;
            case "8":
                send = isdn ? "04d" : "04n";
                break;
            case "12":
            case "18":
            case "26":
            case "29":
                send = isdn ? "09d" : "09n";
                break;
            case "13":
            case "14":
                send = isdn ? "10d" : "10n";
                break;
            case "15":
            case "16":
            case "17":
                send = isdn ? "11d" : "11n";
                break;
            case "19":
            case "20":
            case "21":
            case "22":
            case "23":
            case "24":
            case "25":
            case "33":
                send = "13d";
                break;
            default:
                send = "03d";
                break;
        }
        return "w" + send;
    }
}
