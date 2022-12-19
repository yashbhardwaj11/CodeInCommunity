package com.yash.codeinspoof.Daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Models.RequestedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RequestedUserDao {
    private val auth = FirebaseAuth.getInstance()
    private val db =  FirebaseFirestore.getInstance()
    val requestedUserCollection = db.collection("RequestedUsers")
    private val currentUser = auth.currentUser!!.uid

    fun getUserById(communityId: String) : Task<DocumentSnapshot>{
        return requestedUserCollection.document(communityId).get()
    }



}