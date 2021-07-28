package com.example.smarthome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.smarthome.model.RoomReport
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter (var mCtx: Context, var resources:Int, var items:List<RoomReport>):ArrayAdapter<RoomReport>(mCtx,resources,items) {
    var roomReport : ArrayList<RoomReport> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view:View = layoutInflater.inflate(resources, null)

        val roomNameText: TextView = view.findViewById(R.id.room_name)
        val hoursText: TextView = view.findViewById(R.id.Hour)
        var mItem:RoomReport = items[position]
        roomNameText.text = mItem.roomName
        hoursText.text = mItem.Hour
        return view
    }
//    fun setItems(myList: ArrayList<RoomReport>) {
//        roomReport.clear()
//        roomReport.addAll(myList)
//        notifyDataSetChanged()
//    }
}