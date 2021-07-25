package com.example.smarthome

data class Room(public val name: String) {
    public var deviceList = mutableListOf<Device>()
}