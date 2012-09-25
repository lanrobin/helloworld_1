package com.irefire.cloud.audiorecorder.ui;

import com.irefire.cloud.audiorecorder.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AudioRecordActivity extends Activity implements OnClickListener {

	private TextView mTimerTextView;
	private Button mStartButton;
	private Button mSettingsButton;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0x01:
				mTimerTextView.setText((CharSequence)msg.obj);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_record_activity);
		
		mTimerTextView = (TextView)findViewById(R.id.audio_record_timer_textview);
		
		mStartButton = (Button)findViewById(R.id.audio_record_start_button);
		mSettingsButton = (Button)findViewById(R.id.audio_record_settings_button);
		
		mStartButton.setOnClickListener(this);
		mSettingsButton.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.audio_record_settings_button:
			break;
		case R.id.audio_record_start_button:
			break;
		default:
			break;
		}
	}
}
