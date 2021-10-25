package com.example.love.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.love.other.animation.Constants.DATE_ALARM_ID
import com.example.love.other.animation.Constants.SWITCH_ALARM_ID
import com.example.love.other.animation.Constants.THEME_ALARM_ID
import com.example.love.other.animation.Constants.TIME_ALARM_ID
import com.example.love.other.animation.Constants.VISIBILITY_ALARM_ID
import com.example.love.MainActivity
import com.example.love.R
import com.example.love.SharedPreferences.SharedPreferences.cardVisibility
import com.example.love.SharedPreferences.SharedPreferences.customPreference
import com.example.love.SharedPreferences.SharedPreferences.dateAlarm
import com.example.love.SharedPreferences.SharedPreferences.switchStatus
import com.example.love.SharedPreferences.SharedPreferences.timeAlarm
import com.example.love.other.animation.animateImageView
import com.example.love.databinding.FragmentHomeBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val calendar: Calendar = Calendar.getInstance()

    private var dateAlarm: String = ""
    private var resultTime: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val activityData: MainActivity? = activity as MainActivity?
        val myDataFromActivity: String? = activityData?.getMyData()
        getInfoStatusAlarm(myDataFromActivity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettingsTheme()
        loadAlarm()
    }

    private fun getInfoStatusAlarm(myDataFromActivity: String?) {
        if(myDataFromActivity != null && myDataFromActivity == "true") {
            deleteCardViewAlarm()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        with(binding) {
            buttonSetAlarm.setOnClickListener {
                setAlarmDate()
            }
            statusAlarm.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    setDialogDeleteAlarm()
                }
            }
            imageView.animateImageView()
        }
    }


    private fun setDialogDeleteAlarm() {
        val builder = AlertDialog.Builder(context)
        with(builder) {
            setTitle("Удалить будильник")
            setIcon(R.drawable.ic_nights_stay_dark)
            setMessage("Вы уверены?")
            setPositiveButton("Да"){ _, _ ->
                sendDataBroadcast("cancel", System.currentTimeMillis())
                deleteCardViewAlarm()
            }
            setNegativeButton("Отмена"){ _, _ ->
                binding.statusAlarm.isChecked = true
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun sendDataBroadcast(action: String, timeInMillis: Long) {
        val activity: MainActivity? = activity as MainActivity?
        val test = activity?.sendActionToBroadcast(timeInMillis, action)
    }

    // Удаление card View с будильником
    private fun deleteCardViewAlarm() {
        save("", "", false, -1)
        binding.cardViewActiveAlarm.visibility = View.GONE
    }

    // установка даты будильника
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setAlarmDate() {
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        context?.let { DatePickerDialog(it, DatePickerDialog.OnDateSetListener
        { _, y, m, dM ->
                year = y
                month = m + 1
                dayOfMonth = dM
                dateAlarm = "$dayOfMonth.$month.$year"
                setAlarmTime(year, month, dayOfMonth)
        }, year, month, dayOfMonth)
        }?.show()
    }

    // установка времени будильника
    @SuppressLint("SimpleDateFormat", "Range")
    private fun  setAlarmTime(year: Int, month: Int, dayOfMonth: Int) {
        val picker = MaterialTimePicker
            .Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(24)
            .setMinute(0)
            .setTitleText("Выберите время для будильника")
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()

        picker.addOnPositiveButtonClickListener {
            with(calendar) {
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.MINUTE, picker.minute)
                set(Calendar.HOUR_OF_DAY, picker.hour)
                resultTime = SimpleDateFormat("HH:mm").format(time).toString()
                sendDataBroadcast("set", timeInMillis)
            }
            setAlarmCard(dateAlarm, resultTime)
        }
        fragmentManager?.let { it1 -> picker.show(it1, "tag") }
    }

    // Показываем карточку пользователю с установленным временем и датой для будильника
    private fun setAlarmCard(date: String, time: String) {
        with(binding) {
            statusAlarm.isChecked = true
            dateAlarm.text = date
            timeAlarm.text = time
            cardViewActiveAlarm.visibility = View.VISIBLE
        }
        save(time, date, true, 0)
    }

    // загрузить данные об уже установленном будильнике
    private fun loadAlarm() {
        val prefs = customPreference(requireContext(), "SharedPreferences")
        val savedTime = prefs.getString(TIME_ALARM_ID, "")
        val savedDate = prefs.getString(DATE_ALARM_ID, "")
        val savedStatusSwitch = prefs.getBoolean(SWITCH_ALARM_ID, false)
        val savedVisibility = prefs.getInt(VISIBILITY_ALARM_ID, -1)
        restoreData(savedTime, savedDate, savedVisibility, savedStatusSwitch)

    }

    // запомнить данные об установленном будильнике
    private fun save(timeAlarm: String, dateAlarm: String, checked: Boolean, visibility: Int) {
        val prefs = customPreference(requireContext(), "SharedPreferences")
        prefs.timeAlarm = timeAlarm
        prefs.dateAlarm = dateAlarm
        prefs.switchStatus = checked
        prefs.cardVisibility = visibility
    }
    // восстанавливаем данные из Preferences (если будильник уже был установлен)
    private fun restoreData(savedTime: String?, savedDate: String?, savedVisibility: Int, savedStatusSwitch: Boolean) {
        with(binding) {
            timeAlarm.text = savedTime
            dateAlarm.text = savedDate
            cardViewActiveAlarm.visibility = savedVisibility
            statusAlarm.isChecked = savedStatusSwitch
        }
    }

    private fun loadSettingsTheme() {
       val prefs = customPreference(requireContext(), "SharedPreferences")
       val savedTheme = prefs.getBoolean(THEME_ALARM_ID, false)
       restoreTheme(savedTheme)
    }

    private fun restoreTheme(savedTheme: Boolean) {
        with(binding.homeContainer) {
            if(savedTheme)
                setBackgroundResource(R.drawable.light)
            else
                setBackgroundResource(R.drawable.dark)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}