package com.example.smarthome

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.json.JSONObject

class Utility(var context: Context) {
    fun getDeviceIcon(device: Device) =
        when(device.deviceType){
            DeviceType.Door -> ContextCompat.getDrawable(context, R.drawable.ic_door_closed)
            DeviceType.Light -> ContextCompat.getDrawable(context, R.drawable.ic_light)
            DeviceType.Fan -> ContextCompat.getDrawable(context, R.drawable.ic_fan)
            DeviceType.TV -> ContextCompat.getDrawable(context, R.drawable.ic_tv)
            DeviceType.Airconditioner -> ContextCompat.getDrawable(context, R.drawable.ic_airconditioner)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_settings)
        }

    fun getDeviceImage(device: Device) =
        when(device.deviceType){
            DeviceType.Door -> ContextCompat.getDrawable(context, R.drawable.img_door)
            DeviceType.Light -> ContextCompat.getDrawable(context, R.drawable.img_light)
            DeviceType.Fan -> ContextCompat.getDrawable(context, R.drawable.img_fan)
            DeviceType.TV -> ContextCompat.getDrawable(context, R.drawable.img_tv)
            DeviceType.Airconditioner -> ContextCompat.getDrawable(context, R.drawable.img_airconditioner)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_settings)
        }

    fun turnOnOffDevice(device: Device) {
        var apiController = APIController(context)
        apiController.jsonObjectPOST("/turn-device", JSONObject("""{
            |"room": "${device.room}",
            |"device": "${device.name}",
            |"type": "${device.deviceType.name}",
            |"data": ${!device.status}
            |}""".trimMargin())) { res ->
            Log.d("POST Request", res.toString())
            if (res?.get("message") != "good") {
                Toast.makeText(context, "Something went wrong. Please retry again sometime", Toast.LENGTH_SHORT).show()
            }
        }
    }
}