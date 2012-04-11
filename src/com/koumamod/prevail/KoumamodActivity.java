package com.koumamod.prevail;

//import android.Radio.Butt.tut.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

//import android.os.storage.*;

public class KoumamodActivity extends Activity {
	  private RadioGroup radioModGroup;
	  private RadioButton radioModButton;
	  private CheckBox checkDataMode;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkDataMode();
    }
    public void checkDataMode(){
		radioModGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioModGroup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int selectedId = radioModGroup.getCheckedRadioButtonId();
				radioModButton = (RadioButton) findViewById(selectedId);
				CharSequence SelMode = radioModButton.getText();
				checkDataMode = (CheckBox) findViewById(R.id.checkBox1);
				if(SelMode=="Data") {
					checkDataMode.setClickable(true);
				}
				else {
					checkDataMode.setClickable(false);
				}
			}
		});
    }
}

	
 

