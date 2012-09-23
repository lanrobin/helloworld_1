package com.axen.cloud.auth;

public class NormalFile extends ICloudObject {

	private String source;
	private long size;
	
	public NormalFile(String id, String name, String path) {
		super(id, TYPE_FILE, name, path);
	}
	
	public NormalFile() {
		super();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	
}
