package com.yash.codeinspoof.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Adapters.*
import com.yash.codeinspoof.Daos.CreateCommunityDao
import com.yash.codeinspoof.Daos.RequestedUserDao
import com.yash.codeinspoof.Models.RequestedUser
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentRequestedUsersBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RequestedUsersFragment : Fragment(), IRequestedUser {
    private lateinit var communityId : String
    private var _binding : FragmentRequestedUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var communityDao : CreateCommunityDao
    private lateinit var requestedUserList : ArrayList<String>
    private lateinit var adapter : RequestdeUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestedUsersBinding.inflate(layoutInflater,container,false)
        communityId = arguments?.getString("communityId").toString()
        communityDao = CreateCommunityDao()
        requestedUserList = arrayListOf()

        getData()

        return binding.root
    }

    private fun getData() {
        GlobalScope.launch(Dispatchers.IO) {
            val community = communityDao.getCommunityById(communityId).await().toObject(Community::class.java)
            for(i in 0 until community!!.requestedMembers.size){
                requestedUserList.add(community.requestedMembers.get(i))
            }
            withContext(Dispatchers.Main){
                Log.d("RequestedMembers" , requestedUserList.toString())
                adapter = RequestdeUserAdapter(requireContext(),requestedUserList,this@RequestedUsersFragment)
                binding.requestedUsersRV.layoutManager = LinearLayoutManager(requireContext())
                binding.requestedUsersRV.adapter = adapter

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTickClick(uid: String) {
        requestedUserList.clear()
        getData()
        communityDao.requestedUsersAdd(communityId,uid)
        Toast.makeText(requireContext(), "User added successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(uid: String) {
        requestedUserList.clear()
        getData()
        communityDao.requestedUsersDelete(communityId,uid)
        Toast.makeText(requireContext(), "User Removed", Toast.LENGTH_SHORT).show()

    }


}