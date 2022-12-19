package com.yash.codeinspoof.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yash.codeinspoof.Models.Meme
import com.yash.codeinspoof.R
import org.json.JSONObject

class MemeAdapter(val context: Context, val memeList: ArrayList<Meme>) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {
    inner class MemeViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val subredditName : TextView = view.findViewById(R.id.subredditName)
        val postedByUser : TextView = view.findViewById(R.id.postedByUserName)
        val postedAt : TextView = view.findViewById(R.id.postedAt)
        val postTitle : TextView = view.findViewById(R.id.postTitle)
        val postURL : ImageView = view.findViewById(R.id.postURL)

        val likeBtText : TextView = view.findViewById(R.id.likeBtText)
        val likeBt : ImageView = view.findViewById(R.id.likeBT)

        val commentBtText : TextView = view.findViewById(R.id.commentBtText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        return MemeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.meme_item,parent,false))
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val current = memeList[position]
        holder.likeBtText.text = current.ups.toString()
        holder.postTitle.text = current.title
        holder.postURL.load(current.url){
            crossfade(true)
            crossfade(1000)
        }
        holder.postedByUser.text = current.author
        holder.subredditName.text = current.subreddit
        holder.likeBt.load(R.drawable.ic_liked)
    }

    override fun getItemCount(): Int = memeList.size
}