package com.koumamod.prevail;
 
import java.io.File;

import android.app.Activity;
import android.os.Bundle;

import com.stericson.RootTools.RootTools;

public class KoumaSwapCreate extends Activity {
	@Override
	public void onCreate(Bundle info) {
		File newSwap = (File)info.get("filename");
		String value = info.getString("filesize");
		if(newSwap.getAbsolutePath().contains("/system")){
			RootTools.remount("/system/", "rw");
		}
		try{
			RootTools.sendShell("dd if=/dev/zero of=" + newSwap.getAbsolutePath() + " bs=1048576 count="+ value,-1);
			RootTools.sendShell("mkswap " + newSwap.getAbsolutePath(),-1);							
		}catch (Exception e){}
			if(newSwap.getAbsolutePath().contains("/system")){							
			RootTools.remount("/system/", "ro");								
		}
		finish();
	}
}