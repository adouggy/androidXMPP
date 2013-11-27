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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.synergyinfo.android.demo.bean.KeyValueBean;
//import net.synergyinfo.android.demo.dao.KeyValueDAO;
//
//import org.androidpn.demoapp.DemoAppActivity;
//import org.androidpn.demoapp.SqliteActivity;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;
import android.util.Log;

/**
 * This class notifies the receiver of incoming notifcation packets
 * asynchronously.
 * 
 */
public class NotificationPacketListener implements PacketListener {

	private static final String LOGTAG = LogUtil
			.makeLogTag(NotificationPacketListener.class);

	private final XmppManager xmppManager;

	public NotificationPacketListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	public void processPacket(Packet packet) {
		Log.d(LOGTAG, "NotificationPacketListener.processPacket()...");
		Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());
		
		if (packet instanceof NotificationIQ) {
			NotificationIQ notification = (NotificationIQ) packet;
			
			if (notification.getChildElementXML().contains(
					"androidpn:iq:notification")) {
				
				String notificationId = notification.getId();
				String notificationApiKey = notification.getApiKey();
				String notificationTitle = notification.getTitle();
				String notificationMessage = notification.getMessage();
				// String notificationTicker = notification.getTicker();
				String notificationUri = notification.getUri();
				String devId = notification.getDevId();
				String data = notification.getData();
				
				if( data != null ){
					// deal diff
//					dealDiff(data);
//					return;
				}

				Intent intent = new Intent(Constants.ACTION_SHOW_NOTIFICATION);
				intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
				intent.putExtra(Constants.NOTIFICATION_API_KEY,
						notificationApiKey);
				intent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
				intent.putExtra(Constants.NOTIFICATION_MESSAGE,
						notificationMessage);
				intent.putExtra(Constants.NOTIFICATION_URI, notificationUri);
				intent.putExtra("devId", devId);
				intent.putExtra("data", data);
				// intent.setData(Uri.parse((new StringBuilder(
				// "notif://notification.androidpn.org/")).append(
				// notificationApiKey).append("/").append(
				// System.currentTimeMillis()).toString()));

				xmppManager.getContext().sendBroadcast(intent);
			}
		}

	}
	
//	private void dealDiff(String data){
//		if( data == null )
//			return;
//		
//		String[] operation = data.split("\\|");
//		for(final String o : operation ){
////			System.out.println( o );
//			if( o.startsWith("DELETE") ){
//				System.out.println( "DELETE:" + o );
//				KeyValueBean bean = dealString( o );
//				System.out.println(bean);
//				KeyValueDAO.INSTANCE.delete(bean.getId());
//			}
//			
//			else if( o.startsWith("UPDATE") ){
//				System.out.println( "UPDATE:" + o );
//				int middle = o.indexOf("]");
//				
//				KeyValueBean bean = dealString( o.substring(0, middle+1) );
//				System.out.println(bean);
//				
//				KeyValueBean bean2 = dealString( o.substring(middle) );
//				System.out.println(bean2);
//				
//				if( bean.getId() == bean2.getId() )
//					KeyValueDAO.INSTANCE.update(bean2);
//				else{
//					KeyValueDAO.INSTANCE.delete(bean.getId());
//					KeyValueDAO.INSTANCE.insert(bean2);
//				}
//			}
//			
//			else if( o.startsWith("INSERT") ){
//				System.out.println( "INSERT:" + o );
//				KeyValueBean bean = dealString( o );
//				System.out.println(bean);
//				KeyValueDAO.INSTANCE.insert(bean);
//			}
//		}
//	}
//	
//	private KeyValueBean dealString(String str){
//		Pattern p = Pattern
//				.compile("\\[([a-z0-9]+),([a-z0-9]+),([a-z0-9]+)\\]");
//		Matcher m = p.matcher(str);
//		KeyValueBean bean = new KeyValueBean();
//		while (m.find()) {
//			bean.setId(Integer.parseInt(m.group(1)));
//			bean.setKey(m.group(2));
//			bean.setValue(m.group(3));
//		}
//		return bean;
//	}

}
