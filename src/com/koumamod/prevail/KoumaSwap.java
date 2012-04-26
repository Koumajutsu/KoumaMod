package com.koumamod.prevail;

import android.app.Activity;
import android.os.Bundle;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import android.content.SharedPreferences;

public class KoumaSwap extends Activity {
    /** Called when the activity is first created. */
	  public static final String PREFS_NAME = "KoumaModSettings";
	  public String KoumaModFileData;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        try {
			if (RootTools.isAccessGiven()) {
		            try {
							RootTools.sendShell("swapon /dev/block/mmcblk0p3", 10000);
							RootTools.sendShell("echo "+settings.getString("Swappiness", "60")+" > /proc/sys/vm/swappiness", 10000);
					} catch (RootToolsException e) {
							// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		} catch (TimeoutException e) {}
        finish();
    }
}
