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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

//import net.synergyinfo.android.mdm.MDMConstants;
//import net.synergyinfo.android.mdm.MyUtil;
//import net.synergyinfo.android.mdm.SimCardInfo;
//
//import org.androidpn.demoapp.DemoAppActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/** 
 * Broadcast receiver that handles push notification messages from the server.
 * This should be registered as receiver in AndroidManifest.xml. 
 * 
 */
public final class NotificationReceiver extends BroadcastReceiver {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotificationReceiver.class);

    //    private NotificationService notificationService;

    public NotificationReceiver() {
    }

    //    public NotificationReceiver(NotificationService notificationService) {
    //        this.notificationService = notificationService;
    //    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "NotificationReceiver.onReceive()...");
        String action = intent.getAction();
        Log.d(LOGTAG, "action=" + action);

        if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
            String notificationId = intent
                    .getStringExtra(Constants.NOTIFICATION_ID);
            String notificationApiKey = intent
                    .getStringExtra(Constants.NOTIFICATION_API_KEY);
            String notificationTitle = intent
                    .getStringExtra(Constants.NOTIFICATION_TITLE);
            String notificationMessage = intent
                    .getStringExtra(Constants.NOTIFICATION_MESSAGE);
            String notificationUri = intent
                    .getStringExtra(Constants.NOTIFICATION_URI);
            
            String data = intent.getStringExtra("data");

            Log.d(LOGTAG, "notificationId=" + notificationId);
            Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
            Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
            Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
            Log.d(LOGTAG, "notificationUri=" + notificationUri);
            Log.d(LOGTAG, "data=" + data);

//            if( notificationMessage != null && notificationMessage.compareTo( "����" ) == 0 ){
//            	Log.d( LOGTAG, "�յ���������ָ��~!!!!" );
//            	DemoAppActivity.lockScreen();
//            } else if( notificationMessage != null && notificationMessage.compareTo( "����" ) == 0 ){
//            	DemoAppActivity.unlockScreen();
//            	DemoAppActivity.ligthScreen();
//            } else if( notificationMessage != null && notificationMessage.compareTo( "��" ) == 0 ){
//            	DemoAppActivity.ligthScreen();
//            } else if( notificationMessage != null && notificationMessage.compareTo( "��" ) == 0 ){
//            	DemoAppActivity.dimScreen();
//            } else if( notificationTitle != null && notificationTitle.compareTo( MDMConstants.MDM_TITLE ) == 0 ){
////            	Notifier notifier = new Notifier(context);
////                notifier.notify(notificationId, notificationApiKey,
////                        "�յ�MDM����", "������Ϣ:<br />" + notificationMessage, notificationUri);
//				Toast toast = Toast.makeText(context, "MDM������Ϣ:<br />"
//						+ notificationMessage, Toast.LENGTH_LONG);
//				toast.show();
//            	
//            	SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
//            	String server = sharedPreferences.getString(Constants.XMPP_HOST, "localhost");
//            	String username = sharedPreferences.getString(Constants.XMPP_USERNAME, "");
//            	try {
//            		dealMDMQuery( notificationMessage, server, username, context );
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//            	
//            }
//            else{
            	Notifier notifier = new Notifier(context);
                notifier.notify(notificationId, notificationApiKey,
                        notificationTitle, notificationMessage, notificationUri);
//            }
            
        }

        //        } else if (Constants.ACTION_NOTIFICATION_CLICKED.equals(action)) {
        //            String notificationId = intent
        //                    .getStringExtra(Constants.NOTIFICATION_ID);
        //            String notificationApiKey = intent
        //                    .getStringExtra(Constants.NOTIFICATION_API_KEY);
        //            String notificationTitle = intent
        //                    .getStringExtra(Constants.NOTIFICATION_TITLE);
        //            String notificationMessage = intent
        //                    .getStringExtra(Constants.NOTIFICATION_MESSAGE);
        //            String notificationUri = intent
        //                    .getStringExtra(Constants.NOTIFICATION_URI);
        //
        //            Log.e(LOGTAG, "notificationId=" + notificationId);
        //            Log.e(LOGTAG, "notificationApiKey=" + notificationApiKey);
        //            Log.e(LOGTAG, "notificationTitle=" + notificationTitle);
        //            Log.e(LOGTAG, "notificationMessage=" + notificationMessage);
        //            Log.e(LOGTAG, "notificationUri=" + notificationUri);
        //
        //            Intent detailsIntent = new Intent();
        //            detailsIntent.setClass(context, NotificationDetailsActivity.class);
        //            detailsIntent.putExtras(intent.getExtras());
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_API_KEY, notificationApiKey);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_MESSAGE, notificationMessage);
        //            //            detailsIntent.putExtra(Constants.NOTIFICATION_URI, notificationUri);
        //            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        //
        //            try {
        //                context.startActivity(detailsIntent);
        //            } catch (ActivityNotFoundException e) {
        //                Toast toast = Toast.makeText(context,
        //                        "No app found to handle this request",
        //                        Toast.LENGTH_LONG);
        //                toast.show();
        //            }
        //
        //        } else if (Constants.ACTION_NOTIFICATION_CLEARED.equals(action)) {
        //            //
        //        }

    }
    
