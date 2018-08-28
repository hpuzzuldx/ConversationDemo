package com.ldx.conversationother.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.common.XICacheResourceManager;
import com.ldx.conversationbase.utils.XIAudioManager;
import com.ldx.conversationbase.utils.XIFileSaveUtil;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class XIAudioRecordButton extends android.support.v7.widget.AppCompatButton implements XIAudioManager.AudioStageListener {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private static final int DISTANCE_Y_CANCEL = 50;
    private static final int OVERTIME = 10;
    private int mCurrentState = STATE_NORMAL;
    private boolean isRecording = false;
    private XIDialogManager mDialogManager;
    private float mTime = 0;
    private boolean mReady;
    private XIAudioManager mAudioManager;
    private String saveDir = XIFileSaveUtil.voice_dir;

    private Handler mp3handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case XIAudioManager.MSG_ERROR_AUDIO_RECORD:
                    Toast.makeText(getContext(), getResources().getString(R.string.xiconversation_audio_record_permissionerror),
                            Toast.LENGTH_SHORT).show();
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    mAudioManager.cancel();
                    reset();
                    break;
                default:
                    break;
            }
        }
    };


    public XIAudioRecordButton(Context context) {
        this(context, null);
    }

    public XIAudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            saveDir = XICacheResourceManager.getVoicePath(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDialogManager = new XIDialogManager(context);
        try {
            XIFileSaveUtil.createSDDirectory(saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAudioManager = XIAudioManager.getInstance(saveDir);
        mAudioManager.setOnAudioStageListener(this);
        mAudioManager.setHandle(mp3handler);
        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                try {
                    XIFileSaveUtil.createSDDirectory(saveDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mAudioManager.setVocDir(saveDir);
                mListener.onStart();
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir + saveDir;
    }

    public interface AudioFinishRecorderListener {
        void onStart();

        void onFinished(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(
            AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mhandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                    if (mTime >= OVERTIME) {
                        mTime = 10;
                        mhandler.sendEmptyMessage(MSG_OVERTIME_SEND);
                        isRecording = false;
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGE = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;
    private static final int MSG_OVERTIME_SEND = 0X113;

    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    if (isTouch) {
                        mTime = 0;
                        mDialogManager.showRecordingDialog();
                        isRecording = true;
                        new Thread(mGetVoiceLevelRunnable).start();
                    }
                    break;
                case MSG_VOICE_CHANGE:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(10));
                    break;
                case MSG_DIALOG_DIMISS:
                    isRecording = false;
                    mDialogManager.dimissDialog();
                    break;
                case MSG_OVERTIME_SEND:
                    mDialogManager.tooLong();
                    mhandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);
                    if (mListener != null) {
                        File file = new File(mAudioManager.getCurrentFilePath());
                        if (XIFileSaveUtil.isFileExists(file)) {
                            mListener.onFinished(mTime,
                                    mAudioManager.getCurrentFilePath());
                        } else {
                            mp3handler.sendEmptyMessage(XIAudioManager.MSG_ERROR_AUDIO_RECORD);
                        }
                    }
                    isRecording = false;
                    reset();
                    break;
            }
        };
    };

    @Override
    public void wellPrepared() {
        mhandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    private boolean isTouch = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:

                if (isRecording) {

                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }

                }

                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }

                if (!isRecording || mTime < 0.8f) {
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mhandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else if (mCurrentState == STATE_RECORDING) {
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        BigDecimal b = new BigDecimal(mTime);
                        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                        File file = new File(mAudioManager.getCurrentFilePath());
                        if (XIFileSaveUtil.isFileExists(file)) {
                            mListener.onFinished(f1, mAudioManager.getCurrentFilePath());
                        } else {
                            mp3handler.sendEmptyMessage(XIAudioManager.MSG_ERROR_AUDIO_RECORD);
                        }
                    }
                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    mAudioManager.cancel();
                    mDialogManager.dimissDialog();
                }
                isRecording = false;
                reset();

                break;
            case MotionEvent.ACTION_CANCEL:
                isTouch = false;
                mAudioManager.cancel();
                mDialogManager.dimissDialog();
                reset();
                break;

        }

        return super.onTouchEvent(event);
    }

    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mAudioManager.release();
        mReady = false;
        mTime = 0;
    }

    private boolean wantToCancel(int x, int y) {

        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.xiconversation_btndown_starttalk);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.xiconversation_btnup_undosend);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;

                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.xiconversation_btnup_canclesend);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    @Override
    public boolean onPreDraw() {
        return false;
    }

}
