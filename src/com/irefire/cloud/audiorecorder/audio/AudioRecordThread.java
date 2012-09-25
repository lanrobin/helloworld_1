package com.irefire.cloud.audiorecorder.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.irefire.mp3.LameWrapper;
import com.irefire.utils.L;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioRecordThread extends Thread {
	
	private static final String TAG = "AudioRecordThread";
	
	
	private static final int BUFFER_SIZE = 44100; //4410个short,就是一秒的数据
	
	
	
	private static class Holder {
		public static AudioRecordThread _INSTANCE = new AudioRecordThread();
	}
	
	public static AudioRecordThread getInstance() {
		return Holder._INSTANCE;
	}
	
	private AudioRecordThread() {
		mBuffer = new short[BUFFER_SIZE];
	}
	
	public void init() {
		mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE * 2);
	}
	
	@Override
	public void run() {
		while(true) {
			if(!mLameInitialized) {
				mLameInitialized = true;
				try {
					mRecordOutputStream = new FileOutputStream("/sdcard/test"+System.currentTimeMillis()+ ".mp3");
				}catch(FileNotFoundException ex) {
					ex.printStackTrace();
					return;
				}
				
				LameWrapper.init(mSampleRate, 1, 0, 2);
				LameWrapper.prepare();
				mRecord.startRecording();
			}
			
			if(mStopRecording) {
				mRecord.stop();
			}
			
			
			int len = mRecord.read(mBuffer, 0, BUFFER_SIZE);
			if(len > 0) {
				L.d(TAG, "Read " + len +" short array.");
				byte[] data = LameWrapper.encodeBuffer(mBuffer, null, len);
				L.d(TAG, "Try to write " + data.length + " bytes.");
				try {
					mRecordOutputStream.write(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				L.d(TAG, "AudioRecord read error:" + len);
			}
			
			if(mStopRecording) {
				mRecord.release();
				break;
			}
		}
		
		
		if(mRecordOutputStream != null) {
			byte[] data = LameWrapper.encodeFlush();
			L.d(TAG, "Flush and try to write " + data.length + " bytes.");
			try {
				mRecordOutputStream.write(data);
				mRecordOutputStream.flush();
				mRecordOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		mStopRecording = false;
		L.d(TAG, "Recording finished.");
	}
	
	public void stopRecording() {
		mStopRecording = true;
	}
	
	private AudioRecord mRecord;
	private int mSampleRate = 44100;
	private short[] mBuffer;
	private boolean mStopRecording = false;
	private boolean mLameInitialized = false;
	private OutputStream mRecordOutputStream = null;
}
