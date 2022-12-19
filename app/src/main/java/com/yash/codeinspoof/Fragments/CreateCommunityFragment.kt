package com.yash.codeinspoof.Fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Daos.CreateCommunityDao
import com.yash.codeinspoof.Daos.RequestedUserDao
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentCreateCommunityBinding

class CreateCommunityFragment : Fragment() {

    private var _binding : FragmentCreateCommunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var communityDao: CreateCommunityDao
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var storageRef : StorageReference =FirebaseStorage.getInstance().reference.child("CommunityBanners")
    private var imageUri : Uri? = null
    private lateinit var ru : RequestedUserDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCommunityBinding.inflate(layoutInflater , container , false)

        val communityTypes = resources.getStringArray(R.array.community_options)
        val arrayAdapter = ArrayAdapter(requireContext() , R.layout.community_dropdown_item , communityTypes)
        binding.communityOptionsDD.setAdapter(arrayAdapter)

        communityDao = CreateCommunityDao()
        ru = RequestedUserDao()

        binding.createdCommunityBT.setOnClickListener {

            if (imageUri!=null){
                if (binding.communityNameEt.text!!.isNotEmpty()){
//                    uploadData()
                    uploadData2()
                }
                else{
                    Toast.makeText(requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(requireContext(), "Please Select Image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.communityBanner.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        return binding.root
    }

    private fun uploadData2() {
        val communityName =binding.communityNameEt.text.toString()
        val communityType = binding.communityOptionsDD.text.toString()
        communityDao.createCommunity(communityName,communityType)

        Toast.makeText(requireContext(), "Community Created Successfully", Toast.LENGTH_SHORT).show()
        replaceFragment(CreateCommunityFragment())
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        binding.communityBanner.setImageURI(imageUri)
    }

//    private fun uploadData(){
//        binding.progressbar.visibility = View.VISIBLE
//        binding.createdCommunityBT.visibility = View.GONE
//        storageRef = storageRef.child(System.currentTimeMillis().toString())
//        imageUri?.let {
//            storageRef.putFile(it).addOnCompleteListener() {
//                if (it.isSuccessful){
//                    storageRef.downloadUrl.addOnSuccessListener {
//                        uri ->
//                        val communityName =binding.communityNameEt.text.toString()
//                        val communityType = binding.communityOptionsDD.text.toString()
//                        communityDao.createCommunity(communityName,communityType,it.toString())
//
//                    }.addOnCompleteListener {
//                        replaceFragment(MainFragment())
//                    }
//                }
//                else{
//                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()
//                    binding.progressbar.visibility = View.GONE
//                    binding.createdCommunityBT.visibility = View.VISIBLE
//                }
//            }
//        }
//    }

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