package com.example.signcustomviewexample.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt

class DrawerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GREEN
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8F
    }

    private val paintForExport = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8F
    }

    private val path = Path()
    private lateinit var bitmap: Bitmap
    private var isCleared = false
    private var drawingForExport = false

    private val touchTolerance = 4f

    private var mX = 0f
    private var mY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!drawingForExport)
            canvas?.drawPath(path, paint)
        else {
            canvas?.drawPath(path, paintForExport)
            drawingForExport = false
        }
    }

    private fun handleMove(x: Float, y: Float) {
        val distance = sqrt((x - mX).pow(2) + (y - mY).pow(2))
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

    fun getBitmap(config: Bitmap.Config): Bitmap {
        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, config)

        drawingForExport = true
        draw(Canvas(bitmap))

        return bitmap
    }
}