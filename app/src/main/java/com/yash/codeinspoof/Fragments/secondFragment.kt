package com.yash.codeinspoof.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentSecondBinding

class secondFragment : Fragment() {

    private var _binding : FragmentSecondBinding?= null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(layoutInflater , container,false)

        binding.bottomNavSecondMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.explore -> findNavController().navigate(R.id.action_secondFragment_to_signInFragment)
                R.id.home -> findNavController().navigate(R.id.action_secondFragment_to_signInFragment)
                R.id.post -> findNavController().navigate(R.id.action_secondFragment_to_signInFragment)

                else -> {

                }
            }
            true
        }

        binding.profileSecond.setOnClickListener{
            findNavController().navigate(R.id.action_secondFragment_to_signInFragment)
        }

        return binding.root
    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction  = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.secondMainFragmentFL , fragment)
        fragmentTransaction?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}