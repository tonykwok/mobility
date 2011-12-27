package jxtras.mobility.xpress.view.animation;

import android.view.View;
import android.view.animation.AnimationSet;

public abstract class InOutAnimation extends AnimationSet {
	public final Direction direction;

	public enum Direction {
		IN, OUT;
	}

	public InOutAnimation(Direction direction, long duration, View[] views) {
		super(true);
		this.direction = direction;
		switch (direction) {
			case IN:
				this.addInAnimation(views);
				break;
			case OUT:
				this.addOutAnimation(views);
				break;
		}
		setDuration(duration);
	}

	protected abstract void addInAnimation(View[] views);

	protected abstract void addOutAnimation(View[] views);
}