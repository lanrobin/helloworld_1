package com.irefire.cloud.auth.skydrive;

import org.json.JSONException;
import org.json.JSONObject;

import com.irefire.cloud.auth.ICloudObject;
import com.irefire.cloud.auth.NormalFile;
import com.irefire.utils.Utils;

public class SkyDriveFile extends NormalFile {

	private String parentId;
	private String uploadLocation;

	public SkyDriveFile(String id, String name, String path) {
		super(id, name, path);
	}

	public SkyDriveFile() {
		super();
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getUploadLocation() {
		return uploadLocation;
	}

	public void setUploadLocation(String uploadLocation) {
		this.uploadLocation = uploadLocation;
	}

	public static SkyDriveFile fromJson(JSONObject file) throws JSONException {
		if (file == null) {
			return null;
		}
		String type = file.getString(SkydriveJsonKey.TYPE);
		if ("file".equalsIgnoreCase(type)) {
			SkyDriveFile f = new SkyDriveFile();
			f.setName(file.getString(SkydriveJsonKey.NAME));
			f.setPath(file.getString(SkydriveJsonKey.LINK));
			f.setType(ICloudObject.TYPE_FILE);
			f.setId(file.getString(SkydriveJsonKey.ID));
			f.setSource(file.getString(SkydriveJsonKey.SOURCE));
			f.setSize(Utils.parseIntString(file.getString(SkydriveJsonKey.SIZE)));
			f.setParentId(file.getString(SkydriveJsonKey.PARENT_ID));
			f.setUploadLocation(file.getString(SkydriveJsonKey.UPLOAD_LOCATION));
			return f;
		} else {
			return null;
		}
	}
}
