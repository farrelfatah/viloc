package com.vilocmaker.viloc.ui.floormap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.vilocmaker.viloc.R

class PinView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null
) : SubsamplingScaleImageView(context, attr) {

    private val paint = Paint()
    private val vPin = PointF()
    private var sPins = mutableListOf<PointF>()
    private var pin: Bitmap? = null

    fun addPin(sPin: PointF) {

        this.sPins.add(sPin)

        val density = resources.displayMetrics.densityDpi.toFloat()
        val bitmap = getBitmap(context, R.drawable.ic_baseline_run_circle_24)
        val w = density / 420f * bitmap!!.width
        val h = density / 420f * bitmap.height
        pin = Bitmap.createScaledBitmap(bitmap, w.toInt(), h.toInt(), true)

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) return

        paint.isAntiAlias = true

        for (eachPin in sPins) {
            sourceToViewCoord(eachPin, vPin)
            val vX = vPin.x - pin!!.width / 2
            val vY = vPin.y - pin!!.height
            canvas.drawBitmap(pin!!, vX, vY, paint)
        }
    }

    private fun getBitmap(context: Context, vectorDrawableId: Int): Bitmap? {
        val vectorDrawable = context.getDrawable(vectorDrawableId)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }
}
