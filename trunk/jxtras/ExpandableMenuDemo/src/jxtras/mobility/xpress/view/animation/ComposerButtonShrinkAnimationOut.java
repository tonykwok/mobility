package jxtras.mobility.xpress.view.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class ComposerButtonShrinkAnimationOut extends InOutAnimation {
	
	public ComposerButtonShrinkAnimationOut(int duration) {
		super(InOutAnimation.Direction.OUT, duration, null);
	}

	@Override
	protected void addInAnimation(View[] views) {
		/* default */
	}

	@Override
	protected void addOutAnimation(View[] views) {
		addAnimation(new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F,
				Animation.RELATIVE_TO_SELF, 0.5F, 1, 0.5F));
		addAnimation(new AlphaAnimation(1.0F, 0.0F));
	}
}