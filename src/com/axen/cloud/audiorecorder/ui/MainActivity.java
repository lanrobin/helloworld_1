package com.axen.cloud.audiorecorder.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.axen.cloud.audiorecorder.R;
import com.axen.cloud.audiorecorder.audio.AudioRecordThread;
import com.axen.cloud.audiorecorder.main.MainApp;
import com.axen.cloud.auth.SkydriveJsonKey;
import com.axen.cloud.auth.ui.SkyDriveSignInActivity;
import com.axen.utils.Config;
import com.axen.utils.L;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	private static final int AUTH_REQUEST_CODE = 100;

	protected static final String TAG = "MainActivity";
	
	private Button mStartButton = null;
	private Button mStopButton = null;
	private Button mAuthButton = null;
	private Button mTestButton = null;
	private AudioRecordThread mAudioRecordThread = AudioRecordThread.getInstance();
	
	private MainApp mApp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mApp = (MainApp)getApplication();
        
        mStartButton = (Button)findViewById(R.id.main_button_start_recording);
        mStopButton = (Button)findViewById(R.id.main_button_stop_recording);
        mAuthButton = (Button)findViewById(R.id.main_button_start_auth);
        mTestButton = (Button)findViewById(R.id.main_button_test);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mAuthButton.setOnClickListener(this);
        mTestButton.setOnClickListener(this);
        mAudioRecordThread.init();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_button_start_recording:
			mAudioRecordThread.start();
			break;
		case R.id.main_button_stop_recording:
			mAudioRecordThread.stopRecording();
			break;
		case R.id.main_button_start_auth:
			Intent intent = new Intent(this, SkyDriveSignInActivity.class);
	        startActivityForResult(intent, AUTH_REQUEST_CODE);
			break;
		case R.id.main_button_test:
			LiveConnectClient client = (LiveConnectClient)mApp.getConnectClient();
			
			if(client != null) {
				client.getAsync(Config.SKYDRIVE_HOME_FOLDER + "/files", new LiveOperationListener() {

					@Override
					public void onComplete(LiveOperation op) {
						// L.d(TAG, op.getResult().toString());
						JSONObject result = op.getResult();
						
						if(result.has(SkydriveJsonKey.ERROR)) {
							JSONObject error = result.optJSONObject(SkydriveJsonKey.ERROR);
		                    String message = error.optString(SkydriveJsonKey.MESSAGE);
		                    String code = error.optString(SkydriveJsonKey.CODE);
						}
						
						try {
							JSONArray files = result.optJSONArray(SkydriveJsonKey.DATA);
							for(int i = 0; i < files.length(); i ++) {
								JSONObject file = files.getJSONObject(i);
								L.d(TAG, "file:" + file.getString(SkydriveJsonKey.NAME) +", type =" + file.getString(SkydriveJsonKey.TYPE));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}

					@Override
					public void onError(LiveOperationException ope,
							LiveOperation op) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
			break;
		default:
			break;
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
/*        if (requestCode == AUTH_REQUEST_CODE) {
            if (resultCode == BoxAuthentication.AUTH_RESULT_SUCCESS) {
                // Store auth key in shared preferences.
                // BoxAuthentication activity will set the auth key into the
                // resulting intent extras, keyed as AUTH_TOKEN
                // final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
                // final SharedPreferences.Editor editor = prefs.edit();
            	final String authToken  = data.getStringExtra("AUTH_TOKEN");
            	if(!TextUtils.isEmpty(authToken)) {
            		Box box = Box.getInstance(BoxConstants.API_KEY);
            		// box.getAccountTree(authToken, 01, params, listener)
            	}
            	L.d("BOX_AUTH", authToken);
                // editor.putString(Constants.PREFS_KEY_AUTH_TOKEN, data.getStringExtra("AUTH_TOKEN"));
                // editor.commit();
                // finish();
            } else if (resultCode == BoxAuthentication.AUTH_RESULT_FAIL) {
                Toast.makeText(getApplicationContext(), "Unable to log into Box", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                finish();
            }
        }*/
    }
}