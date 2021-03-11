package com.vilocmaker.viloc.ui.floormap

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.data.preference.SharedPreferences2.levelNumber
import com.vilocmaker.viloc.util.Constant.Companion.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.activity_floor_number.*

class FloorNumberActivity : AppCompatActivity() {

    private lateinit var rg: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_number)

        val floorNumber = intent.getIntExtra(EXTRA_MESSAGE, 1)

        SharedPreferences2.init(this)

        rg = RadioGroup(this)
        rg.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        for (i in 1..levelNumber) {
            val rb = RadioButton(this)
            val text = "Lantai $i"
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80)
            rb.layoutParams = params
            rb.id = View.generateViewId()
            rb.text = text
            rb.setTextAppearance(R.style.TextAppearance_MdcTypographyStyles_Body1)
            rb.isChecked = i == floorNumber
            rg.addView(rb)
        }
        
        floorNumber_linearLayout.addView(rg)

        chooseFloor_button.setOnClickListener {
            val rb = findViewById<RadioButton>(rg.checkedRadioButtonId)

            val intent = Intent(this, FloorMapActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, rb.text.substring(7,8).toInt())
            }
            startActivity(intent)
        }

        floorNumber_topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, FloorMapActivity::class.java)
        startActivity(intent)
    }
}