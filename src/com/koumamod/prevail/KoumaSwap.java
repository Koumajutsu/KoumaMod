package com.koumamod.prevail;

import java.io.File;

import com.stericson.RootTools.RootTools;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

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
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		KoumaSwapData = settings.getString("swaplist", " , \t");
		KoumaSwapList = KoumaSwapData.split("\t");
		KoumaSwapParsed=new String[KoumaSwapList.length][];
		swappiness = settings.getString("Swappiness","60");
		try {
			RootTools.sendShell("echo " + swappiness + "> /proc/sys/vm/swappiness",-1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i=0;i<KoumaSwapList.length;i++){
			KoumaSwapParsed[i]=KoumaSwapList[i].split(",");
		}
		KoumaDepData = settings.getString("dependancies", "<none>");
		KoumaDependancies = KoumaDepData.split(",");
		boolean done = true;
		boolean[] alldone = new boolean[KoumaSwapList.length];
		do {
			done=true;
			for(int i=0;i<KoumaSwapList.length;i++){
				File swap = new File(KoumaSwapParsed[i][0]);
				if(swap.exists()&&!alldone[i]){
	    				try{
	    					RootTools.sendShell("swapon " + (KoumaSwapParsed.length > 1 ?  "-p "+KoumaSwapParsed[i][1]+" " + swap.getAbsolutePath() : swap.getAbsolutePath()), -1);
	    				} catch (Exception e) {}
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