package com.koumamod.prevail;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import java.io.*;
import com.stericson.RootTools.RootTools;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

public class KoumamodActivity extends Activity {
	  private RadioGroup radioModGroup;
	  private CheckBox checkDataMode;
	  private EditText swappyBox;
	  private Button SaveBut;
    /** Called when the activity is first created. */
	  Process p;
	  public static final String PREFS_NAME = "KoumaModSettings";
	  public String KoumaModFileData;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);
		radioModGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		checkDataMode = (CheckBox) findViewById(R.id.checkBox1);
		swappyBox = (EditText) findViewById(R.id.SwappinessText);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        checkDataMode.setClickable(settings.getBoolean("InternEna", false));
		checkDataMode.setVisibility(checkDataMode.isClickable()? android.view.View.VISIBLE : android.view.View.GONE);
        checkDataMode.setChecked(settings.getBoolean("MountIntern", false));
        radioModGroup.check(settings.getInt("ModMode",R.id.radio0));
        swappyBox.setText(settings.getString("Swappiness","60"));
        myDataMode();
    }
    public void savesettings(CheckBox c, int selId) {
		swappyBox = (EditText) findViewById(R.id.SwappinessText);
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putBoolean("MountIntern", c.isChecked());
    	editor.putBoolean("InternEna", c.isClickable());
    	editor.putInt("ModMode", selId);
    	if (swappyBox.getText().toString()==""){
    		swappyBox.setText("60");
    	}
    	editor.putString("Swappiness", swappyBox.getText().toString());
        KoumaModFileData = (
        		getString(R.string.modtext_header) + "\n" + ((c.isChecked())? "" : getString(R.string.modtext_internal)) + 
        		((selId==R.id.radio1) ? getString(R.string.modtext_data) : "") +
        		getString(R.string.modtext_coreblock1) +
        		((selId==R.id.radio2) ? getString(R.string.modtext_ssm) : "") +
        		getString(R.string.modtext_coreblock2)
        		);
        editor.putString("KoumaModText", KoumaModFileData);
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
		Intent serviceIntent = new Intent("com.koumamod.prevail.KoumaSwap");
		startActivity(serviceIntent);
        Toast.makeText(KoumamodActivity.this, "Settings saved.\nReboot for changes to take effect.", Toast.LENGTH_LONG).show();
    }
    public void myDataMode(){
		radioModGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioModGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup myGroup, int mycheckid) {
				int selectedId = radioModGroup.getCheckedRadioButtonId();
				checkDataMode = (CheckBox) findViewById(R.id.checkBox1);
				switch (selectedId) {
				case R.id.radio1:
					checkDataMode.setClickable(true);
					checkDataMode.setVisibility(android.view.View.VISIBLE);
					break;
				case R.id.radio0:
				case R.id.radio2:
					checkDataMode.setClickable(false);
					checkDataMode.setChecked(false);
					checkDataMode.setVisibility(android.view.View.GONE);
					break;
				}
			}
		});

		swappyBox = (EditText) findViewById(R.id.SwappinessText);
		swappyBox.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				try {
					int val = Integer.parseInt(s.toString());
					if(val > 100) {
						s.replace(0, s.length(), "100");
					} else if(val < 1) {
						s.replace(0, s.length(), "1");
					}
				} catch (NumberFormatException ex) {
			      // Do something
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}});
		SaveBut = (Button) findViewById(R.id.SaveButton);
		SaveBut.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				radioModGroup = (RadioGroup) findViewById(R.id.radioGroup1);
				int selectedId = radioModGroup.getCheckedRadioButtonId();
				checkDataMode = (CheckBox) findViewById(R.id.checkBox1);
				savesettings(checkDataMode, selectedId);				
			}
		});
    }
}
