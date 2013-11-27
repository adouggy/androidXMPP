package net.synergyinfosys.xmppclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class XmppHeartbeat extends BroadcastReceiver {
	NotificationService notificationService;

	public XmppHeartbeat(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
//		Log.e("XmppHeartbeat", "onReceive()");
	    if (notificationService != null) {
	        notificationService.sendXmppHeartbeatPacket();
	    }
	}

}
