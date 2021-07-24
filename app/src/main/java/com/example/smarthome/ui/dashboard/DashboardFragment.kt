package com.example.smarthome.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome.ListRoomRecyclerViewAdapter
import com.example.smarthome.MainActivity
import com.example.smarthome.OnSwipeTouchListener
import com.example.smarthome.R
import com.example.smarthome.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var roomAdapter: ListRoomRecyclerViewAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        roomAdapter = ListRoomRecyclerViewAdapter(MainActivity.roomList)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val roomRecyclerView: RecyclerView = binding.recyclerViewRoom
        roomRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        roomRecyclerView.adapter = roomAdapter
        roomAdapter.viewDeviceList(MainActivity.roomList[0], root)

        root.setOnTouchListener(object: OnSwipeTouchListener(context){
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                val nextPos = roomAdapter.lastCheckedPos + 1
                if (nextPos < MainActivity.roomList.size)
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


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}