package com.irefire.cloud.auth.skydrive;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.irefire.cloud.auth.ICloudObject;
import com.irefire.cloud.auth.Operation;
import com.irefire.utils.Config;
import com.irefire.utils.L;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveUploadOperationListener;

public class SkyDriveOperation extends Operation {

	private static final String TAG = "SkyDriveOperation";

	private LiveConnectClient mClient = null;
	private LiveConnectSession mSession = null;

	private static final int T_LS = 0;
	private static final int T_CREATE = 1;
	private static final int T_UPLOAD = 2;

	public SkyDriveOperation(Context c) {
		super(c);

		mSession = (LiveConnectSession) new SkyDriverPersister(c)
				.getOauthObject();
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
		if (TextUtils.isEmpty(path)) {
			realPath = Config.SKYDRIVE_HOME_FOLDER + "/files";
		} else {
			realPath = path + "/files";
		}

		if (mClient != null) {
			mClient.getAsync(realPath, new Parser(T_LS, l));
		}
	}

	@Override
	public void create(ICloudObject parent, String name, String description,
			int type, IOperationListener l) {
		if (!TextUtils.isEmpty(name) && parent != null) {
			if (TYPE_FOLDER == type) {
				Map<String, String> folder = new HashMap<String, String>();
				folder.put(SkydriveJsonKey.NAME, name);
				folder.put(SkydriveJsonKey.DESCRIPTION, description);
				if (mClient != null) {
					mClient.putAsync(parent.getId(), new JSONObject(folder),
							new Parser(T_CREATE, l));
				} else {
					if (l != null) {
						l.onCreatedFinished(null,
								IOperationListener.ERROR_CODE_AUTH_FAILED,
								"mClient is null.");
					}
				}
			} else {
				if (l != null) {
					l.onCreatedFinished(null,
							IOperationListener.ERROR_CODE_INVALID_PARAM,
							"unsupported type:" + type);
				}
			}
		} else {
			if (l != null) {
				l.onCreatedFinished(null,
						IOperationListener.ERROR_CODE_INVALID_PARAM,
						"param name is null.");
			}
		}
	}

