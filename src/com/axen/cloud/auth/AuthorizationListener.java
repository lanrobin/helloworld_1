package com.axen.cloud.auth;

import java.util.Map;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

public interface AuthorizationListener {
	
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFLESH_TOKEN = "reflesh_token";
	public static final String LISTENER_TYPE = "listener_type";
	public static final String LOAD_URL = "load_url";
	
	public static final String LISTENER_TYPE_BOX = "type_box";
	
	
	public void onPageStarted(WebView view, String url, Bitmap favicon);
	public void onPageFinished(WebView view, String url);
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl);
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error);
	
	
}
