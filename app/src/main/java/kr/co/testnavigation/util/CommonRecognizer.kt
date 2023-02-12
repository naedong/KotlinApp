package kr.co.testnavigation.util

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import kr.co.testnavigation.main.service.test


internal val CommonRecognizer : RecognitionListener = object : RecognitionListener {
    private val TAG = javaClass.simpleName

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {
  //      Log.e(TAG, "onRmsChanged: $rmsdB " )
   //     Log.e(TAG, "onRmsChanged: 데시벨")
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
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                Log.e(TAG, "onError: ERROR_SPEECH_TIMEOUT 에러 ", )
            SpeechRecognizer.ERROR_NO_MATCH -> {
                Log.e(TAG, "onError: ERROR_NO_MATCH 에러 ", )
//                test.handlerState.sendEmptyMessage(kr.co.testnavigation.main.service.test.H_VOICE_RESTART)
            }
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                Log.e(TAG, "onError: ERROR_RECOGNIZER_BUSY 에러 ", )
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                Log.e(TAG, "onError: ERROR_INSUFFICIENT_PERMISSIONS 에러 ", )
        }

    }
    override fun onResults(results: Bundle?) {
        Log.i(TAG, "onResults:  실행 확인")
        Log.e(TAG, "onResults: "+results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0] )
    // Toast.makeText(Context., results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0], Toast.LENGTH_LONG).show()
      //  val mResult = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      //  val rs = arrayOfNulls<String>(mResult!!.size)
       // mResult.toArray(rs)
        //Log.i(TAG, "onResults:  실행 확인")
       // Log.d("key", rs.toString())
    }
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}