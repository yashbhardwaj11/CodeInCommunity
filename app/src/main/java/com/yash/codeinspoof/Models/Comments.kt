package com.yash.codeinspoof.Models

data class Comments(val communityId : String? = "",
                    val postId : String? = "",
                    val user : User? = User(),
                    val comment : String = "",
                    val commentedAt : Long? = 0L
)