package com.dicoding.githubuserapi.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuserapi.R
import com.dicoding.githubuserapi.data.local.entity.UserEntity
import com.dicoding.githubuserapi.databinding.ItemRowUserBinding

class ListUserAdapter(private val isClickable: Boolean = true, private val onFavoriteClick: ((UserEntity) -> Unit)? = null) : ListAdapter<UserEntity , ListUserAdapter.ListViewHolder>(DIFF_CALLBACK) {
   private lateinit var onItemClickCallback: OnItemClickCallback

   fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
      this.onItemClickCallback = onItemClickCallback
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
      return ListViewHolder(ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
   }

   override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
      val user = getItem(position)
      Glide.with(holder.binding.civProfile.context)
         .load(user.avatar)
         .into(holder.binding.civProfile)
      holder.binding.tvUsername.text = user.username
      if (isClickable) {
         holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(user)
         }

         val ivFav = holder.binding.ivFav
         if (user.isFav) {
            ivFav.setImageDrawable(ContextCompat.getDrawable(ivFav.context, R.drawable.ic_baseline_favorite_24))
         } else {
            ivFav.setImageDrawable(ContextCompat.getDrawable(ivFav.context, R.drawable.ic_baseline_favorite_border_24))
         }
         ivFav.setOnClickListener {
            onFavoriteClick?.let { it1 -> it1(user) }
         }
      } else {
         holder.binding.ivFav.visibility = View.GONE
      }
   }

   class ListViewHolder(var binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root)

   interface OnItemClickCallback {
      fun onItemClicked(data: UserEntity)
   }

   companion object {
      val DIFF_CALLBACK: DiffUtil.ItemCallback<UserEntity> =
         object : DiffUtil.ItemCallback<UserEntity>() {
            override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
               return oldItem.username == newItem.username
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
               return oldItem == newItem
            }
         }
   }
}