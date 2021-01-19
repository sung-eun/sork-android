package com.sork.sork.main

import android.animation.Animator
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class HeaderScrollBehavior : CoordinatorLayout.Behavior<View>() {
    companion object {
        private val INTERPOLATOR = FastOutSlowInInterpolator()
        private const val ANIMATION_DURATION: Long = 200
    }

    private var dyDirectionSum: Int = 0
    private var isHiding: Boolean = false
    private var isShowing: Boolean = false

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0 && dyDirectionSum < 0 || dy < 0 && dyDirectionSum > 0) {
            child.animate().cancel()
            dyDirectionSum = 0
        }

        dyDirectionSum += dy

        if (dyDirectionSum > child.height) {
            hideView(child)
        } else if (dyDirectionSum < -child.height) {
            showView(child)
        }
    }

    private fun hideView(view: View) {
        if (isHiding || view.visibility != View.VISIBLE) return

        val animator: ViewPropertyAnimator = view.animate()
            .translationY(0f)
            .setInterpolator(INTERPOLATOR)
            .setDuration(ANIMATION_DURATION)

        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                isHiding = true
            }

            override fun onAnimationEnd(p0: Animator?) {
                isHiding = false
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {
                isHiding = false
                showView(view)
            }

            override fun onAnimationRepeat(p0: Animator?) {
                //
            }
        })

        animator.start()
    }


    private fun showView(view: View) {
        if (isShowing || view.visibility == View.VISIBLE) return

        val animator: ViewPropertyAnimator = view.animate()
            .translationY(-view.height.toFloat())
            .setInterpolator(INTERPOLATOR)
            .setDuration(ANIMATION_DURATION)

        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                isShowing = true
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {
                isShowing = false
            }

            override fun onAnimationCancel(p0: Animator?) {
                isShowing = false
                hideView(view)
            }

            override fun onAnimationRepeat(p0: Animator?) {
                //
            }
        })

        animator.start()
    }
}