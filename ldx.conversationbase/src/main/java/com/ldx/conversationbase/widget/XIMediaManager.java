package com.ldx.conversationbase.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class XIMediaManager {

    private static MediaPlayer mPlayer;

    private static boolean isPause;

    public static void playSound(final Context context, String filePathString,
                                 OnCompletionListener onCompletionListener) {

        try {
            if (mPlayer != null) {
                mPlayer.stop();
                releaseAudioFocus(context);
                isPause = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mPlayer.reset();
                    releaseAudioFocus(context);
                    return false;
                }
            });
        } else {
            mPlayer.reset();
            releaseAudioFocus(context);
        }

        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setDataSource(filePathString);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mPlayer.start();
                    requestAudioFocus(context);
                    isPause = false;
                }
            });
            mPlayer.prepareAsync();//network radio prepare
            //mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pause(Context context) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            releaseAudioFocus(context);
            isPause = true;
        }
    }

    public static void play(Context context) {
        if (mPlayer != null) {
            mPlayer.start();
            requestAudioFocus(context);
            isPause = false;
        }
    }

    public static void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }

    public static void release(Context context) {
        if (mPlayer != null) {
            mPlayer.release();
            releaseAudioFocus(context);
            isPause = false;
            mPlayer = null;
        }
    }

    public static boolean isPlaying() {
        if (mPlayer != null && mPlayer.isPlaying() == true) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPause() {
        if (mPlayer != null && isPause) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestAudioFocus(Context context) {
        try {
            AudioManager mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void releaseAudioFocus(Context context) {
        try {
            AudioManager mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.abandonAudioFocus(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
