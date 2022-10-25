package com.sudoajay.stayawake.ui.firebaseMessaging

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.databinding.FragmentPushNotificationBinding
import com.sudoajay.stayawake.ui.BaseFragment
import com.sudoajay.stayawake.utill.HelperClass
import com.sudoajay.stayawake.utill.HelperClass.Companion.isDarkMode

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FirebasePushNotificationFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentPushNotificationBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        isDarkTheme = isDarkMode(requireContext())

        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.bgBoxColor
            ), !isDarkTheme
        )


        _binding = FragmentPushNotificationBinding.inflate(inflater, container, false)
        binding?.lifecycleOwner = this
        binding?.fragment = this



        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onClickSkip()
                }
            })
    }


    fun onClickSkip(){
        openHomeActivity()
    }

    fun onClickGetNotify(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun openHomeActivity() {
        HelperClass.setFirstTimeNotify(requireContext(),false)
        findNavController().navigate(R.id.action_firebasePushNotificationFragment_to_homeFragment)
    }

    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted)
            throwToaster(getString(R.string.permission_not_granted_by_the_user_text))

        openHomeActivity()
    }



    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
