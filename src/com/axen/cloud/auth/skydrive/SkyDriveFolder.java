package com.axen.cloud.auth.skydrive;

import com.axen.cloud.auth.NormalFolder;

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

	
}
