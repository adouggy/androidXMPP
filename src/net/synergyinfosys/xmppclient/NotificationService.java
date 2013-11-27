/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.synergyinfosys.xmppclient;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.synergyinfosys.accesscontrol.xmpp.aidl.XmppInterface;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Service that continues to run in background and respond to the push
 * notification events from the server. This should be registered as service in
 * AndroidManifest.xml.
 * 
 */
public class NotificationService extends Service {

	private static final String LOGTAG = LogUtil.makeLogTag(NotificationService.class);

	public static final String SERVICE_NAME = "net.synergyinfosys.xmppclient.NotificationService";

	private TelephonyManager telephonyManager;

	// private WifiManager wifiManager;
	//
	// private ConnectivityManager connectivityManager;

	// private BroadcastReceiver notificationReceiver;

	private BroadcastReceiver connectivityReceiver;

	private PhoneStateListener phoneStateListener;

	private ExecutorService executorService;

	private TaskSubmitter taskSubmitter;

	private TaskTracker taskTracker;

	private XmppManager xmppManager;

	private SharedPreferences sharedPrefs;

	private String deviceId;

	private static Context mContext;

	// for heartbeat
	private static final long HEARTBEAT_INTERVAL_MS = 30000;
	AlarmManager mAlarmMgr;
	XmppHeartbeat heartbeatAlarm;

	public NotificationService() {
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
		executorService = Executors.newSingleThreadExecutor();
		taskSubmitter = new TaskSubmitter(this);
		taskTracker = new TaskTracker(this);
		heartbeatAlarm = new XmppHeartbeat(this);
	}

	public class MyBinder extends Binder {
		NotificationService getService() {
			return NotificationService.this;
		}
	}

	// private MyBinder mBinder = new MyBinder();
	private XmppInterface.Stub aBinder = new XmppInterface.Stub() {
		@Override
		public boolean isConnected() throws RemoteException {
			Log.d(LOGTAG, "isConnected: " + NotificationService.this.isConnected());
			return NotificationService.this.isConnected();
		}
	};

	@Override
	public void onCreate() {
		Log.d(LOGTAG, "onCreate()...");
		mContext = this;
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

		// Get deviceId
		deviceId = telephonyManager.getDeviceId();
		
		Editor editor = sharedPrefs.edit();
		editor.putString(Constants.DEVICE_ID, deviceId);
		editor.commit();

		// If running on an emulator
		if (deviceId == null || deviceId.trim().length() == 0 || deviceId.matches("0+")) {
			if (sharedPrefs.contains("EMULATOR_DEVICE_ID")) {
				deviceId = sharedPrefs.getString(Constants.EMULATOR_DEVICE_ID, "");
			} else {
				deviceId = (new StringBuilder("EMU")).append((new Random(System.currentTimeMillis())).nextLong()).toString();
				editor.putString(Constants.EMULATOR_DEVICE_ID, deviceId);
				editor.commit();
			}
		}
		Log.d(LOGTAG, "deviceId=" + deviceId);

		xmppManager = new XmppManager(this);
		mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(LOGTAG, "onStart()...");
		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				NotificationService.this.start();
			}
		});
	}

	@Override
	public void onDestroy() {
		Log.i(LOGTAG, "onDestroy()...");
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOGTAG, "onBind()...");
		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				NotificationService.this.start();
			}
		});
		return aBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(LOGTAG, "onRebind()...");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(LOGTAG, "onUnbind()...");
		return true;
	}

	public static Intent getIntent() {
		Intent intent = new Intent(SERVICE_NAME);
		return intent;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	public XmppManager getXmppManager() {
		return xmppManager;
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPrefs;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void connect() {
		Log.d(LOGTAG, "connect()...");
		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				NotificationService.this.getXmppManager().connect();
				startHeartBeat();
			}
		});
	}

	public void disconnect() {
		Log.d(LOGTAG, "disconnect()...");
		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				NotificationService.this.getXmppManager().disconnect();
				stopHeartBeat();
			}
		});
	}

	public boolean isConnected() {
		return this.getXmppManager().getConnection().isConnected();
	}

	private void startHeartBeat() {
		Log.e(LOGTAG, "startHeartBeat()");
		long time = SystemClock.elapsedRealtime() + HEARTBEAT_INTERVAL_MS;
		Intent i = new Intent(Constants.ACTION_HEARTBEAT_ALARM);
		PendingIntent pIntent = PendingIntent.getBroadcast(NotificationService.this, 0, i, 0);
		mAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, HEARTBEAT_INTERVAL_MS, pIntent);
	}

	private void stopHeartBeat() {
		Log.e(LOGTAG, "stopHeartBeat()");
		// Intent i = new Intent(NotificationService.this, XmppHeartbeat.class);
		Intent i = new Intent(Constants.ACTION_HEARTBEAT_ALARM);
		PendingIntent pIntent = PendingIntent.getBroadcast(NotificationService.this, 0, i, 0);
		mAlarmMgr.cancel(pIntent);
	}

	private void registerHeartbeatAlarmReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_HEARTBEAT_ALARM);
		registerReceiver(heartbeatAlarm, filter);
	}

	private void unregisterHeartbeatAlarmReceiver() {
		unregisterReceiver(heartbeatAlarm);
	}

	private void registerConnectivityReceiver() {
		Log.d(LOGTAG, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		// filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		Log.d(LOGTAG, "unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	private void start() {
		Log.i(LOGTAG, "start()...");
		registerConnectivityReceiver();
		registerHeartbeatAlarmReceiver();
		xmppManager.connect();
	}

	private void stop() {
		Log.i(LOGTAG, "stop()...");
		unregisterConnectivityReceiver();
		unregisterHeartbeatAlarmReceiver();
		xmppManager.disconnect();
		executorService.shutdown();
	}

	public void sendXmppHeartbeatPacket() {
		XMPPConnection connect = xmppManager.getConnection();
		if (connect != null && connect.isConnected()) {
			Log.e(LOGTAG, "sendXmppHeartbeatPacket()");
			xmppManager.getConnection().sendPacket(new HeartbeatPacket());
		}
		sendConnectionStatus(connect.isConnected());
	}

	public class HeartbeatPacket extends Packet {
		@Override
		public String toXML() {
			return " ";
		}

	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {

		final NotificationService notificationService;

		public TaskSubmitter(NotificationService notificationService) {
			this.notificationService = notificationService;
		}

		@SuppressWarnings("unchecked")
		public Future submit(Runnable task) {
			Future result = null;
			if (!notificationService.getExecutorService().isTerminated() && !notificationService.getExecutorService().isShutdown() && task != null) {
				result = notificationService.getExecutorService().submit(task);
			}
			return result;
		}

	}

	/**
	 * Class for monitoring the running task count.
	 */
	public class TaskTracker {

		final NotificationService notificationService;

		public int count;

		public TaskTracker(NotificationService notificationService) {
			this.notificationService = notificationService;
			this.count = 0;
		}

		public void increase() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count++;
				Log.d(LOGTAG, "Incremented task count to " + count);
			}
		}

		public void decrease() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count--;
				Log.d(LOGTAG, "Decremented task count to " + count);
			}
		}

	}

	public static void sendConnectionStatus(boolean isConnected) {
		Intent it = new Intent();
		it.setAction("android.intent.xmppconnection.isconnected");
		it.putExtra("isConnected", isConnected);
		mContext.sendBroadcast(it);
	}

}
