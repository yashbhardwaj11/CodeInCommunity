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
import com.yash.codeinspoof.Adapters.CommunityAdapter
import com.yash.codeinspoof.Adapters.ICommunityAdapter
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Daos.CreateCommunityDao
import com.yash.codeinspoof.Daos.RequestedUserDao
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentAllComunityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AllComunityFragment : Fragment(), ICommunityAdapter {

    private var _binding : FragmentAllComunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : CommunityAdapter
    private lateinit var communityDao : CreateCommunityDao
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllComunityBinding.inflate(layoutInflater,container,false)
        communityDao = CreateCommunityDao()
        auth = FirebaseAuth.getInstance()

        setUpRecyclerView()
        getData()

        return binding.root
    }

    private fun setUpRecyclerView() {
        val communityCollection = communityDao.CommunityCollection
        val querry = communityCollection.orderBy("communityCreatedAt",Query.Direction.DESCENDING)
        val recyclerViewOption = FirestoreRecyclerOptions.Builder<Community>().setQuery(querry,Community::class.java).build()
        adapter = CommunityAdapter(recyclerViewOption,this@AllComunityFragment)
        binding.allCommunityRV.adapter = adapter
        binding.allCommunityRV.layoutManager = LinearLayoutManager(requireContext())
    }


    fun getData(){
        val communityCollection = FirebaseFirestore.getInstance().collection("Community")
        communityCollection.get()
            .addOnSuccessListener { document ->
                if (document!=null){
                    val community = document.toObjects(Community::class.java)

                }
                else{
                    Toast.makeText(requireContext(), "Document is null", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onJoinClicked(communityID: String) {
        communityDao.addMembers(communityID)
    }

    override fun onCommunityClicked(communityID: String) {

        GlobalScope.launch(Dispatchers.IO) {
            val comDao = CreateCommunityDao()
            val community = comDao.getCommunityById(communityID).await().toObject(Community::class.java)
            withContext(Dispatchers.Main){
                if (community!!.communityType.equals("Private") && !community.communityMembers.contains(auth.currentUser!!.uid)){
                    Toast.makeText(requireContext(), "You need to be a member to access the community", Toast.LENGTH_LONG).show()
                }
                else{
                    val bundle = Bundle()
                    bundle.putString("communityID" , communityID)
                    findNavController().navigate(R.id.action_mainFragment_to_communityViewFragment2,bundle)
                }
            }
        }
    }

}