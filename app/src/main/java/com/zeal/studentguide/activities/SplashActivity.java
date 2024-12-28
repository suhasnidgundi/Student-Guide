package com.zeal.studentguide.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.zeal.studentguide.MainActivity;
import com.zeal.studentguide.R;
import com.zeal.studentguide.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {
    private static final long ANIMATION_DURATION = 1000;
    private static final long SPLASH_DELAY = 2000;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferenceManager = new PreferenceManager(this);
        startAnimations();
    }

    private void startAnimations() {
        View logoImage = findViewById(R.id.imageViewLogo);
        View appName = findViewById(R.id.textViewAppName);
        View tagline = findViewById(R.id.textViewTagline);

        // Create animations
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoImage, "alpha", 0f, 1f);
        ObjectAnimator logoScale = ObjectAnimator.ofFloat(logoImage, "scaleX", 0.3f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoImage, "scaleY", 0.3f, 1f);

        ObjectAnimator titleFadeIn = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        ObjectAnimator titleTranslateY = ObjectAnimator.ofFloat(appName, "translationY", 50f, 0f);

        ObjectAnimator taglineFadeIn = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f);
        ObjectAnimator taglineTranslateY = ObjectAnimator.ofFloat(tagline, "translationY", 50f, 0f);

        // Configure animations
        logoFadeIn.setDuration(ANIMATION_DURATION);
        logoScale.setDuration(ANIMATION_DURATION);
        logoScaleY.setDuration(ANIMATION_DURATION);
        titleFadeIn.setDuration(ANIMATION_DURATION);
        titleTranslateY.setDuration(ANIMATION_DURATION);
        taglineFadeIn.setDuration(ANIMATION_DURATION);
        taglineTranslateY.setDuration(ANIMATION_DURATION);

        // Set interpolator for smooth animation
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        logoFadeIn.setInterpolator(interpolator);
        logoScale.setInterpolator(interpolator);
        logoScaleY.setInterpolator(interpolator);
        titleFadeIn.setInterpolator(interpolator);
        taglineFadeIn.setInterpolator(interpolator);

        // Play animations together
        animatorSet.play(logoFadeIn).with(logoScale).with(logoScaleY);
        animatorSet.play(titleFadeIn).with(titleTranslateY).after(logoFadeIn);
        animatorSet.play(taglineFadeIn).with(taglineTranslateY).after(titleFadeIn);

        // Start the animation
        animatorSet.start();

        // Check auth and navigate after animations
        new Handler().postDelayed(this::checkAuthAndNavigate, SPLASH_DELAY);
    }

    private void checkAuthAndNavigate() {
        if (preferenceManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}