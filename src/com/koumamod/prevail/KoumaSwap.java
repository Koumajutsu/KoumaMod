package com.koumamod.prevail;

import android.app.Activity;
import android.app.IntentService;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.Mount;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import android.content.SharedPreferences;

public class KoumaSwap extends IntentService {
    public KoumaSwap() {
		super("KoumaSwap");
		// TODO Auto-generated constructor stub
	}
	/** Called when the activity is first created. */
	  public static final String PREFS_NAME = "KoumaModSettings";
	  public String KoumaSwapData;
	  public String[] KoumaSwapList;
	  public String[][] KoumaSwapParsed;
	  public String currentfstab;
	  public String generatedfstab;
	  public static final String fstab_bits1 = "\tswap\tswap\t";
	  public static final String fstab_bits2 = ",defaults\t0\t0\n";
	  public static final String fstab_pri = "pri=";
    @Override
    public void onHandleIntent(Intent intent) {
        //super.onCreate(state);
        //KoumaSwap.this.setVisible(false);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        KoumaSwapData = settings.getString("swaplist", " , \t");
  		KoumaSwapList = KoumaSwapData.split("\t");
  		KoumaSwapParsed=new String[KoumaSwapList.length][];
  		List<Mount> myMounts = new ArrayList<Mount>();
  		try {
			myMounts = RootTools.getMounts();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
  		for(int i=0;i<myMounts.size();i++){
  			Log.i("mounts",myMounts.get(i).getDevice().getName());
  		}
  		for(int i=0;i<KoumaSwapList.length;i++){
  			KoumaSwapParsed[i]=KoumaSwapList[i].split(",");
  		}
        try {
			if (RootTools.isAccessGiven()) {
		            try {
	            		File fstabfile = new File("/system/etc/fstab");
		            	generatedfstab="";
		            	for(int i=0;i<KoumaSwapList.length;i++){
		            		generatedfstab = generatedfstab + KoumaSwapParsed[i][0] + fstab_bits1 + (KoumaSwapParsed[i][1].isEmpty()? "" : fstab_pri + KoumaSwapParsed[i][1]) + fstab_bits2;
		            	}
		            	if(fstabfile.canRead()){
		            		FileReader reader = new FileReader(fstabfile);
		            		char[] s = new char[(int)fstabfile.length()];
		            		reader.read(s);
		            		reader.close();
		            		currentfstab = new String(s);
		            	}
			            if(!currentfstab.contentEquals(generatedfstab)){
			                try {
				                RootTools.remount("/system/", "rw");
				                if (fstabfile.canWrite()) {
			                		FileWriter writer = new FileWriter(fstabfile);
			                		writer.write(generatedfstab);
			                		writer.flush();
			                		writer.close();
					                RootTools.remount("/system/", "ro");
			                	}
			                } catch (IOException e){}
			            }
						RootTools.sendShell("echo "+settings.getString("Swappiness", "60")+" > /proc/sys/vm/swappiness", 10000);
			            RootTools.sendShell("swapon -a", 300000);
					} catch (RootToolsException e) {
							// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		} catch (TimeoutException e) {}
        stopSelf();
//        finish();
    }
}
