package com.koumamod.prevail;


import android.app.Activity;
import android.os.Bundle;
//import android.view.View;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
//import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.lang.Runtime;
import java.io.*;
import com.stericson.RootTools.RootTools;
import android.widget.Toast;
import android.content.SharedPreferences;

//import android.os.storage.*;

public class KoumamodActivity extends Activity {
	  private RadioGroup radioModGroup;
//	  private RadioButton radioModButton;
	  private CheckBox checkDataMode;
    /** Called when the activity is first created. */
	  Process p;
	  public static final String PREFS_NAME = "KoumaModSettings";
	  public String KoumaModFileData;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);
        try {
        	p = Runtime.getRuntime().exec("su");
        } catch (java.io.IOException e){}
		radioModGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		checkDataMode = (CheckBox) findViewById(R.id.checkBox1);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        checkDataMode.setClickable(settings.getBoolean("InternEna", false));
        checkDataMode.setChecked(settings.getBoolean("MountIntern", false));
        radioModGroup.check(settings.getInt("ModMode",R.id.radio0));
        myDataMode();
    }
    public void myDataMode(){
		radioModGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioModGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			//@Override
			public void onCheckedChanged(RadioGroup myGroup, int mycheckid) {
				int selectedId = radioModGroup.getCheckedRadioButtonId();
//				radioModButton = (RadioButton) findViewById(selectedId);
				checkDataMode = (CheckBox) findViewById(R.id.checkBox1);
		    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    	SharedPreferences.Editor editor = settings.edit();
				switch (selectedId) {
				case R.id.radio1:
					checkDataMode.setClickable(true);
					break;
				case R.id.radio0:
				case R.id.radio2:
					checkDataMode.setClickable(false);
					checkDataMode.setChecked(false);
			    	editor.putBoolean("MountIntern", checkDataMode.isChecked());
					break;
				}
		    	editor.putBoolean("InternEna", checkDataMode.isClickable());
		    	editor.putInt("ModMode", selectedId);
		    	editor.commit();
		        RootTools.remount("/system/", "rw");
		        KoumaModFileData = (
		        		getString(R.string.modtext_header) + "\n" + ((checkDataMode.isChecked())? "" : getString(R.string.modtext_internal)) + 
		        		((selectedId==R.id.radio1) ? getString(R.string.modtext_data) : "") +
		        		getString(R.string.modtext_coreblock1) +
		        		((selectedId==R.id.radio2) ? getString(R.string.modtext_ssm) : "") +
		        		getString(R.string.modtext_coreblock2)
		        		);
		        editor.putString("KoumaModText", KoumaModFileData);
		        editor.commit();
		        try {
		        	File outfile = new File("/system/koumamod");
		        	if (outfile.canWrite()) {
		        		FileWriter writer = new FileWriter(outfile);
		        		writer.write(KoumaModFileData);
		        		writer.flush();
		        		writer.close();
		        	}
		        } catch (java.io.IOException e){}
		        RootTools.remount("/system/", "ro");
		        Toast.makeText(KoumamodActivity.this, "Settings saved.\nReboot for changes to take effect.", Toast.LENGTH_LONG).show();
			}
		});
		checkDataMode.setOnClickListener(new OnClickListener() {
	 
			public void onClick(View v) {
				int selectedId = radioModGroup.getCheckedRadioButtonId();
		    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    	SharedPreferences.Editor editor = settings.edit();
		    	editor.putBoolean("MountIntern", checkDataMode.isChecked());
		    	editor.commit();
		        RootTools.remount("/system/", "rw");
		        KoumaModFileData = (
		        		getString(R.string.modtext_header) + ((checkDataMode.isChecked())? "" : getString(R.string.modtext_internal)) + 
		        		((selectedId==R.id.radio1) ? getString(R.string.modtext_data) : "") +
		        		getString(R.string.modtext_coreblock1) +
		        		((selectedId==R.id.radio2) ? getString(R.string.modtext_ssm) : "") +
		        		getString(R.string.modtext_coreblock2)
		        		);
		        editor.putString("KoumaModText", KoumaModFileData);
		        editor.commit();
		        try {
		        	File outfile = new File("/system/koumamod");
		        	if (outfile.canWrite()) {
		        		FileWriter writer = new FileWriter(outfile);
		        		writer.write(KoumaModFileData);
		        		writer.flush();
		        		writer.close();
		        	}
		        } catch (java.io.IOException e){}
		        RootTools.remount("/system/", "ro");
		        Toast.makeText(KoumamodActivity.this, "Settings saved.\nReboot for changes to take effect.", Toast.LENGTH_LONG).show();
			}
		});
    }
}

	
 

