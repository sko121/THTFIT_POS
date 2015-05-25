/*
MediastreamerActivity.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.linphone.mediastream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.capture.AndroidVideoApi5JniWrapper;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * Mediastreamer test activity.
 *
 */
public class VideoMonitor{
	public static final String TAG="VideoMonitor";
	
	public static final int VIDEO_STREAM_SEND_RECV=0;
	public static final int VIDEO_STREAM_SEND_ONLY=1;
	public static final int VIDEO_STREAM_RECV_ONLY=2;
	public static final short VIDEO_STREAM_DEFAULT_PORT=4050;
	private Thread mThread;
	private int mCameraId = 0;
	private String mVideoCodec = VP8_MIME_TYPE;
	private String mRemoteIp = "192.168.70.107";
	private short mRemotePort = VIDEO_STREAM_DEFAULT_PORT;
	private short mLocalPort = VIDEO_STREAM_DEFAULT_PORT;
	private int mBitrate = 256; 	
	private int mNativeObj=0;
	private int mVideoWidth=320;
	private int mVideoHight=240;
	private int mDeviceRotation=0;	
	private int mVideoStreamDir=VIDEO_STREAM_RECV_ONLY;		
	public static String VP8_MIME_TYPE = "103";	
	public static String JPEG_MIME_TYPE ="26";
	public static String H264_MIME_TYPE ="102";
	
	private Object mDestroyMutex= new Object();
	static {
		// FFMPEG (audio/video)
		/*loadOptionalLibrary("avutil");
		loadOptionalLibrary("swscale");
		loadOptionalLibrary("avcore");
		loadOptionalLibrary("avcodec");
		loadOptionalLibrary("srtp");
		loadOptionalLibrary("bcg729");
		loadOptionalLibrary("linphone");*/

		// FFMPEG (audio/video)
		loadOptionalLibrary("avutil");
		loadOptionalLibrary("swscale");
		loadOptionalLibrary("avcore");

		if (!hasNeonInCpuFeatures()) {
			boolean noNeonLibrariesLoaded = loadOptionalLibrary("avcodecnoneon");
			if (!noNeonLibrariesLoaded) {
				loadOptionalLibrary("avcodec");
			}
		} else {
			loadOptionalLibrary("avcodec");
		}

		// lin prefix avoids collision with libs in /system/lib
		loadOptionalLibrary("lincrypto");
		loadOptionalLibrary("linssl");

		// Secure RTP and key negotiation
		loadOptionalLibrary("srtp");

		// g729 A implementation
		loadOptionalLibrary("bcg729");

		//linphone
		loadOptionalLibrary("linphone");

		// Main library
		loadOptionalLibrary("videomonitor");
		setup();
	}
	public void configRemoteAddr(String remoteIPAddr,short remotePort){
		mRemoteIp=remoteIPAddr;
		mRemotePort=remotePort;
	};
	public void configLocalPort(short localPort)
	{
		mLocalPort=localPort;
	}
	public void configCameraID(int cameraID)
	{
		mCameraId=cameraID;
	}
	public void configBitrate(int bitrate){
		mBitrate=bitrate;
	}
	public void configVideoSize(int width,int hight){
		mVideoWidth=width;
		mVideoHight=hight;
	}
	public void configDeviceRotation(int rotation)
	{
		mDeviceRotation=rotation;
	}
	public void configVideoStreamDirect(int dir)
	{
		mVideoStreamDir=dir;
	}
	public void configVideoCodec(String codec){
		mVideoCodec = codec;;
	}
	
	public void configVideoWindowId(AndroidVideoWindowImpl vw)
	{
		synchronized (mDestroyMutex){
			if(mNativeObj==0)
				return;
			setVideoWindowId(vw, mNativeObj);
		}		
		
	}
	public VideoMonitor(){
		//mNativeObj = initDefaultArgs();
	}

	public void initVideoMonitor(){
	
		Log.i(TAG, "initVideoMonitor mRemoteIp:"+mRemoteIp);
		final List<String> args = new ArrayList<String>();
		args.add("prog_name");
		args.add("--local");
		args.add(Short.toString(mLocalPort));
		args.add("--remote");
		args.add(mRemoteIp + ":" + mRemotePort);
		args.add("--payload");
		args.add(mVideoCodec);
		args.add("--camera");
		args.add("Android" + mCameraId);
		// we pass device rotation as an argument (so mediastream.c can tell the videostream about it BEFORE it's configured)
		args.add("--device-rotation");
		
		args.add(Integer.toString(mDeviceRotation));
		args.add("--bitrate");
		args.add(Integer.toString(mBitrate*1000));
		// override default value from mediastream.c (but the actual resolution is limited by the encoder + bitrate)
		args.add("--width");
		args.add(Integer.toString(mVideoWidth));
		args.add("--height");
		args.add(Integer.toString(mVideoHight));
		switch(mVideoStreamDir){
		default:
		case VIDEO_STREAM_SEND_ONLY:
			args.add("--sendonly");
			break;
		case VIDEO_STREAM_SEND_RECV:
			args.add("--sendrecv");
			break;		
		case VIDEO_STREAM_RECV_ONLY:
			args.add("--recvonly");
			break;			
		}			
		String[] _args = new String[args.size()];
		synchronized (mDestroyMutex){
			
			if(mNativeObj!=0){
				stopMediaStream();
				clear(mNativeObj);
				mNativeObj=0;
			}			
			mNativeObj = initDefaultArgs();
			if(mNativeObj==0)
				return ;
			parseArgs(args.size(), args.toArray(_args), mNativeObj);
		}
		
	}
	
	public void startVideoMonitor(String strMsg) {	
		
		synchronized (mDestroyMutex){
			if(mNativeObj==0)
				return;
			setupMediaStreams(mNativeObj);
		}
		
	
	}
	
	public void  stopVideoMonitor(){
		//mVideoWindow.release();					
		Log.d(TAG, "Waiting for complete mediastremer destruction");
		synchronized (mDestroyMutex) {
			Log.d(TAG, "destroyed");
			stopMediaStream();
			if(mNativeObj==0)
				return;
			clear(mNativeObj);
			mNativeObj=0;
		}
	}

	private static boolean loadOptionalLibrary(String s) {
		try {
			System.loadLibrary(s);
			return true;
		} catch (Throwable e) {
			Log.w("Unable to load optional library lib", s);
		}
		return false;
	}

	public static boolean hasNeonInCpuFeatures() {
		ProcessBuilder cmd;
		boolean result = false;
		/*
		try {
			String[] args = { "/system/bin/cat", "/proc/cpuinfo" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[1024];
			while (in.read(re) != -1) {
				String line = new String(re);
				if (line.contains("Features")) {
					result = line.contains("neon");
					break;
				}
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return result;
		*/
		return true;
	}

	private native int initDefaultArgs();
	private native boolean parseArgs(int argc, String[] argv, int msObj); 
	private native void setupMediaStreams(int msObj); 
	private native void runLoop(int msObj); 
	private native void clear(int msObj);
	private native void stopMediaStream();
	
	private native void setVideoWindowId(Object wid, int msObj);
	private native void setVideoPreviewWindowId(Object wid, int msObj);
	private native void setDeviceRotation(int rotation, int msObj);
	private native void changeCamera(int newCameraId, int msObj);
	static private native int setup();
}
