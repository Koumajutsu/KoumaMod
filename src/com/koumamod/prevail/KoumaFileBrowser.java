package com.koumamod.prevail;
 
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import com.stericson.RootTools.RootTools;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
 
public class KoumaFileBrowser extends ListActivity {

	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }

	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	private List<String> directoryEntries = new ArrayList<String>();
	private File currentDirectory = new File("/");
	
	@Override
	public void onBackPressed(){
		upOneLevel();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// setContentView() gets called within the next line,
		// so we do not need it here.
		browseToRoot();
	}
	/**
	* This function browses to the
	* root-directory of the file-system.
	*/
	private void browseToRoot() {
		browseTo(new File("/"));
	}
       
	/**
	* This function browses up one level
	* according to the field: currentDirectory
	*/
	private void upOneLevel(){
		if(this.currentDirectory.getParent() != null){
			this.browseTo(this.currentDirectory.getParentFile());
		} else {
			finish();
		}
	}

	private void browseTo(final File aDirectory){
		if (aDirectory.isDirectory()){
				this.currentDirectory = aDirectory;
				List<String> recls = new ArrayList<String>();
				List<String> ls = new ArrayList<String>();				
				ls.clear();
				try {
//					Log.i("ls",aDirectory.getCanonicalPath());
					RootTools.useRoot=true;
 					recls =  RootTools.sendShell("ls "+aDirectory.getCanonicalPath(), -1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int i=0;i<recls.size();i++){
					File filetest =new File(recls.get(i));
//					Log.i("recls",recls.get(i));
					if(!(recls.get(i).isEmpty())&(recls.get(i)!=recls.get(recls.size()-1))){
						ls.add(filetest.getAbsolutePath());
					}
				}
				File[] testlist = new File[ls.size()];
				for(int i=0;i<ls.size();i++) {
				testlist[i] = new File(aDirectory.getAbsolutePath()+"/"+ls.get(i));
				}
				fill(testlist);
		}else{
			OnClickListener okButtonListener = new OnClickListener(){
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Lets return an Intent containing the filename, that was clicked...
                	Intent output = new Intent();
                	output.setData(Uri.parse(aDirectory.getAbsolutePath()));
                	KoumaFileBrowser.this.setResult(RESULT_OK,output);
                	finish();
				}
			};
			OnClickListener cancelButtonListener = new OnClickListener(){
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Do nothing
				}
			};
			AlertDialog alertdialog = new AlertDialog.Builder(this).create();
			alertdialog.setTitle("Are you sure?");
			alertdialog.setMessage("Use this file as swap?\n"+ aDirectory.getName());
			alertdialog.setButton("OK",okButtonListener);
			alertdialog.setButton2("Cancel", cancelButtonListener);
			alertdialog.show();
		}
	}
 
	private void fill(File[] files) {
		Arrays.sort(files);
		this.directoryEntries.clear();
		// Add the "<Current Directory>" and the "<Up One Level>" == 'Up one level'
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.directoryEntries.add("<Current Directory>");

		if(this.currentDirectory.getParent() != null){
			this.directoryEntries.add("<Up One Level>");
		}
		switch(this.displayMode){
			case ABSOLUTE:
				for (File file : files){
					this.directoryEntries.add(file.getPath());
				}
				break;
			case RELATIVE: // On relative Mode, we have to add the current-path to the beginning
				int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
				for (File file : files){
					this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght));
				}
				break;
		}
               
		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.file_row, this.directoryEntries);

		this.setListAdapter(directoryList);
	}
 
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String selectedFileString = this.directoryEntries.get(position);
		if (selectedFileString.equals("<Current Directory>")) {
			// Refresh
			OnClickListener okButtonListener = new OnClickListener(){
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					AlertDialog swapsize =new AlertDialog.Builder(KoumaFileBrowser.this).create();
					swapsize.setTitle("Creating new swapfile");
					swapsize.setMessage("Enter size in MB");
					final EditText input = new EditText(KoumaFileBrowser.this);
					input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
					input.setText("100");
					swapsize.setView(input);
					OnClickListener okNumberListener = new OnClickListener(){
						public void onClick(DialogInterface arg0, int arg1) {
							//create swapfile and return filename
							String value = input.getText().toString();
							File newSwap = new File("");
							for(int i=0;i<255;i++) {
								newSwap = new File(KoumaFileBrowser.this.currentDirectory.getAbsolutePath()+ "/swapfile" + Integer.toString(i));
								if(!newSwap.exists()) break;
							}
							Intent makefile = new Intent(KoumaFileBrowser.this, KoumaSwapCreate.class);
							Bundle bundle = new Bundle();
							bundle.putString("filename", newSwap.getAbsolutePath());
							bundle.putString("filesize",value);
							makefile.putExtras(bundle);
							startService(makefile);
							Intent output = new Intent();
		                	output.setData(Uri.parse(newSwap.getAbsolutePath()));
		                	KoumaFileBrowser.this.setResult(RESULT_OK,output);
		                	finish();
						}
					};
					OnClickListener cancelNumberListener = new OnClickListener(){
						public void onClick(DialogInterface arg0, int arg1) {
							KoumaFileBrowser.this.browseTo(KoumaFileBrowser.this.currentDirectory);// refresh
						}						
					};
					swapsize.setButton("OK", okNumberListener);
					swapsize.setButton2("Cancel", cancelNumberListener);
					swapsize.show();
				}
			};
			OnClickListener cancelButtonListener = new OnClickListener(){
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					KoumaFileBrowser.this.browseTo(KoumaFileBrowser.this.currentDirectory);// refresh
				}
			};
			AlertDialog swapcreate = new AlertDialog.Builder(this).create();
			swapcreate.setTitle("Question");
			swapcreate.setMessage("Create Swapfile here?\n" + KoumaFileBrowser.this.currentDirectory.getAbsolutePath());
			swapcreate.setButton("OK",okButtonListener);
			swapcreate.setButton2("Cancel", cancelButtonListener);
			swapcreate.show();
		} else if(selectedFileString.equals("<Up One Level>")){
			this.upOneLevel();
		} else {
			File clickedFile = null;
			switch(this.displayMode){
				case RELATIVE:
					clickedFile = new File(this.currentDirectory.getAbsolutePath()
							+ this.directoryEntries.get(position));
					break;
				case ABSOLUTE:
					clickedFile = new File(this.directoryEntries.get(position));
					break;
			}
			if(clickedFile != null){
				this.browseTo(clickedFile);
			}
		}
	}
}
