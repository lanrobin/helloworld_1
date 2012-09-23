package com.axen.cloud.auth;

import java.io.InputStream;
import java.io.OutputStream;

public interface IOperation {
	
	int TYPE_FILE = 0;
	int TYPE_FOLDER = 1;
	
	/**
	 * 返回一个目录下的所有对象，可能是文件或是文件夹。
	 * @param path 目录名称，如果没有传或是空字符或是null表示根目录
	 * @param l 因为是异步的操作，所以要一个监听器来获得反馈结果。
	 * @return 一个Iterable的
	 */
	void ls(String path, IOperationListener l);
	
	/**
	 * 创建一个目录或是文件。
	 * @param name 文件或是目录名称
	 * @param type 类型,TYPE_FILE/TYPE_FOLDER只有这两种
	 * @param l
	 */
	void create(String name, int type, IOperationListener l);
	
	/**
	 * 删除文件
	 * @param name
	 * @param type
	 * @param l
	 */
	void delete(String name, int type, IOperationListener l);
	
	/**
	 * 上传文件
	 * @param name
	 * @param is
	 * @param l
	 */
	void upload(String name, InputStream is, IOperationListener l);
	
	/**
	 * 下载文件
	 * @param name
	 * @param os
	 * @param l
	 */
	void download(String name, OutputStream os, IOperationListener l);
	
	/**
	 * 重命名。
	 * @param name
	 * @param l
	 */
	void rename(String name, IOperationListener l);
	
	public interface IOperationListener {
		int ERROR_CODE_OK = 0;
		int ERROR_CODE_AUTH_FAILED = 1;
		int ERROR_CODE_INPROGRESSING = 2;
		int ERROR_CODE_TYPE_ERR = 3;
		int ERROR_CODE_UNKNOWN = -1;
		
		void onListFinished(Iterable<? extends ICloudObject> list, int errcode, String msg);
		void onCreatedFinished(String path, int errcode, String msg);
		void onDeleteFinished(int errcode, String msg);
		void onUploading(String path, long total, long cur, int errcode, String msg);
		void onDownloading(String path, long total, long cur, int errcode, String msg);
		void onRename(String curPath, String prePath, int errcode, String msg);
	}
}
