package jxtras.mobility.widget.scroller;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;

/**
 * An {@code AdapterListView} is a static list view whose children are
 * determined by an {@link Adapter}.
 */
public class AdapterListView extends AdapterView<Adapter> {

	/** Width of the items compared to the width of the list */
	private static final float ITEM_WIDTH = 0.85f;

	/** Space occupied by the item relative to the height of the item */
	private static final float ITEM_VERTICAL_SPACE = 1.45f;

	/** Represents an invalid child index */
	private static final int INVALID_INDEX = -1;

	/** Distance to drag before we intercept touch events */
	private static final int TOUCH_SCROLL_THRESHOLD = 10;

	/** Children added with this layout mode will be added below the last child */
	private static final int LAYOUT_MODE_BELOW = 0;

	/** Children added with this layout mode will be added above the first child */
	private static final int LAYOUT_MODE_ABOVE = 1;

	/** User is not touching the list */
	private static final int TOUCH_STATE_RESTING = 0;

	/** User is touching the list and right now it's still a "click" */
	private static final int TOUCH_STATE_CLICK = 1;

	/** User is scrolling the list */
	private static final int TOUCH_STATE_SCROLL = 2;

	/** The adapter with all the data */
	private Adapter mAdapter;

	/** Current touch state */
	private int mTouchState = TOUCH_STATE_RESTING;

	/** X-coordinate of the down event */
	private int mTouchStartX;

	/** Y-coordinate of the down event */
	private int mTouchStartY;

	/**
	 * The top of the first item when the touch down event was received
	 */
	private int mListTopStart;

	/** The current top of the first item */
	private int mListTop;

	/**
	 * The offset from the top of the currently first visible item to the top of
	 * the first item
	 */
	private int mListTopOffset;

	/** The adaptor position of the first visible item */
	private int mFirstItemPosition;

	/** The adaptor position of the last visible item */
	private int mLastItemPosition;

	/** A list of cached (re-usable) item views */
	private final LinkedList<View> mCachedItemViews = new LinkedList<View>();

	/** Used to check for long press actions */
	private Runnable mLongPressRunnable;

	/** Reusable rect */
	private Rect mRect;

	/** Paint object to draw with */
	private Paint mPaint;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The context
	 * @param attrs
	 *            Attributes
	 */
	public AdapterListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setAdapter(final Adapter adapter) {
		mAdapter = adapter;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setSelection(final int position) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(event);
			return false;

		case MotionEvent.ACTION_MOVE:
			return startScrollIfNeeded(event);

		default:
			endTouch(0);
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if (getChildCount() == 0) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(event);
			break;

		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_CLICK) {
				startScrollIfNeeded(event);
			}
			if (mTouchState == TOUCH_STATE_SCROLL) {
				// mVelocityTracker.addMovement(event);
				scrollList((int) event.getY() - mTouchStartY);
			}
			break;

		case MotionEvent.ACTION_UP:
			float velocity = 0;
			if (mTouchState == TOUCH_STATE_CLICK) {
				clickChildAt((int) event.getX(), (int) event.getY());
			}
			else if (mTouchState == TOUCH_STATE_SCROLL) {
				// mVelocityTracker.addMovement(event);
				// mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND);
				// velocity = mVelocityTracker.getYVelocity();
			}
			endTouch(velocity);
			break;

