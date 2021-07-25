package com.example.smarthome.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smarthome.MainActivity
import com.example.smarthome.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

//    private lateinit var imageView: ImageView
//    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val signOutBtn = binding.btnSignOutHome
        signOutBtn.setOnClickListener { v: View ->
            Log.i("SIGN OUT", "asdf")
            Firebase.auth.signOut()
            var intent = Intent(context, MainActivity::class.java)
            context?.startActivity(intent)
            activity?.finish()
        }
//        val view : View = inflater.inflate(R.layout.fragment_home, container, false)
//        imageView = view.findViewById(R.id.imageHome)
//        textView = view.findViewById(R.id.text_home)

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}