package jxtras.mobility.xpress.view.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class ComposerButtonGrowAnimationIn extends InOutAnimation {

	public ComposerButtonGrowAnimationIn(int duration) {
		super(InOutAnimation.Direction.IN, duration, null);
	}

	@Override
	protected void addInAnimation(View[] views) {
		addAnimation(new ScaleAnimation(0.0F, 1.0F, 0.0F, 1.0F, 1, 0.5F,
				Animation.RELATIVE_TO_SELF, 0.5F));
		addAnimation(new AlphaAnimation(0.0F, 1.0F));
	}

	@Override
	protected void addOutAnimation(View[] views) {
		/* default */
	}
}