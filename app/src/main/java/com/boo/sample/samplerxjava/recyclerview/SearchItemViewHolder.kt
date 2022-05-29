package com.boo.sample.samplerxjava.recyclerview

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.boo.sample.samplerxjava.R
import com.boo.sample.samplerxjava.SearchData.SearchData
import com.boo.sample.samplerxjava.utils.Constants.TAG

class SearchItemViewHolder(
    itemView: View,
    searchRecyclerViewInterface: ISearchHistoryRecyclerView
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private lateinit var mySearchRecyclerViewInterface: ISearchHistoryRecyclerView

    private val searchTermTextView = itemView.findViewById<TextView>(R.id.search_term_text)
    private val whenSearchedTextView = itemView.findViewById<TextView>(R.id.when_searched_text)
    private val deleteSearchBtn = itemView.findViewById<ImageView>(R.id.delete_search_btn)
    private val constraintSearchItem = itemView.findViewById<ConstraintLayout>(R.id.constraint_search_item)

    init {
        Log.d(TAG, "SearchItemViewHolder - bindWithView() called")
        deleteSearchBtn.setOnClickListener(this)
        constraintSearchItem.setOnClickListener(this)
        this.mySearchRecyclerViewInterface = searchRecyclerViewInterface
    }

    fun bindWithView(searchItem: SearchData){
        Log.d(TAG, "bindWithView() called")

        whenSearchedTextView.text = searchItem.timestamp
        searchTermTextView.text = searchItem.term
    }

    override fun onClick(view: View?) {
        Log.d(TAG, "SearchItemViewHolder onClick() called")
        when(view){
            deleteSearchBtn -> {
                Log.d(TAG, "SearchItemViewHolder 검색 삭제 버튼 클릭")
                this.mySearchRecyclerViewInterface.onSearchItemDeleteClicked(adapterPosition)
            }
            constraintSearchItem -> {
                Log.d(TAG, "SearchItemViewHolder 검색 아이템 클릭")
                this.mySearchRecyclerViewInterface.onSearchItemClicked(adapterPosition)
            }
        }
    }


}