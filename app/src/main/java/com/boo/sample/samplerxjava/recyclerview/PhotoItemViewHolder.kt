package com.boo.sample.samplerxjava.recyclerview

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boo.sample.samplerxjava.App
import com.boo.sample.samplerxjava.R
import com.boo.sample.samplerxjava.SearchData.Photo
import com.boo.sample.samplerxjava.utils.Constants
import com.boo.sample.samplerxjava.utils.Constants.TAG
import com.bumptech.glide.Glide

class PhotoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //뷰들을 가져온다.
    private val photoImageView = itemView.findViewById<ImageView>(R.id.photo_image)
    private val photoCreatedAtText = itemView.findViewById<TextView>(R.id.created_at_text)
    private val photoLikesCountText = itemView.findViewById<TextView>(R.id.likes_count_text)

    fun bindWithView(photoItem: Photo){
        Log.d(Constants.TAG, "bindWithView() called")

        photoCreatedAtText.text = photoItem.createdAt
        photoLikesCountText.text = photoItem.likesCount.toString()

        //이미지를 설정한다.
        Glide.with(App.instance)
            .load(photoItem.thumbnail)
            .placeholder(R.drawable.ic_baseline_insert_photo_24)
            .into(photoImageView)
    }
}