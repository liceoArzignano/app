package it.liceoarzignano.foundation.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import it.liceoarzignano.foundation.R

class CircularProgressBar : View {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private var mSweepAngle = 0f
    private var mProgressColor: Int = 0
    private var mValue: Double = 0.toDouble()

    // Bool attrs
    private var mShouldAnimate = true

    // Color attrs
    private var mTextColor = Color.BLACK
    private var mBackgroundColor = Color.BLACK
    private var mLowColor = Color.RED
    private var mMediumColor = Color.YELLOW
    private var mHighColor = Color.GREEN

    // Float attrs
    private var mLowThreshold = 5.5f
    private var mHighThreshold = 6.0f

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, style: Int) : super(context, attrs, style) {
        init(context, attrs)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initMeasurements()
        drawOutlineArc(canvas)
        drawText(canvas)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0)

        mShouldAnimate = array.getBoolean(R.styleable.CircularProgressBar_shouldAnimate, mShouldAnimate)

        mTextColor = array.getColor(R.styleable.CircularProgressBar_textColor, mTextColor)
        mBackgroundColor = array.getColor(R.styleable.CircularProgressBar_backgroundColor, mBackgroundColor)
        mLowColor = array.getColor(R.styleable.CircularProgressBar_lowColor, mLowColor)
        mMediumColor = array.getColor(R.styleable.CircularProgressBar_mediumColor, mMediumColor)
        mHighColor = array.getColor(R.styleable.CircularProgressBar_highColor, mHighColor)

        mLowThreshold = array.getFloat(R.styleable.CircularProgressBar_lowThreshold, mLowThreshold)
        mHighThreshold = array.getFloat(R.styleable.CircularProgressBar_highThreshold, mHighThreshold)

        array.recycle()
    }

    private fun initMeasurements() {
        mViewWidth = width
        mViewHeight = height
    }

    private fun drawOutlineArc(canvas: Canvas) {
        val diameter = Math.min(mViewWidth, mViewHeight) - 32f
        val outerOval = RectF(16f, 16f, diameter, diameter)

        mPaint.color = mBackgroundColor
        mPaint.strokeWidth = 32f
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval, 0f, 360f, false, mPaint)

        mPaint.color = mProgressColor
        canvas.drawArc(outerOval, -90f, mSweepAngle, false, mPaint)
    }

    private fun drawText(canvas: Canvas) {
        mPaint.textSize = Math.min(mViewWidth, mViewHeight) / 5f
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.strokeWidth = 0f
        mPaint.color = mTextColor

        // Center text
        val posX = canvas.width / 2
        val posY = (canvas.height / 2 - (mPaint.descent() + mPaint.ascent()) / 2).toInt()

        canvas.drawText("%.2f".format(mValue), posX.toFloat(), posY.toFloat(), mPaint)
    }

    private fun calcSweepAngleFromProgress(progress: Int): Float = (36 * progress / 100).toFloat()

    fun setProgress(progress: Double) {
        mValue = progress
        mProgressColor = when {
            mValue < mLowThreshold -> mLowColor
            mValue < mHighThreshold -> mMediumColor
            else -> mHighColor
        }

        // Animate only the first time if needed
        if (!mShouldAnimate || mSweepAngle != 0f) {
            mSweepAngle = calcSweepAngleFromProgress(
                    if (progress < 1) 100 else (progress * 100).toInt())
            return
        }

        mSweepAngle = 0f

        val animator = ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(
                if (progress < 1) 100 else (progress * 100).toInt()))

        animator.interpolator = FastOutSlowInInterpolator()
        animator.duration = 1600
        animator.startDelay = 300
        animator.addUpdateListener { valueAnimator ->
            mSweepAngle = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.start()
    }
}
