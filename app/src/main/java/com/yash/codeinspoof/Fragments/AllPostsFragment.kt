package com.yash.codeinspoof.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.yash.codeinspoof.Adapters.MemeAdapter
import com.yash.codeinspoof.Models.Meme
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentAllPostsBinding


class AllPostsFragment : Fragment() {

    private var _binding : FragmentAllPostsBinding? = null
    private val binding get() = _binding!!
    private val BASE_URL = "https://meme-api.com/gimme/40"
    private lateinit var adapter: MemeAdapter
    private lateinit var memeArrayList: ArrayList<Meme>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllPostsBinding.inflate(layoutInflater ,container,false)
        memeArrayList = arrayListOf()

        binding.swipeRefresh.setOnRefreshListener {
            memeArrayList.clear()
            getMeme()
            binding.swipeRefresh.isRefreshing = false
        }

        getMeme()

        return binding.root
    }

    private fun getMeme() {
        val url = BASE_URL
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                if (response!=null){
                    val jsonArray = response.getJSONArray("memes")
                    for (i in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(i)
                        val meme = Meme(jsonObject.getString("author")
                            ,jsonObject.getString("subreddit")
                            ,jsonObject.getString("title"),
                            jsonObject.getInt("ups"),
                            jsonObject.getString("url"))
                        memeArrayList.add(meme)
                    }

                    binding.allPostRV.layoutManager = LinearLayoutManager(requireContext())
                    adapter = MemeAdapter(requireContext(),memeArrayList)
                    binding.allPostRV.adapter = adapter
                }

            },
            { error ->
                Log.d("ErrorRetro",error.message.toString())
                Toast.makeText(requireContext(), "${error.message.toString()}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
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