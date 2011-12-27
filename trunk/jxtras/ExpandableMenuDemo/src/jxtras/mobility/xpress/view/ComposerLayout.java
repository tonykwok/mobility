package jxtras.mobility.xpress.view;

import jxtras.mobility.xpress.view.animation.ComposerSlideAnimation;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

public class ComposerLayout extends RelativeLayout {
	private Animation animation;
	private final int originalPaddingBottom;
	private final int originalPaddingLeft;
	private final int originalPaddingRight;
	private final int originalPaddingTop;

	public ComposerLayout(Context context) {
		super(context);
		this.originalPaddingLeft = getPaddingLeft();
		this.originalPaddingTop = getPaddingTop();
		this.originalPaddingRight = getPaddingRight();
		this.originalPaddingBottom = getPaddingBottom();
	}

	public ComposerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.originalPaddingLeft = getPaddingLeft();
		this.originalPaddingTop = getPaddingTop();
		this.originalPaddingRight = getPaddingRight();
		this.originalPaddingBottom = getPaddingBottom();
	}

	public void moveDown(int amount) {
		setPadding(this.originalPaddingLeft, this.originalPaddingTop,
				this.originalPaddingRight, this.originalPaddingBottom - amount);
	}

	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		if ((this.animation instanceof ComposerSlideAnimation)) {
			int amount = ((ComposerSlideAnimation) this.animation).yOffset;
			moveDown(amount);
		}
	}

	public void resetPosition() {
		setPadding(this.originalPaddingLeft, this.originalPaddingTop,
				this.originalPaddingRight, this.originalPaddingBottom);
	}

	public void startAnimation(Animation animation) {
		super.startAnimation(animation);
		this.animation = animation;
	}
}