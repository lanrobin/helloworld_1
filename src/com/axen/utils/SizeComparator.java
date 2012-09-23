package com.axen.utils;

import java.util.Comparator;

import android.hardware.Camera.Size;

public class SizeComparator implements Comparator<Size> {

	@Override
	public int compare(Size lhs, Size rhs) {
		if(lhs != null) {
			if(lhs == rhs || lhs.equals(rhs)) {
				return 0;
			}else if(rhs == null) { 
				return 1;
			}else {
				long left = lhs.height * lhs.width;
				long right = rhs.height * rhs.width;
				return (int) (left - right);
			}
		}else {
			if(rhs == null) {
				return 0;
			}else {
				return -1;
			}
		}
	}

}
