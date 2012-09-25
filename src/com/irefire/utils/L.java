package com.irefire.utils;

import android.util.Log;

public class L {

	/**
	 * 是不是要DEBUG；
	 */
	private static final boolean DEBUG = true;

	/**
	 * 封闭Log.d，以使用方便，并且能在Release的时候去掉。
	 * 
	 * @param TAG
	 * @param msg
	 */
	public static void d(String TAG, String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	/**
	 * 
	 */
	public static void w(String TAG, String msg) {
		Log.w(TAG, msg);
	}

	public static void d(String TAG, byte[] data) {
		if (DEBUG) {
			Log.d(TAG, "byte[]:");
			if (data == null || data.length < 1) {
				Log.d(TAG, "data is null or length is 0.");
			}else {
				StringBuilder builder = new StringBuilder();
				for(int i = 0; i < data.length; i ++) {
					builder.append(String.format("%02X ", data[i]));
					
					if(i != 0 && i % 16 == 0) {
						builder.append("\n");
					}
				}
				
				Log.d(TAG, builder.toString());
			}
		}
	}
	
	public static void d(String TAG, byte[] data, int pos, int len) {
		byte[] d = new byte[len];
		System.arraycopy(data, pos, d, 0, len);
		d(TAG, d);
	}
}
