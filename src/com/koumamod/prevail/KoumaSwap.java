package com.koumamod.prevail;

import java.io.File;

import com.stericson.RootTools.RootTools;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class KoumaSwap extends IntentService {
	public KoumaSwap() {
	super("KoumaSwap");
	// TODO Auto-generated constructor stub
	}
	/** Called when the activity is first created. */
	public static final String PREFS_NAME = "KoumaModSettings";
	public String swappiness;
	public String KoumaSwapData;
	public String KoumaDepData;
	public String[] KoumaDependancies;
	public String[] KoumaSwapList;
	public String[][] KoumaSwapParsed;
	@Override
	public void onHandleIntent(Intent intent) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,7);
		KoumaSwapData = settings.getString("swaplist", " , \t");
		KoumaSwapList = KoumaSwapData.split("\t");
		KoumaSwapParsed=new String[KoumaSwapList.length][];
		swappiness = settings.getString("Swappiness","60");
		try {
			RootTools.sendShell("echo " + swappiness + "> /proc/sys/vm/swappiness",-1);
			Log.i("Swappiness set to:",swappiness);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		KoumaSwapData = settings.getString("swaplist", " , \t");
		KoumaSwapList = KoumaSwapData.split("\t");
		KoumaSwapParsed = new String[KoumaSwapList.length][];
		
		for(int i=0;i<KoumaSwapList.length;i++){
			KoumaSwapParsed[i]=KoumaSwapList[i].split(",");
		}
		boolean done = true;
		boolean[] alldone = new boolean[KoumaSwapList.length];
		do {
			done=true;
			for(int i=0;i<KoumaSwapList.length;i++){
				File swap = new File(KoumaSwapParsed[i][0]);
				if(swap.exists()&&!alldone[i]){
	    				try{
	    					if(KoumaSwapParsed[i].length > 1){
	    						RootTools.sendShell("swapon " + "-p " + KoumaSwapParsed[i][1]+" " + swap.getAbsolutePath(), -1);
	    					} else {
		    					RootTools.sendShell("swapon " + swap.getAbsolutePath(), -1);
	    					}
	    					Log.i("Swap starded for:",swap.getAbsolutePath());
	    				} catch (Exception e) {e.printStackTrace();}
	        			alldone[i]=true;
	    		} else if(!alldone[i]) {
	    			done=false;
	    			try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
			}
		} while (!done);
		stopSelf();
	}
}