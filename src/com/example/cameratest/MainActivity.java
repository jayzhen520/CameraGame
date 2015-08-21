package com.example.cameratest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

private static String TAG = "MainActivity";
	
	private Context mContext = null;
	
	private Camera mCamera = null;
	private CameraPreview mPreview = null;
	private Button captureButton = null;
	
	Camera.Parameters mParams = null;
	
	private PictureCallback mPicture;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private int count = 0;
	
	/*
	 * videoRecorder variable
	 * */
	MediaRecorder mMediaRecorder = null;
	Button videoCapture;
	private boolean isRecording = false;
	
	
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
		
		
		mPicture = new PictureCallback(){

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
				
				if (pictureFile == null){
		            Log.d(TAG, "Error creating media file, check storage permissions");
		            return;
		        }

		        try {
		            FileOutputStream fos = new FileOutputStream(pictureFile);
		            fos.write(data);
		            fos.close();
		        } catch (FileNotFoundException e) {
		            Log.d(TAG, "File not found: " + e.getMessage());
		        } catch (IOException e) {
		            Log.d(TAG, "Error accessing file: " + e.getMessage());
		        }
		        
		        TimeTest t = new TimeTest();
		        
		        t.getTimeStart();
		        if(count % 2 == 0){
		        	mPreview.refreshCamera();
		        	Log.d(TAG, "use heavy func");
		        }else{
		        	mPreview.refreshCamera2();
		        	Log.d(TAG, "use light func");
		        }
		        t.getTimeEnd();
		        Log.d(TAG, "time is " + t.getStartEndDistance());
		        count++;
			}
			
		};
		
		captureButton = (Button)findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.takePicture(null, null, mPicture);
				
//				mPreview.refreshCamera();
			}
		});
		
		videoCapture = (Button)findViewById(R.id.video_capture_button);
		videoCapture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isRecording){
					mMediaRecorder.stop();
					releaseMediaRecorder();
					mCamera.lock();
					// inform the user that recording has stopped
//	                setCaptureButtonText("Capture");
	                isRecording = false;
	            } else {
	                // initialize video camera
	                if (prepareVideoRecorder()) {
	                    // Camera is available and unlocked, MediaRecorder is prepared,
	                    // now you can start recording
	                    mMediaRecorder.start();//mCamera called lock implicitly.

	                    // inform the user that recording has started
//	                    setCaptureButtonText("Stop");
	                    isRecording = true;
	                } else {
	                    // prepare didn't work, release the camera
	                    releaseMediaRecorder();
	                    // inform user
	                }
				}
			}
		});

	}
	
	private static File getOutputMediaFile(final int type) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "JZCameraApp");
		if(!mediaStorageDir.exists()){
			if(!mediaStorageDir.mkdir()){ 
				Log.e(TAG, "failed to create " + "JZCameraApp");
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		releaseMediaRecorder();
		releaseCamera();
	}
	
	private boolean prepareVideoRecorder(){
//		mCamera = chooseCamera(false);
		mMediaRecorder = new MediaRecorder();
		
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO){
			mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		}
		mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
		
		try{
			mMediaRecorder.prepare();
		} catch (IllegalStateException e){
			Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
		} catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
		return true;
	}
	
	private void releaseMediaRecorder(){
		if(mMediaRecorder != null){
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;
			mCamera.lock();
		}
	}
	
	private void releaseCamera(){
		if(mCamera != null){
			mCamera.release();
			mCamera = null;
		}
	}
}
