package com.irefire.cloud.auth;

import com.irefire.cloud.auth.skydrive.SkyDriveOperation;

import android.content.Context;

public class OperationFactary {
	public static final IOperation createOperation(Context c) {
		return new SkyDriveOperation(c);
	}
}
