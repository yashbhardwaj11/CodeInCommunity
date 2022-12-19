package com.yash.codeinspoof.Daos

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.yash.codeinspoof.Models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun addUser(user : User?){
        user?.let {
            GlobalScope.launch (Dispatchers.IO) {
                userCollection.document(user.userID!!).set(it)
            }
        }
    }

    fun getUserByUserId(uid : String) : Task<DocumentSnapshot> {
        return userCollection.document(uid).get()
    }
}