package com.koumamod.prevail;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.LinearLayout;

import com.stericson.RootTools.Mount;
import com.stericson.RootTools.RootTools;

public class KoumaswapMenu extends Activity {
	public SeekBar swappinessbar;
	public TextView swappinessInd;
	public Button SaveBut; 
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	public EditText pathtext;
	public EditText prioritytest;
	public TableLayout pathtable;
	public static final String PREFS_NAME = "KoumaModSettings";
	public String paths;
	public String[] PathGroups;
	public String[][] pathItems;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.koumaswap);
		pathtable = (TableLayout) findViewById(R.id.tableLayout1);
		settings = getSharedPreferences(PREFS_NAME, 0);
		pathtext = (EditText) findViewById(R.id.pathText1);
		prioritytest = (EditText) findViewById(R.id.priorityText1);
		LoadPaths();
		swappinessbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
		
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
						swappinessInd.setText(Integer.toString(seekBar.getProgress()));
			}
		
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
		
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		SaveBut = (Button) findViewById(R.id.SaveButton);
		SaveBut.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				boolean addflag = false;
			   	editor = settings.edit();
			   	editor.putString("Swappiness", Integer.toString(swappinessbar.getProgress()));
			   	paths = "";
			   	for(int i=1;i<pathtable.getChildCount();i++){
			   		TableRow checkrow = (TableRow) findViewById(pathtable.getChildAt(i).getId());
			   		for(int x=0;x<checkrow.getChildCount();x++){
		   			LinearLayout checklay = (LinearLayout) findViewById(checkrow.getChildAt(x).getId());
				   		for(int y=0;y<checklay.getChildCount();y++){
				   			TextView checktext = (TextView) findViewById(checklay.getChildAt(y).getId());
				   			addflag = y == 0 ? !(checktext.getText().toString().isEmpty()) : addflag;
				   			paths = addflag ? y == 0 ? paths + checktext.getText().toString() : paths + "," + checktext.getText().toString() : paths;
				   		}
				   	}
				   	paths = paths == "" ? paths : addflag ? paths + "\t" : paths;
			   	};
			   	editor.putString("swaplist", paths);
			   	editor.putString("dependancies", calculateDeps(pathItems));
			   	editor.commit();
		        Intent serviceIntent = new Intent(KoumaswapMenu.this, KoumaSwap.class);//"com.koumamod.prevail.KoumaSwap");
				startService(serviceIntent);
		        Toast.makeText(KoumaswapMenu.this, "Settings saved.", Toast.LENGTH_LONG).show();
		        pathtable.removeViews(2, pathtable.getChildCount()-2);
		        LoadPaths();
			}
		});
		pathtext = (EditText) findViewById(R.id.pathText1);
		addTextChanger(pathtext);
	}
	private void LoadPaths(){
		swappinessbar = (SeekBar) findViewById(R.id.seekBar1);
		swappinessInd =(TextView) findViewById(R.id.seekPosHumanReadable);
		swappinessInd.setText(settings.getString("Swappiness","60"));
		swappinessbar.setProgress(Integer.parseInt(settings.getString("Swappiness","60")));
		String loadpaths = settings.getString("swaplist", "/dev/stl13,100\t");
		PathGroups = loadpaths.split("\t");
		pathItems=new String[PathGroups.length][];
		for(int i=0;i<PathGroups.length;i++){
			pathItems[i]=PathGroups[i].split(",");
			for(int t=0;t<pathItems[i].length;t++){
			}
			if(i>0){
				addRow(i);
			}
			if(pathItems[i].length>0){
				pathtext.setText(pathItems[i][0]);
				if(pathItems[i].length>1){
					prioritytest.setText(pathItems[i][1]);
				}
			}
		}
		if(PathGroups.length<4){
			addRow(PathGroups.length);
		}
	}
	private void addRow(int index){
		TableRow tr =new TableRow(this);
		tr.setLayoutParams(findViewById(R.id.tableRow2).getLayoutParams());
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(findViewById(R.id.linearLayout2).getLayoutParams());
		EditText tv1 = new EditText(this);
		tv1.setLayoutParams(findViewById(R.id.pathText1).getLayoutParams());
		tv1.setInputType(pathtext.getInputType());
		tv1.setHint(pathtext.getHint());
		tv1.setId(200 + ++index);
		EditText tv2 = new EditText(this);
		tv2.setLayoutParams(findViewById(R.id.priorityText1).getLayoutParams());
		tv2.setInputType(prioritytest.getInputType());
		tv2.setId(300+index);
		ll.addView(tv1);
		ll.addView(tv2);
		ll.setId(400+index);
		tr.addView(ll);
		tr.setId(500+index);
		pathtable.addView(tr, index);
		pathtext=tv1;
		addTextChanger(pathtext);
		prioritytest=tv2;
	}
	private String calculateDeps(String[][] Items){
		String Deps = "";
		for(int i = 0;i<Items.length;i++){
			ArrayList<Mount> curMounts = new ArrayList<Mount>();
			try{
				curMounts = RootTools.getMounts();
			}catch (Exception e){
			}
			if (!curMounts.isEmpty()){
				String M = "";
				for (int x=0;x<curMounts.size();x++){
					M = Items[i][1].contains(curMounts.get(x).getMountPoint().getAbsolutePath()) ?
							M.length()<curMounts.get(x).getMountPoint().getAbsolutePath().length() ?
									curMounts.get(x).getMountPoint().getAbsolutePath() : M : M;					
				}
				M = M.isEmpty() ? "<none>" : M;
				Deps = Deps.isEmpty() ? M : Deps + "," + M;
			}
		}
		return Deps;
	}
	private void addTextChanger(EditText h){
		h.setLongClickable(true);
		h.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v){
				Intent browseact = new Intent(KoumaswapMenu.this,KoumaFileBrowser.class);
				startActivityForResult(browseact,v.getId());
				return true;
			}
		});
	}
	@Override
	public void onActivityResult(int requestcode,int resultcode,Intent data){
		if(resultcode==RESULT_OK){
			EditText resultDest = (EditText) findViewById(requestcode);
			resultDest.setText(data.getDataString());
		}
	}
}