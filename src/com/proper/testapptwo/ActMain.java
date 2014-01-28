package com.proper.testapptwo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ActMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyt_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mnu_main, menu);
		return true;
	}

}
