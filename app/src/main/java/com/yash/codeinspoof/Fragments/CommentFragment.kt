package com.yash.codeinspoof.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Adapters.CommentsAdapter
import com.yash.codeinspoof.Daos.CreateCommunityDao
import com.yash.codeinspoof.Models.Comments
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentCommentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CommentFragment : Fragment() {

    private var _binding : FragmentCommentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : CommentsAdapter
    private lateinit var postId : String
    private lateinit var communityID : String
    private lateinit var auth : FirebaseAuth
    private lateinit var currentUserID : String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentBinding.inflate(layoutInflater,container,false)

        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser!!.uid

        postId = arguments?.getString("postId").toString()
        communityID = arguments?.getString("communityID").toString()
        val bundle = Bundle()
        bundle.putString("postid",postId)
        bundle.putString("communityId",communityID)

        setUpRecyclerView()
        checkUser()

        binding.view1.setOnClickListener{
            findNavController().navigate(R.id.action_commentFragment_to_postCommentFragment,bundle)
        }


        return binding.root
    }

    fun checkUser(){
        GlobalScope.launch (Dispatchers.IO){
            val communityDao = CreateCommunityDao()
            val community = communityDao.getCommunityById(communityID).await().toObject(Community::class.java)
            val isMember = community!!.communityMembers.contains(auth.currentUser!!.uid)
            withContext(Dispatchers.Main){
                if (isMember){
                    binding.view1.visibility = View.VISIBLE
                }
                else{
                    binding.view1.visibility = View.GONE
                }
            }
        }
    }

    fun setUpRecyclerView() {
        val postCollection = FirebaseFirestore.getInstance().collection("PostsComment")
        val query = postCollection.orderBy("commentedAt",Query.Direction.DESCENDING).whereEqualTo("postId",postId)
        val recyclerOptions = FirestoreRecyclerOptions.Builder<Comments>().setQuery(query,Comments::class.java).build()
        adapter = CommentsAdapter(recyclerOptions)
        binding.commentRV.adapter = adapter
        binding.commentRV.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}