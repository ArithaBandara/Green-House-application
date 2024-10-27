package git.arithbandara.pavathma.dashbord;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import git.arithbandara.pavathma.R;

public enum AnimationSet {
    INSTANCE;
    private Animation scale_up, scale_down;

    @SuppressLint("ClickableViewAccessibility")
    public void startAnimation(View view) {
        scale_up = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_up);
        scale_down = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_down);

        view.setOnTouchListener((v, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                view.startAnimation(scale_up);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.startAnimation(scale_down);
            }
            return false;
        });
    }

}
