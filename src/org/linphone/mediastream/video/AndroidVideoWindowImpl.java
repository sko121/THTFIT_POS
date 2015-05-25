/*
AndroidVideoWindowImpl.java
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
package org.linphone.mediastream.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.linphone.mediastream.video.display.OpenGLESDisplay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceHolder.Callback;
import org.linphone.mediastream.video.display.GL2JNIView;
import android.graphics.Rect;
import java.nio.IntBuffer;
import android.opengl.GLException;
import android.os.SystemClock;

public class AndroidVideoWindowImpl {
	public final static String TAG = "AndroidVideoWindowImpl";
	private long mLocalThreadId = 0;
	private SurfaceView mVideoRenderingView;
	private SurfaceView mVideoPreviewView;

	private boolean useGLrendering;
	private Bitmap mBitmap;

	private Surface mSurface;
	private VideoWindowListener mListener;
	private Renderer renderer;

	/**
	 * Utility listener interface providing callback for Android events useful
	 * to Mediastreamer.
	 */
	public static interface VideoWindowListener {
		void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw,
				SurfaceView surface);

		void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw);

		void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw,
				SurfaceView surface);

		void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw);
	};

	/**
	 * @param renderingSurface
	 *            Surface created by the application that will be used to render
	 *            decoded video stream
	 * @param previewSurface
	 *            Surface created by the application used by Android's Camera
	 *            preview framework
	 */
	public AndroidVideoWindowImpl(SurfaceView renderingSurface,
			SurfaceView previewSurface) {
		mLocalThreadId = Thread.currentThread().getId();
		mVideoRenderingView = renderingSurface;
		mVideoPreviewView = previewSurface;

		useGLrendering = (renderingSurface instanceof GLSurfaceView);

		mBitmap = null;
		mSurface = null;
		mListener = null;
	}

	public void init() {
		// register callback for rendering surface events
		mVideoRenderingView.getHolder().addCallback(new Callback() {
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
			{
				Log.i("mediastream", "Video display surface is being changed.");
				//if (!useGLrendering) {
					synchronized (AndroidVideoWindowImpl.this) {
						mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
						//Canvas bitCanvas = new Canvas(mBitmap);
						//mVideoRenderingView.doDraw(bitCanvas);
						synchronized(mVideoRenderingView)
						{
							mSurface = holder.getSurface();
						}
					}
				//}
				if (mListener != null)
				{
					synchronized(mVideoRenderingView)
					{
						mListener.onVideoRenderingSurfaceReady(AndroidVideoWindowImpl.this, mVideoRenderingView);
					}
				}
				Log.w("mediastream", "Video display surface changed");
			}

			public void surfaceCreated(SurfaceHolder holder) {
				Log.w("mediastream", "Video display surface created");
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				synchronized(mVideoRenderingView)
				{
					if (!useGLrendering) {
						synchronized (AndroidVideoWindowImpl.this) {
							mSurface = null;
							mBitmap = null;
						}
					}
					if (mListener != null)
						mListener.onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl.this);
					Log.d("mediastream", "Video display surface destroyed");
				}
			}
		});
		// register callback for preview surface events
		if(mVideoPreviewView!=null){
		mVideoPreviewView.getHolder().addCallback(new Callback() {
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.i("mediastream", "Video preview surface is being changed.");
				if (mListener != null)
					mListener.onVideoPreviewSurfaceReady(
							AndroidVideoWindowImpl.this, mVideoPreviewView);
				Log.w("mediastream", "Video preview surface changed");
			}

			public void surfaceCreated(SurfaceHolder holder) {
				Log.w("mediastream", "Video preview surface created");
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mListener != null)
					mListener
							.onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl.this);
				Log.d("mediastream", "Video preview surface destroyed");
			}
		});
		}

		if (useGLrendering) {
			renderer = new Renderer();
			synchronized(mVideoRenderingView)
			{
				((GLSurfaceView) mVideoRenderingView).setRenderer(renderer);
				((GLSurfaceView) mVideoRenderingView).setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
			}
		}
	}

	public void release() {
		// mSensorMgr.unregisterListener(this);
	}

	public void setListener(VideoWindowListener l) {
		mListener = l;
	}

	public Surface getSurface() {
		if (useGLrendering)
			Log.e("mediastream",
					"View class does not match Video display filter used (you must use a non-GL View)");
		synchronized(mVideoRenderingView)
		{
			return mVideoRenderingView.getHolder().getSurface();
		}
	}

	public Bitmap getBitmap() {
		if (useGLrendering)
			Log.e("mediastream",
					"View class does not match Video display filter used (you must use a non-GL View)");
		return mBitmap;
	}

	public void setOpenGLESDisplay(int ptr) {
		if (!useGLrendering)
			Log.e("mediastream",
					"View class does not match Video display filter used (you must use a GL View)");
		renderer.setOpenGLESDisplay(ptr);
	}

	public void requestRender() {
		((GLSurfaceView) mVideoRenderingView).requestRender();
	}

	// Called by the mediastreamer2 android display filter
	public synchronized void update() {
		synchronized(mVideoRenderingView)
		{
			if (mSurface != null) {
				try {
					Canvas canvas = mSurface.lockCanvas(null);
					canvas.drawBitmap(mBitmap, 0, 0, null);
					mSurface.unlockCanvasAndPost(canvas);

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OutOfResourcesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class Renderer implements GLSurfaceView.Renderer {
		int ptr;
		boolean initPending;
		int width, height;
		Bitmap mGlSurfaceBmp = null;
		boolean mNeedSaveSurfaceBmp = false;
		boolean mCaptureSurfaceBmpDone = false;
		private Object mCaptureSurfaceBmpDoneEvent = new Object();
		private Object mCaptureSurfaceBmpCallLocker = new Object();

		public Renderer() {
			ptr = 0;
			initPending = false;
		}

		public void setOpenGLESDisplay(int ptr) {
			Log.d(TAG, "setOpenGLESDisplay("+ptr+")");
			/*
			 * Synchronize this with onDrawFrame: - they are called from
			 * different threads (Rendering thread and Linphone's one) -
			 * setOpenGLESDisplay can modify ptr while onDrawFrame is using it
			 */
			synchronized (this) {
				if (this.ptr != 0 && ptr != this.ptr) {
					initPending = true;
				}
				this.ptr = ptr;
			}
		}

		public void onDrawFrame(GL10 gl) {
			/*
			 * See comment in setOpenGLESDisplay
			 */
			synchronized (this) {
				if (ptr == 0)
					return;
				if (initPending) {
					OpenGLESDisplay.init(ptr, width, height);
					initPending = false;
				}
				OpenGLESDisplay.render(ptr);
			}
			//capture one picture
			//Log.d(TAG, SystemClock.uptimeMillis()+", onDrawFrame");
			//Here we check mNeedSaveSurfaceBmp for saving CPU, no need to depend on the synchronization on mCaptureSurfaceBmpDoneEvent
			if(true == mNeedSaveSurfaceBmp && null != mCaptureSurfaceBmpDoneEvent)
			{
				synchronized(mCaptureSurfaceBmpDoneEvent)
				{
					//Log.d(TAG, SystemClock.uptimeMillis()+", GotMutex mCaptureSurfaceBmpDoneEvent");
					if(mNeedSaveSurfaceBmp)
					{
						mNeedSaveSurfaceBmp = false;
						if(null != mGlSurfaceBmp)
						{
							mGlSurfaceBmp.recycle();
							mGlSurfaceBmp = null;						
						}
						long startTimeMs = SystemClock.uptimeMillis();
						mGlSurfaceBmp = createBitmapFromGLSurface(gl);
						//Log.d(TAG, "CreateBmp took "+(SystemClock.uptimeMillis()-startTimeMs)+" ms");
						mCaptureSurfaceBmpDone = true;
						mCaptureSurfaceBmpDoneEvent.notifyAll();
					}
				}
			}
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.d(TAG, "onSurfaceChanged");
			/* delay init until ptr is set */
			this.width = width;
			this.height = height;
			initPending = true;
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		}

		private Bitmap createBitmapFromGLSurface(GL10 gl)
		{
			Bitmap bmpGlSurface = null;
			int w = width;
			int h = height;
			do
			{
			    int bitmapBuffer[] = new int[w * h];
				if(null == bitmapBuffer)
				{
					break;
				}
			    int bitmapSource[] = new int[w * h];
				if(null == bitmapSource)
				{
					break;
				}
			    IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
			    intBuffer.position(0);

				try {
					gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
					int offset1, offset2;
					for (int i = 0; i < h; i++) {
						offset1 = i * w;
						offset2 = (h - i - 1) * w;
						for (int j = 0; j < w; j++) {
							int texturePixel = bitmapBuffer[offset1 + j];
							int blue = (texturePixel >> 16) & 0xff;
							int red = (texturePixel << 16) & 0x00ff0000;
							int pixel = (texturePixel & 0xff00ff00) | red | blue;
							bitmapSource[offset2 + j] = pixel;
						}
					}
				} catch (GLException e) {
					Log.e(TAG, e.getMessage());
					break;
				}
				bmpGlSurface = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
			}while(false);

			return bmpGlSurface;
		}

		//only can do one catpure operation at a time
		public Bitmap captureGlSurfaceBmp()
		{
			Bitmap bmpGlSurface = null;

			do
			{
				synchronized (this)
				{
					if(null == mCaptureSurfaceBmpDoneEvent)
					{
						break;
					}
				}
				if(null != mCaptureSurfaceBmpCallLocker)
				{
					synchronized(mCaptureSurfaceBmpCallLocker)
					{
						//Log.d(TAG, SystemClock.uptimeMillis()+", emit captureGlSurfaceBmp");
						synchronized(mCaptureSurfaceBmpDoneEvent)
						{
							mNeedSaveSurfaceBmp = true;
							mCaptureSurfaceBmpDone = false;
							if(useGLrendering)
							{
								if(null != mVideoRenderingView)
								{
									((GLSurfaceView)mVideoRenderingView).requestRender();
								}
							}
						}

						long startTimeMs = SystemClock.uptimeMillis();
						while(true)
						{
							synchronized(mCaptureSurfaceBmpDoneEvent)
							{
								//Log.d(TAG, SystemClock.uptimeMillis()+", chk bmp status");
								if(true == mCaptureSurfaceBmpDone)
								{
									//Log.d(TAG, SystemClock.uptimeMillis()+", chk bmp status, ready");
									bmpGlSurface = mGlSurfaceBmp;
									mGlSurfaceBmp = null;
									break;
								}
								try
								{
									mCaptureSurfaceBmpDoneEvent.wait(1000/*ms*/);
								}
								catch(java.lang.InterruptedException IntEx)
								{
								}
								if(4*1000 <= SystemClock.uptimeMillis() - startTimeMs)	//timeout
								{
									Log.e(TAG, "CapSurfaceBmp timeout");
									if(null != mGlSurfaceBmp)
									{
										mGlSurfaceBmp.recycle();
										mGlSurfaceBmp = null;
									}
									break;
								}
							}
						}
					}
				}
			}while(false);

			return bmpGlSurface;
		}
	}

	public static int rotationToAngle(int r) {
		switch (r) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}
	
	public Bitmap getRemoteVideoViewBmp()
	{
		Bitmap bmpRemoteVidPic = null;

		do
		{
			if(null == renderer)
			{
				break;
			}
			bmpRemoteVidPic = renderer.captureGlSurfaceBmp();
		}while(false);

		return bmpRemoteVidPic;
	}
}
