package com.example.cameratest;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

	private static final String TAG = "CameraPreview";
	
	private SurfaceHolder mHolder;
	private Camera mCamera;
	
	public CameraPreview(Context context, AttributeSet attrs){
		super(context, attrs, 0);
		
	}
	
	public void init(Camera camera){
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void refreshCamera(){
		Log.d(TAG, "surfaceChanged func is run.");
		
		// TODO Auto-generated method stub
		if(mHolder.getSurface() == null){
			return;
		}
		
		try{
			mCamera.stopPreview();
		} catch(Exception e){
			
		}
		
		try{
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e){
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
	
	public void refreshCamera2(){
		Log.d(TAG, "surfaceChanged func is run.");
		
		// TODO Auto-generated method stub
		if(mHolder.getSurface() == null){
			return;
		}
		
		try{
			mCamera.startPreview();
		} catch (Exception e){
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		refreshCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	
	
	
	
	
	
	
	
	
}
