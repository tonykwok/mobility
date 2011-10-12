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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;

public class DraggableFastScroller extends android.view.View {

	private static final String TAG = DraggableFastScroller.class.getSimpleName();

	private Paint mPaint;

	private int thumbSize;
	private int trackSize;
	private int radius;

	public interface AdjustmentListener {
		void onAdjustmentValueChanged(int minValue);
	}

	private int min, max;
	private double normalizedValue = 0d;
	private boolean mIsThumbSelected = false;

	private AdjustmentListener listener;

	public DraggableFastScroller(Context context) {
		super(context);
		init(context);
	}

	public DraggableFastScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DraggableFastScroller(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		this.thumbSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75f, metrics);
		this.trackSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f, metrics);
		this.radius = (int) (trackSize * 0.5f);
		this.setBackgroundColor(Color.BLACK);
		this.min = 0;
		this.max = 100;
		this.setValue(0);
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
		int width = this.trackSize;
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			width = specSize;
		}
		else if (specMode == MeasureSpec.AT_MOST) {
			width = Math.min(width, specSize);
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
		mPaint.setShader(new LinearGradient(0, 0, trackSize, 0, Color.parseColor("#505050"), Color.parseColor("#C0C0C0"), TileMode.CLAMP));
		RectF rect = new RectF(0, 0, trackSize, getHeight());
		canvas.drawRoundRect(rect, radius, radius, mPaint);
		// draw scroll bar thumb
		drawThumb(normalizedToScreen(normalizedValue), mIsThumbSelected, canvas);
	}

	private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		int y = (int) (screenCoord - thumbSize * 0.5f);
		RectF rect = new RectF(0, y, trackSize, y + thumbSize);
		if (pressed) {
			mPaint.setShader(new LinearGradient(0, y, trackSize, y, Color.parseColor("#2233FF"), Color.parseColor("#2280FF"), TileMode.CLAMP));
			canvas.drawRoundRect(rect, radius, radius, mPaint);
		}
		else {
			mPaint.setShader(new LinearGradient(0, y, trackSize, y, Color.parseColor("#2244FF"), Color.parseColor("#2280FF"), TileMode.CLAMP));
			canvas.drawRoundRect(rect, radius, radius, mPaint);
		}
	}

	private boolean isThumbSelected(float touchY, double normalizedThumbValue) {
		return Math.abs(touchY - normalizedToScreen(normalizedThumbValue)) <= thumbSize * 0.5f;
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
		return (float) (thumbSize * 0.5f + normalized * (getHeight() - thumbSize));
	}

	private double screenToNormalized(float screenCoord) {
		int height = getHeight();
		if (height <= thumbSize) {
			// prevent division by zero, simply return 0.
			return 0d;
		}
		else {
			double result = (screenCoord - thumbSize * 0.5f) / (height - thumbSize);
			return Math.min(1d, Math.max(0d, result));
		}
	}
}