package com.example.signcustomviewexample.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GREEN
        textAlign = Paint.Align.CENTER
        textSize = 50F
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 5F
    }
    private val path = Path()
    private lateinit var bitmap: Bitmap

    private var resetClicked = false
    private var prevPointerPosition: PointF? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                if (prevPointerPosition != null) {
                    prevPointerPosition?.x = event.x
                    prevPointerPosition?.y = event.y
                } else
                    prevPointerPosition = PointF(event.x, event.y)

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(event.x, event.y)
                prevPointerPosition = null

                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (resetClicked) {
                    path.moveTo(event.x, event.y)
                    resetClicked = false
                } else
                    path.lineTo(event.x, event.y)
                prevPointerPosition?.x = event.x
                prevPointerPosition?.y = event.y

                invalidate()
            }
        }
        return true
    }

    fun resetDrawer() {
        path.reset()
        resetClicked = true
        invalidate()
    }
}