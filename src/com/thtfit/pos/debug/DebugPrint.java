package com.thtfit.pos.debug;

import android.util.Log;

public class DebugPrint
{
	private String TAG = null;
	private static boolean DEBUG = false;

	public DebugPrint(boolean isDebug, String TAG)
	{
		DEBUG = isDebug;
		this.TAG = TAG;
	}

	public void D(String msg)
	{
		if (DEBUG)
		{
			Log.d(TAG, msg);
		}
	}

	public void E(String msg)
	{
		if (DEBUG)
		{
			Log.e(TAG, msg);
		}
	}

	public void V(String msg)
	{
		if (DEBUG)
		{
			Log.v(TAG, msg);
		}
	}

	public void W(String msg)
	{
		if (DEBUG)
		{
			Log.w(TAG, msg);
		}
	}
}
