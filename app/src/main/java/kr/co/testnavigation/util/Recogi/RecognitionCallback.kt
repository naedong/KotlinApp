package kr.co.testnavigation.util.Recogi

import android.os.Bundle
import android.speech.RecognitionListener
import java.util.ArrayList

interface RecognitionCallback : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onError(error: Int) {}
    fun onResults(results: String, scores: ArrayList<String>?) {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onSegmentResults(segmentResults: Bundle) {}
    override fun onEndOfSegmentedSession() {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
    fun onKeywordDetected()
    fun onPrepared(status: RecognitionStatus)

    fun startRecognition()
    fun stopRecognition()
    fun cancel()
}