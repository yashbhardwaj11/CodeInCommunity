package com.yash.codeinspoof.Daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Models.Comments
import com.yash.codeinspoof.Models.Post
import com.yash.codeinspoof.Models.RequestedUser
import com.yash.codeinspoof.Models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CreateCommunityDao {
    private val db = FirebaseFirestore.getInstance()
    val CommunityCollection = db.collection("Community")
    private val auth = FirebaseAuth.getInstance()

    fun createCommunity(communityName : String , communityType: String){//, communityBanner : String
        val currentUser = auth.currentUser!!.uid
        GlobalScope.launch (Dispatchers.IO){
            val userDao = UserDao()
            val user = userDao.getUserByUserId(currentUser).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val member : ArrayList<String> = arrayListOf(auth.uid.toString())
//            val community = Community(communityName,user,currentTime , communityType,communityBanner , member ,member)
            val community = Community(communityName,user,currentTime , communityType,member,
                arrayListOf(currentUser))
            CommunityCollection.document().set(community)
        }
    }

    fun getCommunityById(communityId : String) : Task<DocumentSnapshot>{
        return CommunityCollection.document(communityId).get()
    }

    fun addMembers(communityId : String){
        GlobalScope.launch (Dispatchers.IO){
            val currentUser = auth.currentUser!!.uid
            val community = getCommunityById(communityId).await().toObject(Community::class.java)
            val requestedMember = community!!.requestedMembers.contains(currentUser)
            val isMember = community.communityMembers.contains(currentUser)

            if(community.communityType.equals("Public")){
                if (isMember){
                    community.communityMembers.remove(currentUser)
                    if(community.communityModerators.contains(currentUser)){
                        community.communityModerators.remove(currentUser)
                    }
                }
                else{
                    community.communityMembers.add(currentUser)
                }
            }
            if(community.communityType.equals("Private")){

                if(isMember){
                    community.communityMembers.remove(currentUser)
                    if(community.communityModerators.contains(currentUser)){
                        community.communityModerators.remove(currentUser)
                    }
                }
                else{
                    if (requestedMember){
                        community.requestedMembers.remove(currentUser)
                    }
                    else{
                        community.requestedMembers.add(currentUser)
                    }
                }
            }
            CommunityCollection.document(communityId).set(community)
//            ru.requestedUserCollection.document(communityId).set(requestedUsers)
        }
    }

    fun requestedUsersAdd(communityId: String,uid:String){
        GlobalScope.launch {
            val community = getCommunityById(communityId).await().toObject(Community::class.java)
            community!!.communityMembers.add(uid)
            community.requestedMembers.remove(uid)
            CommunityCollection.document(communityId).set(community)
        }
    }

    fun requestedUsersDelete(communityId: String,uid:String){
        GlobalScope.launch {
            val community = getCommunityById(communityId).await().toObject(Community::class.java)
            community!!.requestedMembers.remove(uid)
            CommunityCollection.document(communityId).set(community)
        }
    }

}