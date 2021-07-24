package com.example.smarthome

import java.io.Serializable

enum class DeviceType {
    Door, TV, Fan, Light, Airconditioner, Other
}

data class Device (
    var name: String,
    var room: String,
    var deviceType: DeviceType,
    var status: Boolean = true
): Serializable {
}