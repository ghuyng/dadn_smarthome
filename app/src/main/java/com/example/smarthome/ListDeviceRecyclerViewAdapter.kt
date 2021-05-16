package com.example.smarthome

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ListDeviceRecyclerViewAdapter(private val deviceList : List<Device>): RecyclerView.Adapter<ListDeviceRecyclerViewAdapter.DeviceViewHolder>() {
    inner class DeviceViewHolder(view: View): RecyclerView.ViewHolder(view){
        var button: Button = view.findViewById(R.id.device_btn)
        init {
            button.setOnClickListener { v: View ->
                val intent = Intent(v.context, ViewDeviceActivity::class.java)
                val device = deviceList[adapterPosition]
                intent.putExtra("device", device)
                v.context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_device_layout, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.button.setText(deviceList[position].name)
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

}


class ListRoomRecyclerViewAdapter(private val roomList : List<Room>): RecyclerView.Adapter<ListRoomRecyclerViewAdapter.RoomViewHolder>() {
    private var lastCheckedTitle: TextView? = null
    inner class RoomViewHolder(view: View): RecyclerView.ViewHolder(view){
        var title: TextView = view.findViewById(R.id.room_name)
        init {
            title.setOnClickListener {
                val position: Int = adapterPosition
                viewDeviceList(roomList[position], view)
                lastCheckedTitle?.isSelected = false
                lastCheckedTitle = title
                title.isSelected = true
            }
        }
    }

     fun viewDeviceList(room: Room, view: View){
        val deviceList = room.getDevices()
        val deviceAdapter = ListDeviceRecyclerViewAdapter(deviceList)
        val root = view.rootView
        val deviceRecyclerView: RecyclerView = root.findViewById(R.id.recycler_view_device)
        deviceRecyclerView.layoutManager = GridLayoutManager(root.context, 2)
        deviceRecyclerView.adapter = deviceAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_room_layout, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.title.text = roomList[position].getName()
        if (position == 0 && lastCheckedTitle == null) {
            holder.title.isSelected = true
            lastCheckedTitle = holder.title
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

}
