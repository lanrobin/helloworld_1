package com.axen.cloud.auth.ui;

import java.util.Arrays;

import com.axen.cloud.audiorecorder.R;
import com.axen.cloud.audiorecorder.main.MainApp;
import com.axen.cloud.auth.SkyDriverPersister;
import com.axen.utils.Config;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SkyDriveSignInActivity extends Activity implements
		LiveAuthListener {
	private MainApp mApp;
	private LiveAuthClient mAuthClient;
	private ProgressDialog mInitializeDialog;
	private Button mSignInButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skydrive_signin);

		mApp = (MainApp) getApplication();
		mAuthClient = new LiveAuthClient(mApp, Config.SKYDRIVE_CLIENT_ID);
		mApp.setAuthClient(mAuthClient);

		mInitializeDialog = ProgressDialog.show(this, "",
				getString(R.string.sign_in_page_is_loading), true);

		mSignInButton = (Button) findViewById(R.id.signInButton);

		mSignInButton.setVisibility(View.GONE);

		mSignInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startSignIn();
			}
		});

		// first time start sign in automatically.
		startSignIn();
	}

	private void startSignIn() {
		mAuthClient.login(SkyDriveSignInActivity.this,
				Arrays.asList(Config.SKYDRIVE_SCOPES), this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// mAuthClient.initialize(Arrays.asList(Config.SKYDRIVE_SCOPES), this);
	}

	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	private void showSignIn() {
		mSignInButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onAuthError(LiveAuthException exception, Object userState) {
		mInitializeDialog.dismiss();
		showSignIn();
		showToast(exception.getMessage());
	}

	@Override
	public void onAuthComplete(LiveStatus status, LiveConnectSession session,
			Object userState) {
		mInitializeDialog.dismiss();
		if (status == LiveStatus.CONNECTED) {
			mApp.setSession(session);
			
			saveSession(session);
			
			finish();
		} else {
			showSignIn();
			showToast(getString(R.string.sign_in_failed));
		}
		
	}
	
	private void saveSession(final LiveConnectSession session) {
		new Thread("skydrivesavesessionthread") {

			@Override
			public void run() {
				SkyDriverPersister.getInstance(mApp).persist(session);
			}
			
		}.start();
	}
}
