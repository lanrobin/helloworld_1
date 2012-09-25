package com.irefire.cloud.auth.skydrive;

import org.json.JSONException;
import org.json.JSONObject;

import com.irefire.cloud.auth.ICloudObject;
import com.irefire.cloud.auth.NormalFolder;
import com.irefire.utils.Utils;

public class SkyDriveFolder extends NormalFolder {
	
	private String parentId;
	private String description;
	private String uploadLocation;
	private boolean isEmbeddable;

	public SkyDriveFolder(String id, String name, String path) {
		super(id, name, path);
	}

	public SkyDriveFolder() {
		super();
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUploadLocation() {
		return uploadLocation;
	}

	public void setUploadLocation(String uploadLocation) {
		this.uploadLocation = uploadLocation;
	}

	public boolean isEmbeddable() {
		return isEmbeddable;
	}

	public void setEmbeddable(boolean isEmbeddable) {
		this.isEmbeddable = isEmbeddable;
	}

	public static SkyDriveFolder fronJson(JSONObject file) throws JSONException {
		if(file != null) {
			String type = file.getString(SkydriveJsonKey.TYPE);
			if ("folder".equalsIgnoreCase(type)
					|| "album".equalsIgnoreCase(type)) {
				SkyDriveFolder f = new SkyDriveFolder();
				f.setName(file.getString(SkydriveJsonKey.NAME));
				f.setPath(file.getString(SkydriveJsonKey.LINK));
				f.setType(ICloudObject.TYPE_FOLDER);
				f.setId(file.getString(SkydriveJsonKey.ID));
				f.setParentId(file.getString(SkydriveJsonKey.PARENT_ID));
				f.setDescription(file
						.getString(SkydriveJsonKey.DESCRIPTION));
				f.setEmbeddable(Utils.parseBooleanString(file
						.getString(SkydriveJsonKey.IS_EMBEDDABLE)));
				f.setCount(Utils.parseIntString(file
						.getString(SkydriveJsonKey.COUNT)));
				f.setUploadLocation(file
						.getString(SkydriveJsonKey.UPLOAD_LOCATION));
				return f;
			}
		}
		return null;
	}
}
