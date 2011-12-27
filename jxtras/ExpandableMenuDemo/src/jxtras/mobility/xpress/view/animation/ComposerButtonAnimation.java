package jxtras.mobility.xpress.view.animation;

import jxtras.mobility.xpress.view.InOutImageButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

public class ComposerButtonAnimation extends InOutAnimation {
	private static final int xOffset = 16;
	private static final int yOffset = 243;

	public ComposerButtonAnimation(InOutAnimation.Direction direction, int duration, View subject) {
		super(direction, duration, new View[] { subject });
	}

	public static void startAnimations(ViewGroup composerButtons, InOutAnimation.Direction direction) {
		switch (direction) {
		case IN:
			startAnimationsIn(composerButtons);
			break;
		case OUT:
			startAnimationsOut(composerButtons);
			break;
		}
	}

	private static void startAnimationsIn(ViewGroup composerButtons) {
		for(int i = 0, count = composerButtons.getChildCount(); i < count; i++) {
			if ((composerButtons.getChildAt(i) instanceof InOutImageButton)) {
				InOutImageButton button = (InOutImageButton) composerButtons.getChildAt(i);
				ComposerButtonAnimation animation = new ComposerButtonAnimation(InOutAnimation.Direction.IN, 200, button);
				long offset = i * 100 / (count - 1);
				animation.setStartOffset(offset);
				animation.setInterpolator(new OvershootInterpolator(2.0F));
				button.startAnimation(animation);
			}
		}
	}

	private static void startAnimationsOut(ViewGroup composerButtons) {
		for(int i = 0, count = composerButtons.getChildCount(); i < count; i++) {
			if ((composerButtons.getChildAt(i) instanceof InOutImageButton)) {
				InOutImageButton button = (InOutImageButton) composerButtons.getChildAt(i);
				ComposerButtonAnimation animation = new ComposerButtonAnimation(InOutAnimation.Direction.OUT, 200, button);
				long offset = (count - 1 -i) * 100 / (count - 1);
				animation.setStartOffset(offset);
				animation.setInterpolator(new AnticipateInterpolator(2.0F));
				button.startAnimation(animation);
			}
		}
	}

	@Override
	protected void addInAnimation(View[] views) {
		ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) views[0].getLayoutParams();
		float x = -layoutParams.leftMargin + xOffset;
		float y = layoutParams.bottomMargin + yOffset;
		TranslateAnimation animation = new TranslateAnimation(x, 0.0F, y, 0.0F);
		addAnimation(animation);
	}

	@Override
	protected void addOutAnimation(View[] views) {
		ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) views[0].getLayoutParams();
		float x = -layoutParams.leftMargin + xOffset;
		float y = layoutParams.bottomMargin + yOffset;
		TranslateAnimation animation = new TranslateAnimation(0.0F, x, 0.0F, y);
		addAnimation(animation);
	}
}