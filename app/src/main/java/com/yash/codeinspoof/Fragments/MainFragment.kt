package com.yash.codeinspoof.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.navigation.NavigationBarView
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater,container,false)


        replaceFragment(AllPostsFragment())

        binding.bottomNavMain.setOnItemSelectedListener { 
            when(it.itemId){
                R.id.home -> replaceFragment(AllPostsFragment())
                R.id.post -> replaceFragment(CreateCommunityFragment())
                R.id.explore -> replaceFragment(AllComunityFragment())
                
                else ->{
                    
                }
            }
            true
        }
        
        return binding.root
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.mainFL , fragment)
        fragmentTransaction?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}