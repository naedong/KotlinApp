//package kr.co.testnavigation.util.Recogi
//
//import android.content.Context
//import android.content.Intent
//import android.media.AudioManager
//import android.os.Build
//import android.os.Bundle
//import android.speech.RecognitionService
//import android.speech.RecognizerIntent
//import android.speech.SpeechRecognizer
//import android.text.TextUtils.indexOf
//import android.util.Log
//import androidx.core.content.getSystemService
//import java.sql.DatabaseMetaData
//import kotlin.contracts.contract
//
//class MyRecognitionManager(
//    private val context : Context,
//    private val activationKeyword : String,
//    private val shouldMute : Boolean = false,
//    private val callback : RecognitionCallback )
//    : RecognitionCallback, RecognitionService(), Secquence{
//    private val TAG  = javaClass.simpleName
//    private var isActivated: Boolean = false
//    private val speech: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
//    private val audioManager: AudioManager? = context.getSystemService()
//
//    override fun onCreate() {
//        super.onCreate()
//        createRecognizer()
//    }
//
//    override fun onKeywordDetected() {
//
//    }
//    override fun onPrepared(status: RecognitionStatus) {
//    }
//
//    override fun onError(error: Int) {
//        super.onError(error)
//        if(isActivated){
//            callback?.onError(error)
//        }
//
//        when(error){
//            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> Log.e(TAG, "onError: 시간 ", )
//            SpeechRecognizer.ERROR_NETWORK -> Log.e(TAG, "onError: network ", )
//            SpeechRecognizer.ERROR_AUDIO -> Log.e(TAG, "onError: 오디오 에러 ", )
//            SpeechRecognizer.ERROR_SERVER -> Log.e(TAG, "onError: 서버 에러 ", )
//            SpeechRecognizer.ERROR_CLIENT -> Log.e(TAG, "onError: ERROR_CLIENT 에러 ", )
//            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> Log.e(TAG, "onError: ERROR_SPEECH_TIMEOUT 에러 ", )       //아무 음성도 듣지 못했을 때
//            SpeechRecognizer.ERROR_NO_MATCH ->      { Log.e(TAG, "onError: ERROR_NO_MATCH 에러 ", ) }
//            SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->  Log.e(TAG, "onError: ERROR_RECOGNIZER_BUSY 에러 ", )
//            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->  Log.e(TAG, "onError: ERROR_INSUFFICIENT_PERMISSIONS 에러 ", )
//        }
//
//    }
//
//    override fun onEndOfSpeech() {
//        super.onEndOfSpeech()
//        callback?.onEndOfSpeech()
//    }
//
//    override fun onBeginningOfSpeech() {
//        super.onBeginningOfSpeech()
//        callback?.onBeginningOfSpeech()
//    }
//
//    override fun onReadyForSpeech(params: Bundle?) {
//        super.onReadyForSpeech(params)
//        callback?.onReadyForSpeech(params)
//    }
//
//    override fun onBufferReceived(buffer: ByteArray?) {
//        super.onBufferReceived(buffer)
//        callback?.onBufferReceived(buffer)
//    }
//
//    override fun onRmsChanged(rmsdB: Float) {
//        super.onRmsChanged(rmsdB)
//        callback?.onRmsChanged(rmsdB)
//    }
//
//    override fun onEndOfSegmentedSession() {
//        super.onEndOfSegmentedSession()
//        callback?.onEndOfSegmentedSession()
//    }
//
//    override fun onEvent(eventType: Int, params: Bundle?) {
//        super.onEvent(eventType, params)
//        callback?.onEvent(eventType, params)
//    }
//
//    override fun onResults(results: Bundle?) {
//        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
//        val scores = results!!.getStringArrayList(SpeechRecognizer.CONFIDENCE_SCORES)
//        if(matches != null){
//            if (isActivated){
//                isActivated = false
//                callback?.onResults(matches, scores)
//                stopRecognition()
//            } else {
//                matches.firstOrNull { it.contains(other = activationKeyword, ignoreCase = true) }
//                    ?.let {
//                        isActivated = true
//                        callback?.onKeywordDetected()
//                    }
//                startRecognition()
//            }
//        }
//    }
//
//    override fun startRecognition() {
//        speech.startListening(recognizerIntent)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        muteRecognition(false)
//        speech.destroy()
//    }
//
//    override fun stopRecognition() {
//        speech.stopListening()
//    }
//
//    override fun cancel() {
//        speech.cancel()
//    }
//
//
//    fun createRecognizer() {
//        if (SpeechRecognizer.isRecognitionAvailable(context)) {
//            speech.setRecognitionListener(this)
//            callback?.onPrepared(RecognitionStatus.SUCCESS)
//        } else {
//            callback?.onPrepared(RecognitionStatus.UNAVAILABLE)
//        }
//    }
//
//    private val recognizerIntent by lazy {
//        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
//            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
//            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 40)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
//            }
//        }
//    }
//    @Suppress("DEPRECATION")
//    private fun muteRecognition(mute: Boolean) {
//        audioManager?.let {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                val flag = if (mute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
//                it.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, flag, 0)
//                it.adjustStreamVolume(AudioManager.STREAM_ALARM, flag, 0)
//                it.adjustStreamVolume(AudioManager.STREAM_MUSIC, flag, 0)
//                it.adjustStreamVolume(AudioManager.STREAM_RING, flag, 0)
//                it.adjustStreamVolume(AudioManager.STREAM_SYSTEM, flag, 0)
//            } else {
//                it.setStreamMute(AudioManager.STREAM_NOTIFICATION, mute)
//                it.setStreamMute(AudioManager.STREAM_ALARM, mute)
//                it.setStreamMute(AudioManager.STREAM_MUSIC, mute)
//                it.setStreamMute(AudioManager.STREAM_RING, mute)
//                it.setStreamMute(AudioManager.STREAM_SYSTEM, mute)
//            }
//        }
//    }
//
//    override fun onStartListening(recognizerIntent: Intent?, listener: Callback?) {
//
//    }
//
//    override fun onCancel(listener: Callback?) {
//
//    }
//
//    override fun onStopListening(listener: Callback?) {
//
//    }
//
//}
//
//private fun Char.contains(other: CharSequence, ignoreCase: Boolean = false): Boolean =
//    if (other is String) indexOf(other, ignoreCase = ignoreCase) >= 0
//    else indexOf(other, 0, length, ignoreCase) >= 0
//
//private  fun CharSequence.indexOf(other: CharSequence, startIndex: Int, endIndex: Int, ignoreCase: Boolean, last: Boolean = false): Int {
//    val indices = if (!last)
//        startIndex.coerceAtLeast(0)..endIndex.coerceAtMost(length)
//    else
//        startIndex.coerceAtMost(lastIndex) downTo endIndex.coerceAtLeast(0)
//
//    if (this is String && other is String) { // smart cast
//        for (index in indices) {
//            if (other.regionMatches(0, this, index, other.length, ignoreCase))
//                return index
//        }
//    } else {
//        for (index in indices) {
//            if (other.regionMatchesImpl(0, this, index, other.length, ignoreCase))
//                return index
//        }
//    }
//    return -1
//}
//internal fun CharSequence.regionMatchesImpl(thisOffset: Int, other: CharSequence, otherOffset: Int, length: Int, ignoreCase: Boolean): Boolean {
//    if ((otherOffset < 0) || (thisOffset < 0) || (thisOffset > this.length - length) || (otherOffset > other.length - length)) {
//        return false
//    }
//
//    for (index in 0 until length) {
//        if (!this[thisOffset + index].equals(other[otherOffset + index], ignoreCase))
//            return false
//    }
//    return true
//}
