package jxtras.mobility.xpress.view;

import jxtras.mobility.xpress.view.animation.InOutAnimation;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;

public class InOutImageButton extends ImageButton {
	private Animation animation;

	public InOutImageButton(Context context) {
		super(context);
	}

	public InOutImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		if ((this.animation instanceof InOutAnimation)) {
			if (((InOutAnimation) this.animation).direction == InOutAnimation.Direction.OUT) {
				setVisibility(View.GONE);
			} else {
				setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onAnimationStart() {
		super.onAnimationStart();
		if ((this.animation instanceof InOutAnimation)) {
			setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void startAnimation(Animation animation) {
		super.startAnimation(animation);
		this.animation = animation;
		getRootView().postInvalidate();
	}
}