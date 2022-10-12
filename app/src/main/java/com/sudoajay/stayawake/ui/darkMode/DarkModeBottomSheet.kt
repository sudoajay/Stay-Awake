package com.sudoajay.stayawake.ui.darkMode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.stayawake.ui.BaseActivity.Companion.openFragmentID
import com.sudoajay.stayawake.ui.mainActivity.MainActivity
import com.sudoajay.stayawake.databinding.LayoutDarkModeBottomSheetBinding
import com.sudoajay.stayawake.utill.HelperClass


class DarkModeBottomSheet(private var passAction: String) : BottomSheetDialogFragment() {
    private var _binding: LayoutDarkModeBottomSheetBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = LayoutDarkModeBottomSheetBinding.inflate(inflater, container, false)
        _binding?.bottomSheet = this

        return binding!!.root
    }


    fun getValue(): String {
        return HelperClass.getDarkMode(requireContext())
    }

    fun setValue(value: String) {
        if (getValue() == value) dismiss()
        else {
            HelperClass.setDarkModeValue(requireContext(), value)

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(openFragmentID, passAction)
            requireActivity().finish()
            startActivity(intent)


        }
    }


}

