package kr.co.testnavigation.main.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kr.co.testnavigation.`object`.Constants
import kr.co.testnavigation.main.MainActivity
import java.util.*
import kotlin.math.log

class BackGroundService : RecognitionService() {
    private  val TAG = javaClass.simpleName
    private var mSrRecognizer : SpeechRecognizer? = null
    private var mAudioManager : AudioManager? = null
    private var mBoolVoiceRecoStarted : Boolean = false
    private var end : Boolean = false
    var itIntent : Intent? = null
  //  val SMS_SEND_PERMISSION = 1

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: backgroundservice ")
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (SpeechRecognizer.isRecognitionAvailable(applicationContext)){
            itIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            itIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
            itIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN.toString())
            itIntent!!.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                1500
            )
            itIntent!!.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                1500
            )
            itIntent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            startListening()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        end = true;
        mSrRecognizer!!.destroy()
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_READY)
        if(mAudioManager != null)
            mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)

    }

    override fun onStartListening(recognizerIntent: Intent?, listener: Callback?) {
        TODO("Not yet implemented")

    }

    fun startListening() {
        if (!end) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!mAudioManager!!.isStreamMute(AudioManager.STREAM_MUSIC)) {
                    mAudioManager!!.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_MUTE,
                        0
                    )
                }
            }else {
                mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            }
            if(!mBoolVoiceRecoStarted){
                if(mSrRecognizer == null){
                    mSrRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
                    mSrRecognizer!!.setRecognitionListener(mClsRecoListener())
                }
                mSrRecognizer!!.startListening(itIntent)
            }
            mBoolVoiceRecoStarted = true;
        }
    }

    override fun onCancel(listener: Callback?) {

        mSrRecognizer!!.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notification: Notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle("Service")
            .setContentText("음성인식 온")
//            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onStopListening(listener: Callback?) {
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_RESTART)
    }

    private fun mClsRecoListener() = object : RecognitionListener {
        // 말할 준비
        override fun onReadyForSpeech(params: Bundle?) {

        }
        // 말 시작
        override fun onBeginningOfSpeech() {

        }
        // 데시벨
        override fun onRmsChanged(rmsdB: Float) {

            Log.i(TAG, "onRmsChanged: your Desb"+rmsdB)
            Log.i(TAG, "onRmsChanged: onYour Desbel")

        }
        // 인식이 된 단어를 버퍼에 담음
        override fun onBufferReceived(buffer: ByteArray?) {
            Log.i(TAG, "onBufferReceived: "+buffer.toString())
        }

        override fun onEndOfSpeech() {

        }

        override fun onError(error: Int) {
            when (error) {
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> { }
                SpeechRecognizer.ERROR_NETWORK -> { }
                SpeechRecognizer.ERROR_AUDIO -> { }
                SpeechRecognizer.ERROR_SERVER -> { }
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END) } // 음성이 없을 때
                SpeechRecognizer.ERROR_NO_MATCH ->                     //적당한 결과를 찾지 못했을 때
                    mHdrVoiceRecoState.sendEmptyMessage(Recognition.MSG_VOICE_RECO_END)
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> { }
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> { }
            }
        }

        override fun onResults(results: Bundle?) {

            Log.i(TAG, "onResults: ")
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            Log.i(TAG, "onResults: "+results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0])
            val matches = results!!.getStringArrayList(key)
            for (result in matches!!) {
                Log.i(TAG, "onResults: " + result.toString())

                Toast.makeText(applicationContext, "result $result", Toast.LENGTH_LONG)
//            val mResult = results!!.getStringArrayList(key)
//            val rs = arrayOfNulls<String>(mResult!!.size)
//            mResult.toArray(rs)
//            Log.i(TAG, "onResults: "+rs.get(0))
//            Log.i(TAG, "onResults: $rs")
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            Log.i(TAG, "onPartialResults: "+partialResults.toString())
            Log.i(TAG, "onPartialResults: "+partialResults)

                // Log.i(TAG, "onPartialResults: "+partialResults!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0])
        }   // 부분 인식

        override fun onEvent(eventType: Int, params: Bundle?) {
            Log.i(TAG, "onEvent: "+params.toString())
            Log.i(TAG, "onEvent: "+eventType.toString())

        }
    }

    private val mHdrVoiceRecoState : Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            when (msg.what){
                MSG_VOICE_RECO_READY -> {}
                MSG_VOICE_RECO_END -> {
                    stopListening()
                    sendEmptyMessageDelayed(MSG_VOICE_RECO_RESTART, 1000)
                }
                MSG_VOICE_RECO_RESTART -> startListening()
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun stopListening() {
        try {
            if (mSrRecognizer != null && mBoolVoiceRecoStarted) {
                mSrRecognizer!!.stopListening() //음성인식 Override 중단을 호출
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        mBoolVoiceRecoStarted = false
    }




    companion object {
        const val MSG_VOICE_RECO_READY = 0
        const val MSG_VOICE_RECO_END = 1
        const val MSG_VOICE_RECO_RESTART = 2
    }
}