package kr.co.testnavigation.main.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import kr.co.testnavigation.R
import kr.co.testnavigation.fragment.FirstFragment
import kr.co.testnavigation.main.MainActivity
import kr.co.testnavigation.model.datastore.RegiStore
import kr.co.testnavigation.model.viewmodel.RegiViewModel
import kr.co.testnavigation.model.viewmodel.ViewFactory
import kr.co.testnavigation.util.CommonNotification

import kr.co.testnavigation.util.Recogi.RecognitionStatus
import java.time.Instant
import java.util.Locale

class test(

)
    : RecognitionService(), ViewModelStoreOwner {

    private val TAG = javaClass.simpleName
    private var hAudioManager : AudioManager? = null
    private var itIntent : Intent? = null
    private var voiceEnd = false
    private var hBoolVoiceRecoStarted = false
    private var hSrRecognizer: SpeechRecognizer? = null

    private val SMS_SEND_PERMISSION = 1
    private lateinit var userRegiData : RegiStore<String>
    private lateinit var viewModel : RegiViewModel

    private val CHANNEL_ID = "testChannel01"   // Channel for notification
    private var notificationManager: NotificationManager? = null

    private  var hViewModelStore : ViewModelStore = ViewModelStore()

    companion object {
        // voice 0 ~ 5
        const val H_VOICE_READY = 0
        const val H_VOICE_START = 1
        const val H_VOICE_END = 2
        const val H_VOICE_RESTART = 3
        const val H_VOICE_CANCEL = 4
        const val H_VOICE_CHECK_RECOGINTION_SPPORT = 5

        // text  6 ~ 8
        const val H_TEXT_READY = 6
        const val H_TEXT_START = 7
        const val H_TEXT_END = 8

        //recode 9 ~ 14
        const val H_RECO_READY = 9
        const val H_RECO_START = 10
        const val H_RECO_END = 11
        const val H_RECO_DOWNLOAD = 12
        const val H_RECO_CANCEL = 13
        const val H_RECO_SETTING = 14
    }

    private val handlerState : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                H_VOICE_READY -> {}
                H_VOICE_START -> {  }
                H_VOICE_END -> { stopRecoListening() }
                H_VOICE_CANCEL -> { }
                H_VOICE_RESTART -> { getStartAudio() }
                H_VOICE_CHECK_RECOGINTION_SPPORT -> {}

                H_TEXT_READY -> {}
                H_TEXT_START -> {}
                H_TEXT_END -> {}

                H_RECO_READY -> {}
                H_RECO_START -> {}
                H_RECO_END -> {}
                H_RECO_DOWNLOAD -> {}
                H_RECO_CANCEL -> {}
                H_RECO_SETTING -> {}
            }
        }

    }


    override fun onCreate() {
        super.onCreate()
        atLifeCycle()
    }

    private fun createNotificationChannel() {
        Log.e(TAG, "createNotificationChannel: 시작", )
        createChannel(CHANNEL_ID, "testChannel", "THIS SI")
        displayNoti()

    }

    private fun displayNoti() {


      //  var notiBuilder = Notification.Builder(applicationContext, CHANNEL_ID)
     //   .setSmallIcon(R.drawable.ic_baseline_mic_24)
     //   .setContentTitle("녹음이 시작되었습니다")
     //   .setContentText("녹음 중")
      //      .build()
        var notiBuilder = CommonNotification(applicationContext, "녹음이 시작되었습니다", "녹음 중")
        var notificationId = 60

        notificationManager?.notify(notificationId, notiBuilder)
    }

    private fun createChannel(channelId: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT // set importance
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = channelDescription
            }
            // Register the channel with the system
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }


    private fun atLifeCycle() {
        setIntent()
        getStartAudio()
        setViewModel()
    }

    private fun setIntent() {
        itIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.`package`)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }
    }


    private fun audioSetting() {
        hAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    private fun getStartAudio() {
        audioSetting()
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            onPrepared(RecognitionStatus.SUCCESS)
            startRecoListening()
        }   else    {
            onPrepared(RecognitionStatus.UNAVAILABLE)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        return START_STICKY
    }

    private fun startRecoListening() {
        Log.e(TAG, "startRecoListening: $voiceEnd", )
        Log.e(TAG, "startRecoListening: $hBoolVoiceRecoStarted", )


        if (!voiceEnd) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hAudioManager!!.isStreamMute(AudioManager.STREAM_MUSIC)) {
                    hAudioManager!!.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_MUTE,
                        0
                    )
                }
            }
            else {
                hAudioManager!!
                    .adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            }
            if(!hBoolVoiceRecoStarted){
                if(hSrRecognizer == null){
                    hSrRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
                    hSrRecognizer!!
                        .setRecognitionListener(testRecognizer)
                }
                hSrRecognizer!!.startListening(itIntent)
            }
            hBoolVoiceRecoStarted = true
        }
    }

    private fun stopRecoListening() {
        try {
            Log.e(TAG, "stopRecoListening: 꺼찜")
            hSrRecognizer!!.stopListening()
        }
        catch (ex : Exception){
            ex.printStackTrace()
            Log.e(TAG, "stopRecoListening: ${ex.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceEnd = true
        hSrRecognizer!!.destroy()
        handlerState.sendEmptyMessage(H_VOICE_READY)
        if (hAudioManager != null)
            hAudioManager!!.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_MUTE,
                0
            )
    }

    override fun onStartListening(recognizerIntent: Intent?, listener: Callback?) {}
    override fun onCancel(listener: Callback?) {
        hSrRecognizer!!.cancel()
    }
    override fun onStopListening(listener: Callback?) {
        Log.e(TAG, "onStopListening: $voiceEnd", )
        handlerState.sendEmptyMessage(H_VOICE_RESTART)
    }

    private val testRecognizer : RecognitionListener = object : RecognitionListener {
        private val TAG = javaClass.simpleName
        override fun onReadyForSpeech(params: Bundle?) {
            muteRecognition(!voiceEnd || !hBoolVoiceRecoStarted)
        }
        // 차후 사용자 설정
        private fun muteRecognition(mute : Boolean) {
            hAudioManager?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val flag = if (mute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
                    it.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, flag, 0)
                    it.adjustStreamVolume(AudioManager.STREAM_ALARM, flag, 0)
                    it.adjustStreamVolume(AudioManager.STREAM_MUSIC, flag, 0)
                    it.adjustStreamVolume(AudioManager.STREAM_RING, flag, 0)
                    it.adjustStreamVolume(AudioManager.STREAM_SYSTEM, flag, 0)
                } else {
                    it.setStreamMute(AudioManager.STREAM_NOTIFICATION, mute)
                    it.setStreamMute(AudioManager.STREAM_ALARM, mute)
                    it.setStreamMute(AudioManager.STREAM_MUSIC, mute)
                    it.setStreamMute(AudioManager.STREAM_RING, mute)
                    it.setStreamMute(AudioManager.STREAM_SYSTEM, mute)
                }
            }
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {
          //  Log.e(TAG, "onRmsChanged: $rmsdB " )
        //    Log.e(TAG, "onRmsChanged: 데시벨")
        }
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {

        }
        override fun onError(error: Int) {
            when(error){
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                    Log.e(TAG, "onError: 시간 ", )
                SpeechRecognizer.ERROR_NETWORK ->
                    Log.e(TAG, "onError: network ", )
                SpeechRecognizer.ERROR_AUDIO ->
                    Log.e(TAG, "onError: 오디오 에러 ", )
               SpeechRecognizer.ERROR_SERVER ->
                    Log.e(TAG, "onError: 서버 에러 ", )
                SpeechRecognizer.ERROR_CLIENT ->
                   Log.e(TAG, "onError: ERROR_CLIENT 에러 ", )
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    Log.e(TAG, "onError: ERROR_SPEECH_TIMEOUT 에러 ", )
                    hBoolVoiceRecoStarted = false
                    handlerState.sendEmptyMessage(H_VOICE_RESTART)

                }//아무 음성도 듣지 못했을 때
                SpeechRecognizer.ERROR_NO_MATCH ->      {
                    Log.e(TAG, "onError: ERROR_NO_MATCH 에러 ", )
                    hBoolVoiceRecoStarted = false
                    handlerState.sendEmptyMessage(H_VOICE_RESTART)

                }
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                    Log.e(TAG, "onError: ERROR_RECOGNIZER_BUSY 에러 ", )
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                    Log.e(TAG, "onError: ERROR_INSUFFICIENT_PERMISSIONS 에러 ", )
            }

        }


