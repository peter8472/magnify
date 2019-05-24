package info.p445m.magnify;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

        private Camera mCamera;
        private CameraPreview mPreview;
        private int zoomLevel;
        private int oldZoom;
        private String oldFocus;
        private String oldFlash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);

        setContentView(R.layout.camera_preview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create an instance of Camera
        //mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.

        Button zoomIn = (Button) findViewById(R.id.zoom_in);
        Button zoomOut = (Button) findViewById(R.id.zoom_out);
        zoomIn.setOnClickListener(zoomInListener);
        zoomOut.setOnClickListener(zoomOutListener);





    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
//        if (mCamera==null)
            mCamera = getCameraInstance();

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        zoomLevel = sharedPref.getInt(getString(R.string.zoomPref),0);
        Camera.Parameters cParam = mCamera.getParameters();
        oldFlash = cParam.getFlashMode();
        oldZoom = cParam.getZoom();
        oldFocus = cParam.getFocusMode();
        cParam.setZoom(zoomLevel);
        mCamera.setParameters(cParam);
        cParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cParam.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        mCamera.setParameters(cParam);

    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            mCamera= Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        //preview.setCamera(null);
        // huh?

        if (mCamera!= null) {
            mCamera.release();
//            mCamera = null;
        }
    }
    public void zoom(int amount) {
        // amount: the amount to zoom, negative for zoom out
        Camera.Parameters cParam = mCamera.getParameters();
        int maxZoom = cParam.getMaxZoom();

        int myZoom = cParam.getZoom();
        myZoom = myZoom + amount;
        if (myZoom < 0)
            myZoom = 0;
        if (myZoom > maxZoom)
            myZoom = maxZoom;

        cParam.setZoom(myZoom);
        mCamera.setParameters(cParam);
    }
    public void closer(View view) {
        zoom(10);
    }
    public void farther(View view) {
        zoom(-10);
    }
    private View.OnClickListener zoomInListener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
            closer(v);
        }
    };
    private View.OnClickListener zoomOutListener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
            farther(v);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPref.edit();
        Camera.Parameters param = mCamera.getParameters();
        editor.putInt(getString(R.string.zoomPref), param.getZoom());
        editor.commit();
        param.setZoom(oldZoom);
        param.setFocusMode(oldFocus);
        param.setFlashMode(oldFlash);
        mCamera.setParameters(param);
        releaseCameraAndPreview();
    }
}
