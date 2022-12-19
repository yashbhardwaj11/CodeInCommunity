package com.yash.codeincommunity.Models

import com.yash.codeinspoof.Models.Post
import com.yash.codeinspoof.Models.User

data class Community (val communityName : String? = "",
                      val communityOwner : User = User(),
                      val communityCreatedAt : Long = 0L,
                      val communityType: String? = "",
//                      val communityBanner : String? ="",
                      val communityMembers : ArrayList<String> = ArrayList(),
                      val communityModerators : ArrayList<String> = ArrayList(),
                      val requestedMembers : ArrayList<String> = ArrayList(),
                      val posts : ArrayList<Post> = ArrayList()
)