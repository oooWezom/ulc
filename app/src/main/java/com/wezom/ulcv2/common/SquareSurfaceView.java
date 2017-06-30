package com.wezom.ulcv2.common;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import java.util.Iterator;
import java.util.List;

/**
 * Created: Zorin A.
 * Date: 25.08.2016.
 */
public class SquareSurfaceView extends SurfaceView {

    private Camera.Size mCorrectSize;

    public SquareSurfaceView(Context context) {
        super(context);
        initPreviewSize();
    }


    public SquareSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreviewSize();
    }

    private void initPreviewSize() {
        Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        List<Camera.Size> cameraPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        camera.release();
        selectCorrectAspectRatio(cameraPreviewSizes);
    }

    private void selectCorrectAspectRatio(List<Camera.Size> cameraPreviewSizes) {
        List list = cameraPreviewSizes;
        if (list != null) {
            android.hardware.Camera.Size size = null;
            Iterator iterator = list.iterator();
            do {
                if (!iterator.hasNext())
                    break;
                android.hardware.Camera.Size size1 = (android.hardware.Camera.Size) iterator.next();
                if (Math.abs(3F * ((float) size1.width / 4F) - (float) size1.height) < 0.1F * (float) size1.width
                        && (size == null || size1.height > size.height && size1.width < 3000))
                    size = size1;
            } while (true);
            if (size != null)
                mCorrectSize = size;
            else
                Log.e("CameraSettings", "No supported picture size found");
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCorrectSize.width, mCorrectSize.height);
    }


}
