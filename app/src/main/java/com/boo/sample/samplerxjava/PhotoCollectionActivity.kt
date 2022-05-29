package com.boo.sample.samplerxjava

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.boo.sample.samplerxjava.SearchData.Photo
import com.boo.sample.samplerxjava.SearchData.SearchData
import com.boo.sample.samplerxjava.recyclerview.ISearchHistoryRecyclerView
import com.boo.sample.samplerxjava.recyclerview.PhotoGridRecyeclerViewAdapter
import com.boo.sample.samplerxjava.recyclerview.SearchHistoryRecyclerViewAdapter
import com.boo.sample.samplerxjava.utils.Constants.TAG
import com.boo.sample.samplerxjava.utils.SharedPrefManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class PhotoCollectionActivity : AppCompatActivity(),
    SearchView.OnQueryTextListener,
    CompoundButton.OnCheckedChangeListener,
    View.OnClickListener, ISearchHistoryRecyclerView
{
    //데이터
    private var photoList = ArrayList<Photo>()

    //검색 기록 배열
    private var searchHistoryList = ArrayList<SearchData>()

    //어댑터
    private lateinit var photoGridRecyeclerViewAdapter: PhotoGridRecyeclerViewAdapter
    private lateinit var mySearchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter

    //서치뷰
    private lateinit var mySearchView: SearchView

    //서치뷰 에딧 텍스트
    private lateinit var mySearchViewEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")
        Log.d(TAG, "PhotoCollectionActivity - onCreate() called / searchTerm : $searchTerm, photoList.count() : ${photoList.count()}")

        val search_history_mode_switch = findViewById<SwitchMaterial>(R.id.search_history_mode_switch)
        search_history_mode_switch.setOnCheckedChangeListener(this)
        val clear_search_history_button = findViewById<Button>(R.id.clear_search_history_button)
        clear_search_history_button.setOnClickListener(this)

        search_history_mode_switch.isChecked = SharedPrefManager.checkSearchHistoryMode()

        //액티비티에서 어떤 액션바를 사용할지 설정한다.
        val top_app_bar = findViewById<MaterialToolbar>(R.id.top_app_bar)
        top_app_bar.title = searchTerm

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>

        //사진 리사이클러뷰 세팅
        this.photoCollectionRecyclerViewSetting(this.photoList)

        //저장된 검색기록 가져오기
        this.searchHistoryList = SharedPrefManager.getSearchHistoryList() as ArrayList<SearchData>

        this.searchHistoryList.forEach {
            Log.d(TAG, "저장된 검색 기록 - it.term : ${it.term} , it.timestamp: ${it.timestamp}")
        }
        handleSearchViewUi()

        //검색기록 리사이클러뷰 준비
        this.searchHistoryRecyclerViewSetting(this.searchHistoryList)
        if(searchTerm?.isNotEmpty() == true){
            val term = searchTerm ?: ""
            this.insertSearchTermHistory(term)
        }



    }
}