package com.wezom.ulcv2.media;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.interfaces.VideoConnectionListener;
import com.wmspanel.libstream.AudioConfig;
import com.wmspanel.libstream.CameraConfig;
import com.wmspanel.libstream.Streamer;
import com.wmspanel.libstream.StreamerGL;
import com.wmspanel.libstream.StreamerGLBuilder;
import com.wmspanel.libstream.VideoConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

import static com.wezom.ulcv2.ui.fragment.VideoFragment.TAG;


/**
 * Created: Zorin A.
 * Date: 17.10.2016.
 */

public class BroadcasterCallback implements SurfaceHolder.Callback, Streamer.Listener {
    private static final int FRAME_RATE = 24;
    private static final int SAMPLE_RATE = 44100;
    private static final int MAX_BUFFER_ITEMS = 200;
    private static final int AUDIO_CHANNELS_COUNT = 1;
    private static final int VIDEO_WIDTH = 320;
    private static final int VIDEO_HEIGHT = 240;
    private static final int KEYFRAME_INTERVAL = 1;
    private static final int VIDEO_BITRATE = 450000;

    private String mRtmpUrl;
    private Context mContext;
    private StreamerGL mStreamer;
    private SurfaceHolder mHolder;
    private SurfaceView mSurfaceView;
    private VideoConnectionListener mConnectionListener;
    private int mConnectionID;
    private Handler mHandler;
    private String cameraId = String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT);
    private StreamerGLBuilder builder;
    boolean isSwitchClick = false;
    Subscription subscription;

    public BroadcasterCallback(Context context, String rtmpUrl, SurfaceView surfaceView) {
        mContext = context;
        mRtmpUrl = rtmpUrl;
        mSurfaceView = surfaceView;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public BroadcasterCallback(Context context, String rtmpUrl, SurfaceView surfaceView, VideoConnectionListener connectionListener) {
        this(context, rtmpUrl, surfaceView);
        mConnectionListener = connectionListener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        createStreamer(mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mStreamer != null) {
            mStreamer.setSurfaceSize(new Streamer.Size(width, height));
        }
    }


    public void switchCamera() {
        isSwitchClick = true;
        mStreamer.flip();
        int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        mStreamer.setDisplayRotation(rotation);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
        releaseStreamer();
    }

    public static Streamer.CameraInfo getActiveCameraInfo(List<Streamer.CameraInfo> cameraList, Context context) {
        Streamer.CameraInfo cameraInfo = null;

        if (cameraList == null || cameraList.size() == 0) {
            Log.e(TAG, "no camera found");
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            String cameraId = sp.getString("cam_key", "0");
            for (Streamer.CameraInfo cursor : cameraList) {
                if (cursor.cameraId.equals(cameraId)) {
                    cameraInfo = cursor;
                }
            }
            if (cameraInfo == null) {
                cameraInfo = cameraList.get(0);
            }
        }
        return cameraInfo;
    }

    private void createStreamer(SurfaceHolder holder) {

        StreamerGLBuilder builder = new StreamerGLBuilder();
        List<Streamer.CameraInfo> cameraList = builder.getCameraList(mContext, false);
        Streamer.CameraInfo cameraInfo = getActiveCameraInfo(cameraList, mContext);

        builder = new StreamerGLBuilder();
        builder.setContext(mContext);
        builder.setListener(this);
        builder.setUserAgent("LarixBroadcaster" + "/" + Streamer.VERSION_NAME);
        builder.setMaxBufferItems(MAX_BUFFER_ITEMS);

        AudioConfig audioConfig = new AudioConfig();
        audioConfig.sampleRate = SAMPLE_RATE;
        audioConfig.channelCount = AUDIO_CHANNELS_COUNT;
        audioConfig.audioSource = MediaRecorder.AudioSource.DEFAULT;
        builder.setAudioConfig(audioConfig);

        builder.setCamera2(false);
        builder.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        VideoConfig videoConfig = new VideoConfig();
        Streamer.Size videoSize = new Streamer.Size(VIDEO_WIDTH, VIDEO_HEIGHT);
        videoConfig.videoSize = videoSize;
        videoConfig.fps = FRAME_RATE;
        videoConfig.keyFrameInterval = KEYFRAME_INTERVAL;
//        int bitRate = VideoEncoder.calcBitRate(videoSize, FRAME_RATE, MediaCodecInfo.CodecProfileLevel.AVCProfileMain);
        videoConfig.bitRate = VIDEO_BITRATE;
        builder.setVideoConfig(videoConfig);

        builder.setSurface(holder.getSurface());
        builder.setSurfaceSize(new Streamer.Size(mSurfaceView.getWidth(), mSurfaceView.getHeight()));

        final int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            builder.setVideoOrientation(StreamerGL.ORIENTATIONS.HORIZON);
        } else {
            builder.setVideoOrientation(StreamerGL.ORIENTATIONS.LANDSCAPE);
        }
        builder.setDisplayRotation(((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation());

        CameraConfig cameraConfig = new CameraConfig();
        // String cameraId = String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT);
        cameraConfig.cameraId = cameraId;
        cameraConfig.videoSize = videoSize;
        builder.addCamera(cameraConfig);
        builder.setCameraId(cameraId);

        String flipCameraId = null;
        if (cameraList.size() > 1) {
            Streamer.Size flipSize = null;
            boolean found = false;

            for (int i = 0; i < cameraList.size(); i++) {
                cameraInfo = cameraList.get(i);
                if (cameraInfo.cameraId.equals(cameraId)) {
                    continue;
                } else {
                    flipCameraId = cameraInfo.cameraId;
                }

                // If secondary camera supports same resolution, use it
                for (int ii = 0; ii < cameraInfo.recordSizes.length; ii++) {
                    if (cameraInfo.recordSizes[ii].width == videoSize.width &&
                            cameraInfo.recordSizes[ii].height == videoSize.height) {
                        flipSize = new Streamer.Size(cameraInfo.recordSizes[ii].width, cameraInfo.recordSizes[ii].height);
                        found = true;
                        break;
                    }
                }

                // If same resolution not found, search for same aspect ratio
                if (!found) {
                    for (int ii = 0; ii < cameraInfo.recordSizes.length; ii++) {
                        if (cameraInfo.recordSizes[ii].width < videoSize.width) {
                            double mTargetAspect = (double) videoSize.width / videoSize.height;
                            double viewAspectRatio = (double) cameraInfo.recordSizes[ii].width / cameraInfo.recordSizes[ii].height;
                            double aspectDiff = mTargetAspect / viewAspectRatio - 1;
                            if (Math.abs(aspectDiff) < 0.01) {
                                flipSize = new Streamer.Size(cameraInfo.recordSizes[ii].width, cameraInfo.recordSizes[ii].height);
                                found = true;
                                break;
                            }
                        }
                    }
                }

                // Same aspect ratio not found, search for less or similar frame sides
                if (!found) {
                    for (int ii = 0; ii < cameraInfo.recordSizes.length; ii++) {
                        if (cameraInfo.recordSizes[ii].height <= videoSize.height &&
                                cameraInfo.recordSizes[ii].width <= videoSize.width) {
                            flipSize = new Streamer.Size(cameraInfo.recordSizes[ii].width, cameraInfo.recordSizes[ii].height);
                            found = true;
                            break;
                        }
                    }
                }

                // Nothing found, use default
                if (!found) {
                    flipSize = new Streamer.Size(cameraInfo.recordSizes[0].width, cameraInfo.recordSizes[0].height);
                }
            }

            // flipSize == null should never happen
            if (flipSize != null) {
                // add second camera to flip list
                CameraConfig flipCameraConfig = new CameraConfig();
                flipCameraConfig.cameraId = flipCameraId;
                flipCameraConfig.videoSize = flipSize;
                builder.addCamera(flipCameraConfig);
                Log.d(TAG, "Camera #" + flipCameraId + " size: " + Integer.toString(flipSize.width) + "x" + Integer.toString(flipSize.height));
            }


            mStreamer = builder.build();
            mStreamer.startVideoCapture();
            mStreamer.startAudioCapture();
            mConnectionID = mStreamer.createConnection(mRtmpUrl, Streamer.MODE.AUDIO_VIDEO, this);
        }
    }

    public void releaseStreamer() {
        // check if Streamer instance exists
        if (mStreamer == null) {
            return;
        }
        // stop broadcast;
        mStreamer.releaseConnection(mConnectionID);
        // stop mp4 recording

        // cancel audio and video capture
        mStreamer.stopAudioCapture();
        mStreamer.stopVideoCapture();
        // finally release streamer, after release(), the object is no longer available
        // if a Streamer is in released state, all methods will throw an IllegalStateException
        mStreamer.release();
        // sanitize Streamer object holder
        mStreamer = null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (mStreamer == null) {
            return;
        }
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mStreamer.setVideoOrientation(StreamerGL.ORIENTATIONS.HORIZON);
        } else {
            mStreamer.setVideoOrientation(StreamerGL.ORIENTATIONS.LANDSCAPE);
        }
        int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        mStreamer.setDisplayRotation(rotation);
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onConnectionStateChanged(int connectionId, Streamer.CONNECTION_STATE state, Streamer.STATUS status) {
        Log.v("BROADCAST", "connectionId - " + connectionId + " connection_state - " + state + " status - " + status);

        switch (state) {
            case INITIALIZED:
                break;
            case CONNECTED:
                if (mConnectionListener != null) {
                    mConnectionListener.onConnected();
                }
                break;
            case SETUP:
                break;
            case RECORD:
                break;
            case DISCONNECTED:
                Log.d("Log_ isSwitchClick ", isSwitchClick + "  " + !(isSwitchClick && mConnectionListener == null));
                if ((!isSwitchClick) && (mConnectionListener != null)) {
                    mConnectionListener.onDisconnected();
                }
                isSwitchClick = false;
                break;
        }
    }

    @Override
    public void onVideoCaptureStateChanged(Streamer.CAPTURE_STATE capture_state) {
        Log.v("BROADCAST", "onVideoCaptureStateChanged");
    }

    @Override
    public void onAudioCaptureStateChanged(Streamer.CAPTURE_STATE capture_state) {
        Log.v("BROADCAST", "onAudioCaptureStateChanged");
    }
}
