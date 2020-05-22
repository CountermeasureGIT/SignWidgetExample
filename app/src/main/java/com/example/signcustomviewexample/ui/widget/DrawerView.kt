package com.example.signcustomviewexample.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.content.withStyledAttributes
import com.example.signcustomviewexample.R
import kotlin.math.pow
import kotlin.math.sqrt

class DrawerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val paintForExport = Paint(paint)
    private val path = Path()

    private var isCleared = false
    private var drawingForExport = false

    private var touchTolerance: Float = 4f
    private var mX = 0f
    private var mY = 0f

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.DrawerView
        ) {
            val strokeColor: Int = getColor(R.styleable.DrawerView_lineColor, Color.BLACK)
            val strokeWidth: Int = getDimensionPixelSize(R.styleable.DrawerView_lineWidth, 8)
            val touchTolerance: Int =
                getDimensionPixelSize(R.styleable.DrawerView_touchTolerance, 4)

            paint.strokeWidth = strokeWidth.toFloat()
            paint.color = strokeColor
            this@DrawerView.touchTolerance = touchTolerance.toFloat()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        when {
            !drawingForExport -> {
                canvas?.drawPath(path, paint)
            }
            else -> {
                canvas?.drawPath(path, paintForExport)
                drawingForExport = false
            }
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

    fun getBitmap(
        config: Bitmap.Config,
        @Dimension lineWidthPx: Int,
        @ColorInt lineColor: Int
    ): Bitmap {
        if (lineWidthPx <= 0)
            throw IllegalArgumentException("Wrong arguments: lineWidthPx=$lineWidthPx")

        paintForExport.apply {
            strokeWidth = lineWidthPx.toFloat()
            color = lineColor
        }

        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, config)
        drawingForExport = true
        draw(Canvas(bitmap))
        return bitmap
    }
}