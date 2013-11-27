package net.synergyinfosys.xmppclient.test;

import org.jivesoftware.smack.XMPPConnection;

import net.synergyinfosys.xmppclient.Constants;
import net.synergyinfosys.xmppclient.NotificationService;
import net.synergyinfosys.xmppclient.ServiceManager;
import net.synergyinfosys.xmppclient.XmppManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

public class TestCaseXMPPService extends ServiceTestCase<NotificationService> {

	public static final String SHARED_PREFERENCE_NAME = "client_preferences";
	public static final String PREFERENCE_USERNAME = "username";
	public static final String PREFERENCE_PASSWORD = "password";
	public static final String PREFERENCE_PUSHNAME = Constants.XMPP_USERNAME;
	public static final String PREFERENCE_PUSHPSWD = Constants.XMPP_PASSWORD;

	public TestCaseXMPPService() {
		super(NotificationService.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Intent intent = NotificationService.getIntent();
		getContext().stopService(intent);
	}

	@SmallTest
	public void testPreconditions() {
	}

	@SmallTest
	public void testSerivceConnection() {

		SharedPreferences sharedPrefs = getContext().getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPrefs.edit();
		editor.putString(PREFERENCE_USERNAME, "login_name_need_to_be_replace");
		editor.putString(PREFERENCE_PASSWORD, "login_name_need_to_be_replace");
		editor.putString(PREFERENCE_PUSHNAME, "login_name_need_to_be_replace");
		editor.putString(PREFERENCE_PUSHPSWD, "login_name_need_to_be_replace");
		editor.putString("XMPP_HOST", "114.242.189.52");
		editor.putInt("XMPP_PORT", 5222);
		editor.putString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "net.synergyinfosys.xmppclienttest");
		editor.putString(Constants.CALLBACK_ACTIVITY_CLASS_NAME, this.getClass().getName());
		editor.commit();

		Intent intent = new Intent(getContext(), NotificationService.class);
		startService(intent);

		final long testDuration = 60 * 1000 *3; // a minute
		

		long start = System.currentTimeMillis();
		long curr = start;
		while (curr - start <= testDuration) {
			NotificationService service = getService();
			if (service == null) {
				System.out.println("Current service is null..");
			} else {
				XmppManager xm = service.getXmppManager();
				if (xm == null) {
					System.out.println("Xmpp manager is null");
				} else {
					XMPPConnection conn = xm.getConnection();
					if (conn == null) {
						System.out.println("Connection is null..");
					} else {
						System.out.println("Connection:" + conn.isConnected());
					}
				}
			}
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			curr = System.currentTimeMillis();
		}

	}

	@MediumTest
	public void testStopable() {

	}
}
