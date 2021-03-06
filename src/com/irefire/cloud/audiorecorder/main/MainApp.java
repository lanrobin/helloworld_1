package com.irefire.cloud.audiorecorder.main;

import com.irefire.cloud.auth.skydrive.SkyDriverPersister;
import com.irefire.mp3.LameWrapper;
import com.irefire.utils.L;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;

import android.app.Application;

public class MainApp extends Application {
	private static final String TAG = "MainApp";
	
	private Object mAuthClient = null;
	private Object mSession = null;
	private Object mLiveConnectClient = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		L.d(TAG, "Lame version:" + LameWrapper.getVersion());
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public void setAuthClient(Object authClient) {
		mAuthClient = authClient;
	}

	public void setSession(Object session) {
		mSession = session;
		if(mSession instanceof LiveConnectSession) {
			mLiveConnectClient = new LiveConnectClient((LiveConnectSession)mSession);
		}
	}

	public void setConnectClient(LiveConnectClient liveConnectClient) {
		mLiveConnectClient = liveConnectClient;
	}
	
	public Object getConnectClient() {
		return mLiveConnectClient;
	}

}
