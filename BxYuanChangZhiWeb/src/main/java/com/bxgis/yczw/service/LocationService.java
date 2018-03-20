package com.bxgis.yczw.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bxgis.yczw.IMyAidlInterface;
import com.bxgis.yczw.event.UpdateEvent;
import com.bxgis.yczw.event.UpdateICountEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xiaoqi on 2016/10/8.
 */
public class LocationService extends Service{
	MyBinder binder;
	MyConn conn;
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		binder = new MyBinder();
		conn = new MyConn();
		EventBus.getDefault().register(this);
	}

	class MyBinder extends IMyAidlInterface.Stub {
		@Override
		public String getServiceName() throws RemoteException {
			return LocationService.class.getSimpleName();
		}
	}

	@Subscribe(threadMode =ThreadMode.MAIN)
	public void reciveMessage(UpdateEvent updateEvent){
		Toast.makeText(LocationService.this, " 本地服务活"+updateEvent.message, Toast.LENGTH_SHORT).show();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(LocationService.this, " 本地服务活了", Toast.LENGTH_SHORT).show();
		this.bindService(new Intent(LocationService.this,RomoteService.class),conn, Context.BIND_IMPORTANT);
        EventBus.getDefault().post(new UpdateICountEvent(444441,2222111));
		return START_STICKY;
	}

	class MyConn implements ServiceConnection{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("xiaoqi", "绑定上了远程服务");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("xiaoqi", "远程服务被干掉了");
			Toast.makeText(LocationService.this, "远程服务挂了", Toast.LENGTH_SHORT).show();
			//开启远程服务
			LocationService.this.startService(new Intent(LocationService.this,RomoteService.class));
			//绑定远程服务
			LocationService.this.bindService(new Intent(LocationService.this,RomoteService.class),conn,Context.BIND_IMPORTANT);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		//开启远程服务
		LocationService.this.startService(new Intent(LocationService.this,RomoteService.class));
		//绑定远程服务
		LocationService.this.bindService(new Intent(LocationService.this,RomoteService.class),conn,Context.BIND_IMPORTANT);

	}
}
