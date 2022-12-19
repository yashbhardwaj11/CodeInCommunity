package com.yash.codeinspoof.Daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.yash.codeinspoof.Models.Comments
import com.yash.codeinspoof.Models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Comment

class CommentsDao {
    private val db = FirebaseFirestore.getInstance()
    val commentCollection = db.collection("PostsComment")
    private val auth = FirebaseAuth.getInstance()

    fun createComment(communityId : String , postId : String , commentTitle : String){
        val currentUser = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val userdao = UserDao()
            val user = userdao.getUserByUserId(currentUser).await().toObject(User::class.java)
            val currentTime = System.currentTimeMillis()
            val comment = Comments(communityId,postId,user,commentTitle,currentTime)
            commentCollection.document().set(comment)
        }
    }

    fun getCommentById(commentId : String) : Task<DocumentSnapshot> {
        return commentCollection.document(commentId).get()
    }

}