//    private void dealMDMQuery(String msg, String server, String username, Context context) throws JSONException{
//    	if( msg == null ) 
//    		return;
//    	
//    	String url = "http://" + server + ":" + MDMConstants.MDM_SERVER_PORT + MDMConstants.MDM_URL + "user";
//    	Log.d(LOGTAG, msg + " start with [QUERY]:" + msg.startsWith( MDMConstants.MDM_QUERY ));
//    	if( msg.startsWith( MDMConstants.MDM_QUERY ) ){
//    		msg = msg.substring(MDMConstants.MDM_QUERY.length());
//    		Log.d(LOGTAG, "MDM query=" + msg);
//    		
//    		JSONObject json = new JSONObject();
//    		json.put("username", username);
//    		
//    		if( msg.contains( MDMConstants.MDM_QUERY_APP_LIST ) ){
//    			List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);
//				StringBuffer sb = new StringBuffer();
//				Iterator<PackageInfo> iter = packageInfoList.iterator();
//				while (iter.hasNext()) {
//					PackageInfo pi = iter.next();
//					sb.append(pi.packageName + ";");
//				}
//    			json.put("applist", sb.toString());
//    		}
//    		
//    		DemoAppActivity.myself.mDPM = /*DemoAppActivity.myself.mDPM;*/(DevicePolicyManager) DemoAppActivity.myself.getSystemService(Context.DEVICE_POLICY_SERVICE);
//    		if (DemoAppActivity.myself.mDPM != null && DemoAppActivity.myself.mDPM.isAdminActive(DemoAppActivity.myself.mDeviceAdminSample)) {
//				StringBuffer sb = new StringBuffer();
//				sb.append("Debug info:");
//				sb.append("<br />");
//				sb.append("\t");
//				sb.append("failed attamps: "
//						+ DemoAppActivity.myself.mDPM.getCurrentFailedPasswordAttempts() + "<br />");
//				sb.append("\t");
//				sb.append("max fail for wipe:"
//						+ DemoAppActivity.myself.mDPM.getMaximumFailedPasswordsForWipe(DemoAppActivity.myself.mDeviceAdminSample)
//						+ "<br />");
//				sb.append("\t");
//				sb.append("max time for lock:"
//						+ DemoAppActivity.myself.mDPM.getMaximumTimeToLock(DemoAppActivity.myself.mDeviceAdminSample)
//						+ "<br />");
//				sb.append("\t");
//				sb.append("������󳤶�:" + DemoAppActivity.myself.mDPM.getPasswordMaximumLength(1)
//						+ "<br />");
//				sb.append("\t");
//				sb.append("������С����:"
//						+ DemoAppActivity.myself.mDPM.getPasswordMinimumLength(DemoAppActivity.myself.mDeviceAdminSample)
//						+ "<br />");
//				sb.append("\t");
//				sb.append("��������:"
//						+ DemoAppActivity.myself.mDPM.getPasswordQuality(DemoAppActivity.myself.mDeviceAdminSample)
//						+ "<br />");
//				sb.append("\t");
//				sb.append("Active admins:" + DemoAppActivity.myself.mDPM.getActiveAdmins() + "<br />");
//				json.put("adminPolicy", sb.toString());
//			} else {
//				json.put("adminPolicy",  "���ȼ���admin");
//			}
//    		
//    		String androidId = android.provider.Settings.System.getString(
//					DemoAppActivity.myself.getContentResolver(),
//					android.provider.Settings.System.ANDROID_ID);
//    		json.put("androidID", androidId);
//    		
//    		String cpuID = DemoAppActivity.getCPUInfo();
//    		if( cpuID != null ){
//    			cpuID = cpuID.replaceAll("\n\r", "<br />");
//    			cpuID = cpuID.replaceAll("\n", "<br />");
//    		}
//    		json.put("cpuID", cpuID);
//    		
//    		boolean isRoot = MyUtil.runRootCommand("ls /data/");
//    		json.put("isRoot", isRoot?"�ѱ�Root":"��δRoot");
//    		
//    		json.put("locationGPS", "δ�ܻ�ȡ��GPS��Դ");
//    		
//    		DemoAppActivity.myself.mainWifi = (WifiManager) DemoAppActivity.myself.getSystemService(Context.WIFI_SERVICE);
//    		StringBuffer wifiLocationSB = new StringBuffer();
//			// �ж�wifi�Ƿ���
//			if (DemoAppActivity.myself.mainWifi.isWifiEnabled()) {
//				// ���ͽ�����ɨ�����󣬷���true�ɹ�������ʧ��
//				if (!DemoAppActivity.myself.isStartScan) {
//					wifiLocationSB.append("��ʼɨ��wifi��~<br />");
//					DemoAppActivity.myself.isStartScan = DemoAppActivity.myself.mainWifi.startScan();
//				}
//
//				if (DemoAppActivity.myself.isStartScan) {
//					Location loc = DemoAppActivity.myself.getLocationViaWifi();
//					if (loc != null) {
//						wifiLocationSB.append(loc.toString());
//					} else {
//						wifiLocationSB.append("��ʱ�޷�ͨ��google&wifiδ���λ����Ϣ�����Ժ�<br />");
//					}
//				} else {
//					wifiLocationSB.append("�޷�ɨ��wifi<br />");
//				}
//
//			} else {
//				wifiLocationSB.append("wifiδ����<br />");
//			}
//    		json.put("locationWIFI", wifiLocationSB.toString().replaceAll(",", "<br />"));
//    		
//    		SimCardInfo siminfo = new SimCardInfo(DemoAppActivity.myself);
//			StringBuffer sb = new StringBuffer();
//			sb.append("Provider:" + siminfo.getProvidersName() + "<br />");
//			sb.append("PhoneNumber:" + siminfo.getNativePhoneNumber()+ "<br />");
//			sb.append("IMSI:" + siminfo.getIMSI() + "<br />");
//    		json.put("simInfo", sb.toString());
//    		
//    		StringBuffer sysSB = new StringBuffer();
//    		sysSB.append("Product: " + android.os.Build.PRODUCT + "<br />");
//    		sysSB.append(", CPU_ABI: " + android.os.Build.CPU_ABI + "<br />");
//    		sysSB.append(", TAGS: " + android.os.Build.TAGS + "<br />");
//    		sysSB.append(", VERSION_CODES.BASE: "
//					+ android.os.Build.VERSION_CODES.BASE + "<br />");
//    		sysSB.append(", MODEL: " + android.os.Build.MODEL + "<br />");
//    		sysSB.append(", SDK: " + android.os.Build.VERSION.SDK + "<br />");
//    		sysSB.append(", VERSION.RELEASE: "
//					+ android.os.Build.VERSION.RELEASE + "<br />");
//    		sysSB.append(", DEVICE: " + android.os.Build.DEVICE + "<br />");
//    		sysSB.append(", DISPLAY: " + android.os.Build.DISPLAY + "<br />");
//    		sysSB.append(", BRAND: " + android.os.Build.BRAND + "<br />");
//    		sysSB.append(", BOARD: " + android.os.Build.BOARD + "<br />");
//    		sysSB.append(", FINGERPRINT: " + android.os.Build.FINGERPRINT
//					+ "<br />");
//    		sysSB.append(", ID: " + android.os.Build.ID + "<br />");
//    		sysSB.append(", MANUFACTURER: " + android.os.Build.MANUFACTURER
//					+ "<br />");
//    		sysSB.append(", USER: " + android.os.Build.USER + "<br />");
//    		json.put("sysList", sysSB.toString().replaceAll(",", ""));
//    		
//    		TelephonyManager telephonyManager = (TelephonyManager) DemoAppActivity.myself.getSystemService(Context.TELEPHONY_SERVICE);	
//    		json.put("telID", "Device Id via telephonyManager:<br />" + telephonyManager.getDeviceId());
//    		
//    		String jsonStr = json.toString();
////    		jsonStr = jsonStr.replaceAll("\r", "<br />");
////    		jsonStr = jsonStr.replaceAll("\t", "   ");
//    		System.out.println( jsonStr );
//    		
////    		JSONObject newJson = new JSONObject( jsonStr );
//    		
//    		try {
//				String res = MyUtil.sendPost(url, jsonStr, "application/json");
//				Log.d(LOGTAG, "Post Response:" + res);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//    		
//    	}
//    }

}
