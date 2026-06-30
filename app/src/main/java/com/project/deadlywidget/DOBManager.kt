package com.project.deadlywidget

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DOBManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dob_prefs", Context.MODE_PRIVATE)

    fun saveDOB(dob: LocalDate) {
        prefs.edit().putString("dob", dob.format(DateTimeFormatter.ISO_LOCAL_DATE)).apply()
    }

    fun getDOB(): LocalDate? {
        val dobString = prefs.getString("dob", null)
        return dobString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }
}
