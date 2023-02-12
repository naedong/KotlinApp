package kr.co.testnavigation.fragment

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kr.co.testnavigation.R
import kr.co.testnavigation.databinding.FragmentFirstBinding
import kr.co.testnavigation.databinding.FragmentTestBinding
import kr.co.testnavigation.main.MainActivity
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestFragment : Fragment() {
    private var viewBinding: FragmentTestBinding? = null
    private val binding get() = viewBinding!!

    private val TAG  = javaClass.simpleName
    private var speechRecognizer : SpeechRecognizer? = null


    private var itIntent //음성인식 Intent
            : Intent? = null
    private var end = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentTestBinding.inflate(inflater, container, false)
        setTestCreateView()
        return binding.root
    }

    private fun setTestCreateView() {
        binding.STTButton.setOnClickListener {
          setTest()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context == null) {
            throw java.lang.IllegalStateException(this.requireContext().toString()+"Not attached to a context.")
        }
    }

    private fun recognitionListener() = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) = Toast.makeText(requireContext(), "음성인식 시작", Toast.LENGTH_SHORT).show()

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(requireContext(), "퍼미션 없음", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_NO_MATCH ->
                {
                    setTest()
                }
            }
        }

        override fun onResults(results: Bundle) {
            Toast.makeText(requireContext(), "음성인식 종료", Toast.LENGTH_SHORT).show()
            binding.textView.text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
            sencSMS()
        }
    }
    private fun setTest() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.`package`)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 86400000)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 50)
            putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE, 1)
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()).apply {
            setRecognitionListener(recognitionListener())
            startListening(speechRecognizerIntent)
        }

    }

    private fun sencSMS()
    {
        try {
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                smsManager = this.requireActivity().getSystemService(SmsManager::class.java)
            } else {
                smsManager = SmsManager.getDefault()
            }
            var pi: PendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                Intent(requireActivity(), MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            var numbes = "01087190962"
            smsManager.sendTextMessage(numbes, null, binding.textView.text.toString(), null, null)
        }
        catch (e: java.lang.Exception){
            Log.i(tag, "sencSMS: "+e.message.toString())
            Log.e(tag, "sencSMS: "+e.localizedMessage.toString() )
        }
    }
}