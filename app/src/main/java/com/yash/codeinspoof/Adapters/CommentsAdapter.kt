package com.yash.codeinspoof.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.yash.codeinspoof.Adapters.CommentsAdapter.CommentsViewHolder
import com.yash.codeinspoof.Models.Comments
import com.yash.codeinspoof.R
import com.yash.codeinspoof.Utils.Utils

class CommentsAdapter(options: FirestoreRecyclerOptions<Comments>)  : FirestoreRecyclerAdapter<Comments, CommentsViewHolder>(options) {
    inner class CommentsViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val userImage = view.findViewById<ImageView>(R.id.userImageComment)
        val userName = view.findViewById<TextView>(R.id.userName)
        val commentedAt = view.findViewById<TextView>(R.id.commentedAt)
        val comment = view.findViewById<TextView>(R.id.comment)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = CommentsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item , parent,false))
        return view
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int, model: Comments) {
        holder.comment.text = model.comment
        holder.commentedAt.text = Utils.getTimeAgo(model.commentedAt!!)
        holder.userName.text = model.user!!.userName
        holder.userImage.load(model.user.userImage.toString()){
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }
}