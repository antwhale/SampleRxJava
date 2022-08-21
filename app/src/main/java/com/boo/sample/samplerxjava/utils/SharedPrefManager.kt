package com.boo.sample.samplerxjava.utils

import android.content.Context
import android.util.Log
import com.boo.sample.samplerxjava.App
import com.boo.sample.samplerxjava.SearchData.SearchData
import com.boo.sample.samplerxjava.utils.Constants.TAG
import com.google.gson.Gson

object SharedPrefManager {
    private const val SHARED_SEARCH_HISTORY = "shared_search_history"
    private const val KEY_SEARCH_HISTORY = "key_search_history"

    private const val SHARED_SEARCH_HISTORY_MODE = "shared_search_history_mode"
    private const val KEY_SEARCH_HISTORY_MODE = "key_search_history_mode"

    //검색어 저장 모드 설정하기
    fun setSearchHistoryMode(isActivated: Boolean){
        Log.d(TAG, "SharedPrefManager - setSearchHistoryMode() called / isActivated: $isActivated")

        //쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        //쉐어드 에디터 가져오기
        val editor = shared.edit()

        editor.putBoolean(KEY_SEARCH_HISTORY_MODE, isActivated)
        editor.apply()
    }
    //검색 목록을 저장
    fun storeSearchHistoryList(searchHistoryList: MutableList<SearchData>){
        Log.d(TAG, "SharedPrefManager - storeSearchHistoryList() called")

        //매개변수로 들어온 배열을 -> 문자열로 변환
        val searchHistoryListString : String = Gson().toJson(searchHistoryList)
        Log.d(TAG, "SharedPrefManager - searchHistoryListString : $searchHistoryListString")

        //쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString(KEY_SEARCH_HISTORY, searchHistoryListString)
        editor.apply()
    }

    //검색어 저장 모드 확인하기
    fun checkSearchHistoryMode(): Boolean {
        //쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        return shared.getBoolean(KEY_SEARCH_HISTORY_MODE, false)
    }

    //검색 목록 가져오기
    fun getSearchHistoryList(): MutableList<SearchData> {
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        val storedSearchHistoryListString = shared.getString(KEY_SEARCH_HISTORY, "")!!
        var storedSearchHistoryList = ArrayList<SearchData>()

        //검색 목록이 값이 있다면
        if(storedSearchHistoryListString.isNotEmpty()){
            storedSearchHistoryList = Gson().fromJson(storedSearchHistoryListString, Array<SearchData>::class.java)
                .toMutableList() as ArrayList<SearchData> /* = java.util.ArrayList<com.boo.sample.samplerxjava.SearchData.SearchData> */
        }
        return storedSearchHistoryList
    }

    fun clearSearchHistoryList(){
        Log.d(TAG, "clearSearchHistoryList called")

        //쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        //쉐어드 에디터 가져오기
        val editor = shared.edit()
        editor.clear()
        editor.apply()
    }
/*// Use this check to determine whether Bluetooth classic is supported on the device.
// Then you can selectively disable BLE-related features.
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
        Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        finish();
    }
// Use this check to determine whether BLE is supported on the device. Then
// you can selectively disable BLE-related features.
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        finish();
    }*/


}