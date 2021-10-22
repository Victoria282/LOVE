package com.example.love.SharedPreferences

import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.love.other.animation.Constants.APP_INSTANCE
import com.example.love.other.animation.Constants.DATE_ALARM_ID
import com.example.love.other.animation.Constants.OBJ
import com.example.love.other.animation.Constants.SWITCH_ALARM_ID
import com.example.love.other.animation.Constants.THEME_ALARM_ID
import com.example.love.other.animation.Constants.TIME_ALARM_ID
import com.example.love.other.animation.Constants.VISIBILITY_ALARM_ID
import com.example.love.other.animation.ObjectPending
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.internal.cache2.Relay.Companion.edit
import okhttp3.internal.concurrent.Task
import java.io.IOException

object SharedPreferences {
    fun customPreference(context: Context, name: String): SharedPreferences = context.
    getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    // установка темы приложения (светлая / темная)
    var SharedPreferences.theme
        get() = getBoolean(THEME_ALARM_ID, false)
        set(value) {
            editMe {
                it.putBoolean(THEME_ALARM_ID, value)
            }
        }

    var SharedPreferences.timeAlarm
        get() = getString(TIME_ALARM_ID, "")
        set(value) {
            editMe {
                it.putString(TIME_ALARM_ID, value)
            }
        }

    var SharedPreferences.dateAlarm
        get() = getString(DATE_ALARM_ID, "")
        set(value) {
            editMe {
                it.putString(DATE_ALARM_ID, value)
            }
        }

    var SharedPreferences.switchStatus
        get() = getBoolean(SWITCH_ALARM_ID, false)
        set(value) {
            editMe {
                it.putBoolean(SWITCH_ALARM_ID, value)
            }
        }

    var SharedPreferences.cardVisibility
        get() = getInt(VISIBILITY_ALARM_ID, 0)
        set(value) {
            editMe {
                it.putInt(VISIBILITY_ALARM_ID, value)
            }
        }

    var SharedPreferences.app
        get() = getBoolean(APP_INSTANCE, false)
        set(value) {
            editMe {
                it.putBoolean(APP_INSTANCE, value)
            }
        }

    var SharedPreferences.arr
        get() = getString("ARR", "")
        set(value) {
            editMe {
                it.putString("task", value)
            }
        }
    }