package com.axen.cloud.auth.skydrive;

import com.axen.cloud.auth.NormalFile;

public class SkyDriveFile extends NormalFile{

	private String parentId;
	private String uploadLocation;
	
	public SkyDriveFile(String id, String name, String path) {
		super(id, name, path);
	}

	public SkyDriveFile(){
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
}
