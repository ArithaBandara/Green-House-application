package git.arithbandara.pavathma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import git.arithbandara.pavathma.dashbord.AnimationSet;
import git.arithbandara.pavathma.dashbord.Home;
import git.arithbandara.pavathma.dashbord.StatusBarSet;
import git.arithbandara.pavathma.dashbord.item.ListParam;
import git.arithbandara.pavathma.dashbord.location.LocationInfo;
import git.arithbandara.pavathma.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarSet.setStatusBarColor(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String filePath = "location_data.json";

        if (new File(this.getFilesDir(), filePath).exists()) {
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jungel);
            Home home = new Home(originalBitmap, this);
            Bitmap finalMap = home.combineBoth();

            binding.background.setImageBitmap(finalMap);
            home.centerFlower(finalMap.getHeight(), this);

            //animation part
            AnimationSet.INSTANCE.startAnimation(binding.login);

            binding.login.setOnClickListener(liner -> {
                Intent intent = new Intent(MainActivity.this, ListParam.class);
                startActivity(intent);
            });

        }else {
            Intent locationIntent = new Intent(MainActivity.this, LocationInfo.class);
            startActivity(locationIntent);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });
    }
}