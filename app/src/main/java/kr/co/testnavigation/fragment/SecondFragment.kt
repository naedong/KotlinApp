package kr.co.testnavigation.fragment

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.testnavigation.databinding.FragmentSecondBinding
import kr.co.testnavigation.fragment.base.BaseFragment
import kr.co.testnavigation.main.MainActivity
import kr.co.testnavigation.model.datastore.RegiStore
import kr.co.testnavigation.model.viewmodel.RegiViewModel
import kr.co.testnavigation.util.CommonTextWatcher

class SecondFragment : BaseFragment() {

    private var secondBinding : FragmentSecondBinding? = null
    private val binding get() = secondBinding!!

    private val TAG = javaClass.simpleName

    private val SMS_SEND_PERMISSION = 1

    private lateinit var userRegiData : RegiStore<String>
    private lateinit var viewModel : RegiViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        secondBinding = FragmentSecondBinding.inflate(inflater, container, false)
        onCheck()
        return binding.root
    }

    private fun onCheck() {
        permissionCheck()
        setSave()
        obseves()
    }


    private fun onData() {
            viewModel = ViewModelProvider(this@SecondFragment)[RegiViewModel::class.java]
            userRegiData = RegiStore(requireActivity())
    }

    private fun permissionCheck() {
        var permissionCheck : Int  = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.SEND_SMS)){
                Toast.makeText(requireActivity(), "SMS 권한 없음", Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS), SMS_SEND_PERMISSION)
        }
    }

    private fun setSave() {
        Log.i(tag, "setSave:ㅎㅁ")
        binding.btnSave.setOnClickListener {
            sencSMS()
          onYourRegi()
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
            var numbes = "01087190962"
            smsManager.sendTextMessage(numbes, null, "test Message", null, null)
        }
        catch (e: java.lang.Exception){
            Log.i(tag, "sencSMS: "+e.message.toString())
            Log.e(tag, "sencSMS: "+e.localizedMessage.toString() )
        }
    }


    private fun obseves() {


        binding.edName.addTextChangedListener(CommonTextWatcher(
            onChanged = {
                source, _, _, _ ->
                Log.e(TAG, "edName obseves: onChanged $source")
            },
            afterChanged = {
                source ->
                viewModel.setName(source.toString(), "NAME")
            }
        ))
        binding.edCellphone.addTextChangedListener(CommonTextWatcher(
            onChanged = {
                source, _, _, _ ->
                Log.e(TAG, "obseves: $source", )
            },
            afterChanged = {
            source ->
            viewModel.setPhone(source.toString(), "PHONE")
        }
        ))
        binding.edSaveWord.addTextChangedListener(CommonTextWatcher(
           onChanged = {
               source, _, _, _ ->
               Log.e(tag, "edSaveWord obseves:  onChanged $source" )
           },
            afterChanged = {
                source ->
                viewModel.setName(source.toString(), "SAVEWORD")
            }
        ))

        binding.edGender.addTextChangedListener(CommonTextWatcher(
            onChanged = {
                source, _, _, _ ->
                Log.e(TAG, "obseves: $source" )
            },
            afterChanged = {
                source ->
                viewModel.setName(source.toString(), "GENDER")
            }
        ))

        binding.edAddress.addTextChangedListener(CommonTextWatcher(
            onChanged = { source, _, _, _ ->
                Log.e(TAG, "obseves: $source")
            },
            afterChanged = {
                source ->
                viewModel.setName(source.toString(), "ADDRESS")
            }
        ))


    }

    fun onYourRegi() {
        Log.e(TAG, "onYourRegi: 여기같은데", )
        viewModel.setName(binding.edName.text.toString(),"NAME")
        viewModel.setSaveWord(binding.edSaveWord.text.toString(), "SAVEWORD")
        viewModel.setPhone(binding.edCellphone.text.toString(), "PHONE")
        viewModel.setAddress(binding.edAddress.text.toString(), "ADDRESS")



        viewModel.getName.observe(requireActivity()){
            binding.txRename.text = viewModel.getName.value
                }
        viewModel.getAddress.observe(requireActivity()){

        }


        Log.e(TAG, "onYourRegi: 여기같은데", )
    }

    override fun onDestroyView() {
        secondBinding = null
        super.onDestroyView()

    }


}

