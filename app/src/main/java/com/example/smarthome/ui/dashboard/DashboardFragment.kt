package com.example.smarthome.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome.*
import com.example.smarthome.databinding.FragmentDashboardBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var roomAdapter: ListRoomRecyclerViewAdapter
    private var roomList = mutableListOf<Room>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mDatabase: DatabaseReference = Firebase.database.reference
        mDatabase.child("Room").get().addOnSuccessListener {
            roomList = mutableListOf()
            (it.value as Map<String, *>).forEach { roomData ->
                val room = Room(roomData.key)
                room.deviceList = mutableListOf()
                (roomData.value as Map<String, *>).forEach { deviceData ->
                    room.deviceList.add(parseDeviceFromDB(room.name, deviceData))
                }
                roomList.add(room)
            }

            dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)

            roomAdapter = ListRoomRecyclerViewAdapter(roomList)

            val roomRecyclerView: RecyclerView = binding.recyclerViewRoom
            roomRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            roomRecyclerView.adapter = roomAdapter
            roomAdapter.viewDeviceList(roomList[0], root)

            root.setOnTouchListener(object: OnSwipeTouchListener(context){
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    val nextPos = roomAdapter.lastCheckedPos + 1
                    if (nextPos < roomList.size)
                        roomRecyclerView.findViewHolderForAdapterPosition(nextPos)?.
                        itemView?.findViewById<TextView>(R.id.room_name)?.performClick()
                    roomRecyclerView.scrollToPosition(nextPos + 1)
                }
                override fun onSwipeRight() {
                    super.onSwipeRight()
                    val nextPos = roomAdapter.lastCheckedPos - 1
                    if (nextPos > 0)
                        roomRecyclerView.findViewHolderForAdapterPosition(nextPos)?.
                        itemView?.findViewById<TextView>(R.id.room_name)?.performClick()
                    roomRecyclerView.findViewHolderForAdapterPosition(nextPos)?.
                    itemView?.findViewById<TextView>(R.id.room_name)?.performClick()
                    roomRecyclerView.scrollToPosition(nextPos - 1)
                }
            })

        }.addOnFailureListener {
            Log.e("FIREBASE", "loadPost:onCancelled", it)
            Toast.makeText(context, "Something went wrong. Please retry again sometime", Toast.LENGTH_SHORT).show()
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseDeviceFromDB(roomName: String, data: Map.Entry<String, *>): Device {
        val name = data.key
        val deviceAttrs = data.value as Map<String, *>
        val type = DeviceType.valueOf(deviceAttrs["Type"] as String)
        val status = deviceAttrs["Status"] as Boolean

        return Device(name, roomName, type, status)
    }

}