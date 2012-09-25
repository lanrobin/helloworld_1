package com.irefire.utils;

import android.content.Context;
import android.content.res.Resources;

public class ResourceUtil {

	public int  getPexil(int resId) {
		int value = mRes.getDimensionPixelSize(resId);
		// return (int)(value * mDensity + 0.5f);
		return value;
	}
	
	public float getDimen(int resId) {
		return mRes.getDimension(resId);
	}
	
	public ResourceUtil(Context c) {
		mContext = c;
		mRes = c.getResources();
		mDensity = mRes.getDisplayMetrics().density;
	}
	
	private Resources mRes = null;
	private Context mContext = null;
	private float mDensity = 0;
}
