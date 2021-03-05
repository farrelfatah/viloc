package com.vilocmaker.viloc.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class ZoomableViewGroup : ViewGroup {
    private var doubleTap = false
    private val MIN_ZOOM = 1f
    private val MAX_ZOOM = 2.5f
    private val topLeftCorner = floatArrayOf(0f, 0f)
    private var scaleFactor = 0f
    private var mode = NONE

    // Matrices used to move and zoom image.
    private val matrixInverse: Matrix = Matrix()
    private val savedMatrix: Matrix = Matrix()

    // Parameters for zooming.
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private var lastEvent: FloatArray? = null
    private var lastDownTime = 0L
    private var downTime = 0L
    private var mDispatchTouchEventWorkingArray = FloatArray(2)
    private var mOnTouchEventWorkingArray = FloatArray(2)
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        mDispatchTouchEventWorkingArray[0] = ev.x
        mDispatchTouchEventWorkingArray[1] = ev.y
        mDispatchTouchEventWorkingArray =
            screenPointsToScaledPoints(mDispatchTouchEventWorkingArray)
        ev.setLocation(mDispatchTouchEventWorkingArray[0], mDispatchTouchEventWorkingArray[1])
        return super.dispatchTouchEvent(ev)
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    private fun scaledPointsToScreenPoints(a: FloatArray): FloatArray {
        matrix.mapPoints(a)
        return a
    }

    private fun screenPointsToScaledPoints(a: FloatArray): FloatArray {
        matrixInverse.mapPoints(a)
        return a
    }

    public override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.getVisibility() !== GONE) {
                child.layout(
                    left,
                    top,
                    left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight()
                )
            }
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val childCount = childCount
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            if (child.getVisibility() !== GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val values = FloatArray(9)
        matrix.getValues(values)
        canvas.save()
        canvas.translate(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y])
        canvas.scale(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y])
        topLeftCorner[0] = values[Matrix.MTRANS_X]
        topLeftCorner[1] = values[Matrix.MTRANS_Y]
        scaleFactor = values[Matrix.MSCALE_X]
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // handle touch events here
        mOnTouchEventWorkingArray[0] = event.x
        mOnTouchEventWorkingArray[1] = event.y
        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray)
        event.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1])
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                mode = DRAG
                lastEvent = null
                downTime = SystemClock.elapsedRealtime()
                if (downTime - lastDownTime < 250L) {
                    doubleTap = true
                    val density = resources.displayMetrics.density
                    if (Math.max(
                            Math.abs(start.x - event.x),
                            Math.abs(start.y - event.y)
                        ) < 40f * density
                    ) {
                        savedMatrix.set(matrix) //repetition of savedMatrix.setmatrix
                        mid[event.x] = event.y
                        mode = ZOOM
                        lastEvent = FloatArray(4)
                        lastEvent!![1] = event.x
                        lastEvent!![0] = lastEvent!![1]
                        lastEvent!![3] = event.y
                        lastEvent!![2] = lastEvent!![3]
                    }
                    lastDownTime = 0L
                } else {
                    doubleTap = false
                    lastDownTime = downTime
                }
                start[event.x] = event.y
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
            }
            MotionEvent.ACTION_UP -> {
                if (doubleTap && scaleFactor < 1.8f) {
                    matrix.postScale(2.5f / scaleFactor, 2.5f / scaleFactor, mid.x, mid.y)
                } else if (doubleTap && scaleFactor >= 1.8f) {
                    matrix.postScale(1.0f / scaleFactor, 1.0f / scaleFactor, mid.x, mid.y)
                }
                val handler = Handler()
                handler.postDelayed(Runnable {
                    if (topLeftCorner[0] >= 0) {
                        matrix.postTranslate(-topLeftCorner[0], 0F)
                    } else if (topLeftCorner[0] < -width * (scaleFactor - 1)) {
                        matrix.postTranslate(-topLeftCorner[0] - width * (scaleFactor - 1), 0F)
                    }
                    if (topLeftCorner[1] >= 0) {
                        matrix.postTranslate(0F, -topLeftCorner[1])
                    } else if (topLeftCorner[1] < -height * (scaleFactor - 1)) {
                        matrix.postTranslate(0F, -topLeftCorner[1] - height * (scaleFactor - 1))
                    }
                    matrix.invert(matrixInverse)
                    invalidate()
                }, 1)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> {
                val density = resources.displayMetrics.density
                if (mode == DRAG) {
                    matrix.set(savedMatrix)
                    val dx = event.x - start.x
                    val dy = event.y - start.y
                    matrix.postTranslate(dx, dy)
                    matrix.invert(matrixInverse)
                    if (Math.max(
                            Math.abs(start.x - event.x),
                            Math.abs(start.y - event.y)
                        ) > 20f * density
                    ) {
                        lastDownTime = 0L
                    }
                } else if (mode == ZOOM) {
                    if (event.pointerCount > 1) {
                        val newDist = spacing(event)
                        if (newDist > 10f * density) {
                            matrix.set(savedMatrix)
                            var scale = newDist / oldDist
                            val values = FloatArray(9)
                            matrix.getValues(values)
                            if (scale * values[Matrix.MSCALE_X] >= MAX_ZOOM) {
                                scale = MAX_ZOOM / values[Matrix.MSCALE_X]
                            }
                            if (scale * values[Matrix.MSCALE_X] <= MIN_ZOOM) {
                                scale = MIN_ZOOM / values[Matrix.MSCALE_X]
                            }
                            matrix.postScale(scale, scale, mid.x, mid.y)
                            matrix.invert(matrixInverse)
                        }
                    } else {
                        if (SystemClock.elapsedRealtime() - downTime > 250L) {
                            doubleTap = false
                        }
                        matrix.set(savedMatrix)
                        var scale = event.y / start.y
                        val values = FloatArray(9)
                        matrix.getValues(values)
                        if (scale * values[Matrix.MSCALE_X] >= MAX_ZOOM) {
                            scale = MAX_ZOOM / values[Matrix.MSCALE_X]
                        }
                        if (scale * values[Matrix.MSCALE_X] <= MIN_ZOOM) {
                            scale = MIN_ZOOM / values[Matrix.MSCALE_X]
                        }
                        matrix.postScale(scale, scale, mid.x, mid.y)
                        matrix.invert(matrixInverse)
                    }
                }
            }
        }
        invalidate()
        return true
    }

    companion object {
        // States.
        private const val NONE: Byte = 0
        private const val DRAG: Byte = 1
        private const val ZOOM: Byte = 2
    }
}