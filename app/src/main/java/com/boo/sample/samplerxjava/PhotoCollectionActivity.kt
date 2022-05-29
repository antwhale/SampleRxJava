package com.boo.sample.samplerxjava

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boo.sample.samplerxjava.SearchData.Photo
import com.boo.sample.samplerxjava.SearchData.SearchData
import com.boo.sample.samplerxjava.recyclerview.ISearchHistoryRecyclerView
import com.boo.sample.samplerxjava.recyclerview.PhotoGridRecyeclerViewAdapter
import com.boo.sample.samplerxjava.recyclerview.SearchHistoryRecyclerViewAdapter
import com.boo.sample.samplerxjava.retrofit.RetrofitManager
import com.boo.sample.samplerxjava.utils.Constants.TAG
import com.boo.sample.samplerxjava.utils.RESPONSE_STATUS
import com.boo.sample.samplerxjava.utils.SharedPrefManager
import com.boo.sample.samplerxjava.utils.toSimpleString
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*
import kotlin.collections.ArrayList

class PhotoCollectionActivity : AppCompatActivity(),
    SearchView.OnQueryTextListener,
    CompoundButton.OnCheckedChangeListener,
    View.OnClickListener, ISearchHistoryRecyclerView {
    //데이터
    private var photoList = ArrayList<Photo>()

    //검색 기록 배열
    private var searchHistoryList = ArrayList<SearchData>()

    //어댑터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyeclerViewAdapter
    private lateinit var mySearchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter

    //서치뷰
    private lateinit var mySearchView: SearchView

    //서치뷰 에딧 텍스트
    private lateinit var mySearchViewEditText: EditText
    val top_app_bar = findViewById<MaterialToolbar>(R.id.top_app_bar)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")
        Log.d(
            TAG,
            "PhotoCollectionActivity - onCreate() called / searchTerm : $searchTerm, photoList.count() : ${photoList.count()}"
        )

        val search_history_mode_switch =
            findViewById<SwitchMaterial>(R.id.search_history_mode_switch)
        search_history_mode_switch.setOnCheckedChangeListener(this)
        val clear_search_history_button = findViewById<Button>(R.id.clear_search_history_button)
        clear_search_history_button.setOnClickListener(this)

        search_history_mode_switch.isChecked = SharedPrefManager.checkSearchHistoryMode()

        //액티비티에서 어떤 액션바를 사용할지 설정한다.

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
        if (searchTerm?.isNotEmpty() == true) {
            val term = searchTerm ?: ""
            this.insertSearchTermHistory(term)
        }

    }

    //검색 기록 리사이클러뷰 준비
    private fun searchHistoryRecyclerViewSetting(searchHistoryList: ArrayList<SearchData>) {
        Log.d(TAG, "PhotoCollectionActivity - searchHistoryRecyclerViewSetting() called")

        this.mySearchHistoryRecyclerViewAdapter = SearchHistoryRecyclerViewAdapter(this)
        this.mySearchHistoryRecyclerViewAdapter.submitList(searchHistoryList)

        val myLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        myLinearLayoutManager.stackFromEnd = true

        val search_history_recycler_view =
            findViewById<RecyclerView>(R.id.search_history_recycler_view)
        search_history_recycler_view.apply {
            layoutManager = myLinearLayoutManager
            this.scrollToPosition(mySearchHistoryRecyclerViewAdapter.itemCount - 1)
            adapter = mySearchHistoryRecyclerViewAdapter
        }
    }

    //그리드 사진 리사이클러뷰 세팅
    private fun photoCollectionRecyclerViewSetting(photoList: ArrayList<Photo>) {
        Log.d(TAG, "PhotoCollectionActivity - photoCollecitonRecyclerViewSetting() called")

        this.photoGridRecyclerViewAdapter = PhotoGridRecyeclerViewAdapter()
        this.photoGridRecyclerViewAdapter.submitList(photoList)

        val my_photo_recycler_view = findViewById<RecyclerView>(R.id.my_photo_recycler_view)
        my_photo_recycler_view.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        my_photo_recycler_view.adapter = this.photoGridRecyclerViewAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "PhotoCollectionActivity - onCreateOptionsMenu() called")

        val inflater = menuInflater
        inflater.inflate(R.menu.top_app_bar_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        this.mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView
        this.mySearchView.apply {
            this.queryHint = "검색어를 입력해주세요"
            this.setOnQueryTextListener(this@PhotoCollectionActivity)
            this.setOnQueryTextFocusChangeListener{ _, hasExpaned ->
                val linear_search_history_view = findViewById<LinearLayout>(R.id.linear_search_history_view)
                when(hasExpaned){
                    true -> {
                        Log.d(TAG, "서치뷰 열림")
                        linear_search_history_view.visibility = View.VISIBLE
                    }
                    false -> {
                        Log.d(TAG, "서치뷰 닫힘")
                        linear_search_history_view.visibility = View.INVISIBLE
                    }
                }
            }
            mySearchViewEditText = this.findViewById(androidx.appcompat.R.id.search_src_text)
        }

        this.mySearchViewEditText.apply {
            this.filters = arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }

        return true
    }

    //서치뷰 검색어 입력 이벤트
    //검색버튼이 클릭되었을 때
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG, "PhotoCollectionActivity - onQueryTextSubmit() called / query: $query")

        if(!query.isNullOrEmpty()){
            top_app_bar.title = query

            this.insertSearchTermHistory(query)
            this.searchPhotoApiCall(query)
        }
        this.top_app_bar.collapseActionView()

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d(TAG, "PhotoCollectionActivity - onQueryTextChange() called / newText: $newText")

        val userInputText = newText ?: ""
        if(userInputText.count() == 12) {
            Toast.makeText(this, "검색어는 12자 까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
        val search_history_mode_switch = findViewById<SwitchMaterial>(R.id.search_history_mode_switch)

        when(switch){
            search_history_mode_switch -> {
                if(isChecked){
                    Log.d(TAG, "검색어 저장기능 온")
                    SharedPrefManager.setSearchHistoryMode(isActivated = true)
                } else {
                    Log.d(TAG, "검색어 저장기능 오프")
                    SharedPrefManager.setSearchHistoryMode(isActivated = false)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        val clear_search_history_button = findViewById<Button>(R.id.clear_search_history_button)
        when(view){
            clear_search_history_button -> {
                Log.d(TAG, "검색 기록 삭제 버튼이 클릭 되었다.")
                SharedPrefManager.clearSearchHistoryList()
                this.searchHistoryList.clear()
                //UI처리
                handleSearchViewUi()
            }
        }
    }

    //검색 아이템 삭제 버튼 이벤트
    override fun onSearchItemDeleteClicked(position: Int) {
        Log.d(TAG, "PhotoCollectionActivity - onSearchItemDeleteClicked() called / position: $position")

        //해당 요소 삭제
        this.searchHistoryList.removeAt(position)
        //데이터 덮어쓰기
        SharedPrefManager.storeSearchHistoryList(this.searchHistoryList)
        //데이터 변경됐다고 알려줌
        this.mySearchHistoryRecyclerViewAdapter.notifyDataSetChanged()

        handleSearchViewUi()
    }

    override fun onSearchItemClicked(position: Int) {
        Log.d(TAG, "PhotoCollectionActivity - onSearchItemClicked() called / position: $position")

        val queryString = this.searchHistoryList[position].term
        searchPhotoApiCall(queryString)
        top_app_bar.title = queryString
        this.insertSearchTermHistory(searchTerm = queryString)
        this.top_app_bar.collapseActionView()
    }

    private fun handleSearchViewUi(){
        Log.d(TAG, "PhotoCollectionActivity - handleSearchViewUi() called / size : ${this.searchHistoryList.size}")

        val search_history_recycler_view = findViewById<RecyclerView>(R.id.search_history_recycler_view)
        val search_history_recycler_view_label = findViewById<TextView>(R.id.search_history_recycler_view_label)
        val clear_search_history_buttton = findViewById<Button>(R.id.clear_search_history_button)
        if(this.searchHistoryList.size > 0){
            search_history_recycler_view.visibility = View.VISIBLE
            search_history_recycler_view_label.visibility = View.VISIBLE
            clear_search_history_buttton.visibility = View.VISIBLE
        } else {
            search_history_recycler_view.visibility = View.INVISIBLE
            search_history_recycler_view_label.visibility = View.INVISIBLE
            clear_search_history_buttton.visibility = View.INVISIBLE
        }
    }
    private fun insertSearchTermHistory(searchTerm: String){
        Log.d(TAG, "PhotoCollectionActivity - insertSearchTermHistory() called")

        if(SharedPrefManager.checkSearchHistoryMode()){
            //중복 아이템 삭제
            var indexListToRemove = ArrayList<Int>()

            this.searchHistoryList.forEachIndexed { index, searchDataItem ->
                if(searchDataItem.term == searchTerm) {
                    Log.d(TAG, "index: $index")
                    indexListToRemove.add(index)
                }
            }

            indexListToRemove.forEach{
                this.searchHistoryList.removeAt(it)
            }

            //새 아이템 넣기
            val newSearchData = SearchData(term = searchTerm, timestamp = Date().toSimpleString())
            this.searchHistoryList.add(newSearchData)

            SharedPrefManager.storeSearchHistoryList(this.searchHistoryList)
            this.mySearchHistoryRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun searchPhotoApiCall(query: String){
        RetrofitManager.instance.searchPhotos(searchTerm = query, completion = { status, list ->
            when(status){
                RESPONSE_STATUS.OKAY -> {
                    Log.d(TAG, "PhotoCollectionActivity - searchPhotoApiCall() called 응답 성공 / list.size : ${list?.size}")

                    if(list != null){
                        this.photoList.clear()
                        this.photoList = list
                        this.photoGridRecyclerViewAdapter.submitList(this.photoList)
                        this.photoGridRecyclerViewAdapter.notifyDataSetChanged()
                    }
                }
                RESPONSE_STATUS.NO_CONTENT -> {
                    Toast.makeText(this, "$query 에 대한 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }
}