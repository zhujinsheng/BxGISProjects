package com.bxgis.yczw.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.amap.api.maps.model.Marker;

/**
 * 感应器 配合地图定位图标
 */
public class SensorEventHelper implements SensorEventListener {

	private SensorManager mSensorManager;  //动作感应管理器SensorManager
	private Sensor mSensor;
	private long lastTime = 0;
	private final int TIME_SENSOR = 100;
	private float mAngle;
	private Context mContext;
	private Marker mMarker;

	public SensorEventHelper(Context context) {
		mContext = context;
		// 在这里真正注册Service服务
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	}

	/**
	 * 注册传感器服务，在本java和Activity里面都要注册，但是取消注册的时候，只在activity里面取消注册即可。
	 */
	public void registerSensorListener() {
		//SENSOR_DELAY_NORMAL为检测的精确度
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void unRegisterSensorListener() {
		mSensorManager.unregisterListener(this, mSensor);
	}

	public void setCurrentMarker(Marker marker) {
		mMarker = marker;
	}


	// 精度发生变化
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
			return;
		}
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ORIENTATION: {
			float x = event.values[0];
			x += getScreenRotationOnPhone(mContext);
			x %= 360.0F;
			if (x > 180.0F)
				x -= 360.0F;
			else if (x < -180.0F)
				x += 360.0F;
			
			if (Math.abs(mAngle - x) < 3.0f) {
				break;
			}
			mAngle = Float.isNaN(x) ? 0 : x;
			if (mMarker != null) {
				mMarker.setRotateAngle(360-mAngle);
			}
			lastTime = System.currentTimeMillis();
		}
		}

	}

	public static int getScreenRotationOnPhone(Context context) {
		final Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			return 0;

		case Surface.ROTATION_90:
			return 90;

		case Surface.ROTATION_180:
			return 180;

		case Surface.ROTATION_270:
			return -90;
		}
		return 0;
	}
}
