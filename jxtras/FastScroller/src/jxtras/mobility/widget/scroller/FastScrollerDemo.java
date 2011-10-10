/*
 * Copyright (c) 2011 mobility.googlecode.com. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of jxtras.mobility nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package jxtras.mobility.widget.scroller;

import jxtras.mobility.widget.scroller.FastScroller.OnAdjustListener;
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

		bar.setAdjustmentListener(new OnAdjustListener() {
			@Override
			public void onAdjustmentValueChanged(int value) {
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