package com.widget.recorder.duongnv.widget_recorder.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

public class ViewRecorderDemoActivity {

    private static final String TAG = "ViewRecorderDemo";

    private final Context mAppContext;

    private final Handler mMainHandler;

    private final Handler mWorkerHandler;

    private ViewRecorder mViewRecorder;

    private boolean mRecording = false;
    private final ViewRecorder.IBitmap iBitmap;

    public ViewRecorderDemoActivity(Context context, ViewRecorder.IBitmap iBitmap) {
        this.iBitmap = iBitmap;
        mAppContext = context.getApplicationContext();
        mMainHandler = new Handler();
        HandlerThread ht = new HandlerThread("bg_view_recorder");
        ht.start();
        mWorkerHandler = new Handler(ht.getLooper());
    }

    private final Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            mMainHandler.postDelayed(this, 500);
        }
    };

    private final MediaRecorder.OnErrorListener mOnErrorListener = new MediaRecorder.OnErrorListener() {

        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.e(TAG, "MediaRecorder error: type = " + what + ", code = " + extra);
            mViewRecorder.reset();
            mViewRecorder.release();
        }
    };


    public void onPause() {
        mMainHandler.removeCallbacks(mUpdateTextRunnable);
        if (mRecording) {
            mMainHandler.post(this::stopRecord);
        }
    }

    public void onResume() {
        mMainHandler.post(mUpdateTextRunnable);
    }

    public void onDestroy() {
        mWorkerHandler.getLooper().quit();
    }



    @Nullable
    public String startRecord(int videoWidth, int videoHeight, int frameRate, boolean enableRecordSoundFromMic) {
        File directory = mAppContext.getFilesDir();
        if (directory != null) {
            directory.mkdirs();
            if (!directory.exists()) {
                Log.e(TAG, "startRecord failed: " + directory + " does not exist!");
                return null;
            }
        }
        mViewRecorder = new ViewRecorder();
        if (enableRecordSoundFromMic) {
            mViewRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT); // uncomment this line if audio required
            mViewRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }
        mViewRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mViewRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mViewRecorder.setVideoFrameRate(frameRate); // 5fps
        mViewRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mViewRecorder.setVideoSize(videoWidth % 2 == 0 ? videoWidth : (videoWidth - 1),
                videoHeight % 2 == 0 ? videoHeight : videoHeight - 1);
        mViewRecorder.setVideoEncodingBitRate(2000 * 1000);
        String file = mAppContext.getCacheDir() + "/" + System.currentTimeMillis() + ".mp4";
        Log.d(TAG, "startRecord: file save in   " + file);
        mViewRecorder.setOutputFile(file);
        mViewRecorder.setOnErrorListener(mOnErrorListener);
        mViewRecorder.setRecordedView(iBitmap);
        try {
            mViewRecorder.prepare();
            mViewRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "startRecord failed", e);
            return null;
        }

        Log.d(TAG, "startRecord successfully!");
        mRecording = true;
        return file;
    }

    public void stopRecord() {
        try {
            mViewRecorder.stop();
            mViewRecorder.reset();
            mViewRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecording = false;
        Log.d(TAG, "stopRecord successfully!");
    }
}
