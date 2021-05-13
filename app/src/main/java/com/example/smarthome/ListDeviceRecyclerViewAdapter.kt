package com.example.smarthome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class DeviceViewHolder(view: View): RecyclerView.ViewHolder(view){
    var button: Button = view.findViewById(R.id.device_btn)
}

class ListDeviceRecyclerViewAdapter(private val deviceList : List<Device>): RecyclerView.Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_device_layout, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.button.setText(deviceList[position].getName())
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

}