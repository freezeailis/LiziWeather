package com.example.liziweather.ui.weather

import java.text.SimpleDateFormat
import java.util.*

object DateDescriptionUtils {
    val idx2dayDes = arrayOf("今天", "明天", "后天")

    /**
     *  输入 yyyy-mm-dd 格式的字符串 /后面可能会接一些描述字符, 将被舍去
     *  返回其代表日期的星期描述
     *  exp- in : 2023-06-19 out : 周一
     *@author aris
     *@time 2023/6/19 20:33
    */
    fun dateString2weekDes(dateString: String): String {
        val formatDate = dateString.substring(0, 10)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val idx2Week = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        val parseDate = dateFormat.parse(formatDate)
        val calendar = Calendar.getInstance()
        calendar.time = parseDate ?: Date()
        var weekIdx = calendar.get(Calendar.DAY_OF_WEEK) - 1
        weekIdx = if (weekIdx < 0) 0 else weekIdx

        return idx2Week[weekIdx]
    }


    /**
     *  输入 yyyy-mm-dd 格式的字符串 /后面可能会接一些描述字符, 将被舍去
     *  返回其中文描述
     *  exp- in : 2023-06-19 out : 6月19日
     *@author aris
     *@time 2023/6/19 20:36
    */
    fun dateString2dateDes(dateString: String): String {
        val formatDate = dateString.substring(5, 10)
        var mm = formatDate.substring(0, 2)
        var dd = formatDate.substring(3, 5)
        mm = if (mm[0] == '0') mm.substring(1) else mm
        dd = if (dd[0] == '0') dd.substring(1) else dd
        return mm + "月" + dd + "日"
    }

}


