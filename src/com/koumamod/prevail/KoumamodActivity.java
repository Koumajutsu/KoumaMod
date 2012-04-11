package com.koumamod.prevail;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.lang.Runtime;

//import android.os.storage.*;

public class KoumamodActivity extends Activity {
	  private RadioGroup radioModGroup;
//	  private RadioButton radioModButton;
	  private CheckBox checkDataMode;
    /** Called when the activity is first created. */
	  Process p;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    try {
        p = Runtime.getRuntime().exec("su");
    } catch (java.io.IOException e){}
        checkDataMode();
    }
    public void checkDataMode(){
		radioModGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioModGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			//@Override
			public void onCheckedChanged(RadioGroup myGroup, int mycheckid) {
				int selectedId = radioModGroup.getCheckedRadioButtonId();
//				radioModButton = (RadioButton) findViewById(selectedId);
				checkDataMode = (CheckBox) findViewById(R.id.checkBox1);
				switch (selectedId) {
				case R.id.radio1:
					checkDataMode.setClickable(true);
					break;
				case R.id.radio0:
				case R.id.radio2:
					checkDataMode.setClickable(false);
					checkDataMode.setChecked(false);
					break;
				}
			}
		});
    }
}

	
 

