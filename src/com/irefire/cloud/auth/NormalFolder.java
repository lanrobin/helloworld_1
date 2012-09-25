package com.irefire.cloud.auth;

public class NormalFolder extends ICloudObject {
	
	private int count;
	
	public NormalFolder(String id, String name, String path) {
		super(id, TYPE_FOLDER, name, path);
	}
	
	public NormalFolder() {
		super();
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
