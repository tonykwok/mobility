package com.jxtras.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView v = new TextView(this);
		v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		v.setGravity(Gravity.CENTER);
		v.setText("Welcome to Main Page");
		v.setShadowLayer(5, 1, 1, Color.BLUE);
		v.setTextSize(20.0f);
		this.setContentView(v);
	}
	// EmergencyLockStateChangedReceiver
}
