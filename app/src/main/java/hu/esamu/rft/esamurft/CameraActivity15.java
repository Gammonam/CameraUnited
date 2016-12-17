package hu.esamu.rft.esamurft;



import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ZoomControls;

/**
 * Created by Gamilan on 2016. 12. 2..
 */

@SuppressWarnings("deprecation")
public class CameraActivity15 extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.Parameters parameters;

    Button captureBtn;
    FrameLayout preview;
    ProgressBar progressBar;
    ZoomControls zoomControls;

    int currentZoomLevel = 0, maxZoomLevel = 100;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera15);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        captureBtn = (Button) findViewById(R.id.button_capture);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        zoomControls = (ZoomControls) findViewById(R.id.camera_zoom_controls);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, new PhotoHandler(getApplicationContext()));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            }
        });

        zoomControls.setIsZoomInEnabled(true);
        zoomControls.setIsZoomOutEnabled(true);

        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CameraActivity15.this, currentZoomLevel+"", Toast.LENGTH_SHORT);
                Log.d("Camera", "onClick: ZOOM IN" + currentZoomLevel + "\\" + maxZoomLevel);
                if(currentZoomLevel+9 < maxZoomLevel){
                    //currentZoomLevel++;
                    currentZoomLevel += 10;
                    parameters.setZoom(currentZoomLevel);
                    mCamera.setParameters(parameters);
                }
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CameraActivity15.this, currentZoomLevel+"", Toast.LENGTH_SHORT);
                Log.d("Camera", "onClick: ZOOM OUT" + currentZoomLevel + "\\" + maxZoomLevel);
                if(currentZoomLevel-9 > 0){
                    //currentZoomLevel--;
                    currentZoomLevel -= 10;
                    parameters.setZoom(currentZoomLevel);
                    mCamera.setParameters(parameters);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        preview.setVisibility(View.INVISIBLE);
        captureBtn.setVisibility(View.INVISIBLE);
        zoomControls.setVisibility(View.INVISIBLE);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (mCamera == null) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mCamera = getCameraInstance();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        while (mCamera==null){
                            //progressBar.setVisibility(View.VISIBLE);
                        }
                        parameters = mCamera.getParameters();
                        maxZoomLevel = mCamera.getParameters().getMaxZoom();
                        preview.setVisibility(View.VISIBLE);
                        captureBtn.setVisibility(View.VISIBLE);
                        setCameraDisplayOrientation(CameraActivity15.this, 0, mCamera);
                        mPreview = new CameraPreview(CameraActivity15.this, mCamera);
                        preview.addView(mPreview);
                        progressBar.setVisibility(View.INVISIBLE);
                        zoomControls.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        t.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = (info.orientation - degrees + 360) % 360;
        if (camera != null) {
            camera.setDisplayOrientation(result);
        }
    }
}