package jxtras.mobility.widget.scroller;

import jxtras.mobility.widget.scroller.FastScroller.AdjustmentListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

public class FastScrollerDemo extends Activity {

	private ViewGroup vg;

	private FastScroller bar;

	private ScrollView sv;

	private int maxScrollableDistance;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bar = (FastScroller) this.findViewById(R.id.adj);
		sv = (ScrollView) this.findViewById(R.id.sv);

		vg = (ViewGroup) this.findViewById(R.id.content);
		// Hide the Scollbar
		sv.setVerticalScrollBarEnabled(false);
		sv.setHorizontalScrollBarEnabled(false);
		sv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					int y = ((ScrollView) v).getScrollY();
					Log.d("AdjustmentScroller", "y value = " + y);
					if (y != bar.getValue()) {
						bar.setValue(y);
					}
				}
				return false;
			}
		});

		bar.setAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(int value) {
				Log.i("AdjustmentScroller", "User selected value =" + value);
				sv.smoothScrollTo(0, value);
			}
		});
	}

	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		maxScrollableDistance = vg.getHeight() - sv.getHeight();
		if (maxScrollableDistance != bar.getMax()) {
			bar.setMax(maxScrollableDistance);
		}
	}
}