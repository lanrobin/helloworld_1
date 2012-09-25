package com.irefire.cloud.audiorecorder.ui;

import com.irefire.cloud.audiorecorder.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.settings);
	}

}
