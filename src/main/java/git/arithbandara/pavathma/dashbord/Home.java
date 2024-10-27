package git.arithbandara.pavathma.dashbord;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import git.arithbandara.pavathma.R;

public class Home {
    private Bitmap top;
    private Bitmap bottom;
    private Context context;

    public Home(Bitmap bitmap, Context context){
        this.context = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
        int scaledHeight = (int) (screenWidth / aspectRatio);

        top = Bitmap.createScaledBitmap(bitmap, screenWidth, scaledHeight, true);
        Bitmap bottomGet = bottomCrop(bitmap, screenWidth, scaledHeight);
        bottom = blurBitmap(bottomGet);
    }


    private Bitmap bottomCrop(Bitmap original, int targetWidth, int targetHeight) {
        int width = original.getWidth();
        int halfHeight = original.getHeight() / 2;

        Bitmap bottomHalf = Bitmap.createBitmap(original, 0, halfHeight, width, halfHeight);
        return Bitmap.createScaledBitmap(bottomHalf, targetWidth, targetHeight, true);
    }

    private Bitmap blurBitmap(Bitmap bitmap) {
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Allocation output = Allocation.createFromBitmap(rs, outputBitmap);
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(25f);

        int blurPasses = 4;
        for (int i = 0; i < blurPasses; i++) {
            script.setInput(input);
            script.forEach(output);
            output.copyTo(outputBitmap);
            input = Allocation.createFromBitmap(rs, outputBitmap);
        }

        rs.destroy();
        return outputBitmap;
    }

    public Bitmap combineBoth() {
        Bitmap bottomHalf = bottom;
        Bitmap topHalf = top;

        int width = Math.max(topHalf.getWidth(), bottomHalf.getWidth());
        int height = topHalf.getHeight() + bottomHalf.getHeight();

        Bitmap combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);

        canvas.drawBitmap(topHalf, 0, 0, null);
        canvas.drawBitmap(bottomHalf, 0, topHalf.getHeight(), null);

        return combinedBitmap;
    }

    public void centerFlower(int finalMap, Activity activity) {
        double half = (double) (finalMap / 2) /2;
        ImageView flowerIcon = activity.findViewById(R.id.centerflower);

        ViewGroup.LayoutParams VlParams = flowerIcon.getLayoutParams();
        VlParams.width = 200;
        VlParams.height = 150;
        flowerIcon.setLayoutParams(VlParams);

        FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) flowerIcon.getLayoutParams();
        frameParams.topMargin = (int) half;
        frameParams.gravity = Gravity.CENTER_HORIZONTAL;

        flowerIcon.setLayoutParams(frameParams);
    }

}
