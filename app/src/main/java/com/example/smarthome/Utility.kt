package com.example.smarthome

import android.content.Context
import androidx.core.content.ContextCompat

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
}