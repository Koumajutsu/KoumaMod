package com.koumamod.prevail;
 
import java.io.File;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.stericson.RootTools.RootTools;

public class KoumaSwapCreate extends IntentService {
	public KoumaSwapCreate() {
		super("KoumaSwapCreate");
	}
	@Override
	public void onHandleIntent(Intent infoX) {
		Bundle extras = infoX.getExtras();
		File newSwap = new File(extras.getString("filename"));
		String value = extras.getString("filesize");
		if(newSwap.getAbsolutePath().contains("/system")){
			RootTools.remount("/system/", "rw");
		}
		try{
			RootTools.sendShell("dd if=/dev/zero of=" + newSwap.getAbsolutePath() + " bs=1048576 count="+ value,-1);
			RootTools.sendShell("mkswap " + newSwap.getAbsolutePath(),-1);
			newSwap.setReadable(true, false);
			RootTools.sendShell("chmod 666 " + newSwap.getAbsolutePath(),-1);
		}catch (Exception e){}
			if(newSwap.getAbsolutePath().contains("/system")){							
			RootTools.remount("/system/", "ro");								
		}
		stopSelf();
	}
}