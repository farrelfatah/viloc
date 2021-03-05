package com.vilocmaker.viloc.ui

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import com.vilocmaker.viloc.R
import kotlinx.android.synthetic.main.activity_floor_map.*

class FloorMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_map)

        svgImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        val svg = SVG.getFromAsset(assets, "floorplan_one.svg")

        val drawable = PictureDrawable(svg.renderToPicture())

        svgImageView.setImageDrawable(drawable)

        zoomControl.setOnTouchListener { _, event ->
            onTouchEvent(event)
        }

    }

}