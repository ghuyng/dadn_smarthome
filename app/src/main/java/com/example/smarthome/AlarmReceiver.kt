package com.example.smarthome

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        val bundle = intent?.getBundleExtra("extra")
        val device = bundle?.getSerializable("device") as Device

        //make message
        val utility = context?.let { Utility(it) }
        if (intent.action == "TURN_ON_DEVICE" && !device.status) {
            utility?.turnOnOffDevice(device)
        }
        else if (intent.action == "TURN_OFF_DEVICE" && device.status){
            utility?.turnOnOffDevice(device)
//            device.status = false
        }

        val i = Intent(context, ViewDeviceActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        Log.d("ALARM RECIEVER", device.room)
        i.putExtra("device", device)
        Log.d("ALARM", intent.action.toString())
        val pendingIntent = PendingIntent.getActivity(context!!, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context!!, "turnonoffnoti")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Smart Home App")
            .setContentText("Device has turned On")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }

}