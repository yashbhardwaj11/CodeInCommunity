package com.yash.codeinspoof.Models


data class Post (val communityId : String? ="",
                 val postedAt : Long? = 0L,
                 val postedByUser : User? = User(),
                 val postTitle : String? ="",
                 val postLikeCount : ArrayList<String> = ArrayList(),
                 val postComment : ArrayList<Comments> = ArrayList(),
                 val postImageURL : String? =""
                 )