package com.wafflestudio.memowithtags.mainMemo.animators

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

object ViewExpandAnimator {

    fun setExpandedState(container: ViewGroup, buttonBar: View) {
        container.visibility = View.VISIBLE
        buttonBar.visibility = View.VISIBLE
        buttonBar.alpha = 1f

        val params = container.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        container.layoutParams = params
    }

    fun setCollapsedState(container: ViewGroup, buttonBar: View) {
        buttonBar.alpha = 0f
        buttonBar.visibility = View.INVISIBLE
        container.layoutParams.height = 0
        container.visibility = View.INVISIBLE
    }

    fun expandView(container: ViewGroup, buttonBar: View) {
        // 1. buttonBar의 실제 높이 측정
        buttonBar.measure(
            View.MeasureSpec.makeMeasureSpec(container.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED
        )
        val targetHeight = buttonBar.measuredHeight

        // 2. container를 0으로 세팅
        container.layoutParams.height = 0
        container.visibility = View.VISIBLE
        container.requestLayout()

        // 3. buttonBar는 alpha=0, visible
        buttonBar.alpha = 0f
        buttonBar.visibility = View.VISIBLE

        // 4. 높이 애니메이션
        val heightAnimator = ValueAnimator.ofInt(0, targetHeight)
        heightAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            container.layoutParams.height = value
            container.requestLayout()
        }
        heightAnimator.duration = 200

        // 5. 높이 애니메이션 끝나면 buttonBar fade-in
        heightAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                buttonBar.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
        })

        heightAnimator.start()
    }

    fun collapseView(container: ViewGroup, buttonBar: View) {
        // 1. fade-out 애니메이션
        buttonBar.animate()
            .alpha(0f)
            .setDuration(50)
            .withEndAction {
                buttonBar.visibility = View.GONE

                // 2. 높이 줄이기 애니메이션
                val initialHeight = container.height
                val heightAnimator = ValueAnimator.ofInt(initialHeight, 0)
                heightAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    container.layoutParams.height = value
                    container.requestLayout()
                }
                heightAnimator.duration = 150
                heightAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        container.visibility = View.GONE
                    }
                })
                heightAnimator.start()
            }
            .start()
    }

    fun animateTextHeightChange(textView: TextView, expanded: Boolean, collapsedMaxLines: Int) {
        // 1. 현재 높이 측정
        val startHeight = textView.height

        // 2. 텍스트 최대 길이 설정
        if (expanded) {
            textView.maxLines = Integer.MAX_VALUE
        } else {
            textView.maxLines = collapsedMaxLines
        }

        // 3. 재측정 → target height 계산
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(textView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED
        )
        val targetHeight = textView.measuredHeight

        // 4. height 애니메이션
        val animator = ValueAnimator.ofInt(startHeight, targetHeight)
        animator.duration = 200
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            textView.layoutParams.height = value
            textView.requestLayout()
        }

        animator.start()
    }

    fun setTextView(textView: TextView, expanded: Boolean, collapsedMaxLines: Int) {
        textView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        textView.requestLayout()
        if (expanded) {
            textView.maxLines = Integer.MAX_VALUE
        } else {
            textView.maxLines = collapsedMaxLines
        }
    }
}
