package kr.co.testnavigation.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kr.co.testnavigation.databinding.FragmentThirdBinding
import kr.co.testnavigation.fragment.base.BaseFragment
import kr.co.testnavigation.model.datastore.RegiStore
import kr.co.testnavigation.model.viewmodel.RegiViewModel


class ThirdFragment : BaseFragment() {
    private val TAG = javaClass.simpleName


    private var thirdBinding : FragmentThirdBinding? = null
    private val binding get() = thirdBinding!!

    private val REQUEST_CODE = 1
//    private val hashMap = HashMap<String, String>()

    private lateinit var userRegiData : RegiStore<String>
    private lateinit var viewModel : RegiViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modelSetting()

    }

    private fun packaging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getMyPackage()
        }
        if (Build.VERSION.SDK_INT >= 23)
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO
                ),
                REQUEST_CODE
            )

//        Log.e(TAG, "packaging: ${viewModel.getSaveTest} " )
//
//        Log.e(TAG, "packaging: ${viewModel.getSaveTest.value} " )
    }

    private fun modelSetting() {
        viewModel = ViewModelProvider(this@ThirdFragment)[RegiViewModel::class.java]
        userRegiData = RegiStore(requireActivity())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getMyPackage() {
        var isExist = false


        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packageManager  = requireContext().packageManager
        val mApps: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (   info in mApps ) {
            Log.d("Test", "packageName: ${info.packageName}"
                    + ", versionName: ${info.versionName}"
                    + ", lastUpdateTime: ${info.lastUpdateTime}"
                    + ", targetSdk: ${info.applicationInfo.targetSdkVersion}"
                    + ", minSdk: ${info.applicationInfo.minSdkVersion}"
                    + ", sourceDir: ${info.applicationInfo.sourceDir}"
                    + ", uid: ${info.applicationInfo.uid}"
                    + ", label: ${info.applicationInfo.loadLabel(packageManager)}")
//            hashMap.put(info.applicationInfo.loadLabel(packageManager) as String, info.packageName)
        }
//
//        viewModel.setSaveTest(hashMap, "SAVETEST")
//        viewModel.getSaveTest.observe(requireActivity(), Observer{
//            viewModel.setSaveTest(hashMap, "SAVETEST")
//        })


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thirdBinding = FragmentThirdBinding.inflate(inflater, container, false)
        createAction()
        return binding.root
    }

    private fun createAction() {
        startClick()
        regiClick()
        usingExample()
        packaging()
    }


    private fun usingExample() {
        val action = ThirdFragmentDirections.actionThirdFragmentToTestFragment()
        binding.btnUsing.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(action)
        }
    }

    private fun regiClick() {
        val action = ThirdFragmentDirections.actionThirdFragmentToSecondFragment()
        binding.btnRegi.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(action)
        }
    }
    private fun startClick() {



        val action = ThirdFragmentDirections.actionThirdFragmentToFirstFragment()
        binding.btnStart.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(action)
        }
    }

    override fun onDestroyView() {
        thirdBinding = null
        super.onDestroyView()
    }
}