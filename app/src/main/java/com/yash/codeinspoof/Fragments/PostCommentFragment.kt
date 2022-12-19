package com.yash.codeinspoof.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.firebase.firestore.ktx.toObject
import com.yash.codeinspoof.Daos.CommentsDao
import com.yash.codeinspoof.Daos.PostDao
import com.yash.codeinspoof.Models.Post
import com.yash.codeinspoof.R
import com.yash.codeinspoof.databinding.FragmentPostCommentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PostCommentFragment : Fragment() {

    private  var _binding : FragmentPostCommentBinding? = null
    private val binding get() = _binding!!
    private lateinit var postid: String
    private lateinit var communityId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostCommentBinding.inflate(layoutInflater,container,false)
        postid = arguments?.getString("postid").toString()
        communityId = arguments?.getString("communityID").toString()

        setCommentTitle(postid)

        binding.postCommentBT.setOnClickListener {
            if (binding.postTitle.text.isNotEmpty()){
                postComment(communityId,postid , binding.commentTitle.text.toString())
            }
        }


        return binding.root
    }

    private fun setCommentTitle(postid: String){
        GlobalScope.launch(Dispatchers.IO) {
            val postDao = PostDao()
            val post = postDao.getPostById(postid).await().toObject(Post::class.java)
            withContext(Dispatchers.Main){
                binding.postTitle.text = post!!.postTitle.toString()
                binding.userImage.load(post.postedByUser?.userImage.toString()){
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                binding.userName.text = post.postedByUser!!.userName
            }
        }
    }

    private fun postComment(communityId : String,postid : String , commentTitle : String) {
        val commentDao = CommentsDao()
        commentDao.createComment(communityId,postid,commentTitle)
        val postdao = PostDao()
        postdao.addComment(communityId,postid,commentTitle)
        binding.commentTitle.text.clear()
        Toast.makeText(requireContext(), "Commented Successfully", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}