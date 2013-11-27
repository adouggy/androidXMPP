package net.synergyinfosys.xmppclient.test;

import net.synergyinfosys.xmppclient.Constants;
import net.synergyinfosys.xmppclient.R;
import net.synergyinfosys.xmppclient.ServiceManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;

public class MainActivity extends Activity {
    
    public static final String SHARED_PREFERENCE_NAME = "client_preferences";
    public static final String PREFERENCE_USERNAME = "username";
    public static final String PREFERENCE_PASSWORD = "password";
    public static final String PREFERENCE_PUSHNAME = Constants.XMPP_USERNAME;
    public static final String PREFERENCE_PUSHPSWD = Constants.XMPP_PASSWORD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        demo();
        
        System.out.println("sm.startService();");
        ServiceManager sm = new ServiceManager(getBaseContext());
        sm.startService();

    }

    private void demo() {
    	SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
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
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
