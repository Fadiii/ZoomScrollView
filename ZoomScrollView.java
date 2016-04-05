package fedila.zoomscroll;

/*
  This class enables zooming and scrolling functionality for any view.
  Author: Fedil A.
*/

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

public class ZoomScrollView extends RelativeLayout {
    private Scroller scroller;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private int width;
    private int height;
    private int lastX;
    private int lastY;
    private int currentX;
    private int currentY;
    private int currentWidth;
    boolean scrolledLastFrame;
    private Rect clipBounds;
    private boolean isLimitedScrolling;
    private boolean isLimitedZooming;
    private Matrix canvasMatrix = new Matrix();
    private Matrix standardMarix = new Matrix();
    private Point screenSize = new Point();
    private int max_left = 10000;
    private int max_top = 10000;
    private int min_left = -10000;
    private int min_top = -10000;
    private float mScaleFactor = 1.0f;
    private float[] values = {1,0,0,0,1,0,0,0,1};

    public ZoomScrollView(Activity activity, RelativeLayout motherLayout, boolean isLimitedScrolling, boolean isLimitedZooming) {
        super(activity);
        this.isLimitedScrolling = isLimitedScrolling;
        this.isLimitedZooming = isLimitedZooming;

        // Get screenSize for limited scrolling
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        // Add statusbar height to screensize
        screenSize.y += getStatusBarHeight();

        // Create intermediate scrollview to extend the view vertically
        PseudoScrollView scrollView = new PseudoScrollView(activity);
        scrollView.addView(this);

        // Add scrollView to mother layout, wrap_content as height
        motherLayout.addView(scrollView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Set default matrix values
        canvasMatrix.setValues(values);
        standardMarix.setValues(values);

        // Setup gesture listeners
        mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleListener);

        // Setup interpolator for scroller - Set scrolling speed here
        LinearInterpolator interpolator = new LinearInterpolator();
        interpolator.getInterpolation(0.1f);
        scroller = new Scroller(getContext(), interpolator);
    }

    public void setScreenSize(Point screenSize){
        this.screenSize = screenSize;
    }

    public Point getScreenSize(){
        return screenSize;
    }

    public void setLimitScrolling(boolean limit){
        isLimitedScrolling = limit;
    }

    public boolean isLimitedScrolling(){
        return isLimitedScrolling;
    }

    public void setLimitZooming(boolean limit){
        isLimitedZooming = limit;
    }

    public boolean isLimitedZooming(){
        return isLimitedZooming;
    }

    public void doScrollBy(int dx, int dy) {
        if(isLimitedScrolling) {
            //Right
            if (currentWidth + dx > width) {
                dx = width - currentWidth;
            }

            //Left
            if (currentX + dx < 0) {
                dx = -currentX;
            }

            //Top
            if (currentY + dy < 0) {
                dy = -currentY;
            }

            //Bottom
            if (currentY + dy + (screenSize.y / mScaleFactor) > height) {
                dy = (height - (currentY + (screenSize.y / (int)mScaleFactor)));
            }
        }

        canvasMatrix.postTranslate(-dx, -dy);
    }

    public void doScrollTo(int x, int y) {
        canvasMatrix.postTranslate((currentX - x), (currentY - y));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setMatrixAndBounds(Canvas canvas){
        canvas.setMatrix(canvasMatrix);

        clipBounds = canvas.getClipBounds();

        currentX = clipBounds.left;
        currentY = clipBounds.top;
        currentWidth = clipBounds.right;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        scaleGestureDetector.onTouchEvent(ev);
        return mGestureDetector.onTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Apply matrix to canvas and get bounds values
        setMatrixAndBounds(canvas);

        // Draw after new matrix has been applied
        super.dispatchDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            if (!scrolledLastFrame) {
                lastX = scroller.getStartX();
                lastY = scroller.getStartY();
            }

            int dx = scroller.getCurrX() - lastX;
            int dy = scroller.getCurrY() - lastY;

            lastX = scroller.getCurrX();
            lastY = scroller.getCurrY();

            doScrollBy(dx, dy);

            invalidate();

            scrolledLastFrame = true;
        } else {
            scrolledLastFrame = false;
        }
    }

    // Get new height and width values when changing device orientation
    @Override
    protected void onSizeChanged(int a1, int b1, int c, int d) {
        height = b1;
        width = a1;
    }

    // GestureDetector for scrolling and flinging
    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            scroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            scroller.fling(currentX, currentY, (int) -velocityX, (int) -velocityY,
                    min_left, max_left, min_top, max_top);
            invalidate();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (scroller.isFinished()) {
                doScrollBy((int) distanceX, (int) distanceY);
                invalidate();
            }
            return true;
        }
    };

    // GestureDetector for zooming
    private final ScaleGestureDetector.SimpleOnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();

            // Limit zooming
            if(isLimitedZooming && mScaleFactor * scale < 1) {
                scale = 1/mScaleFactor;
            }

            mScaleFactor *= scale;

            canvasMatrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());

            invalidate();

            return true;
        }
    };

    // ScrollView with disabled scrolling functionality
    class PseudoScrollView extends ScrollView {
        public PseudoScrollView(Context context){
            super(context);
            setVerticalScrollBarEnabled(false);
        }
        @Override
        public boolean onTouchEvent(MotionEvent ev){
            return false;
        }
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }
    }
}
