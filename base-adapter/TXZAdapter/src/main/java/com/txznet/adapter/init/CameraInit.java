package com.txznet.adapter.module;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.txznet.adapter.BaseInitModule;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.sdk.TXZCameraManager;
import com.txznet.sdk.TXZCameraManager.CapturePictureListener;
import com.txznet.sdk.TXZCameraManager.CaptureVideoListener;

public class CameraInit extends BaseInitModule {

	private final String TAG = CameraInit.class.getSimpleName();

	private static CameraInit instance;
	private CaptureVideoListener mCaptureVideoListener;
	private CapturePictureListener mCapturePictureListener;

	public static CameraInit getInstance() {
		if (instance == null) {
			synchronized (CameraInit.class) {
				if (instance == null)
					instance = new CameraInit();
			}
		}
		return instance;
	}

	@Override
	public void init() {
		TXZCameraManager.getInstance().setCameraTool(
				new TXZCameraManager.CameraTool() {

					@Override
					public boolean captureVideo(CaptureVideoListener arg0,
							CaptureVideoListener arg1) {
						Log.d(TAG, "captureVideo");
						mCaptureVideoListener = arg1;
						BroadCastUtil.sendBroadCast(1007, "action",
								"capture_video");
						return true;
					}

					@Override
					public boolean capturePicure(long arg0,
							CapturePictureListener arg1) {
						Log.d(TAG, "capturePicure");
						mCapturePictureListener = arg1;
						BroadCastUtil.sendBroadCast(1007, "action",
								"capture_picture");
						return true;
					}
				});
	}

	@Override
	public void handleMessage(Intent intent) {
		int keyType = intent.getIntExtra("key_type", 0);
		Bundle bundle = intent.getExtras();
		switch (keyType) {
		case 2002:
			String video_path = bundle.getString("video_path");
			String picture_path = bundle.getString("picture_path");
			if (!picture_path.isEmpty()) {
				Log.d(TAG, "capture picture path = " + picture_path);
				if (mCapturePictureListener == null) {
					Log.e(TAG, "mCapturePictureListener==null");
					return;
				}
				mCapturePictureListener.onSave(picture_path);
			} else if (!video_path.isEmpty()) {
				Log.d(TAG, "capture video path = " + video_path);
				if (mCaptureVideoListener == null) {
					Log.e(TAG, "mCaptureVideoListener==null");
					return;
				}
				mCapturePictureListener.onSave(video_path);
			}
			break;
		default:
			break;
		}

	}
}
