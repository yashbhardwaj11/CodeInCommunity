package com.yash.codeinspoof.Adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.Daos.CreateCommunityDao
import com.yash.codeinspoof.Models.Post
import com.yash.codeinspoof.R
import com.yash.codeinspoof.Utils.Utils
import io.grpc.okhttp.internal.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostAdapter(options: FirestoreRecyclerOptions<Post> , val listener : IPostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(options) {

    inner class PostViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val subredditName : TextView = view.findViewById(R.id.subredditName)
        val postedByUser : TextView = view.findViewById(R.id.postedByUserName)
        val postedAt : TextView = view.findViewById(R.id.postedAt)
        val postTitle : TextView = view.findViewById(R.id.postTitle)
        val postURL : ImageView = view.findViewById(R.id.postURL)
        val likeBT : ImageView = view.findViewById(R.id.likeBT)
        val likeBtText : TextView = view.findViewById(R.id.likeBtText)
        val commentBT : ImageView = view.findViewById(R.id.commentBT)
        val commentBtText : TextView = view.findViewById(R.id.commentBtText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.second_main_item,parent,false))

        view.likeBT.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(view.adapterPosition).id)
        }
        view.commentBT.setOnClickListener{
            listener.onCommentClicked(snapshots.getSnapshot(view.adapterPosition).id)
        }

        return view
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {

        GlobalScope.launch(Dispatchers.IO) {
            val communityDao = CreateCommunityDao()
            val community = communityDao.getCommunityById(model.communityId.toString()).await().toObject(Community::class.java)

            withContext(Dispatchers.Main){
                val auth = FirebaseAuth.getInstance()
                holder.postedAt.text = Utils.getTimeAgo(model.postedAt!!)
                holder.postedByUser.text = "${model.postedByUser!!.userName.toString()} ."
                holder.postTitle.text = model.postTitle
                holder.subredditName.text = community!!.communityName.toString()
                holder.likeBtText.text = model.postLikeCount.size.toString()
                holder.commentBtText.text = model.postComment.size.toString()
//                holder.postURL.visibility = View.INVISIBLE
                if (model.postImageURL.isNullOrEmpty()){
                    holder.postURL.visibility = View.GONE
                }
                else{
                    holder.postURL.load(model.postImageURL){
                        crossfade(true)
                        crossfade(700)
                    }
                }
                val isLiked = model.postLikeCount.contains(auth.uid)

                if (isLiked){
                    holder.likeBT.load(R.drawable.ic_liked)
                }
                else{
                    holder.likeBT.load(R.drawable.ic_normal_like)
                }

            }
        }
    }
}

interface IPostAdapter{
    fun onLikeClicked(postId : String)
    fun onCommentClicked(postId: String)
}
