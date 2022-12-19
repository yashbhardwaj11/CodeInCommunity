package com.yash.codeinspoof.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.yash.codeinspoof.Daos.UserDao
import com.yash.codeinspoof.Models.RequestedUser
import com.yash.codeinspoof.Models.User
import com.yash.codeinspoof.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RequestdeUserAdapter(val context: Context, val mlist : ArrayList<String>, val listener : IRequestedUser) : RecyclerView.Adapter<RequestdeUserAdapter.RequestedUserViewHolder>() {
    inner class RequestedUserViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val userNameRU : TextView = view.findViewById(R.id.userNameRU)
        val userImageRU : ImageView = view.findViewById(R.id.userImageRU)
        val tickBT : ImageView = view.findViewById(R.id.addUser)
        val deleteBT : ImageView = view.findViewById(R.id.deleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestedUserViewHolder {
        val view = RequestedUserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.requested_user_item,parent,false))


        return view
    }

    override fun onBindViewHolder(holder: RequestedUserViewHolder, position: Int) {
        val current = mlist[position]
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserByUserId(current).await().toObject(User::class.java)
            withContext(Dispatchers.Main){

                holder.userNameRU.text = user?.userName
                holder.userImageRU.load(user?.userImage){
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                holder.tickBT.setOnClickListener{
                    listener.onTickClick(current)
                }
                holder.deleteBT.setOnClickListener{
                    listener.onDeleteClick(current)
                }

            }
        }
    }

    override fun getItemCount(): Int = mlist.size
}

interface IRequestedUser{
    fun onTickClick(uid : String)
    fun onDeleteClick(uid : String)
}