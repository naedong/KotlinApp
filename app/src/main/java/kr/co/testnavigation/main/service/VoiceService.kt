package kr.co.testnavigation.main.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.speech.SpeechRecognizer

class VoiceService : Service() {

    private val handler = Handler()
    private lateinit var targetWords: List<String>
    private var textMessage: String = ""
    private var phoneNumber: String = ""
    private var interval: Long = 0
    private lateinit var speechRecognizer: SpeechRecognizer

    private val runnable = object : Runnable {
        override fun run() {
            if (checkWords()) {
                sendTextMessage()
                stopSelf()
            } else {
                sendTextMessage()
                handler.postDelayed(this, interval)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        targetWords = intent!!.getStringArrayListExtra("target_words")!!
        textMessage = intent.getStringExtra("text_message")!!
        phoneNumber = intent.getStringExtra("phone_number")!!
        interval = intent.getLongExtra("interval", 0)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        handler.post(runnable)
        return START_STICKY
    }

    private fun checkWords(): Boolean {
        return false
    }

    private fun sendTextMessage() {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null)
            Log.d("VoiceService", "Text message sent.")
        } catch (e: Exception) {
            Log.e("VoiceService", "Failed to send text message.", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
