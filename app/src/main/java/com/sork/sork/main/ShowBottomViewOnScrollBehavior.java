/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sork.sork.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.core.view.ViewCompat;

import com.google.android.material.animation.AnimationUtils;
import com.sork.sork.R;

/**
 * The {@link Behavior} for a View within a {@link CoordinatorLayout} to hide the view off the
 * bottom of the screen when scrolling down, and show it when scrolling up.
 */
public class ShowBottomViewOnScrollBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    protected static final int ENTER_ANIMATION_DURATION = 225;
    protected static final int EXIT_ANIMATION_DURATION = 175;

    private static final int STATE_SCROLLED_DOWN = 1;
    private static final int STATE_SCROLLED_UP = 2;

    private int height = 0;
    private int currentState = STATE_SCROLLED_UP;
    private int additionalHiddenOffsetY = 0;
    @Nullable
    private ViewPropertyAnimator currentAnimator;

    public ShowBottomViewOnScrollBehavior() {}

    public ShowBottomViewOnScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(
            @NonNull CoordinatorLayout parent, @NonNull V child, int layoutDirection) {
        ViewGroup.MarginLayoutParams paramsCompat =
                (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        height = child.getMeasuredHeight() + paramsCompat.bottomMargin;
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull V child,
            @NonNull View directTargetChild,
            @NonNull View target,
            int nestedScrollAxes,
            int type) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull V child,
            @NonNull View target,
            int dxConsumed,
            int dyConsumed,
            int dxUnconsumed,
            int dyUnconsumed,
            int type,
            @NonNull int[] consumed) {
        Rect rect = new Rect();
        boolean hasRect = coordinatorLayout.findViewById(R.id.title_layout).getLocalVisibleRect(rect);

        if (dyConsumed > 0 && (!hasRect || rect.bottom <= 0)) {
            slideUp(child);
        } else if (dyConsumed < 0 && (hasRect && rect.bottom > 0)) {
            slideDown(child);
        }
    }

    /**
     * Perform an animation that will slide the child from it's current position to be totally on the
     * screen.
     */
    public void slideUp(@NonNull V child) {
        if (currentState == STATE_SCROLLED_DOWN) {
            return;
        }

        if (currentAnimator != null) {
            currentAnimator.cancel();
            child.clearAnimation();
        }
        currentState = STATE_SCROLLED_DOWN;
        animateChildTo(
                child, 0, ENTER_ANIMATION_DURATION, AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
    }

    /**
     * Perform an animation that will slide the child from it's current position to be totally off the
     * screen.
     */
    public void slideDown(@NonNull V child) {
        if (currentState == STATE_SCROLLED_UP) {
            return;
        }

        if (currentAnimator != null) {
            currentAnimator.cancel();
            child.clearAnimation();
        }
        currentState = STATE_SCROLLED_UP;
        animateChildTo(
                child,
                height + additionalHiddenOffsetY,
                EXIT_ANIMATION_DURATION,
                AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
    }

    private void animateChildTo(
            @NonNull V child, int targetY, long duration, TimeInterpolator interpolator) {
        currentAnimator =
                child
                        .animate()
                        .translationY(targetY)
                        .setInterpolator(interpolator)
                        .setDuration(duration)
                        .setListener(
                                new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        currentAnimator = null;
                                    }
                                });
    }
}