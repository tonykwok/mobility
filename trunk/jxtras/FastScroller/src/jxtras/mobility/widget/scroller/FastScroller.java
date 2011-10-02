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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class FastScroller extends android.view.View {

	private static final String TAG = FastScroller.class.getSimpleName();

	private final Paint mPaint = new Paint();
	private final Bitmap mThumb = BitmapFactory.decodeResource(getResources(), R.drawable.scroller_normal);
	private final Bitmap mThumbPressed = BitmapFactory.decodeResource(getResources(), R.drawable.scroller_pressed);

	private Drawable mTrackBackground = getResources().getDrawable(R.drawable.scrollbar_track);
	
	private final int mThumbHeight = mThumb.getHeight();
	private final float mTrackWidth = mThumb.getWidth() - 4;

	public interface AdjustmentListener {
		void adjustmentValueChanged(int minValue);
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
			this.normalizedValue = screenToNormalized(y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsThumbSelected) {
				setNormalizedValue(screenToNormalized(y));
				if (listener != null) {
					listener.adjustmentValueChanged(getValue());
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsThumbSelected = false;
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
		int width = mThumb.getWidth() - 8;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw seek bar background line
		//RectF rect = new RectF(0, 0, mTrackWidth, getHeight());
		//mPaint.setStyle(Style.FILL);
		//mPaint.setColor(Color.GRAY);
		//mPaint.setShader(new LinearGradient(0, 0, getWidth(), 0, Color.parseColor("#505050"), Color.parseColor("#C0C0C0"), TileMode.CLAMP));
		//canvas.drawRect(rect, mPaint);
		// draw thumb
		drawThumb(normalizedToScreen(normalizedValue), mIsThumbSelected, canvas);
	}

	private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(pressed ? mThumbPressed : mThumb, 0 - 8, screenCoord - mThumbHeight * 0.5f, mPaint);
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