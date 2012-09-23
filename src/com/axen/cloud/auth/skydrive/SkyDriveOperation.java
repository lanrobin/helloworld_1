package com.axen.cloud.auth.skydrive;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.axen.cloud.auth.ICloudObject;
import com.axen.cloud.auth.Operation;
import com.axen.utils.Config;
import com.axen.utils.L;
import com.axen.utils.Utils;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;

public class SkyDriveOperation extends Operation {
	
	private static final String TAG = "SkyDriveOperation";
	
	private LiveConnectClient mClient = null;
	private LiveConnectSession mSession = null;
	
	private static final int T_LS = 0;
	private static final int T_CREATE = 1;

	public SkyDriveOperation(Context c) {
		super(c);
		
		mSession = (LiveConnectSession) new SkyDriverPersister(c).getOauthObject();
		mClient = new LiveConnectClient(mSession);
	}
	/**
	 * 
	 * @return
	 */
	public LiveConnectClient getClient() {
		return mClient;
	}

	@Override
	public void ls(String path, IOperationListener l) {
		String realPath;
		if(TextUtils.isEmpty(path)) {
			realPath = Config.SKYDRIVE_HOME_FOLDER + "/files";
		}else {
			realPath = path +"/files";
		}
		
		if(mClient != null) {
			mClient.getAsync(realPath, new Parser(T_LS, l));
		}
	}

	@Override
	public void create(String name, int type, IOperationListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String name, int type, IOperationListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void upload(String name, InputStream is, IOperationListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void download(String name, OutputStream os, IOperationListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String name, IOperationListener l) {
		// TODO Auto-generated method stub

	}

	private class Parser implements LiveOperationListener {

		private static final String TAG = "SkyDriveOperation.Parser";
		private int type;
		private IOperationListener l;
		
		public Parser(int type, IOperationListener l) {
		   this.type = type;
		   this.l = l;
		}
		
		@Override
		public void onComplete(LiveOperation op) {
			switch(type) {
			case T_LS:
				parseLs(op);
				break;
			}
		}

		@Override
		public void onError(LiveOperationException e, LiveOperation op) {
			switch(type) {
			case T_LS:
				break;
			}
		}
		
		private void parseLs(LiveOperation op) {
			JSONObject result = op.getResult();
			
			if(result.has(SkydriveJsonKey.ERROR)) {
				JSONObject error = result.optJSONObject(SkydriveJsonKey.ERROR);
                String message = error.optString(SkydriveJsonKey.MESSAGE);
                String code = error.optString(SkydriveJsonKey.CODE);
                if(l != null) {
                	l.onListFinished(null, IOperationListener.ERROR_CODE_UNKNOWN, "code:" + code +", message:" + message);
                }
                return;
			}
			
			ArrayList<ICloudObject> list = new ArrayList<ICloudObject>();
			
			try {
				JSONArray files = result.optJSONArray(SkydriveJsonKey.DATA);
				L.d(TAG, "" + files);
				for(int i = 0; i < files.length(); i ++) {
					JSONObject file = files.getJSONObject(i);
					String type = file.getString(SkydriveJsonKey.TYPE);
					if("folder".equalsIgnoreCase(type)) {
						SkyDriveFolder f = new SkyDriveFolder();
						f.setName(file.getString(SkydriveJsonKey.NAME));
						f.setPath(file.getString(SkydriveJsonKey.LINK));
						f.setType(ICloudObject.TYPE_FOLDER);
						f.setId(file.getString(SkydriveJsonKey.ID));
						f.setParentId(file.getString(SkydriveJsonKey.PARENT_ID));
						f.setDescription(file.getString(SkydriveJsonKey.DESCRIPTION));
						f.setEmbeddable(Utils.parseBooleanString(file.getString(SkydriveJsonKey.IS_EMBEDDABLE)));
						f.setCount(Utils.parseIntString(file.getString(SkydriveJsonKey.COUNT)));
						f.setUploadLocation(file.getString(SkydriveJsonKey.UPLOAD_LOCATION));
						list.add(f);
					}else if("file".equalsIgnoreCase(type)) {
						SkyDriveFile f = new SkyDriveFile();
						f.setName(file.getString(SkydriveJsonKey.NAME));
						f.setPath(file.getString(SkydriveJsonKey.LINK));
						f.setType(ICloudObject.TYPE_FILE);
						f.setId(file.getString(SkydriveJsonKey.ID));
						f.setSource(file.getString(SkydriveJsonKey.SOURCE));
						f.setSize(Utils.parseIntString(file.getString(SkydriveJsonKey.SIZE)));
						f.setParentId(file.getString(SkydriveJsonKey.PARENT_ID));
						f.setUploadLocation(file.getString(SkydriveJsonKey.UPLOAD_LOCATION));
						list.add(f);
					}
					L.d(TAG, "file:" + file.getString(SkydriveJsonKey.NAME) +", type =" + file.getString(SkydriveJsonKey.TYPE));
				}
				
				if(l != null) {
					l.onListFinished(list, IOperationListener.ERROR_CODE_OK, "");
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
