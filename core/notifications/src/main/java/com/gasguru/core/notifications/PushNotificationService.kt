package com.gasguru.core.notifications

import android.content.Context
import android.content.Intent
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickListener
import com.onesignal.notifications.INotificationLifecycleListener
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val TYPE_PRICE_DROP = "price_drop"
        private const val KEY_STATION_ID = "station_id"
        private const val KEY_TYPE = "type"
    }

    fun init() {
        setupNotificationListeners()
    }

    private fun setupNotificationListeners() {
        // Foreground
        OneSignal.Notifications.addForegroundLifecycleListener(object :
            INotificationLifecycleListener {
            override fun onWillDisplay(event: com.onesignal.notifications.INotificationWillDisplayEvent) {
                handleNotificationReceived(event.notification.additionalData)
            }
        })

        // Click notification
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: com.onesignal.notifications.INotificationClickEvent) {
                handleNotificationClick(event.notification.additionalData)
            }
        })
    }

    private fun handleNotificationReceived(data: JSONObject?) {
        data?.let {
            val stationId = it.optString(KEY_STATION_ID)
            val type = it.optString(KEY_TYPE)

            if (type == TYPE_PRICE_DROP && stationId.isNotBlank()) {
                // Handle notification received in foreground
            }
        }
    }

    private fun handleNotificationClick(data: JSONObject?) {
        data?.let {
            val stationId = it.optString(KEY_STATION_ID)
            val type = it.optString(KEY_TYPE)

            if (type == TYPE_PRICE_DROP && stationId.isNotBlank()) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName(context, "com.gasguru.MainActivity")
                    putExtra("station_id", stationId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                context.startActivity(intent)
            }
        }
    }
}