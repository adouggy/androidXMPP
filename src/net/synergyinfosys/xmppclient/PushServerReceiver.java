package net.synergyinfosys.xmppclient;

import net.synergyinfosys.xmppclient.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

public class PushServerReceiver extends BroadcastReceiver {
    public static final String TAG = "PushServerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra(Constants.XMPP_USERNAME);
        String pwd = intent.getStringExtra(Constants.XMPP_PASSWORD);
        String pname = intent.getStringExtra(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME);
        String cname = intent.getStringExtra(Constants.CALLBACK_ACTIVITY_CLASS_NAME);
        Log.e(TAG, "onReceive  name == " + name + "    pwd == " + pwd);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            Editor editor = sharedPrefs.edit();
            editor.putString(Constants.XMPP_USERNAME, name);
            editor.putString(Constants.XMPP_PASSWORD, pwd);
            editor.putString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, pname);
            editor.putString(Constants.CALLBACK_ACTIVITY_CLASS_NAME, cname);
            editor.commit();
        }

        ServiceManager serviceManager = new ServiceManager(context);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();

    }

}
