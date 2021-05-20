package com.example.smarthome

import java.io.Serializable

enum class DeviceType {
    Door, TV, Fan, Light, Other
}
class Device (var name: String, var room: String, var deviceType: DeviceType): Serializable {
    var status: Boolean = true
}