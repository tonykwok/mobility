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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class FastScroller extends android.view.View {

	private static final String TAG = FastScroller.class.getSimpleName();

	private Paint mPaint;
	//private final Bitmap mThumb = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar_thumb_normal);
	//private final Bitmap mThumbPressed = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar_thumb_pressed);
	
	private final int mThumbHeight = 80;//mThumb.getHeight();
	private final int mTrackWidth = 15;//mThumb.getWidth() - 4;
	private final int radius = (int) (mTrackWidth * 0.5f);

	public interface AdjustmentListener {
		void onAdjustmentValueChanged(int minValue);
	}

	private int min, max;
	private double normalizedValue = 0d;
	private boolean mIsThumbSelected = false;

	private AdjustmentListener listener;

	public FastScroller(Context context) {
		super(context);
		init(0, 100, 0);
	}

	public FastScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(0, 100, 0);
	}

	public FastScroller(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(0, 100, 0);
	}

	public void init(int min, int max, int value) {
		this.setBackgroundColor(Color.BLACK);
		this.min = min;
		this.max = max;
		this.setValue(value);
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getValue() {
		return normalizedToValue(normalizedValue);
	}

	public void setValue(int value) {
		setNormalizedValue(valueToNormailzed(value));
	}

	public void setAdjustmentListener(AdjustmentListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsThumbSelected = this.isThumbSelected(y, normalizedValue);
			setNormalizedValue(screenToNormalized(y));
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsThumbSelected) {
				setNormalizedValue(screenToNormalized(y));				
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsThumbSelected = false;			
			break;
		}
		
		invalidate();
		if (listener != null) {
			listener.onAdjustmentValueChanged(getValue());
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = MeasureSpec.getSize(heightMeasureSpec);
		}
		int width = mTrackWidth;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Style.FILL);
		}
		// draw scroll bar background
		mPaint.setShader(new LinearGradient(0, 2, mTrackWidth, 2, Color.parseColor("#505050"), Color.parseColor("#C0C0C0"), TileMode.CLAMP));
		RectF rect = new RectF(0, 2, mTrackWidth, getHeight());
		canvas.drawRoundRect(rect, radius, radius, mPaint);
		// draw scroll bar thumb
		drawThumb(normalizedToScreen(normalizedValue), mIsThumbSelected, canvas);
	}

	private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		int y = (int) (screenCoord - mThumbHeight * 0.5f);
		//canvas.drawBitmap(pressed ? mThumbPressed : mThumb, 0 - 8, screenCoord - mThumbHeight * 0.5f, mPaint);
		RectF rect = new RectF(0, 2 + y, mTrackWidth, y + 50);
		if (pressed) {
			mPaint.setShader(new LinearGradient(0, 2 + y, mTrackWidth, 2 + y, Color.parseColor("#2233FF"), Color.parseColor("#2280FF"), TileMode.CLAMP));
			canvas.drawRoundRect(rect, radius, radius, mPaint);
		} else {
			mPaint.setShader(new LinearGradient(0, 2 + y, mTrackWidth, 2 + y, Color.parseColor("#2244FF"), Color.parseColor("#2280FF"), TileMode.CLAMP));
			canvas.drawRoundRect(rect, radius, radius, mPaint);
		}
	}

	private boolean isThumbSelected(float touchY, double normalizedThumbValue) {
		return Math.abs(touchY - normalizedToScreen(normalizedThumbValue)) <= mThumbHeight * 0.5f;
	}

	private void setNormalizedValue(double value) {
		normalizedValue = Math.max(0d, Math.min(1d, value));
		invalidate();
	}

	private int normalizedToValue(double normalized) {
		return (int) (min + normalized * (max - min));
	}

	private double valueToNormailzed(int value) {
		if (0 == max - min) {
			return 0d;
		}
		return (value - min) / (max * 1.0D - min);
	}

	private float normalizedToScreen(double normalized) {
		return (float) (mThumbHeight * 0.5f + normalized * (getHeight() - mThumbHeight));
	}

	private double screenToNormalized(float screenCoord) {
		int height = getHeight();
		if (height <= mThumbHeight) {
			// prevent division by zero, simply return 0.
			return 0d;
		}
		else {
			double result = (screenCoord - mThumbHeight * 0.5f) / (height - mThumbHeight);
			return Math.min(1d, Math.max(0d, result));
		}
	}
}