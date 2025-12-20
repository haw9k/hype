package com.example.spaceapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFmt {
    private val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    fun format(millis: Long): String = sdf.format(Date(millis))
}
