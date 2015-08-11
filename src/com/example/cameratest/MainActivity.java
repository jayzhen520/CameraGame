package com.example.cameratest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

private static String TAG = "MainActivity";
	
	private Context mContext = null;
	
	private Camera mCamera = null;
	private CameraPreview mPreview = null;
	
	Camera.Parameters mParams = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(checkCameraHardware(this)){
			mCamera = chooseCamera(false);
			
			mParams = mCamera.getParameters();
		}
		
		mPreview = (CameraPreview)findViewById(R.id.camera_preview);
		mPreview.init(mCamera);

	}
	
	private boolean checkCameraHardware(Context context){
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			//
			return true;
		}else{
			return false;
		}
	}
	
	private Camera chooseCamera(boolean frontCamera){
		int cameraNum = Camera.getNumberOfCameras();
		int i = 0;
		for(; i < cameraNum; i++){
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if(info.facing == CameraInfo.CAMERA_FACING_FRONT && frontCamera){
				break;
			}
			if(info.facing == CameraInfo.CAMERA_FACING_BACK && !frontCamera){
				break;
			}
		}
		if(i == 4){
			Log.d(TAG, "Cannot find the demand camera.");
			i = 0;
		}
		Camera c = getCamera(i);
		
		return c;
	}
	
	public Camera getCamera(int cameraId){
		Camera c = null;
		try{
			c = Camera.open(cameraId);
		}catch(Exception e){
			e.printStackTrace();
		}
//		problem:mCameraÃ»ÉèÖµ
//		mCamera = c;
		return c;
	}
}
