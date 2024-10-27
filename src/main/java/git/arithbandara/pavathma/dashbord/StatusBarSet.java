package git.arithbandara.pavathma.dashbord;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowInsetsController;

public interface StatusBarSet {

    static void setStatusBarColor(Activity context){
        Window window = context.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            window.setStatusBarColor(context.getResources().getColor(android.R.color.white, null));
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
                insetsController.hide(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
    }
}
