package com.example.smarthome

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        val bundle = intent?.getBundleExtra("extra")
        val device = bundle?.getSerializable("device") as Device
        val database = Firebase.database.reference.child("Room").child(device.room).child(device.name)
        var deviceScheduleMode : Int = 0
        database.child("ScheduleMode").get().addOnSuccessListener {
            deviceScheduleMode =  it.value.toString().toInt()
        }

        //make message
        val utility = context?.let { Utility(it) }
        if (intent.action == "TURN_ON_DEVICE" && !device.status) {
            utility?.turnOnOffDevice(device)
            deviceScheduleMode = deviceScheduleMode and SetTurnOnOffTimeActivity.ScheduleMode.TURN_OFF_DEVICE.ordinal
        }
        else if (intent.action == "TURN_OFF_DEVICE" && device.status){
            utility?.turnOnOffDevice(device)
            deviceScheduleMode = deviceScheduleMode and SetTurnOnOffTimeActivity.ScheduleMode.TURN_ON_DEVICE.ordinal
        }

        database.child("ScheduleMode").setValue(deviceScheduleMode)

        val i = Intent(context, ViewDeviceActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        Log.d("ALARM RECIEVER", device.room)
        i.putExtra("device", device)
        Log.d("ALARM", intent.action.toString())
        val pendingIntent = PendingIntent.getActivity(context!!, 0, i, PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(context!!, "turnonoffnoti")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_stat_name))
            .setContentTitle("Smart Home App")
            .setContentText("${device.name} has turned ${if (intent.action == "TURN_ON_DEVICE") "On" else "Off"}")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }

}