		default:
			endTouch(0);
			break;
		}
		return true;
	}

	@Override
	protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		// if we don't have an adapter, we don't need to do anything
		if (mAdapter == null) {
			return;
		}

		if (getChildCount() == 0) {
			mLastItemPosition = -1;
			fillListDown(mListTop, 0);
		}
		else {
			final int offset = mListTop + mListTopOffset - getChildTop(getChildAt(0));
			removeNonVisibleViews(offset);
			fillList(offset);
		}

		positionItems();
		invalidate();
	}

	@Override
	protected void dispatchDraw(final Canvas canvas) {
		final long start = System.currentTimeMillis();
		super.dispatchDraw(canvas);
		final long end = System.currentTimeMillis();
		Log.e("DEGUB", "Time to render frame: " + (end - start));
	}

	// @Override
	protected boolean drawChild(final Canvas canvas, final View child, final long drawingTime) {
		// get the bitmap
		final Bitmap bitmap = child.getDrawingCache();
		if (bitmap == null) {
			// if the is null for some reason, default to the standard
			// drawChild implementation
			return super.drawChild(canvas, child, drawingTime);
		}

		// get top left coordinates
		final int top = child.getTop();
		final int left = child.getLeft();

		// create and initialize the paint object
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
		}

		// draw the bitmap
		canvas.drawBitmap(bitmap, left, top, mPaint);

		return false;
	}

	/**
	 * Sets and initializes all things that need to when we start a touch
	 * gesture.
	 * 
	 * @param event
	 *            The down event
	 */
	private void startTouch(final MotionEvent event) {
		// user is touching the list -> no more fling
		// removeCallbacks(mDynamicsRunnable);

		// save the start place
		mTouchStartX = (int) event.getX();
		mTouchStartY = (int) event.getY();
		mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;

		// start checking for a long press
		startLongPressCheck();

		// obtain a velocity tracker and feed it its first event
		// mVelocityTracker = VelocityTracker.obtain();
		// mVelocityTracker.addMovement(event);

		// we don't know if it's a click or a scroll yet, but until we know
		// assume it's a click
		mTouchState = TOUCH_STATE_CLICK;
	}

	/**
	 * Resets and recycles all things that need to when we end a touch gesture
	 * 
	 * @param velocity
	 *            The velocity of the gesture
	 */
	private void endTouch(final float velocity) {
		// remove any existing check for longpress
		removeCallbacks(mLongPressRunnable);

		// we pretend that each frame of the fling/snap animation is
		// one touch gesture and therefore set the start position
		// every time
		mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;
		// scrollList((int) mDynamics.getPosition() - mListTopStart);

		// reset touch state
		mTouchState = TOUCH_STATE_RESTING;
	}

	/**
	 * Scrolls the list. Takes care of updating rotation (if enabled) and
	 * snapping
	 * 
	 * @param scrolledDistance
	 *            The distance to scroll
	 */
	private void scrollList(final int scrolledDistance) {
		mListTop = mListTopStart + scrolledDistance;
		// setSnapPoint();
		requestLayout();
	}

	/**
	 * Posts (and creates if necessary) a runnable that will when executed call
	 * the long click listener
	 */
	private void startLongPressCheck() {
		// create the runnable if we haven't already
		if (mLongPressRunnable == null) {
			mLongPressRunnable = new Runnable() {
				public void run() {
					if (mTouchState == TOUCH_STATE_CLICK) {
						final int index = getContainingChildIndex(mTouchStartX, mTouchStartY);
						if (index != INVALID_INDEX) {
							longClickChild(index);
						}
					}
				}
			};
		}

		// then post it with a delay
		postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
	}

	/**
	 * Checks if the user has moved far enough for this to be a scroll and if
	 * so, sets the list in scroll mode
	 * 
	 * @param event
	 *            The (move) event
	 * @return true if scroll was started, false otherwise
	 */
	private boolean startScrollIfNeeded(final MotionEvent event) {
		final int xPos = (int) event.getX();
		final int yPos = (int) event.getY();
		if (xPos < mTouchStartX - TOUCH_SCROLL_THRESHOLD || xPos > mTouchStartX + TOUCH_SCROLL_THRESHOLD || yPos < mTouchStartY - TOUCH_SCROLL_THRESHOLD || yPos > mTouchStartY + TOUCH_SCROLL_THRESHOLD) {
			// we've moved far enough for this to be a scroll
			removeCallbacks(mLongPressRunnable);
			mTouchState = TOUCH_STATE_SCROLL;
			return true;
		}
		return false;
	}

	/**
	 * Returns the index of the child that contains the coordinates given.
	 * 
	 * @param x
	 *            X-coordinate
	 * @param y
	 *            Y-coordinate
	 * @return The index of the child that contains the coordinates. If no child
	 *         is found then it returns INVALID_INDEX
	 */
	private int getContainingChildIndex(final int x, final int y) {
		if (mRect == null) {
			mRect = new Rect();
		}
		for (int index = 0; index < getChildCount(); index++) {
			getChildAt(index).getHitRect(mRect);
			if (mRect.contains(x, y)) {
				return index;
			}
		}
		return INVALID_INDEX;
	}

	/**
	 * Calls the item click listener for the child with at the specified
	 * coordinates
	 * 
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 */
	private void clickChildAt(final int x, final int y) {
		final int index = getContainingChildIndex(x, y);
		if (index != INVALID_INDEX) {
			final View itemView = getChildAt(index);
			final int position = mFirstItemPosition + index;
			final long id = mAdapter.getItemId(position);
			performItemClick(itemView, position, id);
		}
	}

	/**
	 * Calls the item long click listener for the child with the specified index
	 * 
	 * @param index
	 *            Child index
	 */
	private void longClickChild(final int index) {
		final View itemView = getChildAt(index);
		final int position = mFirstItemPosition + index;
		final long id = mAdapter.getItemId(position);
		final OnItemLongClickListener listener = getOnItemLongClickListener();
		if (listener != null) {
			listener.onItemLongClick(this, itemView, position, id);
		}
	}

	/**
	 * Removes view that are outside of the visible part of the list. Will not
	 * remove all views.
	 * 
	 * @param offset
	 *            Offset of the visible area
	 */
	private void removeNonVisibleViews(final int offset) {
		// We need to keep close track of the child count in this function. We
		// should never remove all the views, because if we do, we loose track
		// of were we are.
		int childCount = getChildCount();

		// if we are not at the bottom of the list and have more than one child
		if (mLastItemPosition != mAdapter.getCount() - 1 && childCount > 1) {
			// check if we should remove any views in the top
			View firstChild = getChildAt(0);
			while (firstChild != null && getChildBottom(firstChild) + offset < 0) {
				// remove the top view
				removeViewInLayout(firstChild);
				childCount--;
				mCachedItemViews.addLast(firstChild);
				mFirstItemPosition++;

				// update the list offset (since we've removed the top child)
				mListTopOffset += getChildHeight(firstChild);

				// Continue to check the next child only if we have more than
				// one child left
				if (childCount > 1) {
					firstChild = getChildAt(0);
				}
				else {
					firstChild = null;
				}
			}
		}

		// if we are not at the top of the list and have more than one child
		if (mFirstItemPosition != 0 && childCount > 1) {
			// check if we should remove any views in the bottom
			View lastChild = getChildAt(childCount - 1);
			while (lastChild != null && getChildTop(lastChild) + offset > getHeight()) {
				// remove the bottom view
				removeViewInLayout(lastChild);
				childCount--;
				mCachedItemViews.addLast(lastChild);
				mLastItemPosition--;

				// Continue to check the next child only if we have more than
				// one child left
				if (childCount > 1) {
					lastChild = getChildAt(childCount - 1);
				}
				else {
					lastChild = null;
				}
			}
		}
	}

	/**
	 * Fills the list with child-views
	 * 
	 * @param offset
	 *            Offset of the visible area
	 */
	private void fillList(final int offset) {
		final int bottomEdge = getChildBottom(getChildAt(getChildCount() - 1));
		fillListDown(bottomEdge, offset);

		final int topEdge = getChildTop(getChildAt(0));
		fillListUp(topEdge, offset);
	}

	/**
	 * Starts at the bottom and adds children until we've passed the list bottom
	 * 
	 * @param bottomEdge
	 *            The bottom edge of the currently last child
	 * @param offset
	 *            Offset of the visible area
	 */
	private void fillListDown(int bottomEdge, final int offset) {
		while (bottomEdge + offset < getHeight() && mLastItemPosition < mAdapter.getCount() - 1) {
			mLastItemPosition++;
			final View newBottomchild = mAdapter.getView(mLastItemPosition, getCachedView(), this);
			addAndMeasureChild(newBottomchild, LAYOUT_MODE_BELOW);
			bottomEdge += getChildHeight(newBottomchild);
		}
	}

	/**
	 * Starts at the top and adds children until we've passed the list top
	 * 
	 * @param topEdge
	 *            The top edge of the currently first child
	 * @param offset
	 *            Offset of the visible area
	 */
	private void fillListUp(int topEdge, final int offset) {
		while (topEdge + offset > 0 && mFirstItemPosition > 0) {
			mFirstItemPosition--;
			final View newTopCild = mAdapter.getView(mFirstItemPosition, getCachedView(), this);
			addAndMeasureChild(newTopCild, LAYOUT_MODE_ABOVE);
			final int childHeight = getChildHeight(newTopCild);
			topEdge -= childHeight;

			// update the list offset (since we added a view at the top)
			mListTopOffset -= childHeight;
		}
	}

	/**
	 * Adds a view as a child view and takes care of measuring it
	 * 
	 * @param child
	 *            The view to add
	 * @param layoutMode
	 *            Either LAYOUT_MODE_ABOVE or LAYOUT_MODE_BELOW
	 */
	private void addAndMeasureChild(final View child, final int layoutMode) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		final int index = layoutMode == LAYOUT_MODE_ABOVE ? 0 : -1;
		child.setDrawingCacheEnabled(true);
		addViewInLayout(child, index, params, true);

		final int itemWidth = (int) (getWidth() * ITEM_WIDTH);
		child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	/**
	 * Positions the children at the "correct" positions
	 */
	private void positionItems() {
		int top = mListTop + mListTopOffset;
		for (int index = 0; index < getChildCount(); index++) {
			final View child = getChildAt(index);
			final int width = child.getMeasuredWidth();
			final int height = child.getMeasuredHeight();
			final int left = 5;// offset + (getWidth() - width) / 2;
			final int margin = getChildMargin(child);
			final int childTop = top + margin;

			child.layout(left, childTop, left + width, childTop + height);
			top += height + 2 * margin;
		}

	}

	/**
	 * Checks if there is a cached view that can be used
	 * 
	 * @return A cached view or, if none was found, null
	 */
	private View getCachedView() {
		if (mCachedItemViews.size() != 0) {
			return mCachedItemViews.removeFirst();
		}
		return null;
	}

	/**
	 * Returns the margin of the child view taking into account the
	 * ITEM_VERTICAL_SPACE
	 * 
	 * @param child
	 *            The child view
	 * @return The margin of the child view
	 */
	private int getChildMargin(final View child) {
		return (int) (child.getMeasuredHeight() * (ITEM_VERTICAL_SPACE - 1) / 2);
	}

	/**
	 * Returns the top placement of the child view taking into account the
	 * ITEM_VERTICAL_SPACE
	 * 
	 * @param child
	 *            The child view
	 * @return The top placement of the child view
	 */
	private int getChildTop(final View child) {
		return child.getTop() - getChildMargin(child);
	}

	/**
	 * Returns the bottom placement of the child view taking into account the
	 * ITEM_VERTICAL_SPACE
	 * 
	 * @param child
	 *            The child view
	 * @return The bottom placement of the child view
	 */
	private int getChildBottom(final View child) {
		return child.getBottom() + getChildMargin(child);
	}

	/**
	 * Returns the height of the child view taking into account the
	 * ITEM_VERTICAL_SPACE
	 * 
	 * @param child
	 *            The child view
	 * @return The height of the child view
	 */
	private int getChildHeight(final View child) {
		return child.getMeasuredHeight() + 2 * getChildMargin(child);
	}
}
