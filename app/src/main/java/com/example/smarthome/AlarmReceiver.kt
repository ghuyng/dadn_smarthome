package com.example.smarthome

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import org.json.JSONObject


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        var device = intent?.getSerializableExtra("device") as Device

        //make message
//        var utility = context?.let { Utility(it) }
//        utility?.turnOnOffDevice(device)
//        if (intent?.action == "TURN_ON_ACTION") {
//            device.status = true
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
//        }
//        else if (intent?.action == "TURN_OFF_ACTION"){
//            device.status = false
//        }

        val i = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        Log.d("ALARM", intent.action.toString())
        val pendingIntent = PendingIntent.getActivity(context!!, 0, i, 0)

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


    private fun makeMessage(device: Device, data: String): JSONObject {
        val message = JSONObject()
        message.put("id", "11")
        message.put("name", "RELAY")
        message.put("data", data)
        message.put("unit", "")
        return message
    }

}