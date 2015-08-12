package ca.sapphire.schview;

/**
 * Todo: rework file reader to avoid temporary file
 * Todo: fix text rotations and orientations (rotating is only a hack, and that's only for Attribute
 * Todo: make basic and advanced versions (for distribution) based on what is viewed
 * Todo: add selectable input file
 * Todo: add viewing controls
 * Todo: add title block
 * Todo: add visible grid ?? - have to draw grid before other objects
 *
 * Restructure operations:
 *
 * Presently
 * - The compound file is opened.
 * - Sector list is generated for the data file.
 * - The data file is written to the sdcard. (A)
 * - The data file is read back in from the sdcard.
 * - Each record is stored as it's processed.  (B)
 * - For each record, the Altium values are loaded into a class.  (C)
 * - Each drawable element is saved.  (D)
 * - Therefore in essense, we're keeping four copies of the data, A, B, C, D.
 *
 * A can be eliminated if we directly read from the sector list.
 * B can be eliminated as it's strictly not needed, we can parse the current record, then discard it.
 * C can be reduced unless we intend to use the data in an editor (so comment the code, only keep what is needed for rendering.)
 * D is of course what we need to save in a quickly renderable format in the graphics engine.
 */


import ca.sapphire.altium.Options;
import ca.sapphire.schview.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SchViewActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sch_view);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        viewFile();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public final static String TAG = "SchViewActivity";

    boolean shown = false;
    CompoundFile cf;
    Handler viewHandler = new Handler();

    Runnable viewRunnable = new Runnable() {
        @Override
        public void run()  {
            if( cf != null)
                if( cf.done )
                    if( !shown ) {
                        showFile( cf );
                        shown = true;
                        viewHandler.removeCallbacks( viewRunnable );
                    }

            viewHandler.postDelayed( viewRunnable, 100 );
        }
    };

    public void viewFile() {
        viewHandler.postDelayed( viewRunnable, 100 );
        cf = new CompoundFile( "/sdcard/Download/gclk.SchDoc");
    }

    public void showFile( CompoundFile cf ) {
        Log.i(TAG, "Ready to show.");

        Sch sch = new Sch( this );
        setContentView(sch);
//        sch.setBackgroundColor( Color.WHITE );
        sch.setBackgroundColor( 0xfffffcf8 );
        sch.invalidate();
    }

    //    public class Sch extends SurfaceView implements SurfaceHolder.Callback {
    public class Sch extends View {
        private static final int INVALID_POINTER_ID = -1;

        float viewWidth, viewHeight;
        float scale = 1;
        Paint paint = new Paint();

        private float mPosX;
        private float mPosY;

        public float getMPosX() { return mPosX; }

        public float getMPosY() { return mPosY; }

        private float mLastTouchX;
        private float mLastTouchY;
        private int mActivePointerId = INVALID_POINTER_ID;

        private ScaleGestureDetector mScaleDetector;
        private GestureDetector mDetector;

        private int state = 0;


        public Sch( Context context ) {
            super( context );
            mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
            mDetector = new GestureDetector(this, new MyGestureListener());

            Options.INSTANCE.render(cf.sf.grEngine);

            for( ca.sapphire.altium.Object object  : cf.sf.objects ) {
                Options.INSTANCE.render();
                object.render();
            }
            cf.sf.grEngine.render();
        }

        @Override
        protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
            super.onSizeChanged(xNew, yNew, xOld, yOld);
            viewWidth = xNew;
            viewHeight = yNew;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Log.i(TAG, "Drawing.");

//            scale = Math.min(Math.abs(viewWidth / Options.INSTANCE.xSheet), Math.abs(viewHeight / Options.INSTANCE.ySheet));
//            Log.i(TAG, "Scale: " + scale);
            canvas.scale(scale, scale);

            float xOff = (viewWidth-(Options.INSTANCE.xSheet*scale) ) / 2.0F;
            float yOff = (viewHeight+(Options.INSTANCE.ySheet*scale)) / 2.0F;
            canvas.translate(xOff, (viewHeight / scale) - yOff);
            //        canvas.translate(50, 800);

            paint.setAntiAlias(false);
            paint.setStrokeWidth(0);
            paint.setDither(false);
            paint.setStyle(Paint.Style.STROKE);

//            Options.INSTANCE.render(cf.sf.grEngine);

            for( ca.sapphire.altium.Object object  : cf.sf.objects ) {
//                Options.INSTANCE.render();
//                object.render();
                Options.INSTANCE.draw( canvas, paint );
                object.draw( canvas, paint );
            }

//            cf.sf.grEngine.render();
            cf.sf.grEngine.draw(canvas, paint);


            //            paint.setColor( 0xffff0000 );
            //            canvas.drawLine(-500, -500, 500, 500, paint);
            //            canvas.drawLine( 500, -500, -500, 500, paint);
            //
            //            canvas.drawLine( -500, -500, -500, 500, paint );
            //            canvas.drawLine( -500, 500, 500, 500, paint );
            //            canvas.drawLine( 500, 500, 500, -500, paint );
            //            canvas.drawLine( 500, -500, -500, -500, paint );
            //
            //            canvas.drawLine( 500, 0, -500, 0, paint );
            //            canvas.drawLine( 0, 500, 0, -500, paint );
            //
            //            paint.setColor( 0xff00ff00 );
            //            canvas.drawCircle( 500, -500, 50, paint );
        }

        //register user touches as drawing action
        @Override
        public boolean onTouchEvent(MotionEvent event) {

            mScaleDetector.onTouchEvent(event);
            this.mDetector.onTouchEvent(event);
            return super.onTouchEvent(event);
        }

        class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
            private static final String DEBUG_TAG = "Gestures";

            @Override
            public boolean onDown(MotionEvent event) {
                Log.d(DEBUG_TAG,"onDown: " + event.toString());
                return true;
            }

            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2,
                                   float velocityX, float velocityY) {
                Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
                return true;
            }
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale *= detector.getScaleFactor();

                // Don't let the object get too small or too large.
                scale = Math.max(0.2f, Math.min(scale, 3.0f));
                Log.i( TAG, "Scale: " + scale );

                invalidate();
                return true;
            }
        }
    }
}


