package com.widget.recorder.duongnv.widget_recorder.recorder;

import android.Manifest;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recordviews.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ViewRecorderDemoActivity extends AppCompatActivity {
    private static final String TAG = "ViewRecorderDemo";

    private Context mAppContext;

    private View mRootView;

    private Button mButtonRecord;

    private Button mButtonSwitch;

    private TextView mTextView;

    private Handler mMainHandler;

    private Handler mWorkerHandler;

    private ViewRecorder mViewRecorder;

    private static int mNumber = 0;

    private boolean mRecording = false;

    private boolean mFullscreen = false;

    private final Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            mTextView.setText(String.valueOf(mNumber++));
            mMainHandler.postDelayed(this, 500);
        }
    };
    private void checkPermission(){
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }
    private final View.OnClickListener mRecordOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mButtonRecord.setEnabled(false);
            mWorkerHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRecording) {
                        stopRecord();
                    } else {
                        startRecord();
                    }
                    updateRecordButtonText();
                }
            });
        }
    };

    private final View.OnClickListener mSwitchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mButtonSwitch.setEnabled(false);
            if (mRecording) {
                mViewRecorder.setRecordedView(mFullscreen ? mTextView : mRootView);
                mFullscreen = !mFullscreen;
                mButtonSwitch.setText(mFullscreen ? R.string.center_view : R.string.full_screen);
                mButtonSwitch.setEnabled(true);
            }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mAppContext = getApplicationContext();

        mRootView = findViewById(R.id.root);
        mTextView = (TextView) findViewById(R.id.text);
        mButtonRecord = (Button) findViewById(R.id.record);
        mButtonRecord.setOnClickListener(mRecordOnClickListener);
        mButtonSwitch = (Button) findViewById(R.id.switcher);
        mButtonSwitch.setOnClickListener(mSwitchOnClickListener);

        mMainHandler = new Handler();
        HandlerThread ht = new HandlerThread("bg_view_recorder");
        ht.start();
        mWorkerHandler = new Handler(ht.getLooper());
        checkPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainHandler.removeCallbacks(mUpdateTextRunnable);
        if (mRecording) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopRecord();
                    updateRecordButtonText();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainHandler.post(mUpdateTextRunnable);
        updateRecordButtonText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWorkerHandler.getLooper().quit();
    }

    private void updateRecordButtonText() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mButtonRecord.setText(mRecording ? R.string.stop_record : R.string.start_record);
                mButtonRecord.setEnabled(true);

                mButtonSwitch.setEnabled(mRecording);
                if (mRecording) {
                    mFullscreen = false;
                    mButtonSwitch.setText(R.string.full_screen);
                }
            }
        });
    }

    private void startRecord() {
        File directory = mAppContext.getFilesDir();
        if (directory != null) {
            directory.mkdirs();
            if (!directory.exists()) {
                Log.e(TAG, "startRecord failed: " + directory + " does not exist!");
                return;
            }
        }

        mViewRecorder = new ViewRecorder();
        mViewRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // uncomment this line if audio required
        mViewRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mViewRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mViewRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mViewRecorder.setVideoFrameRate(5); // 5fps
        mViewRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mViewRecorder.setVideoSize(720, 1280);
        mViewRecorder.setVideoEncodingBitRate(2000 * 1000);
        String file  = getCacheDir() + "/" + System.currentTimeMillis() + ".mp4";
        Log.d(TAG, "startRecord: file save in   " + file);
        mViewRecorder.setOutputFile(file);
        mViewRecorder.setOnErrorListener(mOnErrorListener);

        mViewRecorder.setRecordedView(mTextView);
        try {
            mViewRecorder.prepare();
            mViewRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "startRecord failed", e);
            return;
        }

        Log.d(TAG, "startRecord successfully!");
        mRecording = true;
    }

    private void stopRecord() {
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
