package com.irefire.encrypt;

public interface IEncrypt {
	boolean startNewFile();

	int write(byte[] data);

	int write(byte[] data, int offset, int len);

	void endNewFile();
}
