package com.axen.cloud.auth;

public class MiniFolder extends ICloudObject {
	
	private int count;

	public MiniFolder(String id,String name, String path) {
		super(id, TYPE_MINI_FOLDER, name, path);
	}

	public MiniFolder(){
		super();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
}
