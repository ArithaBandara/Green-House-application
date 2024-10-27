package git.arithbandara.pavathma.dashbord.item;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import git.arithbandara.pavathma.MainActivity;
import git.arithbandara.pavathma.R;
import git.arithbandara.pavathma.dashbord.AnimationSet;
import git.arithbandara.pavathma.dashbord.StatusBarSet;
import git.arithbandara.pavathma.dashbord.location.LocationData;
import git.arithbandara.pavathma.dashbord.weather.Weather;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListParam extends AppCompatActivity {
    private List<Item> itemList = new ArrayList<>();
    private ProgressBar progressBar;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            o -> {
                int resultCode = o.getResultCode();
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onCreate: 01012012012010210210251");
                } else {
                    Toast.makeText(ListParam.this, "Back to Home", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_param);

        StatusBarSet.setStatusBarColor(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView weather = findViewById(R.id.wether);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        String filePath = "location_data.json";
        String name = LocationData.readFromJson(filePath, "name", this);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = findViewById(R.id.houseName);
        textView.setText(name);

        AnimationSet.INSTANCE.startAnimation(weather);

        weather.setOnClickListener(l -> {
            Intent intent = new Intent(ListParam.this, Weather.class);
            activityResultLauncher.launch(intent);
            weather.setClickable(false);
            weather.setEnabled(false);
            bar(progressBar);
        });

        //list for recycle view
        itemList.add(new Item("Temperature", "0 C", R.drawable.termometer));
        itemList.add(new Item("Humidity", "0 %", R.drawable.water_drop));
        itemList.add(new Item("Light", "0 Iᵥ", R.drawable.hanging_lamp));
        itemList.add(new Item("Soil Humidity", "0 %", R.drawable.temperature_soil));

        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                progressBar.setVisibility(View.INVISIBLE);
                Intent locationIntent = new Intent(ListParam.this, MainActivity.class);
                startActivity(locationIntent);
            }
        });


    }

    public static void bar(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            int progressStatus = 0;
            while (progressStatus <= 20) {
                progressStatus++;
                progressBar.setProgress(progressStatus);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
        }).start();
    }
}
