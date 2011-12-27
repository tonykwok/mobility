package jxtras.mobility.xpress.demo;

import jxtras.mobility.xpress.view.ComposerLayout;
import jxtras.mobility.xpress.view.InOutImageButton;
import jxtras.mobility.xpress.view.animation.ComposerButtonAnimation;
import jxtras.mobility.xpress.view.animation.ComposerButtonGrowAnimationIn;
import jxtras.mobility.xpress.view.animation.ComposerButtonGrowAnimationOut;
import jxtras.mobility.xpress.view.animation.ComposerButtonShrinkAnimationOut;
import jxtras.mobility.xpress.view.animation.ComposerSlideAnimation;
import jxtras.mobility.xpress.view.animation.InOutAnimation;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

public class ExpandableMenuDemo extends Activity {

	private View composerButtonsShowHideButton;
	private View composerButtonsShowHideButtonIcon;
	private ViewGroup composerButtonsWrapper;
	private ComposerLayout composerLayout;
	private Animation rotateStoryAddButtonIn;
	private Animation rotateStoryAddButtonOut;
	private boolean areButtonsShowing;

	private ComposerSlideAnimation composerAnimationIn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.areButtonsShowing = false;
		
		this.composerButtonsShowHideButtonIcon = this.findViewById(R.id.composer_buttons_show_hide_button_icon);
		this.composerButtonsWrapper = (ViewGroup) this.findViewById(R.id.composer_buttons_wrapper);
		this.composerLayout = (ComposerLayout) this.findViewById(R.id.composer);
		
		this.rotateStoryAddButtonIn = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_in);
		this.rotateStoryAddButtonOut = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_out);
		
		this.composerButtonsShowHideButton = this.findViewById(R.id.composer_buttons_show_hide_button);
		this.composerAnimationIn = new ComposerSlideAnimation(68, ComposerSlideAnimation.Direction.UP);;
		this.composerAnimationIn.setDuration(300L);
		
		// this.composerLayout.startAnimation(localComposerSlideAnimation);

		this.composerButtonsShowHideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleComposerButtons();
			}
		});

		for (int i = 0, count = this.composerButtonsWrapper.getChildCount(); i < count; i++) {
			if (this.composerButtonsWrapper.getChildAt(i) instanceof InOutImageButton) {
				final View view = this.composerButtonsWrapper.getChildAt(i);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View subject) {
						startComposerButtonClickedAnimations(subject, null);
					}
				});
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		reshowComposer();
	}

	private void reshowComposer() {
		ComposerButtonGrowAnimationIn growAnimationIn = new ComposerButtonGrowAnimationIn(300);
		growAnimationIn.setInterpolator(new OvershootInterpolator(2.0F));
		this.composerButtonsShowHideButton.startAnimation(growAnimationIn);
		this.composerButtonsShowHideButtonIcon.startAnimation(this.rotateStoryAddButtonOut);
	}

	private void startComposerButtonClickedAnimations(View subject, Runnable job) {
		this.areButtonsShowing = false;
		
		ComposerButtonShrinkAnimationOut shrinkAnimationOut = new ComposerButtonShrinkAnimationOut(300);
		shrinkAnimationOut.setInterpolator(new AnticipateInterpolator(2.0F));
		shrinkAnimationOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(ExpandableMenuDemo.this, MockActivity.class);
				ExpandableMenuDemo.this.startActivity(intent);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub				
			}
		});
		this.composerButtonsShowHideButton.startAnimation(shrinkAnimationOut);
		
		for (int i = 0, j = this.composerButtonsWrapper.getChildCount(); i < j; i++) {
			if (this.composerButtonsWrapper.getChildAt(i) instanceof InOutImageButton) {
				final View view = this.composerButtonsWrapper.getChildAt(i);
				if (view.getId() == subject.getId()) {
					view.startAnimation((Animation) new ComposerButtonGrowAnimationOut(300));
				}
				else {
					view.startAnimation((Animation) new ComposerButtonShrinkAnimationOut(300));
				}
			}
		}
	}

	private void toggleComposerButtons() {
		if (this.areButtonsShowing) {
			ComposerButtonAnimation.startAnimations(this.composerButtonsWrapper, InOutAnimation.Direction.OUT);
			this.composerButtonsShowHideButtonIcon.startAnimation(this.rotateStoryAddButtonOut);
		}
		else {
			ComposerButtonAnimation.startAnimations(this.composerButtonsWrapper, InOutAnimation.Direction.IN);
			this.composerButtonsShowHideButtonIcon.startAnimation(this.rotateStoryAddButtonIn);
		}
		
		this.areButtonsShowing = !this.areButtonsShowing;
	}
}