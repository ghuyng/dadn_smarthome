package com.example.smarthome

import java.io.Serializable

class Device (var name: String, var room: String): Serializable {
    var status: Boolean = true
}