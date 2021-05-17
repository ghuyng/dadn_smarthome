package com.example.smarthome

class Room(private val name: String) {
    private var deviceList : List<Device>
    init {
        deviceList = mutableListOf(Device(name + "'s "+ "door1", name, DeviceType.Door),
            Device(name + "'s "+ "door2", name, DeviceType.Door), Device(name  + "'s "+ "fan", name, DeviceType.Fan),
            Device(name + "'s "+ "light", name, DeviceType.Light))
    }
    public fun getName() = this.name
    public fun getDevices() = this.deviceList
}