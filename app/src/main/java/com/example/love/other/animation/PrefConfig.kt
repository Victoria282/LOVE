package com.example.love.other.animation

import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PrefConfig {
    fun writeList(context: Context, list: ArrayList<PendingIntent>) {
        val pref:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefEd: SharedPreferences.Editor = pref.edit()
        val gson: Gson = Gson()
        val jsonString: String = gson.toJson(list)

        prefEd.putString("test", jsonString)
        prefEd.apply()
    }

    fun readListFromPref(context: Context): ArrayList<PendingIntent>? {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val jsonString = pref.getString("test", "")
        val gson: Gson = Gson()
        val turnsType = object : TypeToken<List<PendingIntent>>() {}.type
        return gson.fromJson(jsonString, turnsType)
    }
}