package kr.co.testnavigation.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import kr.co.testnavigation.R
import kr.co.testnavigation.databinding.FragmentFirstBinding
import kr.co.testnavigation.fragment.base.BaseFragment
import kr.co.testnavigation.main.service.MyFirebaseMessagingSerivce
import kr.co.testnavigation.main.service.MyRecognition
import kr.co.testnavigation.main.service.test
import kr.co.testnavigation.model.datastore.RegiStore
import kr.co.testnavigation.model.viewmodel.RegiViewModel
import kr.co.testnavigation.util.CommonNotification
import java.io.IOException
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */



class FirstFragment : BaseFragment() {

    private val REQUEST_CODE_SPEECH_INPUT = 1

    private val TAG : String? = javaClass.simpleName
    private var manager : NotificationManager? = null
    private var viewBinding: FragmentFirstBinding? = null
    private val binding get() = viewBinding!!

    private lateinit var userRegiData : RegiStore<String>
    private lateinit var viewModel : RegiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentSetting()

    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun fragmentSetting() {
        viewModel = ViewModelProvider(this@FirstFragment)[RegiViewModel::class.java]
        userRegiData = RegiStore(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentFirstBinding.inflate(inflater, container, false)
        startRec()
        clickStop()
        return binding.root
    }

    private fun startRec() {
        binding.STTButton.setOnClickListener {
            val isEmpower = ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat
                .checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED
                //퍼미션 체크 차후엔 모아서 모듈로
            if (isEmpower) {
                empowerRecordAudioAndWriteReadStorage()

                // 권한 부여 되었을 경우
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    startRecording()
                }
            }
        }
        startNoti()
        requireActivity().startService( Intent(requireActivity(), test::class.java))
    }

    private fun startNoti() {
        var notiBuilder = CommonNotification(requireContext(), "일단 넣어", "실행 중")

        var notificationManager : NotificationManager? = this.requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            var notificationChannel = NotificationChannel("채널 아이디", "채널이름", NotificationManager.IMPORTANCE_HIGH)
            notificationManager!!.createNotificationChannel(notificationChannel)
            notificationManager!!.notify(0, notiBuilder)
            Log.e(TAG, "startNoti: 시작했다.", )
//           var myFirebaseMessagingSerivce = MyFirebaseMessagingSerivce()
//            myFirebaseMessagingSerivce.onNewToken("token")
        }
    }

    private fun stopRecording() {

        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
            Toast.makeText(requireContext(), "녹음이 완료 되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "녹음 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun empowerRecordAudioAndWriteReadStorage() {
        val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(requireActivity(), permissions,0)
    }

    private var outputPath: String? = null
    private var mediaRecorder : MediaRecorder? = null
    private var state : Boolean = false


    // 백그라운드 지원
    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        // 이것도 설정으로 변경 가능하게
        val fileName: String = Date().getTime().toString() + ".mp3"

        outputPath = Environment.getExternalStorageDirectory().absolutePath + "/Recordings/Voice Recorder/" + fileName //내장메모리 밑에 위치
        mediaRecorder = MediaRecorder(requireContext())
        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(outputPath)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(requireContext(), "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException){
            e.printStackTrace()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun clickStop() {
        binding.btnStop.setOnClickListener {
            stopRecording()
            requireActivity().stopService(Intent(requireActivity(), test::class.java))
        }
    }






}