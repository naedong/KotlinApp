package kr.co.testnavigation.main.service

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlin.math.log

class MyFirebaseMessagingSerivce
    : FirebaseMessagingService() {
    private val TAG = javaClass.simpleName

    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "onNewToken:d $token", )
        toServer(token)
    }

    private fun toServer(token: String) {
        var token : Task<String> = FirebaseMessaging.getInstance().token
        Log.e(TAG, "onCreate: 실행확인 ", )
        token.addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){
                Log.e(TAG, "onCreate: MYToken"+it.getResult())
                onNewToken(it.getResult())
                return@OnCompleteListener
            }else{
                Log.e(TAG, "onCreate: 실패 ", )
            }

            val tokens = it.result

            Log.e(TAG, "onCreate: $tokens", )


        })
    }
}