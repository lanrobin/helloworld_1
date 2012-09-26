package com.irefire.encrypt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.irefire.utils.L;
import com.irefire.utils.SdcardUtil;

public class NoEncryptWriter implements IEncrypt {

	private static final String TAG = "NoEncryptWriter";
	private FileOutputStream mfos = null;
	
	public NoEncryptWriter() {
	}
	
	@Override
	public boolean startNewFile() {
		boolean ret = false;
		try {
			mfos = new FileOutputStream(SdcardUtil.getStoreFileName(SdcardUtil.FILE_TYPE_MP3));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(mfos != null) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public int write(byte[] data) {
		if(data == null || data.length < 1) {
			L.d(TAG, "data is empty.");
		}
		return write(data, 0, data.length);
	}

	@Override
	public int write(byte[] data, int offset, int len) {
		int ret = 0;
		if(data == null || data.length < 1) {
			L.d(TAG, "data is empty.");
			ret = 0;
		}else if(data.length < offset + len) {
			String msg = "OutofBoudnd, the data( length = "+ data.length + ", offset = " + offset +", len = " + len +")";
			L.d(TAG, msg);
			throw new ArrayIndexOutOfBoundsException(TAG + "write:" + msg);
		}else {
			if(mfos != null) {
				try {
					mfos.write(data, offset, len);
					ret = len;
				} catch (IOException e) {
					e.printStackTrace();
					ret = 0;
				}
			}else {
				L.w(TAG, "Try to write to a closed stream.");
			}
		}
		return ret;
	}

	@Override
	public void endNewFile() {
		if(mfos != null) {
			try {
				mfos.flush();
				mfos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				mfos = null;
			}
		}
	}

}
