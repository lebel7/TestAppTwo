package com.proper.testapptwo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ActDetails extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyt_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mnu_details, menu);
		return true;
	}

}
