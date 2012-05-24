package com.koumamod.prevail;

import java.io.*;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.stericson.RootTools.RootTools;

public class KoumamodMenu extends Activity {
	  public RadioGroup selectedMode;
	  public CheckBox checkDataMode;
	  public CheckBox swapSSMMode;
	  public Button SaveBut;
    /** Called when the activity is first created. */
	  Process p;
	  public static final String PREFS_NAME = "KoumaModSettings";
	  public String KoumaModFileData;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.koumamod);
		selectedMode = (RadioGroup) findViewById(R.id.Settings);
		checkDataMode = (CheckBox) findViewById(R.id.optDisInt);
		swapSSMMode = (CheckBox) findViewById(R.id.optSwpSSM);
        File readsettings = new File("/system/koumamod");
		char[] charsread = new char[2];
        try {
			FileReader reader = new FileReader(readsettings);
			reader.read(charsread);
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(charsread[0]==35){
        	switch (charsread[1]&3) {
    		case 2: selectedMode.check(R.id.optData); //data mode
    			checkDataMode.setClickable(true);
    			checkDataMode.setVisibility(View.VISIBLE);
    			swapSSMMode.setClickable(false);
    			swapSSMMode.setVisibility(View.GONE);
    			checkDataMode.setChecked((charsread[1]&4)==4);
    			swapSSMMode.setChecked(false);
    			break;
        	case 1:  selectedMode.check(R.id.optSSM); //ssm mode
        		checkDataMode.setClickable(false);
				checkDataMode.setVisibility(View.GONE);
				swapSSMMode.setClickable(true);
				swapSSMMode.setVisibility(View.VISIBLE);
				swapSSMMode.setChecked((charsread[1]&8)==8);
				checkDataMode.setChecked(false);
				break;
        	case 0:  selectedMode.check(R.id.optNone); //none
        		checkDataMode.setClickable(false);
        		checkDataMode.setVisibility(View.GONE);
        		swapSSMMode.setClickable(false);
        		swapSSMMode.setVisibility(View.GONE);
    			swapSSMMode.setChecked(false);
				checkDataMode.setChecked(false);
        		break;
        	}
        }else{ //older version of koumamod file, read from sharedprefs xml
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,7);
        checkDataMode.setClickable(settings.getInt("ModMode",R.id.optNone)==R.id.optData);
		checkDataMode.setVisibility(checkDataMode.isClickable()? View.VISIBLE : View.GONE);
        checkDataMode.setChecked(settings.getBoolean("MountIntern", false));
		swapSSMMode.setClickable(settings.getInt("ModMode",R.id.optNone)==R.id.optSSM);
		swapSSMMode.setVisibility(swapSSMMode.isClickable()? View.VISIBLE : View.GONE);
		swapSSMMode.setChecked(false); //older version didn't have this option, default to false
        selectedMode.check(settings.getInt("ModMode", android.os.Build.ID.contains("Chicken Tits") ? R.id.optSSM :R.id.optNone ));
        }
        myDataMode();
	}
    public void savesettings() {
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME,7);
    	Map<String,?> prefsMap = settings.getAll();
    	SharedPreferences.Editor editor = settings.edit();
    	for(Map.Entry<String,?> entry : prefsMap.entrySet()){
    		if(String.class.getName().equals(entry.getValue().getClass().getName())) {
    			editor.putString(entry.getKey(), (String) entry.getValue());
    		}else if(Integer.class.getName().equals(entry.getValue().getClass().getName())){
    			editor.putInt(entry.getKey(), (Integer) entry.getValue());
    		}else if(Boolean.class.getName().equals(entry.getValue().getClass().getName())){
    			editor.putBoolean(entry.getKey(), (Boolean) entry.getValue());
    		}
    	}
    	editor.putBoolean("MountIntern", checkDataMode.isChecked());
    	int selId=selectedMode.getCheckedRadioButtonId();
    	editor.putInt("ModMode", selId);
    	char SettingsEncoded = (char) ((selId==R.id.optSSM?1:0)|(selId==R.id.optData?2:0)|(checkDataMode.isChecked()?4:0)|(swapSSMMode.isChecked()?8:0));
    	String ssmText = getString(R.string.modtext_ssm_start)+" "+getString(swapSSMMode.isChecked()?R.string.modtext_ssm_device2:R.string.modtext_ssm_device1)+" "+getString(R.string.modtext_ssm_app)
    				+getString(R.string.modtext_ssm_start)+" "+getString(swapSSMMode.isChecked()?R.string.modtext_ssm_device1:R.string.modtext_ssm_device2)+" "+getString(R.string.modtext_ssm_dalvik);
        KoumaModFileData = ("#"+ SettingsEncoded +"\n"+
        		getString(R.string.modtext_header) + "\n" + ((checkDataMode.isChecked())? "" : getString(R.string.modtext_internal)) + 
        		((selectedMode.getCheckedRadioButtonId()==R.id.optData) ? getString(R.string.modtext_data) : "") +
        		getString(R.string.modtext_coreblock1) +
        		((selectedMode.getCheckedRadioButtonId()==R.id.optSSM) ? ssmText : "") +
        		getString(R.string.modtext_coreblock2)
        		);
    	editor.commit();
        RootTools.remount("/system/", "rw");
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
        Toast.makeText(KoumamodMenu.this, "Settings saved.\nReboot for changes to take effect.", Toast.LENGTH_LONG).show();
    }
    public void myDataMode(){
		selectedMode = (RadioGroup) findViewById(R.id.Settings);
		selectedMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup myGroup, int mycheckid) {
				int selectedId = selectedMode.getCheckedRadioButtonId();
				checkDataMode = (CheckBox) findViewById(R.id.optDisInt);
				switch (selectedId) {
				case R.id.optData:
					checkDataMode.setClickable(true);
					checkDataMode.setVisibility(View.VISIBLE);
					swapSSMMode.setClickable(false);
					swapSSMMode.setChecked(false);
					swapSSMMode.setVisibility(View.GONE);
					break;
				case R.id.optNone:
					checkDataMode.setClickable(false);
					checkDataMode.setChecked(false);
					checkDataMode.setVisibility(View.GONE);
					swapSSMMode.setClickable(false);
					swapSSMMode.setChecked(false);
					swapSSMMode.setVisibility(View.GONE);
					break;
				case R.id.optSSM:
					swapSSMMode.setClickable(true);
					swapSSMMode.setVisibility(View.VISIBLE);
					checkDataMode.setClickable(false);
					checkDataMode.setChecked(false);
					checkDataMode.setVisibility(View.GONE);
					break;
				}
			}
		});
		SaveBut = (Button) findViewById(R.id.SaveButton);
		SaveBut.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				selectedMode = (RadioGroup) findViewById(R.id.Settings);
				checkDataMode = (CheckBox) findViewById(R.id.optDisInt);
				savesettings();				
			}
		});
    }
}
