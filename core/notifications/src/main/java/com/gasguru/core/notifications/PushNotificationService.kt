package com.gasguru.core.notifications

import android.content.Context
import android.content.Intent
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val KEY_STATION_ID = "station_id"
    }

    fun init() {
        setupNotificationClickListener()
    }

    private fun setupNotificationClickListener() {
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                handleNotificationClick(data = event.notification.additionalData)
            }
        })
    }

    private fun handleNotificationClick(data: JSONObject?) {
        val stationId = data?.optString(KEY_STATION_ID)

        if (!stationId.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(context, "com.gasguru.MainActivity")
                addCategory(Intent.CATEGORY_LAUNCHER)
                putExtra(KEY_STATION_ID, stationId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }
}
