package com.axen.cloud.auth;

public abstract class ICloudObject {
	
	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_FILE = "file";
	public static final String TYPE_MINI_FOLDER = "mini_folder";
	private String id;
	private String type;
	private String name;
	
	public ICloudObject(String id, String type,String name) {
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
