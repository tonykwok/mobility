package jxtras.mobility.widget.scroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class FastScroller extends android.view.View {

	private static final String TAG = FastScroller.class.getSimpleName();

	private final Paint paint = new Paint();
	private final Bitmap knob = BitmapFactory.decodeResource(getResources(), R.drawable.scroller_normal);
	private final Bitmap knob_pressed = BitmapFactory.decodeResource(getResources(), R.drawable.scroller_pressed);

	private final float thumbHeight = knob.getHeight();
	private final float thumbWidth = knob.getHeight();
	private final float lineWidth = knob.getWidth() - 4;
	

	public interface AdjustmentListener {
		void adjustmentValueChanged(int minValue);
	}

	public static class Insets {
		public int top, left, bottom, right;
	}

	private Insets insets;

	private int min, max;
	private double normalizedValue = 0d;

	private boolean mIsBeingDragged = false;

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
		this.min = min;
		this.max = max;
		this.setValue(value);

		this.insets = new Insets();
		this.insets.top = 0;
		this.insets.left = 2;
		this.insets.bottom = 0;
		this.insets.right = 0;
	}

	public Insets getInsets() {
		return this.insets;
	}

	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
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
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsBeingDragged = this.isInThumbRange(event.getY(), normalizedValue);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsBeingDragged) {
				setNormalizedValue(screenToNormalized(event.getY()));
				if (listener != null) {
					listener.adjustmentValueChanged(getValue());
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsBeingDragged = false;
			invalidate();
			if (listener != null) {
				listener.adjustmentValueChanged(getValue());
			}
			break;
		}
		return true;
	}

	/**
	 * Ensures correct size of the widget.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = MeasureSpec.getSize(heightMeasureSpec);
		}
		int width = knob.getWidth() - 8;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw seek bar background line
		RectF rect = new RectF(insets.left, insets.top, insets.left + lineWidth, getHeight() - insets.top - insets.bottom);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.GRAY);
		//canvas.drawRect(rect, paint);
		// draw thumb
		drawThumb(normalizedToScreen(normalizedValue), mIsBeingDragged, canvas);
	}

	private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(pressed ? knob_pressed : knob, 0 - 8, screenCoord - thumbHeight * 0.5f, paint);
	}

	private boolean isInThumbRange(float touchY, double normalizedThumbValue) {
		return Math.abs(touchY - normalizedToScreen(normalizedThumbValue)) <= thumbHeight * 0.5f;
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
		return (float) (insets.top + thumbHeight * 0.5f + normalized * (getHeight() - insets.top - insets.bottom - thumbHeight));
	}

	private double screenToNormalized(float screenCoord) {
		int height = getHeight();
		if (height <= insets.top + insets.bottom + thumbHeight) {
			// prevent division by zero, simply return 0.
			return 0d;
		}
		else {
			double result = (screenCoord - insets.top - thumbHeight * 0.5f) / (height - insets.top - insets.bottom - thumbHeight);
			return Math.min(1d, Math.max(0d, result));
		}
	}
}