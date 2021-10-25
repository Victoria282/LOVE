package com.example.love.SharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.example.love.other.animation.Constants.ALARM_DATE
import com.example.love.other.animation.Constants.ALARM_TIME
import com.example.love.other.animation.Constants.APP_INSTANCE
import com.example.love.other.animation.Constants.DATE_ALARM_ID
import com.example.love.other.animation.Constants.SWITCH_ALARM_ID
import com.example.love.other.animation.Constants.THEME_ALARM_ID
import com.example.love.other.animation.Constants.TIME_ALARM_ID
import com.example.love.other.animation.Constants.VISIBILITY_ALARM_ID

object SharedPreferences {
    fun customPreference(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

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

    var SharedPreferences.alarmTime
        get() = getLong(ALARM_TIME, 0L)
        set(value) {
            editMe {
                it.putLong(ALARM_TIME, value)
            }
        }

    var SharedPreferences.alarmAction
        get() = getString(ALARM_DATE, "")
        set(value) {
            editMe {
                it.putString(ALARM_DATE, value)
            }
        }
}