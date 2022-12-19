package com.yash.codeinspoof.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Adapters.IPostAdapter
import com.yash.codeinspoof.Adapters.PostAdapter
import com.yash.codeinspoof.Daos.CreateCommunityDao
import com.yash.codeinspoof.Daos.PostDao
import com.yash.codeinspoof.Models.Post
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentCommunityViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.checkerframework.checker.units.qual.C

class CommunityViewFragment : Fragment(), IPostAdapter {
    private var _binding : FragmentCommunityViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var communityDao: CreateCommunityDao
    private lateinit var adapter: PostAdapter
    private lateinit var communityID: String
    private lateinit var auth : FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityViewBinding.inflate(layoutInflater,container,false)
        auth = FirebaseAuth.getInstance()
        createPost()
        binding.backBT.setOnClickListener{
            findNavController().navigate(R.id.action_communityViewFragment2_to_mainFragment)
        }
        communityDao = CreateCommunityDao()
        communityID = arguments?.getString("communityID").toString()
        setUpRecyclerView(communityID)

        checkUser()
        checkAdmin()

        binding.requestedUsers.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("communityId" , communityID)
            findNavController().navigate(R.id.action_communityViewFragment2_to_requestedUsersFragment,bundle)
        }



        return binding.root
    }

    private fun checkAdmin() {
        GlobalScope.launch(Dispatchers.IO) {
            val communityDao = CreateCommunityDao()
            val community = communityDao.getCommunityById(communityID).await().toObject(Community::class.java)
            val moderator = community!!.communityModerators.contains(auth.currentUser!!.uid)
            withContext(Dispatchers.Main){
                if(moderator){
                    binding.requestedUsers.visibility = View.VISIBLE
                    binding.requestedUsers.text = "Requested Users : ${community.requestedMembers.size}"
                }
                else{
                    binding.requestedUsers.visibility = View.GONE
                }
            }
        }
    }

    fun checkUser(){
        GlobalScope.launch (Dispatchers.IO){
            val communityDao = CreateCommunityDao()
            val community = communityDao.getCommunityById(communityID).await().toObject(Community::class.java)
            val isMember = community!!.communityMembers.contains(auth.currentUser!!.uid)
            withContext(Dispatchers.Main){
                if (isMember){
                    binding.createPostBT.visibility = View.VISIBLE
                }
                else{
                    binding.createPostBT.visibility = View.GONE
                }
            }
        }
    }

    private fun setUpRecyclerView(communityID: String?) {
        val communityCollection = FirebaseFirestore.getInstance().collection("Posts")
        val querry = communityCollection.orderBy("postedAt",Query.Direction.DESCENDING).whereEqualTo("communityId",communityID)
        val recyclerOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(querry,Post::class.java).build()
        adapter = PostAdapter(recyclerOptions,this@CommunityViewFragment)
        binding.communityPostRV.adapter = adapter
        binding.communityPostRV.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun createPost() {
        binding.createPostBT.setOnClickListener{
            val communityID = arguments?.getString("communityID")
            val bundle = Bundle()
            bundle.putString("communityID" , communityID)
            findNavController().navigate(R.id.action_communityViewFragment2_to_createPostFragment , bundle)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    private fun replaceFragment(fragment: Fragment,bundle: Bundle){
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.communityView , fragment)
        fragmentTransaction?.commit()
    }

    override fun onLikeClicked(postId: String) {
        val postDao = PostDao()
        postDao.addLike(postId)
    }

    override fun onCommentClicked(postId: String) {
        val bundle = Bundle()
        bundle.putString("postId" , postId)
        bundle.putString("communityID",communityID)
        findNavController().navigate(R.id.action_communityViewFragment2_to_commentFragment,bundle)
    }


}