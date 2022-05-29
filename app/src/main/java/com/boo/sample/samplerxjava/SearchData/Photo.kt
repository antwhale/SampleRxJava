package com.boo.sample.samplerxjava.SearchData

import java.io.Serializable

data class Photo(
    var thumbnail: String?,
    var author: String?,
    var createdAt: String?,
    var likesCount: Int?
) : Serializable {

}
