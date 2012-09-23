package com.axen.cloud.auth;

import com.axen.utils.L;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthorizationWebViewClient extends WebViewClient {

	private static final String TAG = "AuthorizationWebViewClient";
	private AuthorizationListener mListener;
	
	public AuthorizationWebViewClient(AuthorizationListener listener) {
		mListener = listener;
	}
	
	
	
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if(mListener != null) {
			mListener.onPageStarted(view, url, favicon);
		}
	}




	@Override
	public void onPageFinished(WebView view, String url) {
		L.d(TAG, "onPageFinished url:" + url);
		if(mListener != null) {
			mListener.onPageFinished(view, url);
		}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		if(mListener != null) {
			mListener.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		if(mListener != null) {
			mListener.onReceivedSslError(view, handler, error);
		}
	}
}
