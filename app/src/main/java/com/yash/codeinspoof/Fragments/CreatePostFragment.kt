package com.yash.codeinspoof.Fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.yash.codeinspoof.Daos.CreateCommunityDao
import com.yash.codeinspoof.Daos.PostDao
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {

    private var _binding : FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var imageUri : Uri? = null
    private lateinit var communityDao : PostDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(layoutInflater,container,false)

        communityDao = PostDao()

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_createPostFragment_to_communityViewFragment2)
        }

        binding.postBT.setOnClickListener {
//            if (imageUri!=null){
////                uploadDataWithImage()
//            }
            if (binding.postTitle.text.toString().isNotEmpty()){
                uploadData()
            }
        }

        binding.postImage.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        return binding.root
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        binding.postImage.setImageURI(imageUri)
    }

    private fun uploadData(){
        val postTitle = binding.postTitle.text.toString().trim()
        val communityId = arguments?.getString("communityID")
        communityDao.createPost(postTitle , communityId.toString())
        binding.postTitle.text.clear()
        Toast.makeText(requireContext(), "Posted Successfully", Toast.LENGTH_SHORT).show()
    }



}