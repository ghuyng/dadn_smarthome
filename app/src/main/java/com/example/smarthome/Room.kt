package com.example.smarthome

class Room(private val name: String) {
    private var deviceList : List<Device>
    init {
        deviceList = mutableListOf(Device(name + "door1"), Device(name + "door2"),
            Device(name + "fan"), Device(name + "TV"))
    }
    public fun getName() = this.name
    public fun getDevices() = this.deviceList
}