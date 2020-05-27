package com.hangtian.customdeteandtimepickerdemo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.lang.reflect.Field
import java.util.*

/**
 * Author:         LYB
 * CreateDate:     2019/3/29 15:11
 * Description:    自定义时间选择器（年月日时分）
 */
class CustomDateAndTimeDialogManager(private val context: Context,private val mDate: Date) {
    private lateinit var picker1: NumberPicker
    private lateinit var picker2: NumberPicker
    private lateinit var picker3: NumberPicker
    private lateinit var picker4: NumberPicker
    private lateinit var picker5: NumberPicker
    private fun showContent(context: Context) {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_date_time, null, false)
        initContentView(contentView)
        val alertDialog =
            AlertDialog.Builder(context)
//                .setTitle("选择时间")
                .setView(contentView)
                .setPositiveButton("确定") { dialog: DialogInterface, which: Int ->
                    isDateEffective(picker1.value, picker2.value, picker3.value, picker4.value, picker5.value)
                    dialog.dismiss()
                }.setNegativeButton("取消") { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                }
                .create()

        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            .setTextColor(Color.parseColor("#ff000000"))
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(Color.parseColor("#ffa6a6a6"))
    }

    /**点击确定后获取选定时间，并向回调接口传入时间 */
    private fun isDateEffective(mYear: Int, mMonth: Int, mDay: Int, mHour: Int, mMinute: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = mYear
        calendar[Calendar.MONTH] = mMonth - 1
        calendar[Calendar.DAY_OF_MONTH] = mDay
        calendar[Calendar.HOUR_OF_DAY] = mHour
        calendar[Calendar.MINUTE] = mMinute
        val selectedDate = calendar.time

        mListenerBlock(selectedDate)
    }

    private fun initContentView(contentView: View) {
        picker1 = contentView.findViewById(R.id.picker1)
        picker2 = contentView.findViewById(R.id.picker2)
        picker3 = contentView.findViewById(R.id.picker3)
        picker4 = contentView.findViewById(R.id.picker4)
        picker5 = contentView.findViewById(R.id.picker5)
        val calendar = Calendar.getInstance()
        calendar.time = mDate
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH] + 1
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        val currentMinute = calendar[Calendar.MINUTE]
        val dateFormatter =
            DateFormatter()
        picker1.maxValue = currentYear + 10
        picker1.minValue = currentYear - 10
        picker1.value = currentYear
        picker1.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS //设置NumberPicker不可编辑
        picker2.maxValue = 12
        picker2.minValue = 1
        picker2.value = currentMonth
        picker2.setFormatter(dateFormatter)
        picker2.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS //设置NumberPicker不可编辑
        picker3.maxValue = 31
        picker3.minValue = 1
        picker3.value = currentDay
        picker3.setFormatter(dateFormatter)
        picker3.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS //设置NumberPicker不可编辑
        picker4.maxValue = 23
        picker4.minValue = 0
        picker4.value = currentHour
        picker4.setFormatter(dateFormatter)
        picker4.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS //设置NumberPicker不可编辑
        picker5.maxValue = 59
        picker5.minValue = 0
        picker5.value = currentMinute
        picker5.setFormatter(dateFormatter)
        picker5.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS //设置NumberPicker不可编辑
        picker1.setOnValueChangedListener(OnValueChangeListener { picker: NumberPicker?, oldVal: Int, newVal: Int ->
            judgeDate(newVal, picker2.value)
        })
        picker2.setOnValueChangedListener(OnValueChangeListener { picker: NumberPicker?, oldVal: Int, newVal: Int ->
            judgeDate(picker1.value, newVal)
        })

        setNumberPickerDividerColor(picker1)
        setNumberPickerDividerColor(picker2)
        setNumberPickerDividerColor(picker3)
        setNumberPickerDividerColor(picker4)
        setNumberPickerDividerColor(picker5)
    }

    private fun judgeDate(mYear: Int, mMonth: Int) { //根据是否为闰年显示日期
        if (picker2.value == 2) {
            if (mYear % 400 == 0) {
                picker3.maxValue = 29
            } else if (mYear % 4 == 0 && mYear % 100 != 0) {
                picker3.maxValue = 29
            } else {
                picker3.maxValue = 28
            }
        } else if (mMonth == 2 || mMonth == 4 || mMonth == 6 || mMonth == 9 || mMonth == 11) {
            picker3.maxValue = 30
        } else {
            picker3.maxValue = 31
        }
    }

    /**回调函数 */
    private lateinit var mListenerBlock : (Date)->Unit
    fun setOnDateAndTimeListener(block:(Date)->Unit){
        mListenerBlock = block
    }

    //设置NumberPicker分割线的颜色值(通过反射的方式设置颜色)
    private fun setNumberPickerDividerColor(picker: NumberPicker) {
        val pickerFields: Array<Field> = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    pf.set(
                        picker,
                        ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary))
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                break
            }
        }
    }


    internal inner class DateFormatter : NumberPicker.Formatter {
        override fun format(value: Int): String {
            return if (value < 10) {
                "0$value"
            } else value.toString() + ""
        }
    }

    init {
        showContent(context)
    }
}