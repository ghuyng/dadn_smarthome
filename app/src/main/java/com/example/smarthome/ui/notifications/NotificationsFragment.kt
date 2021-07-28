package com.example.smarthome.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smarthome.DevicereportActivity
import com.example.smarthome.MainActivity
import com.example.smarthome.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.newFixedThreadPoolContext

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val btnFans: Button= binding.ButtonFans
        val btnLights: Button = binding.ButtonLights
        val btnTV: Button = binding.ButtonTV
        val btnAC: Button = binding.ButtonAC


        btnFans.setOnClickListener {
            activity?.let{
                val intent = Intent (it, DevicereportActivity::class.java)
                intent.putExtra("value","Fan")
                it.startActivity(intent)
            }
        }

        btnTV.setOnClickListener {
            activity?.let{
                val intent = Intent (it, DevicereportActivity::class.java)
                intent.putExtra("value","Television")
                it.startActivity(intent)
            }
        }

        btnAC.setOnClickListener {
            activity?.let{
                val intent = Intent (it, DevicereportActivity::class.java)
                intent.putExtra("value","Air-Conditioners")
                it.startActivity(intent)
            }
        }

        btnLights.setOnClickListener {
            activity?.let{
                val intent = Intent (it, DevicereportActivity::class.java)
                intent.putExtra("value","Light")
                it.startActivity(intent)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


