package com.jxtras.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class SplashScreen extends Activity implements Animation.AnimationListener, View.OnClickListener {

	private void advance() {
		Intent main = new Intent(this, MainActivity.class);
		startActivity(main);
		finish();
	}

	private AnimationSet makeInOut(long preLength, long inLength, long outLength, long totalLength) {
		AnimationSet animationSet = new AnimationSet(false);
		if (inLength > 0L) {
			AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0F, 1.0F);
			fadeInAnimation.setStartOffset(preLength);
			fadeInAnimation.setDuration(inLength);
			animationSet.addAnimation(fadeInAnimation);
		}
		if (outLength > 0L) {
			AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0F, 0.0F);
			fadeOutAnimation.setDuration(outLength);
			long l = totalLength - outLength;
			fadeOutAnimation.setStartOffset(l);
			animationSet.addAnimation(fadeOutAnimation);
		}
		animationSet.setRepeatCount(0);
		animationSet.setFillAfter(true);
		animationSet.setFillBefore(true);
		return animationSet;
	}

	public void onAnimationEnd(Animation animation) {
		advance();
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}

	public void onClick(View view) {
		this.findViewById(R.id.splash_1).setOnClickListener(null);
		advance();
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.splash);

		ImageView splash1 = (ImageView) this.findViewById(R.id.splash_1);// 1
		ImageView splash2 = (ImageView) this.findViewById(R.id.splash_2);// 2
		ImageView splash3 = (ImageView) this.findViewById(R.id.splash_3);// 3
		View splash4 = this.findViewById(R.id.splash_4);// 4

		AnimationSet anim1 = makeInOut(0L, 1000L, 1000L, 2000L);
		AnimationSet anim2 = makeInOut(1500L, 1000L, 500L, 3500L);
		AnimationSet anim3 = makeInOut(1500L, 1000L, 2000L, 4500L);

		TranslateAnimation anmi4 = new TranslateAnimation(0.0F, 0.0F, 0.0F, -50.0F);
		anmi4.setStartOffset(3000L);
		anmi4.setDuration(1000L);
		anmi4.setInterpolator(new AccelerateInterpolator(0.5F));
		anim3.addAnimation(anmi4);

		AnimationSet anim5 = makeInOut(4000L, 500L, 0L, 4500L);

		splash1.startAnimation(anim1);
		splash2.startAnimation(anim2);
		splash3.startAnimation(anim3);
		splash4.startAnimation(anim5);

		anim5.setAnimationListener(this);
		splash1.setClickable(true);
		splash1.setOnClickListener(this);
	}
}