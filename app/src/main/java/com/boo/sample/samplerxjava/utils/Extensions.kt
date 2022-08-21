package com.boo.sample.samplerxjava.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.boo.sample.samplerxjava.utils.Constants.TAG
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.*

fun String?.isJsonObject(): Boolean =
    this?.startsWith("{") == true && this.endsWith("}")

fun String?.isJsonArray(): Boolean {
    return this?.startsWith("[") == true && this.endsWith("]")
}

fun Date.toSimpleString() : String {
    val format = SimpleDateFormat("HH:mm:ss")
    return format.format(this)
}

fun EditText.onMyTextChanged(completion: (Editable?) -> Unit) {
    this.addTextChangedListener(object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }
    })
}

//에딧텍스트 텍스트 변경을 flow로 받기
fun EditText.textChangesToFlow() : Flow<CharSequence?> {
    //flow 콜백 받기
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Unit
            }

            override fun afterTextChanged(p0: Editable?) {
                Unit
            }
            
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "onTextChanged:() called / textChangesToFlow()에 달려있는 텍스트 와쳐 / text : $text")
                trySend(text)
            }
        }
        // 위에서 설정한 리스너 달아주기
        addTextChangedListener(listener)

        //콜백이 사라질 때 실행됨
        awaitClose {
            Log.d(TAG, "textChangesToFlow() awaitClose 실행 ")
            removeTextChangedListener(listener)
       }
    }.onStart {
        Log.d(TAG, "textChangesToFlow() / onStart 발동 ")
        //Rx에서 onNext와 동일
        //emit으로 이벤트를 전달
        emit(text)
    }
}