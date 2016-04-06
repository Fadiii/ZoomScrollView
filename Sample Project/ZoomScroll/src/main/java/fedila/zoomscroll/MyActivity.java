package fedila.zoomscroll;

/*
  Example project for the ZoomScroll class
  Author: Fedil A.
*/

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        // Find main layout declared in activity.xml
        RelativeLayout motherLayout = (RelativeLayout)findViewById(R.id.layout);

        // Create sample imageView and set it up
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.example);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.FIT_START);

        // Create ZoomScroll view
        ZoomScrollView zoomScrollView = new ZoomScrollView(this, motherLayout, true, true);

        // add imageView to ZoomScroll
        zoomScrollView.addView(image);
    }
}
