package com.ldx.conversationbase.utils;

import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class XIAudioManager {
	public static final int MSG_ERROR_AUDIO_RECORD = -4;
	private MediaRecorder mRecorder;
	private String mDirString;
	private String mCurrentFilePathString;
	private Handler handler;
	private boolean isPrepared;

	private static XIAudioManager mInstance;

	private XIAudioManager(String dir) {
		mDirString = dir;
	}

	public static XIAudioManager getInstance(String dir) {
		if (mInstance == null) {
			synchronized (XIAudioManager.class) {
				if (mInstance == null) {
					mInstance = new XIAudioManager(dir);
				}
			}
		}
		return mInstance;
	}

	public void setHandle(Handler handler) {
		this.handler = handler;
	}

	public interface AudioStageListener {
		void wellPrepared();
	}

	public AudioStageListener mListener;

	public void setOnAudioStageListener(AudioStageListener listener) {
		mListener = listener;
	}

	public void setVocDir(String dir) {
		mDirString = dir;
	}

	@SuppressWarnings("deprecation")
	public void prepareAudio() {
		try {
			isPrepared = false;

			File dir = new File(mDirString);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileNameString = generalFileName();
			File file = new File(dir, fileNameString);

			mCurrentFilePathString = file.getAbsolutePath();

			mRecorder = new MediaRecorder();
			mRecorder.setOutputFile(file.getAbsolutePath());
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.prepare();
			mRecorder.start();
			if (mListener != null) {
				mListener.wellPrepared();
			}
			isPrepared = true;

		} catch (IllegalStateException e) {
			e.printStackTrace();
			if (handler != null) {
				handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (handler != null) {
				handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (handler != null) {
				handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
			}
		}
	}

	private String generalFileName() {

		return UUID.randomUUID().toString() + ".amr";
	}

	private int vocAuthority[] = new int[10];
	private int vocNum = 0;
	private boolean check = true;

	public int getVoiceLevel(int maxLevel) {
		// mRecorder.getMaxAmplitude(),0-32767
		if (isPrepared) {
			try {
				int vocLevel = mRecorder.getMaxAmplitude();
				if (check) {
					if (vocNum >= 5) {
						Set<Integer> set = new HashSet<Integer>();
						for (int i = 0; i < vocNum; i++) {
							set.add(vocAuthority[i]);
						}
						if (set.size() == 1) {
							vocNum = 0;
							vocAuthority = null;
							vocAuthority = new int[10];
						} else {
							check = false;
						}
					} else {
						vocAuthority[vocNum] = vocLevel;
						vocNum++;
					}
				}
				return maxLevel * vocLevel / 32768 + 1;
			} catch (Exception e) {
				if (handler != null){
					handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
				}
			}
		}
		return 1;
	}

	public void release() {
		if (null != mRecorder) {
			isPrepared = false;
			try {
				mRecorder.stop();
				mRecorder.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mRecorder = null;
		}
	}

	public void cancel() {
		release();
		if (mCurrentFilePathString != null) {
			File file = new File(mCurrentFilePathString);
			file.delete();
			mCurrentFilePathString = null;
		}
	}

	public String getCurrentFilePath() {
		return mCurrentFilePathString;
	}
}
