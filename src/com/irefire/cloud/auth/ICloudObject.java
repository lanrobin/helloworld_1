package com.irefire.cloud.auth;

public abstract class ICloudObject {
	
	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_FILE = "file";
	private String id;
	private String type;
	private String name;
	private String path;

	public ICloudObject(String id, String type,String name, String path) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.path = path;
	}
	
	public ICloudObject(){
		
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
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
