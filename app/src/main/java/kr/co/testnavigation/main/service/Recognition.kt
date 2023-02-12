package kr.co.testnavigation.main.service


import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import kr.co.testnavigation.R
import kr.co.testnavigation.`object`.Constants
import kr.co.testnavigation.main.MainActivity
import kr.co.testnavigation.util.CommonRecognizer
import java.util.*
import kotlin.math.log

//import kr.co.testnavigation.fragment.FirstFragment.CHANLE_ID

class Recognition : RecognitionService() {
    private val TAG  = javaClass.simpleName
    private var mSrRecognizer: SpeechRecognizer? = null
    var mBoolVoiceRecoStarted = false
    protected var mAudioManager: AudioManager? = null
    var itIntent //음성인식 Intent
            : Intent? = null
    var end = false

    companion object {
        const val MSG_VOICE_RECO_READY = 0
        const val MSG_VOICE_RECO_END = 1
        const val MSG_VOICE_RECO_RESTART = 2
    }

    override fun onCreate() {
        super.onCreate()
        startAudioManager()
    }

    private fun startAudioManager() {
        Log.e(TAG, "startAudioManager: 실행 확인", )
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (SpeechRecognizer.isRecognitionAvailable(applicationContext)) {
            Log.e(TAG, "SpeechRecognizer.isRecognitionAvailable: 실행 확인", )
            itIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            itIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
            itIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN.toString())
            // 시간 조절하는 엑스트라
//            itIntent!!.putExtra(
//                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
//                1500
//            )
//            itIntent!!.putExtra(
//                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
//                1500
//            )
            itIntent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            itIntent!!.putExtra(RecognizerIntent.EXTRA_PROMPT, "녹음이 시작되었습니다.")
            startListening()
        }
    }


    private val mHdrVoiceRecoState: Handler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_VOICE_RECO_READY -> {
                    Log.i(TAG, "handleMessage: MSG_VOICE_RECO_READY")
                }
                MSG_VOICE_RECO_END -> {
                    stopListening()
                    sendEmptyMessageDelayed(MSG_VOICE_RECO_RESTART, 1000)
                }
                MSG_VOICE_RECO_RESTART -> startListening()
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification: Notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle("Service")
            .setContentText("음성인식 온")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        end = true
        mSrRecognizer!!.destroy()
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_READY) //음성인식 서비스 다시 시작
        if (mAudioManager != null) mAudioManager!!.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_UNMUTE,
            0
        )
    }

    override fun onStartListening(recognizerIntent: Intent, listener: Callback) {}
    open fun startListening() {
        if (!end) {
            //음성인식을 시작하기 위해 Mute
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!mAudioManager!!.isStreamMute(AudioManager.STREAM_MUSIC)) {
                    mAudioManager!!.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_MUTE,
                        0
                    )
                }
            } else {
                mAudioManager!!
                    .adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            }
            if (!mBoolVoiceRecoStarted) { // 최초의 실행이거나 인식이 종료된 후에 다시 인식을 시작하려 할 때
                if (mSrRecognizer == null) {
                    mSrRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
                    mSrRecognizer!!.setRecognitionListener(CommonRecognizer)
                }
                mSrRecognizer!!.startListening(itIntent)
            }
            mBoolVoiceRecoStarted = true //음성인식 서비스 실행 중
        }
    }

    fun stopListening() //Override 함수가 아닌 한번만 호출되는 함수 음성인식이 중단될 때
    {
        try {
            if (mSrRecognizer != null && mBoolVoiceRecoStarted) {
                mSrRecognizer!!.stopListening() //음성인식 Override 중단을 호출
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e(TAG, "stopListening: ${ex.toString()}", )
            Log.e(TAG, "stopListening: ${ex.message}" )
        }
        mBoolVoiceRecoStarted = false //음성인식 종료
    }

    override fun onCancel(listener: Callback) {
        mSrRecognizer!!.cancel()
    }

    override fun onStopListening(listener: Callback) { //음성인식 Override 함수의 종료부분
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_RESTART) //음성인식 서비스 다시 시작
    }

    private val mClsRecoListener: RecognitionListener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onResults(results: Bundle) {
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult = results.getStringArrayList(key)
            val rs = arrayOfNulls<String>(mResult!!.size)
            mResult.toArray(rs)
            Log.i(TAG, "onResults:  실행 확인")
            Log.d("key", rs.toString())
        }
        override fun onReadyForSpeech(params: Bundle) {}
        override fun onEndOfSpeech() {}
        override fun onError(intError: Int) {
            when (intError) {
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                    "네트워크 타임아웃"
                }
                SpeechRecognizer.ERROR_NETWORK -> {
                    "네트워크 에러"
                }
                SpeechRecognizer.ERROR_AUDIO -> {
                    "오디오 에러"
                }
                SpeechRecognizer.ERROR_SERVER -> {
                    "서버 에러"
                }
                SpeechRecognizer.ERROR_CLIENT -> {
                    "클라이언트 에러"
                }
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->       {
                    "음성이 없음"
                }              //아무 음성도 듣지 못했을 때
//                {}  mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END)
                SpeechRecognizer.ERROR_NO_MATCH ->               {
                    "매치 값이 없음"
                }      //적당한 결과를 찾지 못했을 때
//                    mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END)
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                    "오류 인식자의 과부하"
                }
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    "권한 체크 안했음"
                }
            }
        }

        override fun onBeginningOfSpeech() {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEvent(eventType: Int, params: Bundle) {}
        override fun onPartialResults(partialResults: Bundle) { //부분 인식을 성공 했을 때
        }
    }


}