//        results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0].let {
//            viewModel.setTEST(it.toString(), "TEST")
//        }

        override fun onResults(results: Bundle?) {
            Log.e(TAG, "onResults: "+results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0] )
            val sults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if ( sults != null) {
                hBoolVoiceRecoStarted = false
                Log.e(TAG, "onResults: "+results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0] )
                sencSMS()
                stopRecoListening()
            }
            else {

            }
        }
        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (hBoolVoiceRecoStarted && matches != null) {
                Log.e(TAG, "onPartialResults: $matches" )
            }
        }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this, ViewFactory(application))[RegiViewModel::class.java]
        userRegiData = RegiStore(applicationContext)
    }

    override fun getViewModelStore(): ViewModelStore {
        return hViewModelStore
    }

    private fun sencSMS()
    {
        try {
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                smsManager = getSystemService(SmsManager::class.java)
            } else {
                smsManager = SmsManager.getDefault()
            }


            smsManager.sendTextMessage("01087190962", null, viewModel.getTest.value, null, null)
        }
        catch (e: java.lang.Exception){
            Log.i(TAG, "sencSMS: "+e.message.toString())
            Log.e(TAG, "sencSMS: "+e.localizedMessage.toString() )
        }
    }


    // 모듈로 나눌 것
    fun onPrepared(status : RecognitionStatus){
        when(status) {
            RecognitionStatus.SUCCESS -> {
                Log.e(TAG, "onPrepared: Success")
            }
            RecognitionStatus.UNAVAILABLE -> {
                Log.e("onPrepared", "onPrepared: Failure or unavailable")
        }
    }
    }

    fun onKeywordDetected(){

    }



}
