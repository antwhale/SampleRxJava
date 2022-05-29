package com.boo.sample.samplerxjava.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boo.sample.samplerxjava.App
import com.boo.sample.samplerxjava.R
import com.boo.sample.samplerxjava.SearchData.Photo
import com.boo.sample.samplerxjava.utils.Constants
import com.bumptech.glide.Glide

class PhotoGridRecyeclerViewAdapter : RecyclerView.Adapter<PhotoItemViewHolder>(){
    private var photoList = ArrayList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val photoItemViewHolder = PhotoItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_photo_item, parent, false))
        return photoItemViewHolder
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.bindWithView(this.photoList[position])
    }

    override fun getItemCount(): Int {
        return this.photoList.size
    }

    fun submitList(photoList: ArrayList<Photo>){
        this.photoList = photoList
    }


}

