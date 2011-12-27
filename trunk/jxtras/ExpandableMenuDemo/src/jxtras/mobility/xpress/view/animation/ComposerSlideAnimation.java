package jxtras.mobility.xpress.view.animation;

import android.view.animation.TranslateAnimation;

public class ComposerSlideAnimation extends TranslateAnimation {
	public final int yOffset;

	public enum Direction {
		DOWN, UP;
	}

	public ComposerSlideAnimation(int activityPopUpBarHeight,
			Direction direction) {
        super(0F, 0F, 0F, direction == Direction.UP ?
        		-activityPopUpBarHeight : activityPopUpBarHeight);

        if(direction == Direction.UP) {
        	activityPopUpBarHeight = -activityPopUpBarHeight;
        }
        yOffset = activityPopUpBarHeight;
	}
}