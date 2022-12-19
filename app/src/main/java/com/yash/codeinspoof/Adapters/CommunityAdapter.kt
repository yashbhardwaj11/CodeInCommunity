package com.yash.codeinspoof.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.yash.codeincommunity.Models.Community
import com.yash.codeinspoof.R

class CommunityAdapter(options: FirestoreRecyclerOptions<Community> , val listener : ICommunityAdapter) : FirestoreRecyclerAdapter<Community , CommunityAdapter.CommunityViewHolder>(
    options
) {

    class CommunityViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val communityName : TextView = view.findViewById(R.id.communityName)
        val joinCommunityBT : Button = view.findViewById(R.id.joinCommunityBT)
        val totalMembers : TextView = view.findViewById(R.id.totalMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val view = CommunityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.all_comunity_item , parent,false))

        view.joinCommunityBT.setOnClickListener {
            listener.onJoinClicked(snapshots.getSnapshot(view.adapterPosition).id)
        }

        view.itemView.setOnClickListener{
            listener.onCommunityClicked(snapshots.getSnapshot(view.adapterPosition).id)
        }

        return view

    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int, model: Community) {
        holder.communityName.text = model.communityName
        holder.totalMembers.text = model.communityMembers.size.toString()


        val auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser!!.uid

        if (model.communityType.equals("Public")){
            val isMember = model.communityMembers.contains(currentUserID)

            if (isMember){
                holder.joinCommunityBT.text = "Joined"
            }
            else{
                holder.joinCommunityBT.text = "Join"
            }
        }
        if(model.communityType.equals("Private")){
            val isRequested = model.requestedMembers.contains(currentUserID)
            val isMember = model.communityMembers.contains(currentUserID)

            if (isRequested && !isMember){
                holder.joinCommunityBT.text = "Requested"
            }
            else if(!isRequested && isMember){
                holder.joinCommunityBT.text = "Joined"
            }
            else{
                holder.joinCommunityBT.text = "Join"
            }
        }
    }
}

interface ICommunityAdapter{
    fun onJoinClicked(communityID: String)
    fun onCommunityClicked(communityID: String)
}