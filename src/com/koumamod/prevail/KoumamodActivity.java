package com.koumamod.prevail;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class KoumamodActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
		TabSpec secondTabSpec = tabHost.newTabSpec("tid1");
		firstTabSpec.setIndicator("KoumaMod Settings").setContent(new Intent(this,KoumamodMenu.class));
		secondTabSpec.setIndicator("Swap Settings").setContent(new Intent(this,KoumaswapMenu.class));
		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);
	}
}
