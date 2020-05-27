package com.hangtian.customdeteandtimepickerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnShowTime.setOnClickListener {
            val currentDate = Date()
            val manager = CustomDateAndTimeDialogManager(this,currentDate)
            manager.setOnDateAndTimeListener { date ->
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val time = format.format(date)
                tvTime.text = time
            }
        }
    }
}
