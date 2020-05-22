package com.example.signcustomviewexample.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class DrawerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GREEN
        textAlign = Paint.Align.LEFT
        textSize = 50F
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 5F
    }

    private val path = Path()
    private lateinit var bitmap: Bitmap
    private var isCleared = false

    private val touchTolerance = 4f

    private var mX = 0f
    private var mY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }

    private fun handleMove(x: Float, y: Float) {
        val distance = sqrt(abs(x - mX).pow(2) + abs(y - mY).pow(2))
        if (distance >= touchTolerance) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
        } else {
            path.lineTo(x, y)
        }
        mX = x
        mY = y
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                mX = event.x
                mY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (isCleared) {
                    path.moveTo(event.x, event.y)
                    isCleared = false
                } else {
                    handleMove(event.x, event.y)
                }

                mX = event.x
                mY = event.y

                invalidate()
            }
        }
        return true
    }

    fun clearDrawer() {
        path.reset()
        isCleared = true
        invalidate()
    }
}