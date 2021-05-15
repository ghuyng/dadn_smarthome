package com.example.smarthome

class Room(private val name: String) {
    private var deviceList : List<Device>
    init {
        deviceList = mutableListOf(Device(name + "door1", name), Device(name + "door2", name),
            Device(name + "fan", name), Device(name + "TV", name))
    }
    public fun getName() = this.name
    public fun getDevices() = this.deviceList
}