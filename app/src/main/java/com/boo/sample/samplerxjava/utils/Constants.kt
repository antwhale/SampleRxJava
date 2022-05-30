package com.boo.sample.samplerxjava.utils

object Constants {
    const val TAG : String = "로그"
}

enum class SEARCH_TYPE {
    PHOTO,
    USER
}

enum class RESPONSE_STATUS {
    OKAY,
    FAIL,
    NO_CONTENT
}


object API {
    const val BASE_URL : String = "https://api.unsplash.com/"

    // 여러분꺼 하셔야됩니다!! ㅎㅎ;;
    const val CLIENT_ID : String = "uDHruO7K6f6n1VCLizMmW9frLJdwf8N7-c0bvUBU__0"

    const val SEARCH_PHOTOS : String = "search/photos"
    const val SEARCH_USERS : String = "search/users"

}