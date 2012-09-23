package com.axen.cloud.auth;

public interface IOauthPersister {
	/**
	 * 将一个授权后的对象保存到本地
	 * @param authObject
	 */
	public boolean persist(Object authObject);

	/**
	 * 从本地保存的信息中恢复一个授权的操作对象
	 * @param context
	 * @return
	 */
	public Object getOauthObject();
	
	String ACCESS_TOKEN_KEY = "access_token_key";
	String REFRESH_TOKEN_KEY = "reflesh_token_key";
	String AUTHENTICATION_TOKEN_KEY = "authentication_token_key";
	String SCOPES = "scopes";
	String EXPIRED_TIME = "expired_time";
	String AUTH_NAME = "auth_name";
	String TOKEN_TYPE = "token_type";
	String ENCODING = "UTF-8";
	String OPTIONAL = "optional";
}
