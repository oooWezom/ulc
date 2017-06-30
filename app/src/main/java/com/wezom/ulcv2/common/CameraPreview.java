package com.wezom.ulcv2.common;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

/**
 * Created: Zorin A.
 * Date: 06.03.2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    protected Camera.Size mOptimalPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
// The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mCamera == null) {
            return;
        }
        resolvePreviewSize(height, width);
        mCamera.stopPreview();

        FrameLayout.LayoutParams v = (FrameLayout.LayoutParams) getLayoutParams();
        int sizeDiff = (width - mOptimalPreviewSize.height);

        v.height = mOptimalPreviewSize.width + (Math.abs(sizeDiff));
        v.width = mOptimalPreviewSize.height + (Math.abs(sizeDiff));
        v.gravity = Gravity.CENTER;

        setLayoutParams(v);

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                mCamera.setDisplayOrientation(90);
                break;
            case Surface.ROTATION_90:
                mCamera.setDisplayOrientation(0);
                break;
            case Surface.ROTATION_180:
                mCamera.setDisplayOrientation(270);
                break;
            case Surface.ROTATION_270:
                mCamera.setDisplayOrientation(180);
                break;
        }

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mOptimalPreviewSize.width, mOptimalPreviewSize.height);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopPreviewAndFreeCamera();
    }

    private void resolvePreviewSize(int width, int height) {
        if (mSupportedPreviewSizes != null) {
            mOptimalPreviewSize = getOptimalPreviewSize(width, height, mSupportedPreviewSizes);
        }
    }

    private Camera.Size getOptimalPreviewSize(int w, int h, List<Camera.Size> sizes) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
