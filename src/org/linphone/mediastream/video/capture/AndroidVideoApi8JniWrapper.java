/*
AndroidVideoApi8JniWrapper.java
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
package org.linphone.mediastream.video.capture;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.os.SystemClock;

public class AndroidVideoApi8JniWrapper {
	static public int detectCameras(int[] indexes, int[] frontFacing,
			int[] orientation) {
		return AndroidVideoApi5JniWrapper.detectCameras(indexes, frontFacing,
				orientation);
	}

	static public int[] selectNearestResolutionAvailable(int cameraId,
			int requestedW, int requestedH) {
		return AndroidVideoApi5JniWrapper.selectNearestResolutionAvailable(
				cameraId, requestedW, requestedH);
	}

	public static Object startRecording(int cameraId, int width, int height,
			int fps, int rotation, final long nativePtr) {
		Log.d("mediastreamer", "startRecording(" + cameraId + ", " + width
				+ ", " + height + ", " + fps + ", " + rotation + ", "
				+ nativePtr + ")");
		Camera camera = Camera.open();

		AndroidVideoApi5JniWrapper.applyCameraParameters(camera, width, height,
				fps);

		int bufferSize = (width * height * ImageFormat.getBitsPerPixel(camera
				.getParameters().getPreviewFormat())) / 8;
		camera.addCallbackBuffer(new byte[bufferSize]);
		camera.addCallbackBuffer(new byte[bufferSize]);

		camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
			public void onPreviewFrame(byte[] data, Camera camera) {
				// forward image data to JNI
				AndroidVideoApi5JniWrapper.putImage(nativePtr, data);

				camera.addCallbackBuffer(data);
			}
		});

		camera.startPreview();
		Log.d("mediastreamer", "Returning camera object: " + camera);
		return camera;
	}

	public static void stopRecording(Object cam) {
		Log.d("mediastreamer8", "stopRecording");
		Camera camera = (Camera) cam;

		if (camera != null) {
			long curTimeMs = SystemClock.uptimeMillis();
			camera.setPreviewCallbackWithBuffer(null);
			//Log.d("mediastreamer8", "stopRecording took "+(SystemClock.uptimeMillis()-curTimeMs)+" ms");
			camera.stopPreview();
			//Log.d("mediastreamer8", "stopRecording took "+(SystemClock.uptimeMillis()-curTimeMs)+" ms");
			camera.release();
			Log.d("mediastreamer8", "stopRecording took "+(SystemClock.uptimeMillis()-curTimeMs)+" ms");
		} else {
			Log.i("mediastreamer", "Cannot stop recording ('camera' is null)");
		}
	}

	public static void setPreviewDisplaySurface(Object cam, Object surf) {
		AndroidVideoApi5JniWrapper.setPreviewDisplaySurface(cam, surf);
	}
}
