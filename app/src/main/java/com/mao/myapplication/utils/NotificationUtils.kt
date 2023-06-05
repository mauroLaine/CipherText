package com.mao.myapplication.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context


const val CHANNEL_ID = "my_channel_01"
private const val CHANNEL_NAME = "file_notification"


fun createNotificationChannel(context: Context) {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
    channel.description = "Channel description"

    // Register the channel with the system
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}