	@Override
	public void delete(String name, int type, IOperationListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void upload(ICloudObject parent, String name, InputStream is,
			IOperationListener l) {
		if (parent != null && !TextUtils.isEmpty(name) && is != null) {
			if (mClient != null) {
				mClient.uploadAsync(parent.getId(), name, is, new Parser(
						T_UPLOAD, l));
			} else {
				if (l != null) {
					l.onUploading(null, 0, 0,
							IOperationListener.ERROR_CODE_AUTH_FAILED,
							"mClient is null.");
				}
			}
		} else {
			if (l != null) {
				l.onUploading(null, 0, 0,
						IOperationListener.ERROR_CODE_INVALID_PARAM,
						"params error.");
			}
		}
	}

	@Override
	public void upload(ICloudObject parent, String name, File file,
			IOperationListener l) {
		if (parent != null && file != null) {
			if(TextUtils.isEmpty(name)) {
				name = file.getName();
			}
			if (mClient != null) {
				mClient.uploadAsync(parent.getId(), name, file, new Parser(
						T_UPLOAD, l));
			} else {
				if (l != null) {
					l.onUploading(null, 0, 0,
							IOperationListener.ERROR_CODE_AUTH_FAILED,
							"mClient is null.");
				}
			}
		} else {
			if (l != null) {
				l.onUploading(null, 0, 0,
						IOperationListener.ERROR_CODE_INVALID_PARAM,
						"params error.");
			}
		}
	}

	@Override
	public void download(String name, OutputStream os, IOperationListener l) {
	}

	@Override
	public void rename(String name, IOperationListener l) {
		// TODO Auto-generated method stub

	}

	private class Parser implements LiveOperationListener,
			LiveUploadOperationListener {

		private static final String TAG = "SkyDriveOperation.Parser";
		private int type;
		private IOperationListener l;

		public Parser(int type, IOperationListener l) {
			this.type = type;
			this.l = l;
		}

		@Override
		public void onComplete(LiveOperation op) {
			switch (type) {
			case T_LS:
				parseLs(op);
				break;
			case T_CREATE:
				parseCreate(op);
				break;
			}
		}

		@Override
		public void onError(LiveOperationException e, LiveOperation op) {
			switch (type) {
			case T_LS:
				break;
			}
		}

		private void parseLs(LiveOperation op) {
			JSONObject result = op.getResult();

			if (result.has(SkydriveJsonKey.ERROR)) {
				JSONObject error = result.optJSONObject(SkydriveJsonKey.ERROR);
				String message = error.optString(SkydriveJsonKey.MESSAGE);
				String code = error.optString(SkydriveJsonKey.CODE);
				if (l != null) {
					l.onListFinished(null,
							IOperationListener.ERROR_CODE_UNKNOWN, "code:"
									+ code + ", message:" + message);
				}
				return;
			}

			ArrayList<ICloudObject> list = new ArrayList<ICloudObject>();

			try {
				JSONArray files = result.optJSONArray(SkydriveJsonKey.DATA);
				L.d(TAG, "" + files);
				for (int i = 0; i < files.length(); i++) {
					JSONObject file = files.getJSONObject(i);
					String type = file.getString(SkydriveJsonKey.TYPE);
					if ("folder".equalsIgnoreCase(type)
							|| "album".equalsIgnoreCase(type)) {
						SkyDriveFolder f = SkyDriveFolder.fronJson(file);
						if (f != null) {
							list.add(f);
						}
					} else if ("file".equalsIgnoreCase(type)) {
						SkyDriveFile f = SkyDriveFile.fromJson(file);
						if (f != null) {
							list.add(f);
						}
					}
					L.d(TAG, "file:" + file.getString(SkydriveJsonKey.NAME)
							+ ", type =" + file.getString(SkydriveJsonKey.TYPE));
				}

				if (l != null) {
					l.onListFinished(list, IOperationListener.ERROR_CODE_OK, "");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		private void parseCreate(LiveOperation op) {
			JSONObject result = op.getResult();

			if (result.has(SkydriveJsonKey.ERROR)) {
				JSONObject error = result.optJSONObject(SkydriveJsonKey.ERROR);
				String message = error.optString(SkydriveJsonKey.MESSAGE);
				String code = error.optString(SkydriveJsonKey.CODE);
				if (l != null) {
					l.onCreatedFinished(null,
							IOperationListener.ERROR_CODE_UNKNOWN, "code:"
									+ code + ", message:" + message);
				}
				return;
			} else {
				try {
					SkyDriveFolder f = SkyDriveFolder.fronJson(result);
					if (f != null) {
						if (l != null) {
							l.onCreatedFinished(f,
									IOperationListener.ERROR_CODE_OK,
									"Folder created.");
						}
					} else {
						if (l != null) {
							l.onCreatedFinished(
									f,
									IOperationListener.ERROR_CODE_UNKNOWN,
									"Folder failed, with json:"
											+ result.toString());
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onUploadCompleted(LiveOperation op) {
			if (l != null) {
				JSONObject file = op.getResult();
				try {
					SkyDriveFile f = SkyDriveFile.fromJson(file);
					if (f != null) {
						if (l != null) {
							l.onUploading(f, 0, 0,
									IOperationListener.ERROR_CODE_OK,
									"file uploaded.");
						}
					} else {
						if (l != null) {
							l.onUploading(
									null,
									0,
									0,
									IOperationListener.ERROR_CODE_UNKNOWN,
									"Folder failed, with json:"
											+ file.toString());
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onUploadFailed(LiveOperationException ex, LiveOperation op) {

		}

		@Override
		public void onUploadProgress(int totalBytes, int bytesRemaining,
				LiveOperation op) {
			if (l != null) {
				l.onUploading(null, totalBytes, totalBytes - bytesRemaining,
						IOperationListener.ERROR_CODE_INPROGRESSING,
						"Uploading.");
			}
		}
	}
}
