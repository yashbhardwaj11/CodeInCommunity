package com.yash.codeinspoof.Daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.yash.codeinspoof.Models.Comments
import com.yash.codeinspoof.Models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    private val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("Posts")

    private val auth = FirebaseAuth.getInstance()

    fun createPost(postTitle : String , communityId : String){
        val currentUser = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserByUserId(currentUser).await().toObject(com.yash.codeinspoof.Models.User::class.java)
            val communityDao = CreateCommunityDao()
            val community = communityDao.getCommunityById(communityId)
            val currentTime = System.currentTimeMillis()
            val post = Post(communityId,currentTime,user,postTitle)
            postCollection.document().set(post)
        }
    }

    fun getPostById(postId: String) : Task<DocumentSnapshot>{
        return postCollection.document(postId).get()
    }

    fun addLike(postId : String){
        val auth = FirebaseAuth.getInstance()
        GlobalScope.launch(Dispatchers.IO){
            val user = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)
            val isLiked = post!!.postLikeCount.contains(user)

            if (isLiked){
                post.postLikeCount.remove(user)
            }
            else{
                post.postLikeCount.add(user)
            }
            postCollection.document(postId).set(post)

        }
    }

    fun addComment(communityId: String,postId: String , commentTitle : String){
        val auth = FirebaseAuth.getInstance()
        GlobalScope.launch {
            val post = getPostById(postId).await().toObject(Post::class.java)
            val userDao = UserDao()
            val user = userDao.getUserByUserId(auth.currentUser!!.uid).await().toObject(com.yash.codeinspoof.Models.User::class.java)
            val comment = Comments(communityId,postId,user,commentTitle,System.currentTimeMillis())

            post!!.postComment.add(comment)
            postCollection.document(postId).set(post)

        }
    }
}