package com.axen.cloud.auth;

import android.content.Context;

public abstract class Operation implements IOperation {
    protected Context mContext = null;
    
    public Operation(Context c) {
    	mContext = c;
    }
}
